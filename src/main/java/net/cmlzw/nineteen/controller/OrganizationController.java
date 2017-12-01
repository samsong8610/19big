package net.cmlzw.nineteen.controller;

import net.cmlzw.nineteen.domain.Organization;
import net.cmlzw.nineteen.repository.OrganizationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@RestController
@RequestMapping("/organizations")
public class OrganizationController {
    @Autowired
    OrganizationRepository repository;

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
