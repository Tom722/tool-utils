package com.scc.toolutils.security.handler;

import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 当未登录或者token失效访问接口时，自定义的返回结果
 * @author : scc
 * @date : 2023/10/18
 **/
@Component
@Slf4j
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthenticationException e) throws IOException, ServletException {
        log.info("请求地址："+ httpServletRequest.getRequestURI());
        httpServletResponse.setCharacterEncoding("UTF-8");
        httpServletResponse.setContentType("application/json");
//       TODO httpServletResponse.getWriter().println(JSONUtil.parse(R.error(ResultCodeEnum.UNAUTHORIZED.getCode(),ResultCodeEnum.UNAUTHORIZED.getMessage())));
        httpServletResponse.getWriter().flush();
    }
}
