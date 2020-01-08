package com.photosharingapp.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.io.Serializable;

@Entity
public class UserRole implements Serializable {

    private static final long serialVersionUID = 14653065175L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(updatable = false, nullable = false)
    private long userRoleId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private AppUser appUser;

    @ManyToOne(fetch = FetchType.EAGER)
    private Role role;

    public UserRole(long userRoleId, AppUser appUser, Role role) {
        this.userRoleId = userRoleId;
        this.appUser = appUser;
        this.role = role;
    }

    public UserRole() {
    }

    public UserRole(AppUser appUser, Role role) {
        this.appUser = appUser;
        this.role = role;
    }

    public long getUserRoleId() {
        return userRoleId;
    }

    public void setUserRoleId(long userRoleId) {
        this.userRoleId = userRoleId;
    }

    public AppUser getAppUser() {
        return appUser;
    }

    public void setAppUser(AppUser appUser) {
        this.appUser = appUser;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}
