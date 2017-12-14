package net.cmlzw.nineteen.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.cmlzw.nineteen.domain.Organization;
import net.cmlzw.nineteen.domain.User;
import net.cmlzw.nineteen.domain.Quiz;
import net.cmlzw.nineteen.repository.OrganizationRepository;
import net.cmlzw.nineteen.repository.UserRepository;
import net.cmlzw.nineteen.repository.QuizRepository;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.*;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.atLeast;
import static org.mockito.internal.verification.VerificationModeFactory.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@WebMvcTest(value = QuizController.class, secure = false)
public class QuizControllerTest {
    @MockBean
    QuizRepository repository;
    @MockBean
    UserRepository userRepository;
    @MockBean
    OrganizationRepository orgRepository;
    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper mapper;

    @Test
    public void submitQuizByPartyMember() throws Exception {
        UsernamePasswordAuthenticationToken principal = new UsernamePasswordAuthenticationToken("u1", "p1");
        User user = new User();
        user.setUsername("u1");
        user.setNickname("u1");
        principal.setDetails(user);
        Organization org = new Organization();
        org.setId(1L);
        org.setName("org");
        org.setTotalMembers(100);
        org.setVersion(1);

        Quiz newQuiz = new Quiz();
        newQuiz.setUsername(user.getUsername());
        newQuiz.setOrganizationId(org.getId());
        newQuiz.setLevel(1);
        newQuiz.setScore(8);
        newQuiz.setPhone("15800000000");
        newQuiz.setId(1L);
        String body = mapper.writeValueAsString(newQuiz);

        given(userRepository.findOne(newQuiz.getUsername())).willReturn(user);
        given(orgRepository.findOne(newQuiz.getOrganizationId())).willReturn(org);
        given(repository.countDistinctUsernameByOrganizationId(org.getId())).willReturn(1);
        mockMvc.perform(
                post("/quizzes").contentType(MediaType.APPLICATION_JSON_UTF8).content(body)
                        .principal(principal)
        ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.user.username").value("u1"))
                .andExpect(jsonPath("$.user.nickname").value("u1"))
                .andExpect(jsonPath("$.organization").value("org"))
                .andExpect(jsonPath("$.level").value(1))
                .andExpect(jsonPath("$.score").value(8))
                .andExpect(jsonPath("$.phone").value("15800000000"))
                .andExpect(jsonPath("$.created").exists());
        then(orgRepository).should(times(1)).save(org);
    }

    @Test
    public void submitByPerson() throws Exception {
        UsernamePasswordAuthenticationToken principal = new UsernamePasswordAuthenticationToken("u1", "p1");
        User user = new User();
        user.setUsername("u1");
        user.setNickname("u1");
        principal.setDetails(user);

        Quiz newQuiz = new Quiz();
        newQuiz.setUsername(user.getUsername());
        newQuiz.setLevel(1);
        newQuiz.setScore(8);
        newQuiz.setPhone("15800000000");
        newQuiz.setId(1L);
        String body = mapper.writeValueAsString(newQuiz);

        given(userRepository.findOne(newQuiz.getUsername())).willReturn(user);
        mockMvc.perform(
                post("/quizzes").contentType(MediaType.APPLICATION_JSON_UTF8).content(body)
                        .principal(principal)
        ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.user.username").value("u1"))
                .andExpect(jsonPath("$.user.nickname").value("u1"))
                .andExpect(jsonPath("$.organization").doesNotExist())
                .andExpect(jsonPath("$.level").value(1))
                .andExpect(jsonPath("$.score").value(8))
                .andExpect(jsonPath("$.phone").value("15800000000"))
                .andExpect(jsonPath("$.created").exists());
    }

    @Test
    public void submitQuizMoreThanOnceFailed() throws Exception {
        UsernamePasswordAuthenticationToken principal = new UsernamePasswordAuthenticationToken("u1", "p1");
        User user = new User();
        user.setUsername("u1");
        user.setNickname("u1");
        principal.setDetails(user);
        Organization org = new Organization();
        org.setId(1L);
        org.setName("org");
        org.setTotalMembers(100);

        Quiz newQuiz = new Quiz();
        newQuiz.setUsername(user.getUsername());
        newQuiz.setOrganizationId(org.getId());
        newQuiz.setLevel(1);
        newQuiz.setScore(8);
        newQuiz.setPhone("15800000000");
        newQuiz.setId(1L);
        String body = mapper.writeValueAsString(newQuiz);

        given(userRepository.findOne(newQuiz.getUsername())).willReturn(user);
        given(orgRepository.findOne(newQuiz.getOrganizationId())).willReturn(org);
        Date today = DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH);
        given(repository.findByUsernameAndLevelAndCreated(user.getUsername(), 1, today)).willReturn(newQuiz);
        mockMvc.perform(
                post("/quizzes").contentType(MediaType.APPLICATION_JSON_UTF8).content(body)
                        .principal(principal)
        ).andDo(print())
                .andExpect(status().isBadRequest());

    }

    @Test
    public void submitQuizConflictShouldRetry() throws Exception {
        UsernamePasswordAuthenticationToken principal = new UsernamePasswordAuthenticationToken("u1", "p1");
        User user = new User();
        user.setUsername("u1");
        user.setNickname("u1");
        principal.setDetails(user);
        Organization org = new Organization();
        org.setId(1L);
        org.setName("org");
        org.setTotalMembers(100);

        Quiz newQuiz = new Quiz();
        newQuiz.setUsername(user.getUsername());
        newQuiz.setOrganizationId(org.getId());
        newQuiz.setLevel(1);
        newQuiz.setScore(8);
        newQuiz.setPhone("15800000000");
        newQuiz.setId(1L);
        String body = mapper.writeValueAsString(newQuiz);

        given(userRepository.findOne(newQuiz.getUsername())).willReturn(user);
        given(orgRepository.findOne(newQuiz.getOrganizationId())).willReturn(org);
        given(repository.countDistinctUsernameByOrganizationId(org.getId())).willReturn(1);
        given(orgRepository.save(org)).willThrow(ObjectOptimisticLockingFailureException.class);
        mockMvc.perform(
                post("/quizzes").contentType(MediaType.APPLICATION_JSON_UTF8).content(body)
                        .principal(principal)
        ).andDo(print())
                .andExpect(status().isServiceUnavailable());

        then(orgRepository).should(atLeast(3)).save(org);
    }

    @Test
    public void getSubmitted() throws Exception {
        UsernamePasswordAuthenticationToken principal = new UsernamePasswordAuthenticationToken("u1", "p1");
        User user = new User();
        user.setUsername("u1");
        user.setNickname("u1");
        principal.setDetails(user);
        Organization org = new Organization();
        org.setId(1L);
        org.setName("org");
        org.setTotalMembers(100);

        Date today = DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH);
        Quiz newQuiz = new Quiz();
        newQuiz.setUsername(user.getUsername());
        newQuiz.setOrganizationId(org.getId());
        newQuiz.setLevel(1);
        newQuiz.setScore(8);
        newQuiz.setPhone("15800000000");
        newQuiz.setCreated(today);
        newQuiz.setId(1L);

        given(userRepository.findOne(newQuiz.getUsername())).willReturn(user);
        given(orgRepository.findOne(newQuiz.getOrganizationId())).willReturn(org);
        given(repository.findByUsernameAndCreated(user.getUsername(), today)).willReturn(Arrays.asList(newQuiz));
        mockMvc.perform(get("/quizzes/u1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].user.username").value("u1"))
                .andExpect(jsonPath("$[0].user.nickname").value("u1"))
                .andExpect(jsonPath("$[0].organization").value("org"))
                .andExpect(jsonPath("$[0].level").value(1))
                .andExpect(jsonPath("$[0].score").value(8))
                .andExpect(jsonPath("$[0].phone").value("15800000000"))
                .andExpect(jsonPath("$[0].created").exists());
    }

