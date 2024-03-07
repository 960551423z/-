package com.xz.partnerbackend.exception;

import com.xz.partnerbackend.common.ErrorCode;

/**
 * @Author: 阿庆
 * @Date: 2024/3/7 16:47
 * 基础异常处理
 */

public class BusinessException extends RuntimeException {
    public BusinessException() {
    }

    public BusinessException(String msg) {
        super(msg);
    }

}
