package com.cst.web;

import com.cst.po.Type;
import com.cst.po.User;
import com.cst.service.BlogService;
import com.cst.service.CommentService;
import com.cst.service.TypeService;
import com.cst.vo.BlogQueryVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * @description:
 * @author: cst
 * @date: Created in 2020/1/11 3:05 下午
 * @version:
 * @modified By:
 */
@Controller
public class TypeShowController {

    @Autowired
    TypeService typeService;

    @Autowired
    BlogService blogService;

    @Autowired
    CommentService commentService;
    private  void setNewCom(Model model, HttpSession session){

        User user= (User) session.getAttribute("user");
        if(user==null)return;
        model.addAttribute("newcom",commentService.getNewCommentByUser(user));
    }


    @GetMapping("/types/{id}")
    public String showType(@PageableDefault(size=5,sort = {"updateTime"},direction = Sort.Direction.DESC) Pageable pageable,
                           @PathVariable Long id, Model model, HttpSession session){
        List<Type> types=typeService.listType(100000);
        if(id==-1){
            id=types.get(0).getId();
        }
        BlogQueryVo blogQueryVo=new BlogQueryVo();
        blogQueryVo.setTypeId(id);
        setNewCom(model,session);
        model.addAttribute("types",types);
        model.addAttribute("page",blogService.listBlog(pageable,blogQueryVo));
        model.addAttribute("activeTypeId",id);
        return "types";
    }
}
