package net.cmlzw.nineteen.repository;

import net.cmlzw.nineteen.domain.Quiz;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@DataJpaTest
public class QuizRepositoryTest {
    @Autowired
    TestEntityManager entityManager;
    @Autowired
    QuizRepository repository;
    private Date today;
    private Date yesterday;

    @Before
    public void setUp() throws Exception {
        today = DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH);
        yesterday = DateUtils.addDays(today, -1);

        Quiz qXuemin = new Quiz();
        qXuemin.setPhone("15800000000");
        qXuemin.setScore(5);
        qXuemin.setLevel(1);
        qXuemin.setUsername("u1");
        qXuemin.setCreated(today);
        entityManager.persist(qXuemin);

        Quiz qXueba = new Quiz();
        qXueba.setPhone("15800000000");
        qXueba.setScore(4);
        qXueba.setLevel(2);
        qXueba.setUsername("u1");
        qXueba.setCreated(today);
        entityManager.persist(qXueba);

        Quiz qXueshen = new Quiz();
        qXueshen.setPhone("15800000000");
        qXueshen.setScore(3);
        qXueshen.setLevel(3);
        qXueshen.setUsername("u1");
        qXueshen.setCreated(today);
        entityManager.persist(qXueshen);

        Quiz anotherXueba = new Quiz();
        anotherXueba.setPhone("15800000001");
        anotherXueba.setScore(9);
        anotherXueba.setLevel(2);
        anotherXueba.setUsername("u2");
        anotherXueba.setOrganizationId(1L);
        anotherXueba.setCreated(today);
        entityManager.persist(anotherXueba);

        Quiz qYesterday = new Quiz();
        qYesterday.setPhone("15800000000");
        qYesterday.setScore(8);
        qYesterday.setLevel(1);
        qYesterday.setUsername("u1");
        qYesterday.setCreated(yesterday);
        entityManager.persist(qYesterday);
    }

    @Test
    public void findByPersonIdAndCreated() throws Exception {
        List<Quiz> actual = repository.findByUsernameAndCreated("u1", today);
        assertNotNull(actual);
        assertEquals(3, actual.size());
        assertEquals(1, actual.get(0).getLevel());
        assertEquals(2, actual.get(1).getLevel());
        assertEquals(3, actual.get(2).getLevel());
        assertEquals(0, repository.findByUsernameAndCreated("u2", yesterday).size());
    }

    @Test
    public void findByPersonIdAndLevelAndCreated() throws Exception {
        Quiz actual = repository.findByUsernameAndLevelAndCreated("u1", 1, today);
        assertNotNull(actual);
        assertEquals(5, actual.getScore());
        assertNull(repository.findByUsernameAndLevelAndCreated("u1", 2, yesterday));
    }

    @Test
    public void findTop20ByLevelAndCreatedOrderByScoreDesc() throws Exception {
        List<Quiz> actual = repository.findTop20ByLevelAndCreatedOrderByScoreDesc(2, today);
        assertNotNull(actual);
        assertEquals(2, actual.size());
        assertEquals("u2", actual.get(0).getUsername());
        assertEquals(9, actual.get(0).getScore());
        assertEquals("u1", actual.get(1).getUsername());
        assertEquals(4, actual.get(1).getScore());
    }

    @Test
    public void countDistinctUsernameByOrganizationId() throws Exception {
        Quiz anotherXueba = new Quiz();
        anotherXueba.setPhone("15800000001");
        anotherXueba.setScore(1);
        anotherXueba.setLevel(1);
        anotherXueba.setUsername("u2");
        anotherXueba.setOrganizationId(1L);
        anotherXueba.setCreated(today);
        entityManager.persist(anotherXueba);

        int actual = repository.countDistinctUsernameByOrganizationId(1L);
        assertEquals(1, actual);
    }

    @Test
    public void countDistinctUsernameFromTwoDays() throws Exception {
        Quiz qYesterday = new Quiz();
        qYesterday.setPhone("15800000000");
        qYesterday.setScore(1);
        qYesterday.setLevel(1);
        qYesterday.setUsername("u1");
        qYesterday.setOrganizationId(1L);
        qYesterday.setCreated(yesterday);
        entityManager.persist(qYesterday);
        int actual = repository.countDistinctUsernameByOrganizationId(1L);
        assertEquals(2, actual);
    }
}