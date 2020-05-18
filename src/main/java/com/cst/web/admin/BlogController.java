package com.cst.web.admin;

import com.cst.po.Blog;
import com.cst.po.User;
import com.cst.service.BlogService;
import com.cst.service.TagService;
import com.cst.service.TypeService;
import com.cst.vo.BlogQueryVo;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin")
public class BlogController {
    private static final String INPUT="admin/blogs-input";
    private static final String LIST="admin/blogs";
    private static final String REDIRECT_LIST="redirect:/admin/blogss";

    @Autowired
    private BlogService blogService;
    @Autowired
    private TypeService typeService;
    @Autowired
    private TagService tagService;

    @RequiresRoles("admin")
    @GetMapping("/blogss")
    public String blogs(@PageableDefault(size=5,sort = {"updateTime"},direction = Sort.Direction.DESC)Pageable pageable,
                        BlogQueryVo blog,
                        Model model){
        model.addAttribute("types",typeService.listType());
        model.addAttribute("page",blogService.listBlog(pageable,blog));

        return "/admin/blogs";
    }

    @RequiresRoles("admin")
    @PostMapping("/blogs/search")
    public String search(@PageableDefault(size=5,sort = {"updateTime"},direction = Sort.Direction.DESC)Pageable pageable,
                        BlogQueryVo blog,
                        Model model){
        model.addAttribute("page",blogService.listBlog(pageable,blog));
        return "admin/blogs :: blogList";
    }


    @GetMapping("/blogs/input")
    public String input(Model model){
        setTypeAndTag(model);
        model.addAttribute("blog",new Blog());
        return INPUT;
    }

    /**
     * 修改博客内容
     * @param id
     * @param model
     * @return
     */
    @GetMapping("/blogs/{id}/input")
    public String editnput(@PathVariable Long id,
                           Model model){
        setTypeAndTag(model);
        Blog blog=blogService.getBlog(id);
        blog.init();
        model.addAttribute("blog",blog);
        return INPUT;
    }

    /**
     * 博客是否推荐
     * @param id
     * @param model
     * @return
     */
    @RequiresRoles("admin")
    @GetMapping("/recom/{id}/{page}")
    public String isCom(@PathVariable Long id,@PathVariable Long page,
                           Model model){
        Blog blog=blogService.getBlog(id);
        blog.setRecommened(!blog.isRecommened());
        blogService.saveBlog(blog);
//        model.addAttribute("blog",blog);
//        String str="admin/blogs :: aa";
//        System.out.println(str);
        return REDIRECT_LIST+"?page="+page;
    }

    @RequiresRoles("admin")
    @GetMapping("/blogs/{id}/{page}/delete")
    public String delete(@PathVariable Long id,@PathVariable Long page,
                         Model model){
        blogService.deleteBlog(id);
        model.addAttribute("message","删除成功");
        return REDIRECT_LIST+"?page="+page;
    }

    private void setTypeAndTag(Model model){
        model.addAttribute("types",typeService.listType());
        model.addAttribute("tags",tagService.listTag());
    }

    @PostMapping("/blogs")
    public String post(Blog blog,
                       HttpSession session,
                       RedirectAttributes attributes
                       ){
        blog.setUser((User) session.getAttribute("user"));
        blog.setType(typeService.getType(blog.getType().getId()));
        blog.setTags(tagService.listTag(blog.getTagIds()));
        Blog b=blogService.saveBlog(blog);
        if(!blog.isPublished()){
        if(b==null){
            attributes.addFlashAttribute("message","保存失败");
        }else{
            attributes.addFlashAttribute("message","保存成功");
        }}
        else{
            if(b==null){
                attributes.addFlashAttribute("message","发布失败");
            }else{
                attributes.addFlashAttribute("message","发布成功");
            }
        }
        return REDIRECT_LIST;
    }
}
