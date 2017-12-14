package net.cmlzw.auth.config;

import net.cmlzw.auth.social.SimpleConnectionSignUpService;
import net.cmlzw.nineteen.repository.SocialMpAccessTokenRepository;
import net.cmlzw.nineteen.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.core.env.Environment;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.social.UserIdSource;
import org.springframework.social.config.annotation.ConnectionFactoryConfigurer;
import org.springframework.social.config.annotation.EnableSocial;
import org.springframework.social.config.annotation.SocialConfigurer;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.connect.ConnectionSignUp;
import org.springframework.social.connect.UsersConnectionRepository;
import org.springframework.social.connect.jdbc.JdbcUsersConnectionRepository;
import org.springframework.social.wechat.api.MpAccessTokenRepository;
import org.springframework.social.wechat.api.WeChat;
import org.springframework.social.wechat.api.WeChatMp;
import org.springframework.social.wechat.api.impl.MpAccessTokenManager;
import org.springframework.social.wechat.api.impl.WeChatMpTemplate;
import org.springframework.social.wechat.connect.WeChatConnectionFactory;

import javax.sql.DataSource;

@Configuration
@EnableSocial
public class SocialConfig implements SocialConfigurer {
    @Autowired
    DataSource dataSource;
    @Autowired
    UserRepository userRepository;
    @Autowired
    ConnectionFactoryLocator connectionFactoryLocator;
    @Value("${spring.social.wechat.mp.appid}")
    String mpAppId;
    @Value("${spring.social.wechat.mp.appsecret}")
    String mpAppSecret;

    @Bean
    public ConnectionSignUp connectionSignUpService() {
        return new SimpleConnectionSignUpService(userRepository);
    }

    @Override
    public void addConnectionFactories(ConnectionFactoryConfigurer connectionFactoryConfigurer, Environment environment) {
        connectionFactoryConfigurer.addConnectionFactory(
                new WeChatConnectionFactory((environment.getProperty("spring.social.wechat.appid")),
                        environment.getProperty("spring.social.wechat.appsecret")));
    }

    @Override
    public UserIdSource getUserIdSource() {
        return null;
    }

    @Override
    public UsersConnectionRepository getUsersConnectionRepository(ConnectionFactoryLocator connectionFactoryLocator) {
        JdbcUsersConnectionRepository repository = new JdbcUsersConnectionRepository(
                dataSource, connectionFactoryLocator, Encryptors.noOpText());
        repository.setConnectionSignUp(connectionSignUpService());
        return repository;
    }

    @Bean
    @Scope(value="request", proxyMode= ScopedProxyMode.INTERFACES)
    public ConnectionRepository connectionRepository() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return getUsersConnectionRepository(connectionFactoryLocator).createConnectionRepository(authentication.getName());
    }

    @Bean
    @Scope(value = "request", proxyMode = ScopedProxyMode.INTERFACES)
    public WeChat weChat() {
        return connectionRepository().getPrimaryConnection(WeChat.class).getApi();
    }

    @Bean
    public MpAccessTokenRepository accessTokenRepository() {
        return new SocialMpAccessTokenRepository();
    }

    @Bean
    public MpAccessTokenManager tokenManager() {
        return new MpAccessTokenManager(mpAppId, mpAppSecret, accessTokenRepository());
    }

    @Bean
    public WeChatMp weChatMp() {
        return new WeChatMpTemplate(tokenManager());
    }
}
