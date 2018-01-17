package net.cmlzw.nineteen.controller;

import net.cmlzw.nineteen.domain.Organization;
import net.cmlzw.nineteen.domain.OrganizationArchive;
import net.cmlzw.nineteen.repository.OrganizationArchiveRepository;
import net.cmlzw.nineteen.repository.OrganizationRepository;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Matchers.anyCollectionOf;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(value = OrganizationController.class, secure = false)
public class OrganizationControllerTest {
    @MockBean
    OrganizationRepository repository;
    @MockBean
    OrganizationArchiveRepository archiveRepository;
    @Autowired
    MockMvc mockMvc;

    @Test
    public void listOrganizationStatics() throws Exception {
        List<Organization> organizations = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            Organization org = new Organization();
            org.setId(new Long(i));
            org.setName("org" + i);
            org.setTotalMembers(20);
            org.setSubmittedMembers(i);

            organizations.add(org);
        }

        given(repository.findAll()).willReturn(organizations);
        mockMvc.perform(get("/organizations"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0].id").value(2))
                .andExpect(jsonPath("$[0].percent").value(0.1))
                .andExpect(jsonPath("$[2].id").value(0))
                .andExpect(jsonPath("$[2].percent").value(0));
    }

    @Test
    public void archiveOrganization() throws Exception {
        List<Organization> organizations = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            Organization org = new Organization();
            org.setId(new Long(i));
            org.setName("org" + i);
            org.setTotalMembers(20);
            org.setSubmittedMembers(i);

            organizations.add(org);
        }

        given(repository.findAll()).willReturn(organizations);
        mockMvc.perform(post("/organizations/archives"))
                .andDo(print())
                .andExpect(status().isNoContent());
        then(archiveRepository).should().save(anyCollectionOf(OrganizationArchive.class));
        then(repository).should().save(anyCollectionOf(Organization.class));
    }

    @Test
    public void listArchivesEmpty() throws Exception {
        mockMvc.perform(get("/organizations/archives"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    public void listArchives() throws Exception {
        Date today = DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH);
        Date yesterday = DateUtils.addDays(today, -1);

        given(archiveRepository.findAllGroupByCreated()).willReturn(Arrays.asList(today, yesterday));
        mockMvc.perform(get("/organizations/archives"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0]").value(DateFormatUtils.ISO_DATE_FORMAT.format(today)))
                .andExpect(jsonPath("$[1]").value(DateFormatUtils.ISO_DATE_FORMAT.format(yesterday)));
    }

    @Test
    public void listArchivesByDate() throws Exception {
        Date today = DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH);
        List<OrganizationArchive> archives = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            OrganizationArchive archive = new OrganizationArchive();
            archive.setId(new Long(i*2));
            archive.setOrganizationId(new Long(i));
            archive.setName("org" + i);
            archive.setSubmittedMembers(i);
            archive.setTotalMembers(i);
            archive.setCreated(today);
            archives.add(archive);
        }

        String date = DateFormatUtils.ISO_DATE_FORMAT.format(today);
        given(archiveRepository.findAllByCreated(today)).willReturn(archives);
        mockMvc.perform(get("/organizations/archives/" + date))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(3));
    }

}