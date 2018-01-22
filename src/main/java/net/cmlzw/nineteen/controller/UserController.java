package net.cmlzw.nineteen.controller;

import net.cmlzw.nineteen.domain.Quiz;
import net.cmlzw.nineteen.domain.Token;
import net.cmlzw.nineteen.domain.TokenType;
import net.cmlzw.nineteen.domain.User;
import net.cmlzw.nineteen.repository.QuizRepository;
import net.cmlzw.nineteen.repository.TokenRepository;
import net.cmlzw.nineteen.repository.UserRepository;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
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

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    @Autowired
    UserRepository userRepository;
    @Autowired
    QuizRepository quizRepository;
    @Autowired(required = false)
    ConnectionRepository connectionRepository;
    @Autowired(required = false)
    WeChatMp weChatMp;
    @Value("${spring.social.wechat.mp.appid}")
    String mpAppId;
    @Autowired
    TokenRepository tokenRepository;

    @GetMapping("/userinfo")
    @ResponseBody
    public Map<String, String> userInfo(HttpServletResponse response, Principal principal) {
        if (principal == null || StringUtils.isEmpty(principal.getName())) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return null;
        }
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
        if (agent == null || wx.matcher(agent).matches()) {
            return "redirect:/auth/wechat?scope=snsapi_login";
        }
        return "redirect:/auth/wechat?scope=snsapi_login_qrconnect";
//        return "login";
    }

    @GetMapping("/19da.html")
    public String home(HttpServletRequest request, HttpServletResponse response, Principal principal) {
        String username = principal != null ? principal.getName() : null;
        if (!StringUtils.isEmpty(username)) {
            User user = userRepository.findOne(username);
            if (user == null) {
                throw new ResourceNotExistedException("user '" + username + "'");
            }
            logger.info("response with cookies username: {}, nickname: {}",
                    user.getUsername(), user.getNickname());
            response.addCookie(new Cookie("username", user.getUsername()));
            response.addCookie(new Cookie("nickname", user.getNickname()));
        }
        return "19da";
    }

    @GetMapping("/jsapi/sign")
    @ResponseBody
    public Map<String, String> jsApiSignature(@RequestParam String url) throws NoSuchAlgorithmException {
        JsApiTicket ticket = getTicket();
        HashMap<String, String> params = new HashMap<>();
        params.put("jsapi_ticket", ticket == null ? "" : ticket.getTicket());
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

    private JsApiTicket getTicket() {
        List<Token> tickets = tokenRepository.findAllByType(TokenType.JsApiTicket);
        Token current;
        JsApiTicket result;
        if (tickets != null && tickets.size() > 0) {
            current = tickets.get(0);
            if (current.isExpired()) {
                result = weChatMp.jsApiOperations().getTicket();
                if (result == null) {
                    return result;
                }
                current.setContent(result.getTicket());
                current.setExpiresIn(result.getExpiresIn());
                current.setExpiresAt(Instant.now().getEpochSecond() + result.getExpiresIn());
            } else {
                result = new JsApiTicket(current.getContent(), (int) current.getExpiresIn());
            }
        } else {
            result = weChatMp.jsApiOperations().getTicket();
            if (result == null) {
                return result;
            }
            long expiresAt = Instant.now().getEpochSecond() + result.getExpiresIn();
            current = new Token(TokenType.JsApiTicket, result.getTicket(), result.getExpiresIn(), expiresAt);
        }
        tokenRepository.save(current);
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
