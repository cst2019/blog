package com.cst.shiro;

import com.cst.po.User;
import com.cst.service.UserService;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;

public class CustomRealm extends AuthorizingRealm {

    @Autowired
    UserService userService;
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {

        return null;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        UsernamePasswordToken taken= (UsernamePasswordToken) authenticationToken;
        String username=taken.getUsername();
        String password=new String((char[]) taken.getPassword());
        User user=userService.checkUser(username,password);
        if (user == null) {
            throw new AccountException("用户名或密码不正确");
        }
        return new SimpleAuthenticationInfo(username, password,getName());
    }
}
