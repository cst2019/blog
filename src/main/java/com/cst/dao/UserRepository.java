package com.cst.dao;

import com.cst.po.Blog;
import com.cst.po.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface UserRepository  extends JpaRepository<User,Long> {

    User findByUsernameAndPassword(String username,String password);

    User findByUsername(String username);

    User findByEmail(String email);

    User findByTelephone(String telephone);

    @Transactional
    @Modifying
    @Query("update User u set u.avatar=?2 where u.id=?1")
    int updateAvatar(Long id,String avatar);

    @Query("select u.following from User u where u.id=?1")
    Page<User> findFollowed(Long userId, Pageable pageable);
}