    @Test
    public void getBoardsAtEmpty() throws Exception {
        UsernamePasswordAuthenticationToken principal = new UsernamePasswordAuthenticationToken("u1", "p1");
        User user = new User();
        user.setUsername("u1");
        user.setNickname("u1");
        principal.setDetails(user);
        Organization org = new Organization();
        org.setId(1L);
        org.setName("org");
        org.setTotalMembers(100);

        Date today = DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH);
        Quiz newQuiz = new Quiz();
        newQuiz.setUsername(user.getUsername());
        newQuiz.setOrganizationId(org.getId());
        newQuiz.setLevel(1);
        newQuiz.setScore(8);
        newQuiz.setPhone("15800000000");
        newQuiz.setCreated(today);
        newQuiz.setId(1L);
        mockMvc.perform(get("/quizzes/boards/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    public void getBoardsTop5() throws Exception {
        Organization org = new Organization();
        org.setId(1L);
        org.setName("org");
        org.setTotalMembers(100);
        Date today = DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH);
        List<Quiz> boards = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            User user = new User();
            user.setUsername("u"+i);
            user.setNickname("u"+i);
            given(userRepository.findOne(user.getUsername())).willReturn(user);

            Quiz newQuiz = new Quiz();
            newQuiz.setUsername(user.getUsername());
            newQuiz.setOrganizationId(org.getId());
            newQuiz.setLevel(1);
            newQuiz.setScore(i+1);
            newQuiz.setPhone("15800000000");
            newQuiz.setCreated(today);
            newQuiz.setId(new Long(i));

            boards.add(newQuiz);
        }

        Quiz tmp;
        for (int i = 0; i < 5; i++) {
            tmp = boards.get(i);
            boards.set(i, boards.get(9 - i));
            boards.set(9 - i, tmp);
        }

        given(orgRepository.findOne(org.getId())).willReturn(org);
        given(repository.findTop20ByLevelAndCreatedOrderByScoreDesc(1, today)).willReturn(boards);
        mockMvc.perform(get("/quizzes/boards/1").param("topn", "5"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(5))
                .andExpect(jsonPath("$[0].id").value(9))
                .andExpect(jsonPath("$[0].user.username").value("u9"))
                .andExpect(jsonPath("$[0].organization").value("org"))
                .andExpect(jsonPath("$[4].id").value(5))
                .andExpect(jsonPath("$[4].user.username").value("u5"))
                .andExpect(jsonPath("$[4].organization").value("org"));
    }

}