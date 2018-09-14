package net.cmlzw.nineteen.controller;

import net.cmlzw.nineteen.domain.*;
import net.cmlzw.nineteen.repository.*;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/awards")
public class AwardController {
    private Logger logger = Logger.getLogger(this.getClass());
    public static final String JOB_NAME = "cal_awards";

    @Autowired
    AwardRepository repository;
    @Autowired
    JobLockRepository jobLockRepository;
    @Autowired
    QuizRepository quizRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    QuizArchiveRepository archiveRepository;
    @Autowired
    AuditLogRepository auditRepository;

    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public Slice<Award> present(Principal principal) {
        // note: present awards to the top 20 quizzes of each level
        List<Award> awards = new ArrayList<>(60);
        for (int level = 0; level < 3; level++) {
            List<Quiz> quizzes = quizRepository.findTop20ByLevelOrderByScoreDescCreatedDesc(level + 1);
            if (quizzes.size() > 0) {
                Quiz last = quizzes.get(quizzes.size() - 1);
                quizzes = quizRepository.findByLevelAndScoreGreaterThanEqual(level + 1, last.getScore());
                Set<String> usernames = new HashSet<>();
                List<Quiz> toAwards = new ArrayList<>(40);
                for (Quiz each : quizzes) {
                    if (!usernames.contains(each.getUsername())) {
                        usernames.add(each.getUsername());
                        toAwards.add(each);
                    }
                }
                logger.info(String.format("Found %d quizzes on level %d with score above or equal %d",
                        toAwards.size(), level + 1, last.getScore()));

                for (Quiz quiz : toAwards) {
                    Award award = new Award();
                    award.setUsername(quiz.getUsername());
                    award.setNickname(getNickname(quiz));
                    award.setGift(level + 1);
                    award.setScore(quiz.getScore());
                    award.setPhone(quiz.getPhone());
                    award.setCreated(new Date());
                    award.setNotified(false);
                    award.setClaimed(false);
                    awards.add(award);
                }
            }
        }
        repository.save(awards);
        logger.info(String.format("Present %d awards totally", awards.size()));
        List<Quiz> all = quizRepository.findAll();
        List<QuizArchive> archives = all.stream().map(QuizArchive::new).collect(Collectors.toList());
        archiveRepository.save(archives);
        // clear all the quiz
        quizRepository.deleteAllInBatch();
        auditRepository.save(new AuditLog(principal.getName(), "present"));
        PageRequest pr = new PageRequest(0,
                20, Sort.Direction.DESC, "created");
        return list(pr);
    }

    @GetMapping
    public Slice<Award> list(@PageableDefault(20) Pageable pageable) {
        PageRequest pr = new PageRequest(pageable.getPageNumber(),
            pageable.getPageSize(), Sort.Direction.DESC, "created");
        return repository.findAll(pr);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/queries/phone/{phone}")
    public List<Award> findByPhone(@PathVariable String phone) {
        List<Award> awards = repository.findByPhone(phone);
        if (awards == null || awards.size() == 0) {
            throw new ResourceNotExistedException("award");
        }
        return awards;
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/{id}")
    public Award claim(@PathVariable Long id) {
        Award award = repository.findOne(id);
        if (award == null) {
            throw new ResourceNotExistedException("award");
        }
        if (award.isClaimed()) {
            throw new AlreadyClaimedException();
        }
        try {
            award.setClaimed(true);
            repository.save(award);
        } catch (OptimisticLockingFailureException e) {
            logger.warn("The award has been claimed.");
            throw new AlreadyClaimedException();
        }
        return award;
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("/{id}")
    public Award edit(@PathVariable Long id, @RequestBody Award updated) {
        Award award = repository.findOne(id);
        if (award == null) {
            throw new ResourceNotExistedException("award");
        }
        award.setNotified(updated.isNotified());
        repository.save(award);
        return award;
    }

    // Everyday at midnight
    // note: not automatically calculate awards
//    @Scheduled(cron = "0 0 0 * * *")
    public void calculateAwards() {
        int retry = 0;
        JobLock found = null;
        boolean locked = false;
        do {
            try {
                // get lock
                found = jobLockRepository.findOne(JOB_NAME);
                if (found == null) {
                    JobLock jobLock = new JobLock(JOB_NAME, new Date());
                    jobLockRepository.save(jobLock);
                    locked = true;

                    doCalculateAwards();
                }
                break;
            } catch (DuplicateKeyException dke) {
                logger.error("Lock awards calculation job failed", dke);
            } catch (Exception e) {
                logger.error("Calculate awards failed", e);
            } finally {
                if (locked) {
                    try {
                        jobLockRepository.delete(JOB_NAME);
                    } catch (Exception e) {
                        logger.warn("Unlock awards calculation job failed", e);
                    }
                }
            }
        } while(retry++ < 3);
        if (retry >= 3) {
            logger.error("Awards calculation failed after 3 times retry.");
        }
    }

    public void doCalculateAwards() {
        Date today = DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH);
        Date yesterday = DateUtils.addDays(today, -1);
        List<Award> awards = new ArrayList<>(15);
        for (int level = 1; level <= 3; level++) {
            List<Quiz> quizzes = quizRepository.findTop20ByLevelAndCreatedOrderByScoreDesc(level, yesterday);
            int count = 5;
            if (quizzes.size() < 5) {
                count = quizzes.size();
            }
            for (int i = 0; i < count; i++) {
                Award award = new Award();
                award.setNickname(getNickname(quizzes.get(i)));
                award.setGift(level);
                award.setPhone(quizzes.get(i).getPhone());
                award.setCreated(new Date());
                award.setNotified(false);
                award.setClaimed(false);
                awards.add(award);
            }
        }
        repository.save(awards);
    }

    private String getNickname(Quiz quiz) {
        String username = quiz.getUsername();
        try {
            User user = userRepository.findOne(username);
            if (user == null || user.getUsername() == null) {
                logger.warn("Could not find user with username " + username + ", try to find user by a quiz.");
                if (quiz.getPhone() != null) {
                    List<Quiz> byPhone = quizRepository.findByPhone(quiz.getPhone());
                    if (byPhone.size() > 0) {
                        username = byPhone.get(0).getUsername();
                        user = userRepository.findOne(username);
                    }
                }
            }
            if (user != null && user.getNickname() != null) {
                return user.getNickname();
            }
        } catch (Exception e) {
            logger.warn("Get nickname of username " + username + " failed", e);
        }
        return "";
    }
}
