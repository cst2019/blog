package com.cst.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

/**
 * @description:
 * @author: cst
 * @date: Created in 2020/4/24 4:47 下午
 * @version:
 * @modified By:
 */
@Service
public class ImageServiceImpl implements ImageService {
    private final String filePath="/blogImages/";
    @Override
    public String upload(MultipartFile file) {
        String trueFileName = file.getOriginalFilename();

        String suffix = trueFileName.substring(trueFileName.lastIndexOf("."));
        int i=(int)(Math.random()*900)+100;
        String fileName = System.currentTimeMillis()+"_"+i+suffix;

        String path = filePath;
        File targetFile = new File(path, fileName);
        if(!targetFile.exists()){
            targetFile.mkdirs();
        }

        //保存
        try {
            // 写入文件
            file.transferTo(new File(path + File.separator + fileName));
            // file.transferTo(targetFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return " http://image.xunzhi.com/"+fileName;
    }
}
