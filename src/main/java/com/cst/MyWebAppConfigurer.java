package com.cst;

/**
 * @description:
 * @author: cst
 * @date: Created in 2020/1/14 11:06 上午
 * @version:
 * @modified By:
 */
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 资源映射路径
 */
@Configuration
public class MyWebAppConfigurer implements WebMvcConfigurer {

    /**上传地址*/
    private final String filePath="/Users/cst/Downloads/blog/src/main/resources/static/images/";
    /**显示相对地址*/
    private final String fileRelativePath="/Users/cst/Downloads/blog/src/main/resources/static/images/**";

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler(fileRelativePath).
                addResourceLocations("file:/" + filePath);
    }
}