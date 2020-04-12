package com.cst.dao;

import com.cst.po.Comment;
import com.cst.po.User;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @description:
 * @author: cst
 * @date: Created in 2020/1/9 9:47 下午
 * @version:
 * @modified By:
 */
public interface CommentRepository extends JpaRepository<Comment,Long> {


        List<Comment> findByBlogIdAndParentCommentNull(Long blogId, Sort sort);


        @Query("select c from Comment c where c.parentComment in (select tc.parentComment from Comment tc where tc.user=?1)")
        List<Comment> getisnewComments(User user);

        List<Comment> getCommentsByUser(User user);

        @Transactional
        @Modifying
        @Query("update Comment c set c.isNew=false where c.id=?1")
        void updateNew(Long id);
}
