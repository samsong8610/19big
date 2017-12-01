package net.cmlzw.nineteen.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Date;

@Entity
public class JobLock {
    @Id
    @Column(unique = true, length = 16)
    String job;
    Date created;

    public JobLock(String jobName, Date date) {
        this.job = jobName;
        this.created = date;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }
}
