package net.cmlzw.auth.social;

import net.cmlzw.nineteen.domain.User;
import net.cmlzw.nineteen.repository.UserRepository;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionSignUp;
import org.springframework.social.connect.UserProfile;

public class SimpleConnectionSignUpService implements ConnectionSignUp {
    public static final Logger logger = Logger.getLogger(SimpleConnectionSignUpService.class);

    private UserRepository userRepository;

    public SimpleConnectionSignUpService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public String execute(Connection<?> connection) {
        UserProfile userProfile = connection.fetchUserProfile();
        // TODO: username generated by db?
        int retry = 0;
        do {
            try {
                String username = "User" + RandomStringUtils.randomNumeric(7);
                String password = RandomStringUtils.randomAlphanumeric(12);
                User newUser = new User(username, password, true);
                newUser.setNickname(userProfile.getName());
                newUser.addAuthority(this.assignAuthority(userProfile));
                userRepository.save(newUser);
                return newUser.getUsername();
            } catch (DuplicateKeyException dke) {
                logger.warn("SignUp new user failed for duplicated username.", dke);
            } catch (Exception e) {
                logger.warn("SignUp new user failed for unknown reason.", e);
            }
        } while(retry++ < 3);
        logger.warn("SignUp failed after retrying 3 times.");
        return null;
    }

    private String assignAuthority(UserProfile userProfile) {
        // Assign ADMIN authority to the first connected user!
        // TODO: add transaction control when determine if the user is the first
        if (userRepository.count() == 0) {
            logger.info("Assign ADMIN authority to " + userProfile.getName());
            return "ADMIN";
        }
        return "USER";
    }
}