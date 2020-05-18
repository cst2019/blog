package com.cst.web;

import com.cst.po.Blog;
import com.cst.po.Comment;
import com.cst.po.User;
import com.cst.service.BlogService;
import com.cst.service.CommentService;
import com.cst.util.IkAnalyzerUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

/**
 * @description:
 * @author: cst
 * @date: Created in 2020/1/9 9:33 下午
 * @version:
 * @modified By:
 */
@Controller
public class CommentController {

    @Autowired
    private CommentService commentService;

    @Autowired
    private BlogService blogService;

    @Value("${comment.avatar}")
    private String avatar;





    @GetMapping("/comments/{blogId}")
    public String comments(@PathVariable Long blogId, Model model){
        List<Comment> comments = commentService.listCommentByBlogId(blogId);
        Blog blog = blogService.getBlog(blogId);
        for (Comment comment:comments) {
            if(comment.getUser().getId()==blog.getUser().getId()){
                comment.setAdminComment(true);
            }
        }
        model.addAttribute("comments",comments);
        return "blog :: commentList";
    }

    /**
     * 评论
     * @param comment
     * @param session
     * @param attributes
     * @return
     */
    @PostMapping("/comments")
    public String saveComment(Comment comment,
                       HttpSession session,
                       RedirectAttributes attributes){
        Long blogId=comment.getBlog().getId();
        User user= (User) session.getAttribute("user");
        //出错
        if(user==null){
            attributes.addFlashAttribute("message","您还没有登录，请先登录");
            return "redirect:/";
        }
        if(IkAnalyzerUtil.getIk(comment.getContent())){
            attributes.addFlashAttribute("message","评论提交失败，请不要在本站发表不当言论");
            return "redirect:/comments/"+blogId;
        }
        comment.setUser(user);
        comment.setBlog(blogService.getBlog(blogId));
        comment.setAvatar(user.getAvatar());
        comment.setEmail(user.getEmail());
        comment.setNickname(user.getNickname());
        commentService.saveComment(comment);
        attributes.addFlashAttribute("message","评论提交成功");
        return "redirect:/comments/"+blogId;
    }
    /**
     * 回复消息
     * @param comment
     * @param session
     * @param attributes
     * @return
     */
    @PostMapping("/mcomments")
    public String post2(Long page,Comment comment,
                       HttpSession session,
                       RedirectAttributes attributes){
        Long blogId=comment.getBlog().getId();
        User user= (User) session.getAttribute("user");
        //出错
        if(user==null){
            attributes.addFlashAttribute("message","您还没有登录，请先登录");
            return "redirect:/";
        }
        if(IkAnalyzerUtil.getIk(comment.getContent())){
            attributes.addFlashAttribute("message","回复失败，请不要在本站发表不当言论");
            return "redirect:/mycomments?page="+page;
        }
        comment.setUser(user);
        comment.setBlog(blogService.getBlog(blogId));
        comment.setAvatar(user.getAvatar());
        comment.setEmail(user.getEmail());
        comment.setNickname(user.getNickname());
        commentService.saveComment(comment);
        attributes.addFlashAttribute("message","回复成功");

        return "redirect:/mycomments?page="+page;
    }
    /**
     * 展示消息
     * @param session
     * @param model
     * @return
     */
    @GetMapping("/mycomments")
    public String myComments(@PageableDefault(size=5,sort = {"createTime"},direction = Sort.Direction.ASC) Pageable pageable,
                        HttpSession session,
                       Model model){
        User user= (User) session.getAttribute("user");
        List<Comment> comments=commentService.getNewCommentByUser(user);

        for (Comment cc:comments) {
                commentService.updateNew(cc.getId());
        }
        setNewCom(model,session);
        model.addAttribute("comments",commentService.getCommentByUser(pageable,user));
        return "mycomments";
    }
    private  void setNewCom(Model model,HttpSession session){

        User user= (User) session.getAttribute("user");
        if(user==null)return;
        model.addAttribute("newcom",commentService.getNewCommentByUser(user));
    }



    /**
     * 点赞
     * @param comment
     * @param session
     * @param attributes
     * @param model
     * @return
     */
    @PostMapping("/likes")
    public String like(Comment comment,HttpSession session,
                       RedirectAttributes attributes,Model model){
        Long blogId=comment.getBlog().getId();
        User user= (User) session.getAttribute("user");
        if(user==null){
            attributes.addFlashAttribute("message","您还没有登录，请先登录");
            return "redirect:/";
        }

        Blog blog = new Blog();
        blog.setId(blogId);
        blog=blogService.updateLiked(blog,user);
        boolean isLike=false;
        for (User c:blog.getLiked()) {
            if(c.getId().equals(user.getId())){
                isLike=true;
            }
        }
        model.addAttribute("isLike",isLike);
        model.addAttribute("blog",blog);
        return "blog :: liket";
    }
    @PostMapping("/follows")
    public String follow(Comment comment,HttpSession session,
                       RedirectAttributes attributes,Model model){
        Long blogId=comment.getBlog().getId();
        User user= (User) session.getAttribute("user");
        if(user==null){
            attributes.addFlashAttribute("message","您还没有登录，请先登录");
            return "redirect:/";
        }

        Blog blog = new Blog();
        blog.setId(blogId);
        blog=blogService.updateFollow(blog,user);
        boolean isFollow=false;
        for (User c:blog.getUser().getFollowed()) {
            if(c.getId().equals(user.getId())){
                isFollow=true;
            }
        }
        model.addAttribute("isFollow",isFollow);
        model.addAttribute("blog",blog);
        return "blog :: followt";
    }
}
