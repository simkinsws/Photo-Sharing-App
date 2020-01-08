package com.photosharingapp.model;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
public class Comment implements Serializable {

    private static final long serialVersionUID = 14651065165L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(updatable = false, nullable = false)
    private Integer id;
    private String username;

    @Column(columnDefinition = "text")
    private String content;
    private Date postedDate;

    public Comment(Integer id, String username, String content, Date postedDate) {
        this.id = id;
        this.username = username;
        this.content = content;
        this.postedDate = postedDate;
    }

    public Comment() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getPostedDate() {
        return postedDate;
    }

    public void setPostedDate(Date postedDate) {
        this.postedDate = postedDate;
    }
}
