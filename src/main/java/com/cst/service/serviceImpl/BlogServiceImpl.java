package com.cst.service.serviceImpl;

import com.cst.dao.BlogRepository;
import com.cst.dao.CommentRepository;
import com.cst.dao.UserRepository;
import com.cst.exception.NotFoundException;
import com.cst.po.Blog;
import com.cst.po.Type;
import com.cst.po.User;
import com.cst.service.BlogService;
import com.cst.util.MarkdownUtils;
import com.cst.vo.BlogQueryVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.*;
import java.util.*;

/**
 * @description:
 * @author: cst
 * @date: Created in 2020/1/6 1:45 下午
 * @version:
 * @modified By:
 */
@Service
public class BlogServiceImpl implements BlogService {

    @Autowired
    BlogRepository blogRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    CommentRepository commentRepository;

    @Override
    public Blog getBlog(Long id) {

        return blogRepository.findById(id).get();
    }

    @Transactional
    @Override
    public Blog getAndConvert(Long id) {
        Blog blog=blogRepository.findById(id).get();
        if(blog==null){
            throw new NotFoundException("该博客不存在");
        }
        Blog b=new Blog();
        BeanUtils.copyProperties(blog,b);
        String content=b.getContent();
        b.setContent( MarkdownUtils.markdownToHtmlExtensions(content));
        blogRepository.updateView(id);
        return b;
    }

    @Override
    public Page<Blog> listBlog(Pageable pageable, BlogQueryVo blog) {
        return blogRepository.findAll(new Specification<Blog>() {
            @Override
            public Predicate toPredicate(Root<Blog> root, CriteriaQuery<?> cq, CriteriaBuilder cb) {
                List<Predicate> predicates=new ArrayList<>();
                if(!"".equals(blog.getTitle()) && blog.getTitle() !=null){
                            predicates.add(cb.like(root.<String>get("title"),"%"+blog.getTitle()+"%"));
                }
                if(blog.getTypeId()!=null){
                    predicates.add(cb.equal(root.<Type>get("type").get("id"),blog.getTypeId()));
                }
                if(blog.isRecommened()){
                    predicates.add(cb.equal(root.<Boolean>get("recommened"),blog.isRecommened()));
                }
                cq.where(predicates.toArray(new Predicate[predicates.size()]));
                return null;
            }
        },pageable);
    }

    @Override
    public Page<Blog> listBlog(Pageable pageable) {
        return blogRepository.findAll(pageable);
    }

    @Override
    public Page<Blog> listBlog(String query,Pageable pageable) {
        return blogRepository.findByQuery(query, pageable);

    }

    @Override
    public Page<Blog> listBlog(Long tagId, Pageable pageable) {

        return blogRepository.findAll(new Specification<Blog>() {
            @Override
            public Predicate toPredicate(Root<Blog> root, CriteriaQuery<?> cq, CriteriaBuilder cb) {
                Join join=root.join("tags");
                return cb.equal(join.get("id"),tagId);
            }
        },pageable);
    }

    /**
     * 展示某个人的所有博客
     * @param pageable
     * @param user
     * @return
     */

    @Override
    public Page<Blog> listBlog(Pageable pageable, User user) {
        return   blogRepository.findBlogsByUserId(user.getId(),pageable);
    }

    @Override
    public Page<Blog> listLikeBlog(Pageable pageable, User user) {
        User user1=userRepository.findById(user.getId()).get();
        List<Blog> like=user1.getLikes();



        return listConvertToPage(like,pageable);
    }

    private  <T> Page<T> listConvertToPage(List<T> list, Pageable pageable) {
        int start = (int)pageable.getOffset();
        int end = (start + pageable.getPageSize()) > list.size() ? list.size() : ( start + pageable.getPageSize());
        return new PageImpl<T>(list.subList(start, end), pageable, list.size());
    }
    @Override
    public List<Blog> listRecommendBlogTop(Integer size) {
        Sort sort=Sort.by(Sort.Direction.DESC,"updateTime");
        Pageable pageable= PageRequest.of(0,size,sort);
        return blogRepository.findTop(pageable);
    }

