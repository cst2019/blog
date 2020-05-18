package com.cst.web;

import com.cst.po.Blog;
import com.cst.po.Comment;
import com.cst.po.User;
import com.cst.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

@Controller
public class IndexController {
    @Autowired
    private BlogService blogService;

    @Autowired
    private TypeService typeService;

    @Autowired
    TagService tagService;

    @Autowired
    CommentService commentService;

    @Autowired
    UserService userService;
    private  void setNewCom(Model model,HttpSession session){

        User user= (User) session.getAttribute("user");
        if(user==null)return;
        model.addAttribute("newcom",commentService.getNewCommentByUser(user));
    }

    @GetMapping("/")
    public String loginPage(HttpSession session){
        if(session.getAttribute("user")!=null){
            return "redirect:/index";
        }
        return "login";
    }

    @GetMapping("/index")
    public String index(@PageableDefault(size=5,sort = {"updateTime"},direction = Sort.Direction.DESC) Pageable pageable,
                        Model model,HttpSession session){

        setNewCom(model,session);
        model.addAttribute("page",blogService.listRecommendBlog(pageable));
        model.addAttribute("types",typeService.listType(6));
        model.addAttribute("tags",tagService.listTag(10));
        model.addAttribute("recommenedBlogs",blogService.listRecommendBlogTop(8));
        model.addAttribute("a",1);
        return "index";
    }

    /**
     * 关注的人
     * @param pageable
     * @param model
     * @param session
     * @return
     */
    @GetMapping("/index1")
    public String index1(@PageableDefault(size=5,sort = {"createTime"},direction = Sort.Direction.DESC) Pageable pageable,
                        Model model,HttpSession session){
        User user= (User) session.getAttribute("user");
        model.addAttribute("page",blogService.listFollowingBlog(pageable,user));
        model.addAttribute("types",typeService.listType(6));
        model.addAttribute("tags",tagService.listTag(10));
        model.addAttribute("recommenedBlogs",blogService.listRecommendBlogTop(8));
        model.addAttribute("a",2);
        setNewCom(model,session);
        return "index";
    }
    @GetMapping("/index2")
    public String index2(@PageableDefault(size=5,sort = {"view"},direction = Sort.Direction.DESC) Pageable pageable,
                         Model model,HttpSession session){
        model.addAttribute("page",blogService.listHotBlog(pageable));
        model.addAttribute("types",typeService.listType(6));
        model.addAttribute("tags",tagService.listTag(10));
        model.addAttribute("recommenedBlogs",blogService.listRecommendBlogTop(8));
        model.addAttribute("a",3);
        setNewCom(model,session);
        return "index";
    }
    @PostMapping("/search")
    public String search(@PageableDefault(size=5,sort = {"updateTime"},direction = Sort.Direction.DESC) Pageable pageable,
                         @RequestParam String query, Model model,HttpSession session){
       setNewCom(model, session);
        model.addAttribute("page",blogService.listBlog("%"+query+"%",pageable));
        model.addAttribute("query",query);
        return "search";
    }
    @GetMapping("/search1/{query}")
    public String search1(@PageableDefault(size=5,sort = {"updateTime"},direction = Sort.Direction.DESC) Pageable pageable,
                          @PathVariable String query, Model model,HttpSession session){
        setNewCom(model, session);
        model.addAttribute("page",blogService.listBlog("%"+query+"%",pageable));
        model.addAttribute("query",query);
        return "search";
    }

    /**
     * 个人空间
     * @param id
     * @param bid
     * @param model
     * @param session
     * @return
     */
    @GetMapping("/selfzon/{id}")
    public String selfzone(@PageableDefault(size=3,sort = {"updateTime"},direction = Sort.Direction.DESC) Pageable pageable,
                          @PathVariable Long id,
                        Model model,HttpSession session){
        User user= (User) session.getAttribute("user");//当前用户
        boolean isFollow=false;
      User us= userService.getUser(id);  //查看的用户
        //判断是否已经关注用户
        if(user!=null){
            List<User> followedList=us.getFollowed();
            for(User c:followedList){
                if(c.getId()==user.getId()){
                    isFollow=true;
                }
            }
        }
        setNewCom(model,session);
        model.addAttribute("isFollow",isFollow);
        model.addAttribute("us",us);
        model.addAttribute("page",blogService.listBlog(pageable,us));
        return "selfzone";
    }

    @GetMapping("/blog/{id}")
    public String blog(@PathVariable Long id, Model model, HttpSession session){
        User user= (User) session.getAttribute("user");
        boolean isLike=false;
        boolean isFollow=false;
        Blog blog=blogService.getAndConvert(id);
        if(user!=null){
        for (User c:blog.getLiked()) {
            if(c.getId()==user.getId()){
                isLike=true;
            }
        }
        List<User> followedList=blog.getUser().getFollowed();
            for(User c:followedList){
                if(c.getId()==user.getId()){
                    isFollow=true;
                }
            }
        }
        setNewCom(model, session);
        model.addAttribute("blog",blog);
        model.addAttribute("isLike",isLike);
        model.addAttribute("isFollow",isFollow);
    return "blog";
    }

    /**
     * 返回到新增博客页面
     * @param model
     * @return
     */
    @GetMapping("/blogs/input")
    public String input(Model model,HttpSession session){
        setTypeAndTag(model);
        model.addAttribute("blog",new Blog());
        setNewCom(model,session);
        return "blogsinput";
    }
    @GetMapping("/yh")
    public String yh(){ ;
        return "yhxy";
    }
    private void setTypeAndTag(Model model){
        model.addAttribute("types",typeService.listType());
        model.addAttribute("tags",tagService.listTag());
    }
}
