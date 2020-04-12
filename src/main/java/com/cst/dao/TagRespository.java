package com.cst.dao;


import com.cst.po.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * @description:
 * @author: cst
 * @date: Created in 2020/1/6 10:59 上午
 * @version:
 * @modified By:
 */
public interface TagRespository extends JpaRepository<Tag,Long> {
    Tag findByName(String name);

    @Query("select t from  Tag t")
    List<Tag> finTop(Pageable pageable);
}
