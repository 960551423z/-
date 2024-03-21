package com.xz.partnerbackend.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import com.xz.partnerbackend.utils.SessionUtils;
import com.xz.partnerbackend.utils.UserThread;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author: 阿庆
 * @Date: 2024/3/7 16:38
 * 用户相关接口
 */
@RestController
@RequestMapping("/user")
//@CrossOrigin(origins = "*") // 后端解决跨域问题
public class UserController {

    @Resource
    private UserService userService;



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

        String token = userService.userLogin(userAccount, userPassword, request);

        return Result.success(token);
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

        UserLoginVO userLoginVO = UserThread.getUser();
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


    @PostMapping("/update")
    public Result updateUser(@RequestBody User user, HttpServletRequest request) {
        // 1. 判断参数
        if (user == null) {
            throw new BusinessException(UserMsgFailedConstant.PARAM_EMPTY);
        }

        // 2. 判断是不是管理员 或者 没登录
        if (!SessionUtils.isAdmin(request) || SessionUtils.getLogin(request) == null) {
            throw new BusinessException(UserMsgFailedConstant.NO_LOGIN);
        }

        // 3. 更新
        Integer result = userService.updateUser(user, request);
         return Result.success(result);
    }


    /**
     * 搜索标签
     * @param tagNameList
     * @return
     * @throws JsonProcessingException
     */
    @GetMapping("/search/tags")
    public Result<List<UserLoginVO>> searchUser(@RequestParam(required = false) List<String> tagNameList) throws JsonProcessingException {
        if (CollectionUtils.isEmpty(tagNameList)) {
            throw new BusinessException(UserMsgFailedConstant.PARAM_ERR);
        }

        List<UserLoginVO> userLoginVOS = userService.searchUserByTags(tagNameList);

        return Result.success(userLoginVOS);
    }


    @GetMapping("/recommend")
    public Result<List<UserLoginVO>> recommendUser(HttpServletRequest request) {

        QueryWrapper<User> wrapper = new QueryWrapper<>();
        List<User> list = userService.list(wrapper);
        UserLoginVO userLoginVO;
        List<UserLoginVO> newList = new ArrayList<>();
        for (User user : list) {

            userLoginVO = UserLoginVO.builder().build();
            BeanUtils.copyProperties(user,userLoginVO);
//            ObjectMapper mapper = new ObjectMapper();
            newList.add(userLoginVO);
        }

        return Result.success(newList);
    }

}
