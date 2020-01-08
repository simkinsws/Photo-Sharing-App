package com.photosharingapp.service.impl;

import com.photosharingapp.model.Comment;
import com.photosharingapp.model.Post;
import com.photosharingapp.repository.CommentRepository;
import com.photosharingapp.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Date;

@Service
@Transactional
public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Override
    public void saveComment(Post post, String username, String content) {
        Comment comment = new Comment();
        comment.setContent(content);
        comment.setUsername(username);
        comment.setPostedDate(new Date());
        post.setCommentList(comment);
        commentRepository.save(comment);
    }
}
