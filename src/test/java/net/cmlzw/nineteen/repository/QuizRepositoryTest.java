package net.cmlzw.nineteen.repository;

import net.cmlzw.nineteen.domain.Quiz;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

@RunWith(SpringRunner.class)
@DataJpaTest
public class QuizRepositoryTest {
    @Autowired
    TestEntityManager entityManager;
    @Autowired
    QuizRepository repository;
    private LocalDate today;
    private LocalDate yesterday;

    @Before
    public void setUp() throws Exception {
        today = LocalDate.now().atStartOfDay().toLocalDate();
        yesterday = today.minusDays(1);

        Quiz qXuemin = new Quiz();
        qXuemin.setPhone("15800000000");
        qXuemin.setScore(5);
        qXuemin.setLevel(1);
        qXuemin.setPersonId(1L);
        qXuemin.setCreated(today);
        entityManager.persist(qXuemin);

        Quiz qXueba = new Quiz();
        qXueba.setPhone("15800000000");
        qXueba.setScore(4);
        qXueba.setLevel(2);
        qXueba.setPersonId(1L);
        qXueba.setCreated(today);
        entityManager.persist(qXueba);

        Quiz qXueshen = new Quiz();
        qXueshen.setPhone("15800000000");
        qXueshen.setScore(3);
        qXueshen.setLevel(3);
        qXueshen.setPersonId(1L);
        qXueshen.setCreated(today);
        entityManager.persist(qXueshen);

        Quiz anotherXueba = new Quiz();
        anotherXueba.setPhone("15800000001");
        anotherXueba.setScore(9);
        anotherXueba.setLevel(2);
        anotherXueba.setPersonId(2L);
        anotherXueba.setOrganizationId(1L);
        anotherXueba.setCreated(today);
        entityManager.persist(anotherXueba);

        Quiz qYesterday = new Quiz();
        qYesterday.setPhone("15800000000");
        qYesterday.setScore(8);
        qYesterday.setLevel(1);
        qYesterday.setPersonId(1L);
        qYesterday.setCreated(yesterday);
        entityManager.persist(qYesterday);
    }

    @Test
    public void findByPersonIdAndCreated() throws Exception {
        List<Quiz> actual = repository.findByPersonIdAndCreated(1L, today);
        assertNotNull(actual);
        assertEquals(3, actual.size());
        assertEquals(1, actual.get(0).getLevel());
        assertEquals(2, actual.get(1).getLevel());
        assertEquals(3, actual.get(2).getLevel());
        assertEquals(0, repository.findByPersonIdAndCreated(2L, yesterday).size());
    }

    @Test
    public void findByPersonIdAndLevelAndCreated() throws Exception {
        Quiz actual = repository.findByPersonIdAndLevelAndCreated(1L, 1, today);
        assertNotNull(actual);
        assertEquals(5, actual.getScore());
        assertNull(repository.findByPersonIdAndLevelAndCreated(1L, 2, yesterday));
    }

    @Test
    public void findTop20ByLevelAndCreatedOrderByScoreDesc() throws Exception {
        List<Quiz> actual = repository.findTop20ByLevelAndCreatedOrderByScoreDesc(2, today);
        assertNotNull(actual);
        assertEquals(2, actual.size());
        assertEquals(2L, actual.get(0).getPersonId().longValue());
        assertEquals(9, actual.get(0).getScore());
        assertEquals(1L, actual.get(1).getPersonId().longValue());
        assertEquals(4, actual.get(1).getScore());
    }
}