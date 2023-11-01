package com.scc.toolutils.security.handler;

import cn.hutool.http.HttpStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 全局异常处理类
 * @author : scc
 * @date : 2023/10/18
 **/
@ControllerAdvice
@ResponseBody
@Slf4j
public class GlobalExceptionHandler {
    // TODO 切换为自己的返回对象
//    @ExceptionHandler(Exception.class)
//    public R baseExceptionHandler(HttpServletResponse response, Exception e) {
//        log.error(e.getMessage(), e);
//        response.setStatus(HttpStatus.HTTP_INTERNAL_ERROR);
//        return R.error("服务器异常，请稍后再试！，原因："+e.getMessage());
//    }
//
//    /**
//     * 处理AccessDeineHandler无权限异常
//     * @param req
//     * @param e
//     * @return
//     */
//    @ExceptionHandler(value = AccessDeniedException.class)
//    public R exceptionHandler(HttpServletRequest req, AccessDeniedException e){
//        log.error("不允许访问！原因是:",e.getMessage());
//        return R.error(ResultCodeEnum.FORBIDDEN.getCode(),ResultCodeEnum.FORBIDDEN.getMessage());
//    }

}
