package com.cst.web.admin;

import com.cst.po.Type;
import com.cst.service.TypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

/**
 * @description:
 * @author: cst
 * @date: Created in 2020/1/5 8:02 下午
 * @version:
 * @modified By:
 */
@Controller
@RequestMapping("admin")
public class TypeController {
    @Autowired
    private TypeService typeService;



    @GetMapping("/types")
    public String types(@PageableDefault(size = 5,sort = {"id"},direction = Sort.Direction.DESC)
                                    Pageable pageable, Model model){
        model.addAttribute("page",  typeService.listType(pageable));
        return "admin/types";
    }





    @GetMapping("types/input")
    public String input(){
        return "admin/types-input";
    }



    @GetMapping("/types/{id}/input")
    public String editInput(@PathVariable Long id,Model model){
        System.out.println(id);
        model.addAttribute("type",typeService.getType(id));
        return "admin/types-edit";
    }


    @PostMapping("/types")
    public String post(Type type,
                       RedirectAttributes attributes){
        Type dbType1=typeService.getTypeByName(type.getName());
        if(dbType1!=null){
            attributes.addFlashAttribute("message","添加失败,名称不能重复");
            return "redirect:/admin/types";
        }
//        if(bindingResult.hasErrors()){
//            return "admin/types-input";
//        }
        Type  t=typeService.saveType(type);
    if(t==null){
        attributes.addFlashAttribute("message","添加失败");
    }else{
        attributes.addFlashAttribute("message","添加成功");
    }
    return "redirect:/admin/types";
    }


    @PostMapping("/types/{id}")
    public String editPost(Type type,
                       @PathVariable Long  id,
                       RedirectAttributes attributes){
        Type dbType=typeService.getTypeByName(type.getName());
        if(dbType!=null){
            attributes.addFlashAttribute("message","修改失败,名称不能重复");
            return "redirect:/admin/types";
        }
        Type  t=typeService.updateType(id,type);
        if(t==null){
            attributes.addFlashAttribute("message","更新失败");
        }else{
            attributes.addFlashAttribute("message","更新成功");
        }
        return "redirect:/admin/types";
    }


    @GetMapping("/types/{id}/delete")
    public String delete(@PathVariable Long id,RedirectAttributes attributes){
        Type dbType=typeService.getType(id);
        if(dbType==null){
            attributes.addFlashAttribute("message","不存在该类别");
        }
        typeService.deleteType(id);
        attributes.addFlashAttribute("message","删除成功");
        return "redirect:/admin/types";
    }

}
