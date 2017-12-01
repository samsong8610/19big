package net.cmlzw.nineteen.repository;

import net.cmlzw.nineteen.domain.Question;
import net.cmlzw.nineteen.domain.QuestionLevel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.*;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@DataJpaTest
public class QuestionLevelRepositoryTest {
    @Autowired
    TestEntityManager entityManager;
    @Autowired
    QuestionLevelRepository repository;

    @Before
    public void setUp() throws Exception {
        QuestionLevel xuemin = new QuestionLevel("xuemin", 1, true);
        xuemin.setCreated(new Date());
        xuemin.setQuestions(Arrays.asList(new Question("q1", new String[]{"o1", "o2", "o3"}, "o1")));
        entityManager.persist(xuemin);
        QuestionLevel inactiveXuemin = new QuestionLevel("inactivexuemin", 1, false);
        inactiveXuemin.setCreated(new Date());
        inactiveXuemin.setQuestions(Arrays.asList(new Question("q2", new String[]{"o1", "o2", "o3"}, "o1")));
        entityManager.persist(inactiveXuemin);

        QuestionLevel xueba = new QuestionLevel("xueba", 2, true);
        xueba.setCreated(new Date());
        xueba.setQuestions(Arrays.asList(new Question("q3", new String[]{"o1", "o2", "o3"}, "o1")));
        entityManager.persist(xueba);

        QuestionLevel xueshen = new QuestionLevel("xueshen", 3, true);
        xueshen.setCreated(new Date());
        xueshen.setQuestions(Arrays.asList(new Question("q4", new String[]{"o1", "o2", "o3"}, "o1")));
        entityManager.persist(xueshen);
    }

    @Test
    public void queryLevel() throws Exception {
        List<Question> questions = new ArrayList<>(1);
        Question q = new Question("question 1",
                new String[]{"answer 1", "answer 2", "answer 3"}, "answer 1");
        questions.add(q);
        QuestionLevel level = new QuestionLevel("xueba", 2, true);
        level.setQuestions(questions);
        entityManager.persist(level);

        QuestionLevel actual = entityManager.find(QuestionLevel.class, level.getId());
        assertNotNull(actual);
        assertEquals(1, actual.getQuestions().size());
    }

    @Test
    public void findByActiveAndLevel() throws Exception {
        Collection<QuestionLevel> actual = repository.findByActiveAndLevel(true, 1);
        assertNotNull(actual);
        assertEquals(1, actual.size());
        assertEquals("xuemin", ((QuestionLevel)actual.toArray()[0]).getTitle());
    }

    @Test
    public void findByActive() throws Exception {
        Collection<QuestionLevel> actual = repository.findByActive(true);
        assertNotNull(actual);
        assertEquals(3, actual.size());
        assertEquals("xuemin", ((QuestionLevel)actual.toArray()[0]).getTitle());
        assertEquals("xueba", ((QuestionLevel)actual.toArray()[1]).getTitle());
        assertEquals("xueshen", ((QuestionLevel)actual.toArray()[2]).getTitle());
    }

    @Test
    public void findByLevel() throws Exception {
        Collection<QuestionLevel> actual = repository.findByLevel(1);
        assertNotNull(actual);
        assertEquals(2, actual.size());
        assertEquals("xuemin", ((QuestionLevel)actual.toArray()[0]).getTitle());
        assertEquals("inactivexuemin", ((QuestionLevel)actual.toArray()[1]).getTitle());
    }

}