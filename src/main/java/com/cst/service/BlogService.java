package com.cst.service;

import com.cst.po.Blog;
import com.cst.po.User;
import com.cst.vo.BlogQueryVo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

/**
 * @description:
 * @author: cst
 * @date: Created in 2020/1/6 1:42 下午
 * @version:
 * @modified By:
 */
public interface BlogService {
    Blog getBlog(Long id);

    Blog getAndConvert(Long id);

    Page<Blog> listBlog(Pageable pageable, BlogQueryVo blog);

    Page<Blog> listBlog(Pageable pageable);


    Page<Blog> listBlog(String query,Pageable pageable);

    Page<Blog> listBlog(Long tagId,Pageable pageable);

    Page<Blog> listBlog(Pageable pageable,User user);

    Page<Blog> listLikeBlog(Pageable pageable,User user);

    List<Blog> listRecommendBlogTop(Integer size);

    Page<Blog> listPublishedBlog(Pageable pageable);

    Page<Blog> listRecommendBlog(Pageable pageable);

    Page<Blog> listHotBlog(Pageable pageable);

    Map<String,List<Blog>> archiveBlog(User user);

    Blog saveBlog(Blog blog);

   Blog updateLiked(Blog blog, User user);

    Blog updateFollow(Blog blog, User user);

    Blog updateBlog(Long id,Blog blog);

    Blog allowComment(Long id);

    void deleteBlog(Long id);

    Long countBlog(User user);
}
