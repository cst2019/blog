package com.cst.web;

import com.cst.po.Blog;
import com.cst.po.User;
import com.cst.service.BlogService;
import com.cst.service.CommentService;
import com.cst.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * @description:
 * @author: cst
 * @date: Created in 2020/1/13 12:47 下午
 * @version:
 * @modified By:
 */
@Controller
public class SelfController {
    @Autowired
    BlogService blogService;
    @Autowired
    UserService userService;
    @Autowired
    CommentService commentService;
    private  void setNewCom(Model model, HttpSession session){

        User user= (User) session.getAttribute("user");
        if(user==null)return;
        model.addAttribute("newcom",commentService.getNewCommentByUser(user));
    }

    /**
     * 我的博客
     * @param pageable
     * @param session
     * @param model
     * @param attributes
     * @return
     */
    @GetMapping("/selfblogs")
    public String selfblogs(@PageableDefault(size=3,sort = {"updateTime"},direction = Sort.Direction.DESC) Pageable pageable,
                            HttpSession session, Model model, RedirectAttributes attributes){
        User user= (User) session.getAttribute("user");
        if(user==null){
            attributes.addFlashAttribute("message","您还没有登录，请先登录");
            return "redirect:/";
        }
        setNewCom(model, session);
        model.addAttribute("page",blogService.listBlog(pageable,user));
        return "selfblogss";
    }

    /**
     * 点赞的博客
     * @param pageable
     * @param session
     * @param model
     * @param attributes
     * @return
     */
    @GetMapping("/likedBlog")
    public String likeblog(@PageableDefault(size=3,sort = {"updateTime"},direction = Sort.Direction.DESC) Pageable pageable,
                            HttpSession session, Model model, RedirectAttributes attributes){
        User user= (User) session.getAttribute("user");
        if(user==null){
            attributes.addFlashAttribute("message","您还没有登录，请先登录");
            return "redirect:/";
        }
        setNewCom(model, session);
        model.addAttribute("page",blogService.listLikeBlog(pageable,user));
        return "likeblogss";
    }

    /**
     *  获取关注的人
     * @param pageable
     * @param session
     * @param model
     * @param attributes
     * @return
     */
    @GetMapping("/followingUser")
    public String myfollowed(@PageableDefault(size=3) Pageable pageable,
                             HttpSession session, Model model, RedirectAttributes attributes){
        User user= (User) session.getAttribute("user");
        if(user==null){
            attributes.addFlashAttribute("message","您还没有登录，请先登录");
            return "redirect:/";
        }
        setNewCom(model, session);
        model.addAttribute("page",userService.getFollowed(user.getId(),pageable));
        return "myfollowed";
    }
    @GetMapping("/unfollow/{id}")
    public String removefollowed(@PathVariable Long id
                                , HttpSession session,RedirectAttributes attributes){
        User user= (User) session.getAttribute("user");
        if(user==null){
            attributes.addFlashAttribute("message","您还没有登录，请先登录");
            return "redirect:/";
        }
        userService.removeFollowed(id,user.getId());
        return "redirect:/followingUser";
    }
    @GetMapping("/blogs/{id}/delete")
    public String delete(@PathVariable Long id,
                         Model model,RedirectAttributes attributes){
        try {
            blogService.deleteBlog(id);
            attributes.addFlashAttribute("message","删除成功");
            return "redirect:/selfblogs";
        } catch (Exception e) {
            e.printStackTrace();
            attributes.addFlashAttribute("message","删除失败");
            return "redirect:/selfblogs";
        }
    }
    @GetMapping("/blogs/{id}/{page}/comment")
    public String comment(@PathVariable Long id,@PathVariable Long page,
                         Model model,RedirectAttributes attributes){
        blogService.allowComment(id);

        return "redirect:/selfblogs?page="+page;
    }
    @GetMapping("/archives")
    public String archives(Model model,HttpSession session,RedirectAttributes attributes){
        User user= (User) session.getAttribute("user");
        if(user==null){
            attributes.addFlashAttribute("message","您还没有登录，请先登录");
            return "redirect:/";
        }
        model.addAttribute("archiveMap",blogService.archiveBlog(user));
        model.addAttribute("blogCount",blogService.countBlog(user));
       setNewCom(model, session);
        return "archives";
    }
    @GetMapping("/about")
    public String about(Model model,HttpSession session,RedirectAttributes attributes){
        User user= (User) session.getAttribute("user");
        if(user==null){
            attributes.addFlashAttribute("message","您还没有登录，请先登录");
            return "redirect:/";
        }
        setNewCom(model,session);
        model.addAttribute("us",userService.getUser(user.getId()));
        return "about";
    }

}
