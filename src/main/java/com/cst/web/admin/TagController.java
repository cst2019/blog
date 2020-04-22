package com.cst.web.admin;

import com.cst.po.Tag;
import com.cst.service.TagService;
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

/**
 * @description:
 * @author: cst
 * @date: Created in 2020/1/5 8:02 下午
 * @version:
 * @modified By:
 */
@Controller
@RequestMapping("admin")
public class TagController {
    @Autowired
    private TagService tagService;



    @GetMapping("/tags")
    public String types(@PageableDefault(size = 5,sort = {"id"},direction = Sort.Direction.DESC)
                                    Pageable pageable, Model model){
        model.addAttribute("page",  tagService.listTag(pageable));
        return "admin/tags";
    }





    @GetMapping("tags/input")
    public String input(){
        return "admin/tags-input";
    }



    @GetMapping("/tags/{id}/input")
    public String editInput(@PathVariable Long id,Model model){
        model.addAttribute("tag",tagService.getTag(id));
        return "admin/tags-edit";
    }


    @PostMapping("/tags")
    public String post(Tag tag,
                       RedirectAttributes attributes){
        Tag dbTag=tagService.getTagByName(tag.getName());
        if(dbTag!=null){
            attributes.addFlashAttribute("message","添加失败,标签名不能重复");
            return "redirect:/admin/tags";
        }
//        if(bindingResult.hasErrors()){
//            return "admin/types-input";
//        }
        Tag  t=tagService.saveTag(tag);
    if(t==null){
        attributes.addFlashAttribute("message","添加失败");
    }else{
        attributes.addFlashAttribute("message","添加成功");
    }
    return "redirect:/admin/tags";
    }


    @PostMapping("/tags/{id}")
    public String editPost(Tag tag,
                       @PathVariable Long  id,
                       RedirectAttributes attributes){
        Tag dbTag=tagService.getTagByName(tag.getName());
        if(dbTag!=null){
            attributes.addFlashAttribute("message","修改失败,名称不能重复");
            return "redirect:/admin/types";
        }
        Tag  t=tagService.updateTag(id,tag);
        if(t==null){
            attributes.addFlashAttribute("message","更新失败");
        }else{
            attributes.addFlashAttribute("message","更新成功");
        }
        return "redirect:/admin/tags";
    }


    @GetMapping("/tags/{id}/{page}/delete")
    public String delete(@PathVariable Long id,@PathVariable Long page,RedirectAttributes attributes){
        Tag dbTag=tagService.getTag(id);
        if(dbTag==null){
            attributes.addFlashAttribute("message","不存在该类别");
        }
        tagService.deleteTag(id);
        attributes.addFlashAttribute("message","删除成功");
        return "redirect:/admin/tags?page="+page;
    }

}
