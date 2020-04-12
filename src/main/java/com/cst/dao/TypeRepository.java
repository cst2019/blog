package com.cst.dao;


import com.cst.po.Type;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * @description:
 * @author: cst
 * @date: Created in 2020/1/5 7:18 下午
 * @version:
 * @modified By:
 */
public interface TypeRepository extends JpaRepository<Type,Long> {
    Type findByName(String name);

    @Query("select t from  Type t")
    List<Type> findTop(Pageable pageable);
}
