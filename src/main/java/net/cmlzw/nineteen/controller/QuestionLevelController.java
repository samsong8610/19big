package net.cmlzw.nineteen.controller;

import net.cmlzw.nineteen.domain.Question;
import net.cmlzw.nineteen.domain.QuestionLevel;
import net.cmlzw.nineteen.repository.QuestionLevelRepository;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/questionlevels")
public class QuestionLevelController {
    private static final Logger logger = Logger.getLogger(QuestionLevelController.class);

    @Autowired
    QuestionLevelRepository repository;

    @GetMapping
    public Collection<QuestionLevel> list(
            @RequestParam(required = false) boolean active,
            @RequestParam(required = false, defaultValue = "-1") int level) {
        if ((level < 0 && level != -1) || level > 3) {
            throw new InvalidQuestionLevelException();
        }
        if (active) {
            if (level >= 1 && level <= 3) {
                return repository.findByActiveAndLevel(active, level);
            } else {
                return repository.findByActive(active);
            }
        } else {
            if (level >= 1 && level <= 3) {
                return repository.findByLevel(level);
            } else {
                List<QuestionLevel> all = repository.findAll();
                if (all.size() > 0) {
                    System.out.println("questions: " + all.get(0).getQuestions().size());
                }
                return all;
            }
        }
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping
    public QuestionLevel create(@RequestBody QuestionLevel level) {
//        for (Question q : level.getQuestions()) {
//            q.setQuestionLevel(level);
//        }
        repository.save(level);
        return level;
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("/{id}")
    public QuestionLevel edit(@PathVariable Long id, @RequestBody QuestionLevel level) {
        QuestionLevel existed = repository.findOne(id);
        if (existed == null) {
            throw new ResourceNotExistedException("QuestionLevel");
        }
        existed.setTitle(level.getTitle());
        existed.setLevel(level.getLevel());
        existed.setActive(level.isActive());

        HashMap<Long, Question> map = new HashMap<>();
        existed.getQuestions().forEach(each -> map.put(each.getId(), each));
        existed.clearQuestions();
        int updated = 0, added = 0, deleted = 0;
        for (Question q : level.getQuestions()) {
            if (map.containsKey(q.getId())) {
                // update question
                Question cur = map.get(q.getId());
                cur.setTitle(q.getTitle());
                cur.setAnswer(q.getAnswer());
                cur.setAnswers(q.getAnswers());
                existed.addQuestion(cur);
                updated++;
            } else {
                // new question
                existed.addQuestion(q);
                added++;
            }
        }
        deleted = map.size() - updated;
        logger.info(String.format("Edit question level %s, added: %d, updated: %d, deleted: %d",
                existed.getTitle(), added, updated, deleted));

        repository.save(existed);
        return existed;
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        repository.delete(id);
    }
}
