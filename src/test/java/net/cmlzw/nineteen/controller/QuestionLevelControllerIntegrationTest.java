package net.cmlzw.nineteen.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.cmlzw.nineteen.domain.Question;
import net.cmlzw.nineteen.domain.QuestionLevel;
import net.cmlzw.nineteen.domain.User;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import static org.junit.Assert.*;

@Ignore("How to switch off security in integration test")
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class QuestionLevelControllerIntegrationTest {
    @Autowired
    ObjectMapper mapper;
    @Autowired
    TestRestTemplate restTemplate;

    @Before
    public void authenticate() {
        User user = new User("u1", "password", true);
        user.setNickname("u1");
        user.addAuthority("USER");
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(user, user.getPassword());
        SecurityContextHolder.getContext().setAuthentication(token);
        System.out.println("===SecurityContext setAuthentication");
    }

    @Test
    public void create() throws Exception {
        List<Question> questions = new ArrayList<>(1);
        Question q = new Question("question 1",
                new String[]{"answer 1", "answer 2", "answer 3"}, "answer 1");
        questions.add(q);
        QuestionLevel level = new QuestionLevel("xueba", 2, true);
        level.setQuestions(questions);
        String body = mapper.writeValueAsString(level);

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_UTF8_VALUE);
        HttpEntity<String> entity = new HttpEntity<>(body, headers);

        ResponseEntity<String> responseEntity;
        responseEntity = restTemplate.exchange("/questionlevels", HttpMethod.POST, entity, String.class);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        QuestionLevel respLevel = mapper.readValue(responseEntity.getBody(), QuestionLevel.class);
        assertNotNull(respLevel);
        assertNotNull(respLevel.getId());
        assertEquals(level.getTitle(), respLevel.getTitle());
        assertEquals(level.getLevel(), respLevel.getLevel());
        assertNotNull(respLevel.getCreated());
        assertEquals(1, respLevel.getQuestions().size());
        Question respQuestion = respLevel.getQuestions().get(0);
        assertEquals(q.getTitle(), respQuestion.getTitle());
        assertEquals(q.getAnswer(), respQuestion.getAnswer());
        assertArrayEquals(q.getAnswersList().toArray(), respQuestion.getAnswersList().toArray());

        ResponseEntity<String> getEntity;
        getEntity = restTemplate.getForEntity("/questionlevels/", String.class);
        assertEquals(HttpStatus.OK, getEntity.getStatusCode());
        List<LinkedHashMap> getLevels = mapper.readValue(getEntity.getBody(), ArrayList.class);
        assertEquals(1, getLevels.size());
        List getQuestions = (List) getLevels.get(0).get("questions");
        assertEquals(1, getQuestions.size());
        LinkedHashMap getQ = (LinkedHashMap) getQuestions.get(0);
        assertEquals(q.getTitle(), getQ.get("title").toString());
    }

    @Test
    public void addQuestionToLevel() throws Exception {
        List<Question> questions = new ArrayList<>(1);
        Question q = new Question("question 1",
                new String[]{"answer 1", "answer 2", "answer 3"}, "answer 1");
        questions.add(q);
        QuestionLevel level = new QuestionLevel("xueba", 2, true);
        level.setQuestions(questions);
        String body = mapper.writeValueAsString(level);

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_UTF8_VALUE);
        HttpEntity<String> entity = new HttpEntity<>(body, headers);

        ResponseEntity<String> responseEntity;
        responseEntity = restTemplate.exchange("/questionlevels", HttpMethod.POST, entity, String.class);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        QuestionLevel respLevel = mapper.readValue(responseEntity.getBody(), QuestionLevel.class);

        // Add new Question into level
        Question newQ = new Question("q2", new String[]{"o1", "o2", "o3"}, "o1");
        respLevel.getQuestions().add(newQ);

        HttpEntity<String> addEntity = new HttpEntity<>(mapper.writeValueAsString(respLevel), headers);
        ResponseEntity<String> addResponse;
        addResponse = restTemplate.exchange("/questionlevels/" + respLevel.getId(), HttpMethod.PUT, addEntity, String.class);
        assertEquals(HttpStatus.OK, addResponse.getStatusCode());
        QuestionLevel addLevel = mapper.readValue(addResponse.getBody(), QuestionLevel.class);
        List<Question> addQuestions = addLevel.getQuestions();
        assertEquals(2, addQuestions.size());
        assertEquals(q.getTitle(), addQuestions.get(0).getTitle());
        assertEquals(newQ.getTitle(), addQuestions.get(1).getTitle());
    }

    @Test
    public void deleteQuestionFromLevel() throws Exception {
        List<Question> questions = new ArrayList<>(1);
        Question q = new Question("question 1",
                new String[]{"answer 1", "answer 2", "answer 3"}, "answer 1");
        questions.add(q);
        QuestionLevel level = new QuestionLevel("xueba", 2, true);
        level.setQuestions(questions);
        String body = mapper.writeValueAsString(level);

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_UTF8_VALUE);
        HttpEntity<String> entity = new HttpEntity<>(body, headers);

        ResponseEntity<String> responseEntity;
        responseEntity = restTemplate.exchange("/questionlevels", HttpMethod.POST, entity, String.class);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        QuestionLevel respLevel = mapper.readValue(responseEntity.getBody(), QuestionLevel.class);

        // Delete existed questions
        respLevel.getQuestions().clear();
        // Add new Question into level
        Question newQ = new Question("q2", new String[]{"o1", "o2", "o3"}, "o1");
        respLevel.getQuestions().add(newQ);

        HttpEntity<String> addEntity = new HttpEntity<>(mapper.writeValueAsString(respLevel), headers);
        ResponseEntity<String> addResponse;
        addResponse = restTemplate.exchange("/questionlevels/" + respLevel.getId(), HttpMethod.PUT, addEntity, String.class);
        assertEquals(HttpStatus.OK, addResponse.getStatusCode());
        QuestionLevel addLevel = mapper.readValue(addResponse.getBody(), QuestionLevel.class);
        List<Question> addQuestions = addLevel.getQuestions();
        assertEquals(1, addQuestions.size());
        assertEquals(newQ.getTitle(), addQuestions.get(0).getTitle());
    }
}