package com.cst.service.serviceImpl;

import com.cst.dao.BlogRepository;
import com.cst.dao.CommentRepository;
import com.cst.dao.UserRepository;
import com.cst.po.Blog;
import com.cst.po.Comment;
import com.cst.po.User;
import com.cst.service.BlogService;
import com.cst.service.CommentService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * @description:
 * @author: cst
 * @date: Created in 2020/1/9 9:45 下午
 * @version:
 * @modified By:
 */
@Service
public class CommentServiceImpl  implements CommentService {

    @Autowired
    CommentRepository commentRepository;
    @Autowired
    BlogRepository blogRepository;
    @Autowired
    UserRepository userRepository;

    @Override
    public List<Comment> listCommentByBlogId(Long blogId) {
        Sort sort=Sort.by(Sort.Direction.ASC,"createTime");
        List<Comment> comments=commentRepository.findByBlogIdAndParentCommentNull(blogId,sort);
        return eachComment(comments);
    }

    @Transactional
    @Override
    public Comment saveComment(Comment comment) {
        Long parentCommentId=comment.getParentComment().getId();
        if(parentCommentId!=-1){
            comment.setParentComment(commentRepository.findById(parentCommentId).get());
        }else{
            comment.setParentComment(null);
        }
        comment.setCreateTime(new Date());
        comment.setNew(true);
        return commentRepository.save(comment);
    }

    /**
     * 获取用户新的消息
     * @param user
     * @return
     */
    @Override
    public List<Comment> getNewCommentByUser(User user) {
        List<Comment> commentList=new ArrayList<>();
        user=userRepository.findById(user.getId()).get();
        //select出该用户的博客下为new且parentComment为null的评论
        List<Blog> blogs = blogRepository.findBlogsByUserId(user.getId());
        for (Blog blog:blogs) {
            List<Comment> comments=blog.getComments();
            for (Comment c:comments) {
                if(c.getParentComment()==null&&c.isNew()){
                       commentList.add(c);

               }
            }
        }
        List<Comment> commentList1=commentRepository.getCommentsByUser(user);
        for (Comment c:commentList1) {
            List<Comment> comments=c.getReplayComments();
            for (Comment cc:comments
                 ) {
           if(cc.isNew()){
                  commentList.add(cc);
               }
            }
        }
        return commentList;
    }

    /**
     * 获取用户的消息
     * @param user
     * @return
     */
    @Override
    public Page<Comment> getCommentByUser(Pageable pageable,User user) {
        List<Comment> commentList=new ArrayList<>();
        user=userRepository.findById(user.getId()).get();
        //select出该用户的博客下为new且parentComment为null的评论
        List<Blog> blogs = blogRepository.findBlogsByUserId(user.getId());
        for (Blog blog:blogs) {
            List<Comment> comments=blog.getComments();
            for (Comment c:comments) {
                if(c.getParentComment()==null) {
                    commentList.add(c);
                }
            }
        }
        List<Comment> commentList1=commentRepository.getCommentsByUser(user);
        for (Comment c:commentList1) {
            List<Comment> comments=c.getReplayComments();
            for (Comment cc:comments
            ) {
                commentList.add(cc);
            }
        }
        Collections.sort(commentList);
        return listConvertToPage(commentList,pageable);
    }
    /**
     * list转pageable
     * @param list
     * @param pageable
     * @param <T>
     * @return
     */
    private  <T> Page<T> listConvertToPage(List<T> list, Pageable pageable) {
        int start = (int)pageable.getOffset();
        int end = (start + pageable.getPageSize()) > list.size() ? list.size() : ( start + pageable.getPageSize());
        return new PageImpl<T>(list.subList(start, end), pageable, list.size());
    }
    @Override
    public void updateNew(Long id) {
       commentRepository.updateNew(id);
    }

    private List<Comment> eachComment(List<Comment> comments){
        List<Comment> commentsView=new ArrayList<>();
        for(Comment comment : comments){
            Comment c=new Comment();
            BeanUtils.copyProperties(comment,c);
            commentsView.add(c);
        }
        //合并评论的各层子代到第一级子代集合中
        combineChildren(commentsView);
        return commentsView;
    }

    private void combineChildren(List<Comment> comments){
        for(Comment comment : comments){
            List<Comment> replys1=comment.getReplayComments();
            for(Comment reply1 : replys1){
            //循环迭代，找出子代，存放在temreplys中
                recursively(reply1);
            }
            //修改顶级节点的reply集合为迭代处理后的集合
            comment.setReplayComments(tempReplys);
            //清除临时存放区
            tempReplys= new ArrayList<>();
        }
    }
            private List<Comment> tempReplys=new ArrayList<>();

    private void recursively(Comment comment){
        tempReplys.add(comment);//顶节点添加到临时存放集合
        if(comment.getReplayComments().size()>0) {
            List<Comment> replys=comment.getReplayComments();
            for(Comment reply : replys){
                tempReplys.add(reply);
                if(reply.getReplayComments().size()>0){
                    recursively(reply);
                }
            }
        }
    }
}
