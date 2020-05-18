package com.cst.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * @description:
 * @author: cst
 * @date: Created in 2020/4/24 4:36 下午
 * @version:
 * @modified By:
 */
public interface ImageService {
    public String upload(MultipartFile file);
}
