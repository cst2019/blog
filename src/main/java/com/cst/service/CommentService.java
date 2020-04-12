package com.cst.service;

import com.cst.po.Comment;
import com.cst.po.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * @description:
 * @author: cst
 * @date: Created in 2020/1/9 9:40 下午
 * @version:
 * @modified By:
 */
public interface CommentService {
        List<Comment> listCommentByBlogId(Long blogId);
        Comment saveComment(Comment comment);
    Page<Comment> getCommentByUser(Pageable pageable,User user);
        List<Comment> getNewCommentByUser(User user);
        void updateNew(Long id);

}
