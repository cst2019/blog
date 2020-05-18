package com.cst.filter;

/**
 * @description:
 * @author: cst
 * @date: Created in 2020/4/25 10:37 上午
 * @version:
 * @modified By:
 */
import org.apache.shiro.authz.AuthorizationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice
public class MyExceptionhandler {

    @ExceptionHandler
    @ResponseBody
    public ModelAndView ErrorHandler(AuthorizationException e) {
        ModelAndView modelAndView=new ModelAndView();
        modelAndView.setViewName("error/404");

        return modelAndView;
    }
}