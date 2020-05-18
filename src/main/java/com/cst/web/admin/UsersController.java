package com.cst.web.admin;

import com.cst.po.User;
import com.cst.service.UserService;
import com.cst.vo.BlogQueryVo;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;

/**
 * @description:
 * @author: cst
 * @date: Created in 2020/4/22 9:53 上午
 * @version:
 * @modified By:
 */
@Controller
@RequestMapping("/admin")
public class UsersController {
    @Autowired
    UserService userService;

    @RequiresRoles("admin")
    @GetMapping("/userManager")
    public String getAllUser(@PageableDefault(size=5,sort = {"createTime"},direction = Sort.Direction.DESC) Pageable pageable,
                             Model model){
        model.addAttribute("page",userService.listUser(pageable));
        return "/admin/users";
    }
    @RequiresRoles("admin")
    @PostMapping("/userManager/search")
    public String getUserByUserName(@PageableDefault(size=5,sort = {"createTime"},direction = Sort.Direction.DESC) Pageable pageable,
                                    @RequestParam(value = "username") String username,
                                    Model model){
        model.addAttribute("page",userService.listUserByUsername(pageable,username));
        return "/admin/users :: blogList";
    }

    /**
     *获取或者取消管理员权限
     * @param id
     * @param attributes
     * @return
     */
    @GetMapping("/add/{id}/{page}")
    public String addAdmin(@PathVariable Long id, @PathVariable Long page, RedirectAttributes attributes
            , HttpSession session){
       User user=(User)session.getAttribute("user");
       if(!user.getUsername().equals("cst")){
           attributes.addFlashAttribute("message","请不要操作未授权操作");
           return "redirect:/admin/userManager?page="+page;
       }
        int a=userService.addAdmin(id);
        if(a==0){
            attributes.addFlashAttribute("message","取消管理员权限成功");
        }else{
            attributes.addFlashAttribute("message","授权管理员权限成功");
        }
        return "redirect:/admin/userManager?page="+page;
    }
    @GetMapping("/user/{id}/delete/{page}")
    public String deleteUser(@PathVariable Long id, @PathVariable Long page, RedirectAttributes attributes
            , HttpSession session){
       User us=userService.getUser(id);
       if(us.getType()==0){
        User user=(User)session.getAttribute("user");
        if(!user.getUsername().equals("cst")){
            attributes.addFlashAttribute("message","请不要操作未授权操作");
            return "redirect:/admin/userManager?page="+page;
        }}
        userService.deleteById(id);

        return "redirect:/admin/userManager?page="+page;
    }

}
