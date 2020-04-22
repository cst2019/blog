package com.cst.web.admin;

import com.cst.service.UserService;
import com.cst.vo.BlogQueryVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

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

    @GetMapping("/userManager")
    public String getAllUser(@PageableDefault(size=5,sort = {"createTime"},direction = Sort.Direction.DESC) Pageable pageable,
                             Model model){
        model.addAttribute("page",userService.listUser(pageable));
        return "/admin/users";
    }

}
