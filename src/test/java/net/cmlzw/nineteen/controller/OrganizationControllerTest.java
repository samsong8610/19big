package net.cmlzw.nineteen.controller;

import net.cmlzw.nineteen.domain.Organization;
import net.cmlzw.nineteen.repository.OrganizationRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(value = OrganizationController.class, secure = false)
public class OrganizationControllerTest {
    @MockBean
    OrganizationRepository repository;
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

}