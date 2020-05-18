package com.cst.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @description:
 * @author: cst
 * @date: Created in 2020/4/22 3:14 下午
 * @version:
 * @modified By:
 */


@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler(" http://image.xunzhi.com/**").addResourceLocations("file:/blogImages/");
    }
}
