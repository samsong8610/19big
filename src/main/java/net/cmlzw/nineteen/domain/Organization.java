package net.cmlzw.nineteen.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Version;

@Entity
public class Organization {
    @Id
    @GeneratedValue
    Long id;
    String name;
    int totalMembers;
    int submittedMembers;
    @Version
    long version;

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

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }
}
