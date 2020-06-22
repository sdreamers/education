package com.sixing.base.utils.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value= HttpStatus.UNAUTHORIZED,reason="用户不匹配")
public class UserNotMatchException extends RuntimeException{
}
