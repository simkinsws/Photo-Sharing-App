package com.photosharingapp.repository;

import com.photosharingapp.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
        @Query("SELECT p from Post p order by p.postedDate DESC")
        public List<Post> findAll();

        @Query("SELECT p from Post p WHERE p.username=:username order by p.postedDate DESC")
        public List<Post> findByUsername(@Param("username") String username);

        @Query("SELECT p FROM Post p where p.id=:x")
        public Post findPostById(@Param("x") Long id);

        @Modifying
        @Query("DELETE from Post WHERE id=:x")
        public void deletePostById(@Param("x") Long id);






}
