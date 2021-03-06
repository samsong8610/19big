package net.cmlzw.auth.config;

import net.cmlzw.auth.social.SimpleSocialUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.social.security.SocialUserDetailsService;
import org.springframework.social.security.SpringSocialConfigurer;

import javax.sql.DataSource;

@Configuration
@EnableWebSecurity
@Profile("dev")
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    DataSource dataSource;

    @Bean
    public SocialUserDetailsService socialUserDetailsService() {
        return new SimpleSocialUserDetailsService(userDetailsService());
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.jdbcAuthentication()
                .dataSource(dataSource);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.formLogin()
                .loginPage("/login")
                .loginProcessingUrl("/login/authenticate")
                .failureForwardUrl("/login?param.error=bad_credentials")
                .permitAll()
                .and().authorizeRequests()
                .antMatchers("/public/**", "/favicon.ico", "/console/**").permitAll()
                .antMatchers("/static/MP_verify_GX9TZYFwRrjCnD90.txt", "/MP_verify_GX9TZYFwRrjCnD90.txt").permitAll()
//                .antMatchers("/19da.html").permitAll() // TODO: remove permitAll for 19da.html
                .antMatchers("/**").permitAll()
                .and().csrf().disable();
        http.headers().frameOptions().sameOrigin();
//        http.apply(new SpringSocialConfigurer());
    }
}
