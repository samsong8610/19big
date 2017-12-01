package net.cmlzw.nineteen.controller;

import java.math.BigDecimal;

public class OrganizationDto {
    Long id;
    String name;
    int totalMembers;
    int submittedMembers;
    BigDecimal percent;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public BigDecimal getPercent() {
        return percent;
    }

    public void setPercent(BigDecimal percent) {
        this.percent = percent;
    }
}