    /**
     * 展示最新的博客内容
     * @param pageable
     * @return
     */
    @Override
    public Page<Blog> listPublishedBlog(Pageable pageable) {
        return blogRepository.findPublishd(pageable);
    }

    /**
     * 展示被推荐的博客内容
     * @param pageable
     * @return
     */
    @Override
    public Page<Blog> listRecommendBlog(Pageable pageable) {
        return blogRepository.findRecommend(pageable);
    }

    /**
     * 展示最热的博客内容
     * @param pageable
     * @return
     */
    @Override
    public Page<Blog> listHotBlog(Pageable pageable) {
        return blogRepository.findPublishd(pageable);
    }

    /**
     * 归档
     * @param user
     * @return
     */
    @Transactional
    @Override
    public Map<String, List<Blog>> archiveBlog(User user) {
        List<String> years=blogRepository.findGroupYear(user);
        Map<String, List<Blog>> map=new LinkedHashMap<>();
        for (String year: years) {
            map.put(year,blogRepository.findByYear(year,user));
        }
        return map;
    }


    /**
     * 保存blog对象
     * @param blog
     * @return
     */
    @Transactional
    @Override
    public Blog saveBlog(Blog blog) {
        if(blog.getId()==null){
        blog.setCreateTime(new Date());
        blog.setUpdateTime(new Date());
        blog.setView(0);}
        else{
            Blog blog1=blogRepository.findById(blog.getId()).get();
            blog.setView(blog1.getView());
            blog.setCreateTime(blog1.getCreateTime());
            blog.setUpdateTime(blog1.getUpdateTime());
            blog.setLiked(blog1.getLiked());
        }
        return blogRepository.save(blog);
    }

    /**
     * 点赞或取消点赞
     * @param blog
     * @param user
     * @return
     */
    @Transactional
    @Override
    public Blog updateLiked(Blog blog, User user) {
        Blog blog1=blogRepository.findById(blog.getId()).get();
        List<User> userList=blog1.getLiked();
        boolean liked=true;
        for (User us:userList) {
            if(user.getId().equals(us.getId())){
                user=us;
                liked=false;
            }

        }
        if(liked) {
            userList.add(user);
        }
        else{
            userList.remove(user);
        }
        blog1.setLiked(userList);
        return blogRepository.save(blog1);
    }

    /**
     * 关注或取关
     * @param blog
     * @param user
     * @return
     */
    @Override
    public Blog updateFollow(Blog blog, User user) {
        Blog blog1=blogRepository.findById(blog.getId()).get();
        User buser=blog1.getUser();
        List<User> userList=buser.getFollowed();
        boolean isfollow=true;
        for (User us:userList) {
            if(user.getId().equals(us.getId())){
                user=us;
                isfollow=false;
            }

        }
        if(isfollow) {
            userList.add(user);
        }
        else{
            userList.remove(user);
        }
        buser.setFollowed(userList);
        userRepository.save(buser);
        blog1=blogRepository.findById(blog.getId()).get();
        return blog1;
    }

    @Transactional
    @Override
    public Blog updateBlog(Long id, Blog blog) {
        Blog blog1=blogRepository.findById(id).get();
        if(blog1==null){
            throw new NotFoundException("该博客不存在");
        }
        BeanUtils.copyProperties(blog,blog1);
        return blogRepository.save(blog1);
    }

    @Override
    public Blog allowComment(Long id) {
        Blog blog=blogRepository.findById(id).get();
        blog.setCommentabled(!blog.isCommentabled());
        return blogRepository.save(blog);
    }

    @Transactional
    @Override
    public void deleteBlog(Long id) {
        commentRepository.deleteCommentsByBlogId(id);
        blogRepository.deleteById(id);
    }

    @Override
    public Long countBlog(User user) {
        return blogRepository.countBlogByUser(user);
    }
}
