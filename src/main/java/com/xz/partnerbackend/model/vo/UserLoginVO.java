package com.xz.partnerbackend.model.vo;


import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @Author: 阿庆
 * @Date: 2024/3/7 17:17
 * 返回给前端的数据格式
 */
@Data
@Builder
public class UserLoginVO implements Serializable {
    /**
     * id
     */
    private long id;

    /**
     * 用户昵称
     */
    private String username;

    /**
     * 账号
     */
    private String userAccount;

    /**
     * 用户头像
     */
    private String avatarUrl;

    /**
     * 性别
     */
    private Integer gender;


    /**
     * 手机号
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 状态 0-正常
     */
    private Integer userStatus;


    /**
     * 用户角色 0-普通用户  1-管理员
     */
    private Integer userRose;



    /**
     * tags
     */
    private List<String> tags;

    /**
     * 简介
     */
    private String profile;

    /**
     * 创建时间
     */
    private Date createTime;

}
