package com.cst;

import com.cst.po.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;

/**
 * @description:
 * @author: cst
 * @date: Created in 2020/1/12 1:37 下午
 * @version:
 * @modified By:
 */
public class yggu {
    public static void main(String[] args) {
        List<String> strings = new ArrayList<String>();
        for (int i=0;i<18;i++){
            strings.add("第"+i+"数据");
        }
        Pageable pageRequest = PageRequest.of(1, 10);
        Page<String> strings1 = listConvertToPage1(strings, pageRequest);
        System.out.println(strings1);
    }

    public static <T> Page<T> listConvertToPage1(List<T> list, Pageable pageable) {
        int start = (int) pageable.getOffset();
        int end = (start + pageable.getPageSize()) > list.size() ? list.size() : (start + pageable.getPageSize());
        return new PageImpl<T>(list.subList(start, end), pageable, list.size());
    }
}
