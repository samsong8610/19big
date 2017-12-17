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
import java.util.stream.Collectors;

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
        // note: allow submit many times but only record the best score
//        Quiz found = repository.findByUsernameAndLevelAndCreated(username, quiz.getLevel(), today);
//        if (found != null) {
//            logger.info(String.format("Username %s has submitted today", username));
//            throw new AlreadySubmittedException();
//        }
        Quiz found = repository.findByUsernameAndLevel(username, quiz.getLevel());
        if (found != null) {
            logger.info(String.format("Username %s has a quiz at level %d with score %d",
                    username, quiz.getLevel(), quiz.getScore()));
            if (quiz.getScore() > found.getScore()) {
                found.setScore(quiz.getScore());
                repository.save(found);
            }
            return valueFrom(found);
        }

        quiz.setUsername(username);
        quiz.setCreated(today);
        repository.save(quiz);
        if (quiz.getOrganizationId() != null && quiz.getOrganizationId() != 0) {
            int retry = 0;
            do {
                Organization org = orgRepository.findOne(quiz.getOrganizationId());
                if (org == null) {
                    logger.info(String.format("Could not find org with id %d", quiz.getOrganizationId()));
                    break;
                }
                int count = repository.countDistinctUsernameByOrganizationId(quiz.getOrganizationId());
                org.setSubmittedMembers(count);
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

    @PutMapping("/{id}")
    public QuizDto update(@PathVariable Long id, @RequestBody Quiz quiz, Principal principal) {
        String username = principal.getName();
        Quiz found = repository.findOne(id);
        if (found == null) {
            throw new ResourceNotExistedException("quiz");
        }

        if (quiz.getScore() > found.getScore()) {
            found.setScore(quiz.getScore());
            repository.save(found);
        }
        return valueFrom(found);
    }

    @GetMapping("/{username}")
    public List<QuizDto> getSubmmitted(@PathVariable String username) {
        // Note: Allow submit many times
        throw new ResourceNotExistedException("quiz");
//        Date today = DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH);
//        List<Quiz> found = repository.findByUsernameAndCreated(username, today);
//        if (found != null && found.size() > 0) {
//            List<QuizDto> result = new ArrayList<>(found.size());
//            for (Quiz each : found) {
//                result.add(valueFrom(each));
//            }
//            return result;
//        }
//        throw new ResourceNotExistedException("quiz");
    }

    @GetMapping("/{username}/{level}")
    public QuizDto getSubmittedAtLevel(@PathVariable String username, @PathVariable int level) {
        if (level < 1 || level > 3) {
            throw new InvalidQuestionLevelException();
        }

        Date today = DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH);
        // note: return the best quiz at the level
//        Quiz found = repository.findByUsernameAndLevelAndCreated(username, level, today);
        Quiz found = repository.findFirstByUsernameAndLevelOrderByScoreDesc(username, level);
        if (found == null) {
            throw new ResourceNotExistedException("quiz");
        }
        return valueFrom(found);
    }

    @GetMapping("/boards")
    public List<List<QuizDto>> getBoards() {
        Date today = DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH);
        List<List<QuizDto>> result = new ArrayList<>(3);
        for (int i = 0; i < 3; i++) {
            // Note: historical board for each level
//            List<Quiz> quizzes = repository.findTop20ByLevelAndCreatedOrderByScoreDesc(i + 1, today);
            List<Quiz> quizzes = repository.findTop20ByLevelOrderByScoreDesc(i + 1);
            List<QuizDto> dtos = quizzes.stream().map(quiz -> valueFrom(quiz)).collect(Collectors.toList());
            result.add(i, dtos);
        }
        return result;
    }

    @GetMapping("/boards/{level}")
    public List<QuizDto> getBoardsAt(
            @PathVariable Integer level,
            @RequestParam(required = false, defaultValue = "-1") int topn) {
        Date today = DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH);
        // Note: historical board for each level
//        List<Quiz> boards = repository.findTop20ByLevelAndCreatedOrderByScoreDesc(level, today);
        List<Quiz> boards = repository.findTop20ByLevelOrderByScoreDesc(level);
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
                result.setOrganizationId(org.getId());
            }
        }
        return result;
    }
}
