package net.cmlzw.nineteen.controller;

import net.cmlzw.nineteen.domain.User;
import net.cmlzw.nineteen.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

@Controller
public class UserController {
    @Autowired
    UserRepository userRepository;

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
}
