package net.cmlzw.nineteen.repository;

import net.cmlzw.nineteen.domain.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@DataJpaTest
public class UserRepositoryTest {
    @Autowired
    UserRepository repository;

    @Test
    public void saveCascade() throws Exception {
        User user = new User("u1", "password", true);
        user.addAuthority("USER");
        repository.save(user);
    }
}