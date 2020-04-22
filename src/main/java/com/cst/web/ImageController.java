package com.cst.web;

/**
 * @description:
 * @author: cst
 * @date: Created in 2020/1/14 12:47 下午
 * @version:
 * @modified By:
 */
import com.cst.po.Blog;
import com.cst.po.User;
import com.cst.service.CommentService;
import com.cst.service.TagService;
import com.cst.service.TypeService;
import com.cst.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
    private  void setNewCom(Model model,HttpSession session){

        User user= (User) session.getAttribute("user");
        if(user==null)return;
        model.addAttribute("newcom",commentService.getNewCommentByUser(user));
    }
    /**上传地址*/
    private final String filePath="/Users/cst/Downloads/blog/src/main/resources/static/images/";

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
        // 获取上传文件名
        int i=(int)(Math.random()*900)+100;
        String filename = file.getOriginalFilename();
        String st=filename.substring(0, filename.length() - 5);
        String end=filename.substring(filename.length() - 5, filename.length());
        filename=st+i+end;
        // 定义上传文件保存路径
        String path = filePath;
        // 新建文件
        File filepath = new File(path, filename);
        // 判断路径是否存在，如果不存在就创建一个
        if (!filepath.getParentFile().exists()) {
            filepath.getParentFile().mkdirs();
        }
        try {
            // 写入文件
            file.transferTo(new File(path + File.separator + filename));
        } catch (IOException e) {
            e.printStackTrace();
            attributes.addFlashAttribute("message","上传失败");
            return "redirect:/about";
        }
        userService.updateAvatar(user.getId(),"/images/"+filename);
        user=userService.getUser(user.getId());
        session.setAttribute("user",user);
        attributes.addFlashAttribute("message","上传成功");
        return "redirect:/about";
    }

    // 执行上传
    @PostMapping("/upload")
    public String upload(@RequestParam("fileName") MultipartFile file, Blog blog, Model model, HttpSession session) {
        // 获取上传文件名
        int i=(int)(Math.random()*900)+100;
        String filename = file.getOriginalFilename();
        String st=filename.substring(0, filename.length() - 5);
        String end=filename.substring(filename.length() - 5, filename.length());
        filename=st+i+end;
        // 定义上传文件保存路径
        String path = filePath;
        // 新建文件
        File filepath = new File(path, filename);
        // 判断路径是否存在，如果不存在就创建一个
        if (!filepath.getParentFile().exists()) {
            filepath.getParentFile().mkdirs();
        }
        try {
            // 写入文件
            file.transferTo(new File(path + File.separator + filename));
        } catch (IOException e) {
            e.printStackTrace();
        }
        String firstPicture="/images/"+filename;
        blog.setFirstPicture(firstPicture);
        setTypeAndTag(model);
        setNewCom(model,session);
   model.addAttribute("blog", blog);
        return "blogsinputs";
    }
}
