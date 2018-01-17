package net.cmlzw.nineteen.domain;

import org.apache.commons.lang3.time.DateUtils;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Calendar;
import java.util.Date;

@Entity
public class OrganizationArchive {
    @Id
    @GeneratedValue
    Long id;
    @Column(nullable = false)
    Long organizationId;
    @Column(nullable = false)
    String name;
    int totalMembers;
    int submittedMembers;
    @Column(nullable = false)
    Date created;

    public OrganizationArchive() {}

    public static OrganizationArchive valueFrom(Organization organization) {
        OrganizationArchive board = new OrganizationArchive();
        board.setOrganizationId(organization.getId());
        board.setName(organization.getName());
        board.setSubmittedMembers(organization.getSubmittedMembers());
        board.setTotalMembers(organization.getTotalMembers());
        board.setCreated(DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH));
        return board;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getTotalMembers() {
        return totalMembers;
    }

    public void setTotalMembers(int totalMembers) {
        this.totalMembers = totalMembers;
    }

    public int getSubmittedMembers() {
        return submittedMembers;
    }

    public void setSubmittedMembers(int submittedMembers) {
        this.submittedMembers = submittedMembers;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }
}
