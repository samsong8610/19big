package net.cmlzw.nineteen.controller;

import net.cmlzw.nineteen.repository.UserRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.wechat.api.WeChat;
import org.springframework.social.wechat.api.WeChatMp;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.Instant;
import java.util.Date;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@WebMvcTest(UserController.class)
public class UserControllerTest {
    @Autowired
    UserController controller;
    @MockBean
    UserRepository userRepository;
    @MockBean
    ConnectionRepository connectionRepository;
    @MockBean
    WeChatMp weChatMp;

    @Test
    public void sign() throws Exception {
        HashMap<String, String> param = new HashMap<>();
        param.put("jsapi_ticket", "sM4AOVdWfPE4DxkXGEs8VMCPGGVi4C3VM0P37wVUCFvkVAy_90u5h9nbSlYy3-Sl-HhTdfl2fzFy1AOcHKP7qg");
        param.put("noncestr", "Wm3WZYTPz0wzccnW");
        param.put("timestamp", "1414587457");
        param.put("url", "http://mp.weixin.qq.com?params=value");
        assertEquals("0f9de62fce790f9a083d5c99e95740ceb90c27ed", controller.sign(param));
        System.out.println(String.format("%d", Instant.now().getEpochSecond()));
        System.out.println(String.format("%d", new Date().getTime()));
    }

}