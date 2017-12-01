package net.cmlzw.nineteen.controller;

import net.cmlzw.nineteen.domain.Organization;
import net.cmlzw.nineteen.repository.OrganizationRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class OrganizationControllerIntegrationTest {
    @Autowired
    OrganizationRepository orgRepository;

    @Test(expected = OptimisticLockingFailureException.class)
    public void concurrentUpdateThrowsException() throws Exception {
        Organization org = new Organization();
        org.setName("org");
        org.setTotalMembers(10);
        orgRepository.save(org);

        Organization v1 = orgRepository.findOne(org.getId());
        Organization v2 = orgRepository.findOne(org.getId());
        assertEquals(v1.getVersion(), v2.getVersion());

        int submitted = v1.getSubmittedMembers() + 1;
        v1.setSubmittedMembers(submitted);
        orgRepository.save(v1);
        assertEquals(submitted, v1.getSubmittedMembers());

        v2.setSubmittedMembers(v2.getSubmittedMembers() + 1);
        orgRepository.save(v2);
    }
}
