package com.xz.partnerbackend.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.xz.partnerbackend.common.Result;
import com.xz.partnerbackend.constant.SessionConstant;
import com.xz.partnerbackend.constant.UserMsgFailedConstant;
import com.xz.partnerbackend.exception.BusinessException;
import com.xz.partnerbackend.model.domain.User;
import com.xz.partnerbackend.model.dto.UserLoginRequest;
import com.xz.partnerbackend.model.dto.UserRegisterRequest;
import com.xz.partnerbackend.model.vo.UserLoginVO;
import com.xz.partnerbackend.properties.JwtProperties;
import com.xz.partnerbackend.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @Author: 阿庆
 * @Date: 2024/3/7 16:38
 * 用户相关接口
 */
@RestController
@RequestMapping("/user")
@CrossOrigin(origins = {"http://localhost:5173"}) // 后端解决跨域问题
public class UserController {

    @Resource
    private UserService userService;


    @Resource
    private JwtProperties jwtProperties;


    /**
     * 用户注册
     *
     * @param userRegisterRequest
     * @return
     */
    @PostMapping("/register")
    public Result userRegister(@RequestBody UserRegisterRequest userRegisterRequest) {
        // 校验
        if (userRegisterRequest == null) {
            throw new BusinessException(UserMsgFailedConstant.PARAM_EMPTY);
        }
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)) {
            return null;
        }
        userService.userRegister(userAccount, userPassword, checkPassword);
        return Result.success();
    }


    /**
     * 用户登录
     *
     * @param userLoginRequest
     * @return
     */
    @PostMapping("/login")
    public Result<UserLoginVO> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {
        if (userLoginRequest == null) {
            return Result.error(UserMsgFailedConstant.PARAM_EMPTY);
        }
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            return Result.error(UserMsgFailedConstant.PARAM_EMPTY);
        }

        UserLoginVO userLoginVO = userService.userLogin(userAccount, userPassword, request);

        return Result.success(userLoginVO);
    }


    /**
     * 获取当前用户
     *
     * @param request
     * @return
     *  todo: 登录了之后存储到session，就感觉不太需要这个了，先写着，后期看能不能复用
     */
    @GetMapping("/current")
    public Result<UserLoginVO> getCurrentUser(HttpServletRequest request) {
        Object userObj = request.getSession().getAttribute(SessionConstant.USER_LOGIN_STATE);
        UserLoginVO currentUser = (UserLoginVO) userObj;
        if (currentUser == null) {
            throw new BusinessException(UserMsgFailedConstant.NO_LOGIN);
        }
        long userId = currentUser.getId();
        // 获取当前用户
        User user = userService.getById(userId);
        UserLoginVO userLoginVO = UserLoginVO.builder().build();
        BeanUtils.copyProperties(user,userLoginVO);
        return Result.success(userLoginVO);
    }

    /**
     * 用户注销
     *
     * @param request
     * @return
     */
    @PostMapping("/logout")
    public Result<Integer> userLogout(HttpServletRequest request) {
        if (request == null) {
            throw new BusinessException(UserMsgFailedConstant.PARAM_EMPTY);
        }
        int result = userService.userLogout(request);
        return Result.success(result);
    }




    @GetMapping("/search/tags")
    public Result<List<UserLoginVO>> searchUser(@RequestParam(required = false) List<String> tagNameList) throws JsonProcessingException {
        if (CollectionUtils.isEmpty(tagNameList)) {
            throw new BusinessException(UserMsgFailedConstant.PARAM_ERR);
        }

        List<UserLoginVO> userLoginVOS = userService.searchUserByTags(tagNameList);

        return Result.success(userLoginVOS);
    }

}
