package com.cst.dao;

import com.cst.po.SysRole;
import com.cst.po.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * @description:
 * @author: cst
 * @date: Created in 2020/5/14 9:19 上午
 * @version:
 * @modified By:
 */
public interface RoleRepository extends JpaRepository<SysRole,Integer> {

    @Query("select u from SysRole u where u.role=?1")
    SysRole findByusId(String role);
}
