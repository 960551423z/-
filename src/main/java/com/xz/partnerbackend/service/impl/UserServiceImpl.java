package com.xz.partnerbackend.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.UUID;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xz.partnerbackend.constant.PwdSalt;
import com.xz.partnerbackend.constant.SessionConstant;
import com.xz.partnerbackend.constant.UserMsgFailedConstant;
import com.xz.partnerbackend.exception.BusinessException;
import com.xz.partnerbackend.mapper.UserMapper;
import com.xz.partnerbackend.model.domain.User;
import com.xz.partnerbackend.model.vo.UserLoginVO;
import com.xz.partnerbackend.service.UserService;
import com.xz.partnerbackend.utils.SessionUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import static com.xz.partnerbackend.constant.RedisConstant.LOGIN;
import static com.xz.partnerbackend.constant.RedisConstant.LOGIN_EXPIRE;

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

    @Resource
    private RedisTemplate<String, Object> redisTemplate;



    @Override
    public void userRegister(String userAccount, String userPassword, String checkPassword) {

        // 1. 校验
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)) {
            throw new BusinessException(UserMsgFailedConstant.PARAM_EMPTY);
        }
        if (userAccount.length() < 4) {
            throw new BusinessException(UserMsgFailedConstant.PARAM_AC_SHORT);
        }
        // todo: 测试阶段（4位密码，后面更新为8位）
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
    public String userLogin(String userAccount, String userPassword, HttpServletRequest request) {
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
            throw new BusinessException("user login failed, userAccount cannot match userPassword");
        }

        // 脱敏

        // todo: UserLoginVO 中的 tags 是list，user 中是 String，拷贝时会丢失，后期解决（也不需要解决，登录也不一定要展示标签，展示的时候再解决）
        UserLoginVO userLoginVO = UserLoginVO.builder().build();
        BeanUtils.copyProperties(user, userLoginVO);


        // 存储Session
        // 存储到 redis 中
        // 1. 生成uuid，作为登录的token
        String token = UUID.randomUUID().toString(true);
        // 2. 将UserLoginVO 转为对象
        Map<String, Object> userLoginVOMap = BeanUtil.beanToMap(userLoginVO);

        //3.设置键
        String tokenKey = LOGIN + token;
        redisTemplate.opsForHash().putAll(tokenKey,userLoginVOMap);

        //4.设置过期时间
        redisTemplate.expire(tokenKey,LOGIN_EXPIRE, TimeUnit.MINUTES);

////        request.getSession().setAttribute(SessionConstant.USER_LOGIN_STATE,userLoginVO);
//        // todo: 考虑拦截时候请求还是这个时候请求
//        UserThread.saveUser(userLoginVO);
        return token;
    }


    /**
     * 根据标签搜索用户 （内存过滤）
     *
     * @param tageNameList
     * @return
     */
    @Override
    public List<UserLoginVO> searchUserByTags(List<String> tageNameList) throws JsonProcessingException {
        if (CollectionUtils.isEmpty(tageNameList)) {
            throw new BusinessException(UserMsgFailedConstant.PARAM_EMPTY);
        }

        // 方法二： 先查，再进行判断
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        List<User> userList = userMapper.selectList(queryWrapper);
        Gson gson = new Gson();
        List<UserLoginVO> newList = new ArrayList<>();
        UserLoginVO userLoginVO;
        // 在内存中判断是否包含要求标签
        for (User user : userList) {
            String tags = user.getTags();
            Set<String> tempSet = gson.fromJson(tags, new TypeToken<Set<String>>() {
            }.getType());
            tempSet = Optional.ofNullable(tempSet).orElse(new HashSet<>());
            for (String tagName : tageNameList) {
                if (tempSet.contains(tagName)) {
                    userLoginVO = UserLoginVO.builder().build();
                    BeanUtils.copyProperties(user, userLoginVO);
                    ObjectMapper mapper = new ObjectMapper();
                    List<String> list = mapper.readValue(tags, new TypeReference<List<String>>(){});
                    userLoginVO.setTags(list);
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

    @Override
    public int userLogout(HttpServletRequest request) {
        request.getSession().removeAttribute(SessionConstant.USER_LOGIN_STATE);
        return 1;
    }

    @Override
    public Integer updateUser(User user, HttpServletRequest request) {
        // 仅管理员和自己能修改

        // 1. 管理员修改
        long userId = user.getId();
        if (userId <= 0)
            throw new BusinessException(UserMsgFailedConstant.PARAM_ERR);


        // 如果不是管理员(并且也不是自己本身，则抛异常)
        if (!SessionUtils.isAdmin(request) && userId != SessionUtils.AdminAndInfo(request).getId()) {
            throw new BusinessException(UserMsgFailedConstant.NO_AUTH);
        }

        // 是管理员或者是自己本身，则可以进行修改
        User oldUser = userMapper.selectById(user);
        if (oldUser == null)
            throw new BusinessException(UserMsgFailedConstant.NULL_ERR);
        return userMapper.updateById(user);
    }
}





