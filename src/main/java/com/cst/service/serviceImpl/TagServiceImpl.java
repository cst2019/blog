package com.cst.service.serviceImpl;

import com.cst.dao.BlogRepository;
import com.cst.dao.TagRespository;
import com.cst.exception.NotFoundException;
import com.cst.po.Blog;
import com.cst.po.Tag;
import com.cst.service.TagService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * @description:
 * @author: cst
 * @date: Created in 2020/1/6 11:04 上午
 * @version:
 * @modified By:
 */
@Service
public class TagServiceImpl implements TagService {

    @Autowired
    TagRespository tagRespository;
    @Autowired
    BlogRepository blogRepository;

    @Transactional
    @Override
    public Tag saveTag(Tag tag) {
        return tagRespository.save(tag);
    }

    @Transactional
    @Override
    public Tag getTag(Long id) {
        return tagRespository.findById(id).get();
    }

    @Transactional
    @Override
    public Tag getTagByName(String name) {
        return tagRespository.findByName(name);
    }

    @Transactional
    @Override
    public Page<Tag> listTag(Pageable pageable) {
        return tagRespository.findAll(pageable);
    }

    @Transactional
    @Override
    public List<Tag> listTag() {
        return tagRespository.findAll();
    }

    @Override
    public List<Tag> listTag(Integer size) {
        Sort sort=Sort.by(Sort.Direction.DESC,"blogs.size");
        Pageable pageable= PageRequest.of(0,size,sort);
        return tagRespository.finTop(pageable);
    }

    @Transactional
    @Override
    public List<Tag> listTag(String ids) {

        return tagRespository.findAllById(convertToList(ids));
    }

    private  List<Long> convertToList(String ids){
            List<Long> list=new ArrayList<>();
            if(!"".equals(ids) && ids!=null){
                String[] idarray=ids.split(",");
                for(int i=0;i<idarray.length;i++){
                    list.add(new Long(idarray[i]));
                }

            }
            return list;
    }

    @Transactional
    @Override
    public Tag updateTag(Long id, Tag tag) {
            Tag tag1=tagRespository.getOne(id);
        if(tag1==null){
            throw new NotFoundException("不存在该类型");
        }
        BeanUtils.copyProperties(tag,tag1);
        return tagRespository.save(tag1);
    }

    @Transactional
    @Override
    public void deleteTag(Long id) {
        Tag tag=tagRespository.findById(id).get();
     List<Blog> blogs=tag.getBlogs();
     Tag tag2=tagRespository.findById(new Long(91)).get();
     for(Blog b:blogs){
         List<Tag> tag1 = b.getTags();
         tag1.remove(tag);
         b.setTags(tag1);
         if(tag1.size()==0){
             tag1.add(tag2);
         }
         blogRepository.save(b);
     }

//      tagRespository.save(tag);
        tagRespository.deleteById(id);
    }
}
