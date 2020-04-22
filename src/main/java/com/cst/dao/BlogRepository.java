package com.cst.dao;

import com.cst.po.Blog;
import com.cst.po.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @description:
 * @author: cst
 * @date: Created in 2020/1/6 1:46 下午
 * @version:
 * @modified By:
 */
public interface BlogRepository extends JpaRepository<Blog,Long>, JpaSpecificationExecutor<Blog> {

    @Query("select b from Blog b where b.recommened=true and b.published=true")
    List<Blog> findTop(Pageable pageable);

    @Query("select b from Blog b where b.published=true")
    Page<Blog> findPublishd(Pageable pageable);

    @Query("select b from Blog b where b.recommened=true and b.published=true")
    Page<Blog> findRecommend(Pageable pageable);

    @Query("select b from Blog b where b.title like ?1 or b.content like ?1")
    Page<Blog> findByQuery(String query,Pageable pageable);

    Page<Blog> findBlogsByUserId(Long userId,Pageable pageable);

    List<Blog> findBlogsByUserId(Long userId);

    @Transactional
    @Modifying
    @Query("update Blog b set b.view=b.view+1 where b.id=?1")
    int updateView(Long id);

    @Query("select function('date_format',b.updateTime,'%Y') as year from Blog b where b.user=?1 group by function('date_format',b.updateTime,'%Y') order by year desc")
    List<String> findGroupYear(User user);

    @Query("select b from Blog b where function('date_format',b.updateTime,'%Y')=?1 and b.user=?2")
    List<Blog> findByYear(String year,User user);

    Long countBlogByUser(User user);

    @Transactional
    void deleteBlogsByTypeId(Long typeId);


}
