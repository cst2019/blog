package com.cst.service.serviceImpl;

import com.cst.dao.UserRepository;
import com.cst.po.User;
import com.cst.service.UserService;
import com.cst.util.MD5Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    UserRepository userRepository;
    @Override
    public User checkUser(String username, String password) {

        User user=userRepository.findByUsernameAndPassword(username,password);
        return user;
    }

    @Override
    public User getUser(Long userId) {
        return userRepository.findById(userId).get();
    }

    @Override
    public void updateAvatar(Long id, String avatar) {
       userRepository.updateAvatar(id, avatar);
    }

    @Override
    public Page<User> getFollowed(Long userId, Pageable pageable) {

        return userRepository.findFollowed(userId,pageable);
    }

    /**
     * 取消关注
     * @param userId
     */
    @Override
    public void removeFollowed(Long buserId, Long userId) {
        User buser=userRepository.findById(buserId).get();
        List<User> userList=buser.getFollowed();
        User user=new User();
        for (User us:userList) {
            if(userId.equals(us.getId())){
                user=us;
            }
        }
            userList.remove(user);
        buser.setFollowed(userList);
        userRepository.save(buser);
    }

    /**
     * 新增用户
     * @param user
     * @return
     */
    @Override
    public User addUser(User user) {
        user.setCreateTime(new Date());
        user.setType(1);
        user.setAvatar("/images/avatar.png");
        user.setDes("心情好");
        user.setPassword(MD5Utils.code(user.getPassword()));
        return   userRepository.save(user);
    }

    @Override
    public User existUserByEmail(String email) {
        return userRepository.findByEmail(email);

    }

    @Override
    public User existUserByTelephone(String telephone) {
        return userRepository.findByTelephone(telephone);
    }

    @Override
    public User existUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }
}
