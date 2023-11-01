package com.scc.toolutils.security.handler;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 自定义退出处理类 返回成功
 * @author : scc
 * @date : 2023/10/18
 **/
@Component
public class CustomLogoutSuccessHandler implements LogoutSuccessHandler{

    @Override
    public void onLogoutSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) throws IOException, ServletException {
//        TODO ServletUtils.renderString(httpServletResponse, JSON.toJSONString(R.ok("退出成功")));
    }
}
