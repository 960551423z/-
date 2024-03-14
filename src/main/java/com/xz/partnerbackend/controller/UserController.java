package com.xz.partnerbackend.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xz.partnerbackend.common.Result;
import com.xz.partnerbackend.constant.JwtClaimsConstant;
import com.xz.partnerbackend.constant.UserMsgFailedConstant;
import com.xz.partnerbackend.exception.BusinessException;
import com.xz.partnerbackend.model.domain.User;
import com.xz.partnerbackend.model.dto.UserLoginRequest;
import com.xz.partnerbackend.model.dto.UserRegisterRequest;
import com.xz.partnerbackend.model.vo.UserLoginVO;
import com.xz.partnerbackend.properties.JwtProperties;
import com.xz.partnerbackend.service.UserService;
import com.xz.partnerbackend.utils.JwtUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public Result<UserLoginVO> userLogin(@RequestBody UserLoginRequest userLoginRequest) {
        if (userLoginRequest == null) {
            return Result.error(UserMsgFailedConstant.PARAM_EMPTY);
        }
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            return Result.error(UserMsgFailedConstant.PARAM_EMPTY);
        }

        User user = userService.userLogin(userAccount, userPassword);
        //登录成功后，生成jwt令牌
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.USER_ID, user.getId());
        String token = JwtUtil.createJWT(
                jwtProperties.getAdminSecretKey(),
                jwtProperties.getAdminTtl(),
                claims);
        UserLoginVO userLoginVO = UserLoginVO.builder().build();
        BeanUtils.copyProperties(user, userLoginVO);
        userLoginVO.setToken(token);

        return Result.success(userLoginVO);
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
