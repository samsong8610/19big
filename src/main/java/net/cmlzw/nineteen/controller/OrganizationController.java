package net.cmlzw.nineteen.controller;

import net.cmlzw.nineteen.domain.Organization;
import net.cmlzw.nineteen.domain.OrganizationArchive;
import net.cmlzw.nineteen.repository.OrganizationArchiveRepository;
import net.cmlzw.nineteen.repository.OrganizationRepository;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/organizations")
public class OrganizationController {
    @Autowired
    OrganizationRepository repository;
    @Autowired
    OrganizationArchiveRepository archiveRepository;

    @GetMapping
    public List<OrganizationDto> list() {
        List<Organization> all = repository.findAll();
        List<OrganizationDto> result = new ArrayList<>(all.size());
        for (Organization each : all) {
            result.add(dtoFrom(each));
        }
        Collections.sort(result, (a, b) -> b.getPercent().compareTo(a.getPercent()));
        return result;
    }

    @PostMapping("/archives")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void archive() {
        List<Organization> all = repository.findAll();
        List<OrganizationArchive> archives = all.stream().map(OrganizationArchive::valueFrom).collect(Collectors.toList());
        archiveRepository.save(archives);

        // reset organization submitted members to 0
        for (Organization each : all) {
            each.setSubmittedMembers(0);
        }
        repository.save(all);
    }

    @GetMapping("/archives")
    public List<String> listArchives() {
        List<Date> archiveDates = archiveRepository.findAllGroupByCreated();
        return archiveDates.stream().map(DateFormatUtils.ISO_DATE_FORMAT::format).collect(Collectors.toList());
    }

    @GetMapping("/archives/{date}")
    public List<OrganizationArchive> getArchive(@PathVariable String date) {
        Date created = null;
        try {
            created = DateFormatUtils.ISO_DATE_FORMAT.parse(date);
        } catch (ParseException e) {
            throw new InvalidArchiveDateException();
        }
        List<OrganizationArchive> result = archiveRepository.findAllByCreated(created);
        return result;
    }

    private OrganizationDto dtoFrom(Organization each) {
        OrganizationDto dto = new OrganizationDto();
        dto.setId(each.getId());
        dto.setName(each.getName());
        dto.setSubmittedMembers(each.getSubmittedMembers());
        dto.setTotalMembers(each.getTotalMembers());
        dto.setPercent(new BigDecimal(each.getSubmittedMembers()*1.0/each.getTotalMembers()));
        return dto;
    }
}
