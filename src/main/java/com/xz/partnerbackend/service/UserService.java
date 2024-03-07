package com.xz.partnerbackend.service;

import com.xz.partnerbackend.model.domain.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xz.partnerbackend.model.vo.UserLoginVO;

/**
* @author 96055
* @description 针对表【user(用户)】的数据库操作Service
* @createDate 2024-03-07 16:32:32
*/
public interface UserService extends IService<User> {

    void userRegister(String userAccount, String userPassword, String checkPassword);

    User userLogin(String userAccount, String userPassword);
}
