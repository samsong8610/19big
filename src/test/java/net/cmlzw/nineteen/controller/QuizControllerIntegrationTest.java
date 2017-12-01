package net.cmlzw.nineteen.controller;

import net.cmlzw.nineteen.repository.OrganizationRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class QuizControllerIntegrationTest {

    @Autowired
    TestRestTemplate restTemplate;

    @Test
    public void getSubmittedQuiz() throws Exception {
        ResponseEntity<String> resp = restTemplate.getForEntity("/quizzes/1", String.class);
        assertNotNull(resp);
        assertEquals(resp.getStatusCode(), HttpStatus.NOT_FOUND);
    }
}
