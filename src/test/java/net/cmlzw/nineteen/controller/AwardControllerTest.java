package net.cmlzw.nineteen.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.cmlzw.nineteen.domain.*;
import net.cmlzw.nineteen.repository.*;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;
import java.util.stream.Collectors;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.never;
import static org.mockito.internal.verification.VerificationModeFactory.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@WebMvcTest(value = AwardController.class, secure = false)
@EnableSpringDataWebSupport
public class AwardControllerTest {
    @Autowired
    MockMvc mockMvc;
    @MockBean
    AwardRepository repository;
    @MockBean
    UserRepository userRepository;
    @MockBean
    OrganizationRepository orgRepository;
    @MockBean
    QuizRepository quizRepository;
    @MockBean
    JobLockRepository jobLockRepository;
    @MockBean
    QuizArchiveRepository archiveRepository;
    @MockBean
    AuditLogRepository auditRepository;
    @Autowired
    AwardController controller;
    @Autowired
    ObjectMapper mapper;

    @Test
    public void listEmpty() throws Exception {
        given(repository.findAll(any(Pageable.class))).willReturn(new PageImpl<>(new ArrayList<>()));
        mockMvc.perform(get("/awards").param("page", "0").param("size", "20"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(0))
                .andExpect(jsonPath("$.first").value(true))
                .andExpect(jsonPath("$.last").value(true))
                .andExpect(jsonPath("$.number").value(0));
    }

    @Test
    public void list() throws Exception {
        Award award = new Award();
        award.setId(1L);
        award.setNickname("nickname");
        award.setPhone("15800000000");
        award.setGift(1);
        award.setCreated(new Date());

        given(repository.findAll(any(PageRequest.class))).willReturn(new PageImpl<>(Arrays.asList(award)));
        mockMvc.perform(get("/awards"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].nickname").value("nickname"))
                .andExpect(jsonPath("$.content[0].phone").value("15800000000"))
                .andExpect(jsonPath("$.content[0].gift").value(1))
                .andExpect(jsonPath("$.content[0].created").exists())
                .andExpect(jsonPath("$.content[0].notified").value(false))
                .andExpect(jsonPath("$.first").value(true))
                .andExpect(jsonPath("$.last").value(true))
                .andExpect(jsonPath("$.number").value(0));
    }

    @Test
    public void findByPhone() throws Exception {
        Award award = new Award();
        award.setId(1L);
        award.setNickname("nickname");
        award.setPhone("15800000000");
        award.setGift(1);
        award.setCreated(new Date());

        given(repository.findByPhone("15800000000")).willReturn(Arrays.asList(award));
        mockMvc.perform(get("/awards/queries/phone/15800000000"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void findByPhoneNotExisted() throws Exception {
        mockMvc.perform(get("/awards/queries/phone/15811111111"))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    public void claim() throws Exception {
        Award award = new Award();
        award.setId(1L);
        award.setNickname("nickname");
        award.setPhone("15800000000");
        award.setGift(1);
        award.setCreated(new Date());
        Award saved = new Award();
        saved.setId(award.getId());
        saved.setNickname(award.getNickname());
        saved.setPhone(award.getPhone());
        saved.setGift(award.getGift());
        saved.setCreated(award.getCreated());
        saved.setClaimed(true);

        given(repository.findOne(award.getId())).willReturn(award);
        given(repository.save(any(Award.class))).willReturn(saved);
        mockMvc.perform(post("/awards/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nickname").value("nickname"))
                .andExpect(jsonPath(("$.phone")).value("15800000000"))
                .andExpect(jsonPath("$.gift").value(1))
                .andExpect(jsonPath("$.created").exists())
                .andExpect(jsonPath("$.notified").exists())
                .andExpect(jsonPath("$.claimed").exists());
    }

    @Test
    public void claimConflict() throws Exception {
        Award award = new Award();
        award.setId(1L);
        award.setNickname("nickname");
        award.setPhone("15800000000");
        award.setGift(1);
        award.setCreated(new Date());
        award.setClaimed(true);

        given(repository.findOne(award.getId())).willReturn(award);
        mockMvc.perform(post("/awards/1"))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void claimConcurrentConflict() throws Exception {
        Award award = new Award();
        award.setId(1L);
        award.setNickname("nickname");
        award.setPhone("15800000000");
        award.setGift(1);
        award.setCreated(new Date());

        given(repository.findOne(award.getId())).willReturn(award);
        given(repository.save(award)).willThrow(OptimisticLockingFailureException.class);
        mockMvc.perform(post("/awards/1"))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void notifyAward() throws Exception {
        Award award = new Award();
        award.setId(1L);
        award.setNickname("nickname");
        award.setPhone("15800000000");
        award.setGift(1);
        award.setCreated(new Date());
        award.setNotified(true);

        given(repository.findOne(award.getId())).willReturn(award);
        given(repository.save(any(Award.class))).willReturn(award);
        mockMvc.perform(put("/awards/1").content(mapper.writeValueAsBytes(award))
                        .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nickname").value("nickname"))
                .andExpect(jsonPath(("$.phone")).value("15800000000"))
                .andExpect(jsonPath("$.gift").value(1))
                .andExpect(jsonPath("$.created").exists())
                .andExpect(jsonPath("$.notified").value(true));
    }

    @Test
    public void calculateAwardsSuccess() throws Exception {
        Organization org = new Organization();
        org.setId(1L);
        org.setName("org");
        org.setTotalMembers(100);
        Date today = DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH);
        Date yesterday = DateUtils.addDays(today, -1);
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
            newQuiz.setCreated(yesterday);
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
        given(quizRepository.findTop20ByLevelAndCreatedOrderByScoreDesc(1, yesterday)).willReturn(boards);
        given(quizRepository.findTop20ByLevelAndCreatedOrderByScoreDesc(2, yesterday)).willReturn(new ArrayList<>());
        given(quizRepository.findTop20ByLevelAndCreatedOrderByScoreDesc(3, yesterday)).willReturn(new ArrayList<>());

        controller.calculateAwards();
        then(jobLockRepository).should(times(1)).save(any(JobLock.class));
        then(repository).should(times(1)).save(argThat(new ListSizeMatcher<Iterable<Award>>(5)));
        then(jobLockRepository).should(times(1)).delete(AwardController.JOB_NAME);
    }

    @Test
    public void calculateAwardsLockFailed() throws Exception {
        JobLock lock = new JobLock(AwardController.JOB_NAME, new Date());
        given(jobLockRepository.findOne(AwardController.JOB_NAME)).willReturn(lock);
        controller.calculateAwards();
        then(repository).should(never()).save(any(Iterable.class));
        then(jobLockRepository).should(never()).delete(AwardController.JOB_NAME);
    }

    @Test
    public void presentGift() throws Exception {
        Organization org = new Organization();
        org.setId(1L);
        org.setName("org");
        org.setTotalMembers(100);
        Date today = DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH);
        List<Quiz> boards = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
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
        for (int i = 0; i < 15; i++) {
            tmp = boards.get(i);
            boards.set(i, boards.get(29 - i));
            boards.set(29 - i, tmp);
        }

        // set quizzes between 21-25 to the same score as 20
        Quiz last = boards.get(19);
        List<Quiz> sameScoreQuizzes = new ArrayList<>();
        sameScoreQuizzes.add(last);
        for (int i = 20; i < 25; i++) {
            Quiz quiz = boards.get(i);
            quiz.setScore(last.getScore());
            sameScoreQuizzes.add(quiz);
        }

        List<Award> awards = boards.stream().filter(q -> q.getScore() >= last.getScore())
                .map(q -> {
                    Award award = new Award();
                    award.setNickname(q.getUsername()); // just use username id
                    award.setGift(q.getLevel());
                    award.setPhone(q.getPhone());
                    award.setCreated(new Date());
                    award.setNotified(false);
                    award.setClaimed(false);
                    return award;
                })
                .collect(Collectors.toList());

        given(orgRepository.findOne(org.getId())).willReturn(org);
        given(quizRepository.findTop20ByLevelOrderByScoreDescCreatedDesc(1)).willReturn(boards.subList(0, 20));
        given(quizRepository.findByLevelAndScore(last.getLevel(), last.getScore())).willReturn(sameScoreQuizzes);
        given(repository.findAll(any(PageRequest.class))).willReturn(new PageImpl<>(awards));
        mockMvc.perform(post("/awards").principal(new UsernamePasswordAuthenticationToken("u1", "p")))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.first").value(true))
                .andExpect(jsonPath("$.last").value(true))
                .andExpect(jsonPath("$.number").value(0));
        then(archiveRepository).should().save(anyListOf(QuizArchive.class));
        then(quizRepository).should().deleteAllInBatch();
        then(auditRepository).should().save(any(AuditLog.class));
    }

    private class ListSizeMatcher<I> extends ArgumentMatcher<I> {
        private int desired = 0;

        public ListSizeMatcher(int desired) {
            this.desired = desired;
        }

        @Override
        public boolean matches(Object argument) {
            if (argument instanceof Collection) {
                if (((Collection)argument).size() == desired) {
                    System.out.println("Collection.size() matched.");
                    return true;
                }
            } else if (argument instanceof Iterable) {
                int count = 0;
                for (Object each : (Iterable) argument) {
                    count++;
                    if (count > desired) {
                        return false;
                    }
                }
                if (count == desired) {
                    System.out.println("Iterable size matched.");
                    return true;
                }
            }
            return false;
        }
    }
}