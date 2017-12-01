package net.cmlzw.nineteen.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.cmlzw.Application;
import net.cmlzw.nineteen.domain.Question;
import net.cmlzw.nineteen.domain.QuestionLevel;
import net.cmlzw.nineteen.repository.QuestionLevelRepository;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(value = QuestionLevelController.class, secure = false)
public class QuestionLevelControllerTest {
    @Autowired
    MockMvc mockMvc;
    @MockBean
    QuestionLevelRepository repository;
    @Autowired
    ObjectMapper mapper;

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void listEmpty() throws Exception {
        given(repository.findAll()).willReturn(new ArrayList<>());
        mockMvc.perform(get("/questionlevels")).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(content().json("[]"));
    }

    @Test
    public void listSuccess() throws Exception {
        ArrayList<QuestionLevel> levels = new ArrayList<>();
        QuestionLevel l1 = new QuestionLevel("xuemin", 1, true);
        l1.setId(1L);
        levels.add(l1);
        List<Question> questions = new ArrayList<>(1);
        questions.add(new Question("question 1",
                new String[]{"answer 1", "answer 2", "answer 3"}, "answer 1"));
        QuestionLevel l2 = new QuestionLevel("xueba", 2, true);
        l2.setId(2L);
        l2.setQuestions(questions);
        levels.add(l2);

        given(repository.findAll()).willReturn(levels);
        mockMvc.perform(get("/questionlevels")).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].title").value("xuemin"))
                .andExpect(jsonPath("$[0].questions").isArray())
                .andExpect(jsonPath("$[0].questions.length()").value(0))
                .andExpect(jsonPath("$[1].title").value("xueba"))
                .andExpect(jsonPath("$[1].questions").isArray())
                .andExpect(jsonPath("$[1].questions.length()").value(1));
    }

    @Test
    public void listActive() throws Exception {
        ArrayList<QuestionLevel> levels = new ArrayList<>();
        QuestionLevel l1 = new QuestionLevel("xuemin", 1, true);
        l1.setId(1L);
        List<Question> questions = new ArrayList<>(1);
        questions.add(new Question("question 1",
                new String[]{"answer 1", "answer 2", "answer 3"}, "answer 1"));
        l1.setQuestions(questions);
        levels.add(l1);

        given(repository.findByActive(true)).willReturn(levels);
        mockMvc.perform(get("/questionlevels").param("active", "true"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].title").value("xuemin"))
                .andExpect(jsonPath("$[0].questions").isArray())
                .andExpect(jsonPath("$[0].questions.length()").value(1));
    }

    @Test
    public void listByLevel() throws Exception {
        ArrayList<QuestionLevel> levels = new ArrayList<>();
        QuestionLevel l1 = new QuestionLevel("xuemin", 1, true);
        l1.setId(1L);
        List<Question> questions = new ArrayList<>(1);
        questions.add(new Question("question 1",
                new String[]{"answer 1", "answer 2", "answer 3"}, "answer 1"));
        l1.setQuestions(questions);
        levels.add(l1);

        given(repository.findByLevel(1)).willReturn(levels);
        mockMvc.perform(get("/questionlevels").param("level", "1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].title").value("xuemin"))
                .andExpect(jsonPath("$[0].questions").isArray())
                .andExpect(jsonPath("$[0].questions.length()").value(1));
    }

    @Test
    public void listByActiveAndLevel() throws Exception {
        ArrayList<QuestionLevel> levels = new ArrayList<>();
        QuestionLevel l1 = new QuestionLevel("xuemin", 1, true);
        l1.setId(1L);
        List<Question> questions = new ArrayList<>(1);
        questions.add(new Question("question 1",
                new String[]{"answer 1", "answer 2", "answer 3"}, "answer 1"));
        l1.setQuestions(questions);
        levels.add(l1);

        given(repository.findByActiveAndLevel(true, 1)).willReturn(levels);
        mockMvc.perform(get("/questionlevels").param("level", "1")
            .param("active", "true")).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].title").value("xuemin"))
                .andExpect(jsonPath("$[0].questions").isArray())
                .andExpect(jsonPath("$[0].questions.length()").value(1));
    }

    @Test
    public void listWithInvalidLevel() throws Exception {
        mockMvc.perform(get("/questionlevels").param("level", "4"))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void create() throws Exception {
        List<Question> questions = new ArrayList<>(1);
        questions.add(new Question("question 1",
                new String[]{"answer 1", "answer 2", "answer 3"}, "answer 1"));
        QuestionLevel level = new QuestionLevel("xueba", 2, true);
        level.setId(2L);
        level.setQuestions(questions);
        String body = mapper.writeValueAsString(level);
        mockMvc.perform(post("/questionlevels").content(body).contentType(MediaType.APPLICATION_JSON_UTF8))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.title").value("xueba"))
                .andExpect(jsonPath("$.level").value(2))
                .andExpect(jsonPath("$.active").value(true))
                .andExpect(jsonPath("$.created").isNotEmpty())
                .andExpect(jsonPath("$.questions").isArray())
                .andExpect(jsonPath("$.questions.length()").value(1))
                .andExpect(jsonPath("$.questions[0].title").value("question 1"))
                .andExpect(jsonPath("$.questions[0].answers.length()").value(3))
                .andExpect(jsonPath("$.questions[0].answer").value("answer 1"));
    }

    @Test
    public void edit() throws Exception {
        List<Question> oldQuestions = new ArrayList<>(1);
        Question question2 = new Question("question 2",
                new String[]{"answer 1", "answer 2", "answer 3"}, "answer 1");
        question2.setId(2L);
        oldQuestions.add(question2);
        QuestionLevel oldLevel = new QuestionLevel("xueba", 2, false);
        oldLevel.setId(2L);
        oldLevel.setQuestions(oldQuestions);
        given(repository.findOne(2L)).willReturn(oldLevel);

        List<Question> questions = new ArrayList<>(1);
        question2.setAnswer("answer 2");
        questions.add(question2);
        Question question3 = new Question("question 3",
                new String[]{"answer 1", "answer 2", "answer 3"}, "answer 3");
        question3.setId(3L);
        questions.add(question3);
        QuestionLevel level = new QuestionLevel("xueba", 2, false);
        level.setId(2L);
        level.setQuestions(questions);
        String body = mapper.writeValueAsString(level);
        mockMvc.perform(put("/questionlevels/2").content(body).contentType(MediaType.APPLICATION_JSON_UTF8))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.title").value("xueba"))
                .andExpect(jsonPath("$.level").value(2))
                .andExpect(jsonPath("$.active").value(false))
                .andExpect(jsonPath("$.created").isNotEmpty())
                .andExpect(jsonPath("$.questions").isArray())
                .andExpect(jsonPath("$.questions.length()").value(2))
                .andExpect(jsonPath("$.questions[0].title").value("question 2"))
                .andExpect(jsonPath("$.questions[0].answers.length()").value(3))
                .andExpect(jsonPath("$.questions[0].answer").value("answer 2"))
                .andExpect(jsonPath("$.questions[1].title").value("question 3"))
                .andExpect(jsonPath("$.questions[1].answers.length()").value(3))
                .andExpect(jsonPath("$.questions[1].answer").value("answer 3"));
    }

    @Test
    public void editNotExisted() throws Exception {
        List<Question> questions = new ArrayList<>(1);
        questions.add(new Question("question 2",
                new String[]{"answer 1", "answer 2", "answer 3"}, "answer 2"));
        questions.add(new Question("question 3",
                new String[]{"answer 1", "answer 2", "answer 3"}, "answer 3"));
        QuestionLevel level = new QuestionLevel("xueba", 2, false);
        level.setId(2L);
        level.setQuestions(questions);
        String body = mapper.writeValueAsString(level);
        mockMvc.perform(put("/questionlevels/3").content(body).contentType(MediaType.APPLICATION_JSON_UTF8))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    public void deleteSuccess() throws Exception {
        List<Question> oldQuestions = new ArrayList<>(1);
        oldQuestions.add(new Question("question 2",
                new String[]{"answer 1", "answer 2", "answer 3"}, "answer 1"));
        QuestionLevel oldLevel = new QuestionLevel("xueba", 2, false);
        oldLevel.setId(2L);
        oldLevel.setQuestions(oldQuestions);
        given(repository.findOne(2L)).willReturn(oldLevel);
        mockMvc.perform(delete("/questionlevels/2"))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    public void deleteNotExisted() throws Exception {
        mockMvc.perform(delete("/questionlevels/2"))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

}