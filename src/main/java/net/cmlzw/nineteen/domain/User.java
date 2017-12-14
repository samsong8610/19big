package net.cmlzw.nineteen.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
public class User {
    @Id
    String username;
    @JsonIgnore
    String password;
    @JsonIgnore
    boolean enabled;
    String nickname;
    @JsonIgnore
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "username")
    Set<Authority> authorities;

    public User() {}

    public User(String username, String password, boolean enabled) {
        this.username = username;
        this.password = password;
        this.enabled = enabled;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Set<Authority> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(Set<Authority> authorities) {
        if (authorities == null) {
            authorities = new HashSet<>();
        }
        this.authorities = authorities;
    }

    public void addAuthority(String authority) {
        if (this.authorities == null) {
            this.authorities = new HashSet<>();
        }
        Authority auth = new Authority(authority);
        this.authorities.add(auth);
    }
}
