package net.cmlzw.nineteen.controller;

import net.cmlzw.nineteen.domain.Quiz;
import net.cmlzw.nineteen.domain.User;
import net.cmlzw.nineteen.repository.QuizRepository;
import net.cmlzw.nineteen.repository.UserRepository;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.crypto.codec.Hex;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.wechat.api.JsApiTicket;
import org.springframework.social.wechat.api.WeChat;
import org.springframework.social.wechat.api.WeChatMp;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@Controller
public class UserController {
    @Autowired
    UserRepository userRepository;
    @Autowired
    QuizRepository quizRepository;
    @Autowired
    ConnectionRepository connectionRepository;
    @Autowired
    WeChatMp weChatMp;
    @Value("${spring.social.wechat.mp.appid}")
    String mpAppId;

    @GetMapping("/userinfo")
    @ResponseBody
    public Map<String, String> userInfo(Principal principal) {
        String username = principal.getName();
        HashMap<String, String> info = new HashMap<>();
        info.put("username", username);
        User user = userRepository.findOne(username);
        if (user == null) {
            throw new ResourceNotExistedException("user '" + username + "'");
        }
        info.put("nickname", user.getNickname());
        String openid = "";
        Connection<WeChat> connection = connectionRepository.findPrimaryConnection(WeChat.class);
        if (connection != null) {
            openid = connection.getKey().getProviderUserId();
            System.out.println("===provider user id: " + openid);
        } else {
            System.out.println("===Does not find connect");
        }
        info.put("openid", openid);

        // TODO: Yes it is ugly to find the phone and org info from quiz
        String phone = "";
        String organizationId = "";
        List<Quiz> quizzes = quizRepository.findByUsername(username);
        if (quizzes != null && quizzes.size() > 0) {
            Quiz quiz = quizzes.get(0);
            phone = quiz.getPhone();
            if (quiz.getOrganizationId() != null) {
                organizationId = Long.toString(quiz.getOrganizationId());
            }
        }
        info.put("phone", phone);
        info.put("organizationId", organizationId);
        return info;
    }

    @GetMapping("/login")
    public String login(HttpServletRequest request) {
        String agent = request.getHeader(HttpHeaders.USER_AGENT);
        Pattern wx = Pattern.compile(".*MicroMessenger.*", Pattern.CASE_INSENSITIVE);
        if (wx.matcher(agent).matches()) {
            return "redirect:/auth/wechat?scope=snsapi_login";
        }
        return "redirect:/auth/wechat?scope=snsapi_login_qrconnect";
//        return "login";
    }

    @GetMapping("/jsapi/sign")
    @ResponseBody
    public Map<String, String> jsApiSignature(@RequestParam String url) throws NoSuchAlgorithmException {
        JsApiTicket ticket = weChatMp.jsApiOperations().getTicket();
        HashMap<String, String> params = new HashMap<>();
        params.put("jsapi_ticket", ticket.getTicket());
        params.put("noncestr", RandomStringUtils.randomAlphanumeric(16));
        params.put("timestamp", String.format("%d", Instant.now().getEpochSecond()));
        params.put("url", url);

        HashMap<String, String> result = new HashMap<>();
        result.put("appId", mpAppId);
        result.put("nonceStr", params.get("noncestr"));
        result.put("timestamp", params.get("timestamp"));
        result.put("url", url);
        result.put("signature", sign(params));
        return result;
    }

    public String sign(Map<String, String> param) throws NoSuchAlgorithmException {
        String jsApiTicket = param.get("jsapi_ticket");
        String noncestr = param.get("noncestr");
        String timestamp = param.get("timestamp");
        String url = param.get("url");
        StringBuilder builder = new StringBuilder();
        String[] keys = new String[]{"jsapi_ticket", "noncestr", "timestamp", "url"};
        for (String key : keys) {
            if (builder.length() != 0) {
                builder.append("&");
            }
            builder.append(key).append("=").append(param.get(key));
        }
        String request = builder.toString();
        System.out.println("Sign " + builder.toString());
        MessageDigest sha1 = MessageDigest.getInstance("SHA1");
        byte[] digest = sha1.digest(request.getBytes());
        return new String(Hex.encode(digest));
    }
}
