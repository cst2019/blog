package com.cst.web;

/**
 * @description:
 * @author: cst
 * @date: Created in 2020/1/14 12:47 下午
 * @version:
 * @modified By:
 */
import com.alibaba.fastjson.JSONObject;
import com.cst.po.Blog;
import com.cst.po.User;
import com.cst.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;

@Controller
public class ImageController {

    @Autowired
    UserService userService;

    @Autowired
    private TypeService typeService;

    @Autowired
    TagService tagService;
    @Autowired
    CommentService commentService;
    @Autowired
    ImageService imageService;
    private  void setNewCom(Model model,HttpSession session){

        User user= (User) session.getAttribute("user");
        if(user==null)return;
        model.addAttribute("newcom",commentService.getNewCommentByUser(user));
    }
    /**上传地址*/
    private final String filePath="/blogImages/";

    private void setTypeAndTag(Model model){
        model.addAttribute("types",typeService.listType());
        model.addAttribute("tags",tagService.listTag());
    }

    // 执行上传
    @RequestMapping("uploadAvatar")
    public String upload(@RequestParam("file") MultipartFile file, Model model, HttpSession session, RedirectAttributes attributes) {
        User user= (User) session.getAttribute("user");
        if(user==null){
            attributes.addFlashAttribute("message","您还没有登录，请先登录");
            return "redirect:/";
        }
        if(file.getOriginalFilename()==""){
            attributes.addFlashAttribute("message","请不要上传空文件");
            return "redirect:/about";
        }
        String a=imageService.upload(file);
        userService.updateAvatar(user.getId(),a);
        user=userService.getUser(user.getId());
        session.setAttribute("user",user);
        attributes.addFlashAttribute("message","上传成功");
        return "redirect:/about";
    }

    // 执行上传
    @PostMapping("/upload")
    public String upload(@RequestParam("fileName") MultipartFile file, Blog blog, Model model, HttpSession session) {

        String firstPicture=imageService.upload(file);
        blog.setFirstPicture(firstPicture);
        setTypeAndTag(model);
        setNewCom(model,session);
   model.addAttribute("blog", blog);
        return "blogsinputs";
    }

    @RequestMapping("/editormdPic")
    @ResponseBody
    public JSONObject editormdPic (@RequestParam(value = "editormd-image-file", required = true) MultipartFile file, HttpServletRequest request, HttpServletResponse response) throws Exception{

        JSONObject res = new JSONObject();
        res.put("url", imageService.upload(file));
        res.put("success", 1);
        res.put("message", "upload success!");

        return res;

    }
}
