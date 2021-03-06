package com.cst.web;

import com.cst.po.User;
import com.cst.service.UserService;
import com.cst.util.MD5Utils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;

@Controller
public  class LoginController {

    @Autowired
    UserService userService;

    @PostMapping("/login")
    public String login(@RequestParam  String username,
                        @RequestParam String password,
                        HttpSession session,
                        RedirectAttributes attributes){
        // 从SecurityUtils里边创建一个 subject
        Subject subject = SecurityUtils.getSubject();
        // 在认证提交前准备 token（令牌）
        UsernamePasswordToken token = new UsernamePasswordToken(username, MD5Utils.code(password));
        // 执行认证登陆
        try {
            subject.login(token);

        } catch (AuthenticationException e) {
            attributes.addFlashAttribute("message","账号或密码错误！");
            return "redirect:/";
        } catch (AuthorizationException e) {
            return "404";
        }
        User user=userService.checkUser(username,MD5Utils.code(password));
        session.setAttribute("user",user);
        if(user.getType()==1){
            return "redirect:index";
        }
        return "admin/index";

    }

    @GetMapping("/logout")
    public String logout(HttpSession session){
        User user= (User) session.getAttribute("user");
        session.removeAttribute("user");
        Subject subject = SecurityUtils.getSubject();
        subject.logout();
        return "redirect:/";
    }
    @GetMapping("/registers")
    public String register(){
        return "register";
    }
    @PostMapping("/regristry")
    public String regristry(User user, @RequestParam("terms") String  terms, RedirectAttributes attributes, Model model){
        User s=userService.existUserByEmail(user.getEmail());
        if(s!=null){
            attributes.addFlashAttribute("message","邮箱已被注册使用");
            return "redirect:/registers";
        }
        s=userService.existUserByTelephone(user.getTelephone());
        if(s!=null){
            attributes.addFlashAttribute("message","手机号码已经注册过了");
            return "redirect:/registers";
        }
        s=userService.existUserByUsername(user.getUsername());
        if(s!=null){
            attributes.addFlashAttribute("message","用户名不可用，已经别人注册过了");
            return "redirect:/registers";
        }
        userService.addUser(user);
        return "redirect:/";
    }
}
