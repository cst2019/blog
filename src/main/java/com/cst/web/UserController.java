package com.cst.web;

import com.cst.po.User;
import com.cst.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * @description:
 * @author: cst
 * @date: Created in 2020/3/7 4:02 下午
 * @version:
 * @modified By:
 */
@Controller
public class UserController {
    @Autowired
    UserService userService;

//    @GetMapping("/existUser")
//    public String existUser(String userame,Model model){
//        if(userService.existUserByUsername(userame)==null)
//            System.out.println(userame);
//        model.addAttribute("allow","用户名可用！");
//     return "register :: commentList";
//    }
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
