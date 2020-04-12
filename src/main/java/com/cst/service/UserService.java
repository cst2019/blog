package com.cst.service;

import com.cst.po.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {

    User checkUser(String username,String password);

    User getUser(Long userId);

    void updateAvatar(Long id,String avatar);

    Page<User> getFollowed(Long userId, Pageable pageable);

    void removeFollowed(Long buserId,Long userId);

    User addUser(User user);

    User existUserByEmail(String email);

    User existUserByTelephone(String telephone);

    User existUserByUsername(String username);
}
