package com.xz.partnerbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xz.partnerbackend.constant.PwdSalt;
import com.xz.partnerbackend.constant.UserMsgFailedConstant;
import com.xz.partnerbackend.exception.BusinessException;
import com.xz.partnerbackend.mapper.UserMapper;
import com.xz.partnerbackend.model.domain.User;
import com.xz.partnerbackend.model.vo.UserLoginVO;
import com.xz.partnerbackend.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author 96055
 * @description 针对表【user(用户)】的数据库操作Service实现
 * @createDate 2024-03-07 16:32:32
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
        implements UserService {

    @Resource
    private UserMapper userMapper;


    @Override
    public void userRegister(String userAccount, String userPassword, String checkPassword) {

        // 1. 校验
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)) {
            throw new BusinessException(UserMsgFailedConstant.PARAM_EMPTY);
        }
        if (userAccount.length() < 4) {
            throw new BusinessException(UserMsgFailedConstant.PARAM_AC_SHORT);
        }
        if (userPassword.length() < 8 || checkPassword.length() < 8) {
            throw new BusinessException(UserMsgFailedConstant.PARAM_PWD_LONG);
        }
        // 账户不能包含特殊字符
        String validPattern = "[`~!@#$%^&*()+=|{}':;',\\\\[\\\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
        if (matcher.find()) {
            throw new BusinessException(UserMsgFailedConstant.AC_CHAR);
        }
        // 密码和校验密码相同
        if (!userPassword.equals(checkPassword)) {
            throw new BusinessException(UserMsgFailedConstant.PWD_NO_CONSISTENT);
        }
        // 账户不能重复
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(User::getUserAccount, userAccount);
        long count = userMapper.selectCount(queryWrapper);
        if (count > 0) {
            throw new BusinessException(UserMsgFailedConstant.AC_EXIST);
        }

        // 2. 加密
        String encryptPassword = DigestUtils.md5DigestAsHex((PwdSalt.SALT + userPassword).getBytes());
        // 3. 插入数据
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(encryptPassword);
        int insert = userMapper.insert(user);
        if (insert <= 0) {
            throw new BusinessException(UserMsgFailedConstant.SAVE_FAILED);
        }
    }

    @Override
    public User userLogin(String userAccount, String userPassword) {
        // 1. 校验
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            return null;
        }
        if (userAccount.length() < 4) {
            return null;
        }
        if (userPassword.length() < 8) {
            return null;
        }
        // 账户不能包含特殊字符
        String validPattern = "[`~!@#$%^&*()+=|{}':;',\\\\[\\\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
        if (matcher.find()) {
            return null;
        }
        // 2. 加密
        String encryptPassword = DigestUtils.md5DigestAsHex((PwdSalt.SALT + userPassword).getBytes());
        // 查询用户是否存在
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(User::getUserAccount, userAccount);
        queryWrapper.lambda().eq(User::getUserPassword, encryptPassword);
        User user = userMapper.selectOne(queryWrapper);
        // 用户不存在
        if (user == null) {
            log.info("user login failed, userAccount cannot match userPassword");
            return null;
        }

        return user;
    }


    /**
     * 根据标签搜索用户 （内存过滤）
     *
     * @param tageNameList
     * @return
     */
    @Override
    public List<UserLoginVO> searchUserByTags(List<String> tageNameList) {
        if (CollectionUtils.isEmpty(tageNameList)) {
            throw new BusinessException(UserMsgFailedConstant.PARAM_EMPTY);
        }

        // 方法二： 先查，再进行判断
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        List<User> userList = userMapper.selectList(queryWrapper);
        Gson gson = new Gson();
        List<UserLoginVO> newList = new ArrayList<>();
        UserLoginVO userLoginVO = UserLoginVO.builder().build();
        // 在内存中判断是否包含要求标签
        for (User user : userList) {
            String tags = user.getTags();
            Set<String> tempSet = gson.fromJson(tags, new TypeToken<Set<String>>() {
            }.getType());
            tempSet = Optional.ofNullable(tempSet).orElse(new HashSet<>());
            for (String tagName : tageNameList) {
                if (tempSet.contains(tagName)) {
                    BeanUtils.copyProperties(user, userLoginVO);
                    newList.add(userLoginVO);
                }
            }
        }
        return newList;
    }

    /**
     * 根据标签搜索用户 （SQL 版本）
     * @param tageNameList
     * @return
     */
    @Override
    @Deprecated
    public List<UserLoginVO> searchUserByTagsBySQL(List<String> tageNameList) {
        // 方法一： 拼接
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        // 拼接 and 查询
        for (String tagName : tageNameList) {
            queryWrapper = queryWrapper.like(User::getTags, tagName);
        }
        List<User> userList = userMapper.selectList(queryWrapper);
        UserLoginVO userLoginVO = UserLoginVO.builder().build();
        List<UserLoginVO> newList = new ArrayList<>();
        for (User user : userList) {
            BeanUtils.copyProperties(user, userLoginVO);
            newList.add(userLoginVO);
        }
        return newList;
    }
}





