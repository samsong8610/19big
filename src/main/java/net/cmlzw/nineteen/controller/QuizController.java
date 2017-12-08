package net.cmlzw.nineteen.controller;

import net.cmlzw.nineteen.domain.Organization;
import net.cmlzw.nineteen.domain.Quiz;
import net.cmlzw.nineteen.repository.OrganizationRepository;
import net.cmlzw.nineteen.repository.QuizRepository;
import net.cmlzw.nineteen.repository.UserRepository;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/quizzes")
public class QuizController {
    Logger logger = Logger.getLogger(this.getClass());
    @Autowired
    QuizRepository repository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    OrganizationRepository orgRepository;

    @PostMapping
    public QuizDto create(@RequestBody Quiz quiz, Principal principal) {
        String username = principal.getName();
        Date today = DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH);
        Quiz found = repository.findByUsernameAndLevelAndCreated(username, quiz.getLevel(), today);
        if (found != null) {
            throw new AlreadySubmittedException();
        }
        quiz.setUsername(username);
        quiz.setCreated(today);
        repository.save(quiz);
        if (quiz.getOrganizationId() != null) {
            int retry = 0;
            do {
                Organization org = orgRepository.findOne(quiz.getOrganizationId());
                org.setSubmittedMembers(org.getSubmittedMembers() + 1);
                try {
                    orgRepository.save(org);
                    break;
                } catch (OptimisticLockingFailureException e) {
                    logger.warn(String.format("Optimistic lock organization failed %d times, retry", retry));
                }
            } while(retry++ < 3);
            if (retry >= 3) {
                throw new ConcurrentConflictException("Could not update organization statistics.");
            }
        }

        return valueFrom(quiz);
    }

    @GetMapping("/{username}")
    public List<QuizDto> getSubmmitted(@PathVariable String username) {
        Date today = DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH);
        List<Quiz> found = repository.findByUsernameAndCreated(username, today);
        if (found != null && found.size() > 0) {
            List<QuizDto> result = new ArrayList<>(found.size());
            for (Quiz each : found) {
                result.add(valueFrom(each));
            }
            return result;
        }
        throw new ResourceNotExistedException("quiz");
    }

    @GetMapping("/boards/{level}")
    public List<QuizDto> getBoardsAt(
            @PathVariable Integer level,
            @RequestParam(required = false, defaultValue = "-1") int topn) {
        Date today = DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH);
        List<Quiz> boards = repository.findTop20ByLevelAndCreatedOrderByScoreDesc(level, today);
        if (topn <= 0 || topn > boards.size()) {
            topn = boards.size();
        }
        List<QuizDto> result = new ArrayList<>(topn);
        for (int i = 0; i < topn; i++) {
            result.add(valueFrom(boards.get(i)));
        }
        return result;
    }

    public QuizDto valueFrom(Quiz quiz) {
        QuizDto result = new QuizDto();
        if (quiz == null || quiz.getId() == null) {
            throw new RuntimeException("id is null");
        }
        result.setId(quiz.getId());
        result.setLevel(quiz.getLevel());
        result.setPhone(quiz.getPhone());
        result.setScore(quiz.getScore());
        result.setCreated(new Date(quiz.getCreated().getTime()));
        result.setUser(userRepository.findOne(quiz.getUsername()));
        if (quiz.getOrganizationId() != null) {
            Organization org = orgRepository.findOne(quiz.getOrganizationId());
            if (org != null) {
                result.setOrganization(org.getName());
            }
        }
        return result;
    }
}
