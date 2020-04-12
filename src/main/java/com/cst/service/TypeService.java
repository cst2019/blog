package com.cst.service;


import com.cst.po.Type;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * @description:
 * @author: cst
 * @date: Created in 2020/1/5 7:09 下午
 * @version:
 * @modified By:
 */
public interface TypeService {
    Type saveType(Type type);

    Type getType(Long id);

    Type getTypeByName(String name);

    Page<Type> listType(Pageable pageable);

    List<Type> listType();

    List<Type> listType(Integer size);

    Type updateType(Long id,Type type);

    void deleteType(Long id);
}
