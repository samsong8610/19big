package net.cmlzw.nineteen.repository;

import net.cmlzw.nineteen.domain.Award;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@DataJpaTest
public class AwardRepositoryTest {
    @Autowired
    TestEntityManager entityManager;
    @Autowired
    AwardRepository repository;

    @Test
    public void findByPhone() throws Exception {
        Award award = new Award();
        award.setNickname("u1");
        award.setPhone("15200000000");
        award.setGift(1);
        award.setCreated(new Date());
        entityManager.persist(award);
        Award another = new Award();
        another.setNickname("u1");
        another.setPhone("15200000001");
        another.setGift(1);
        another.setCreated(new Date());

        List<Award> actual = repository.findByPhone("15200000000");
        assertNotNull(actual);
        assertEquals(1, actual.size());
        assertEquals(award, actual.get(0));
    }

    @Test
    public void findByPhoneMultiple() throws Exception {
        Award award = new Award();
        award.setNickname("u1");
        award.setPhone("15200000000");
        award.setGift(1);
        award.setCreated(new Date());
        entityManager.persist(award);
        Award another = new Award();
        another.setNickname("u1");
        another.setPhone("15200000000");
        another.setGift(2);
        another.setCreated(new Date());
        entityManager.persist(another);

        List<Award> actual = repository.findByPhone("15200000000");
        assertNotNull(actual);
        assertEquals(2, actual.size());
    }

    @Test
    public void findByPhoneNotFound() throws Exception {
        List<Award> actual = repository.findByPhone("15200000002");
        assertNotNull(actual);
        assertEquals(0, actual.size());
    }

}