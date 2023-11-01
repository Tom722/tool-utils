package com.scc.toolutils.security.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * JWT登录授权过滤器
 * @author : scc
 * @date : 2023/10/18
 **/
@Slf4j
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {

    @Value("${jwt.tokenHeader}")
    private String tokenHeader;
    @Value("${jwt.tokenHead}")
    private String tokenHead;

    @Autowired
    private UserDetailsService userDetailsService;


    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
// TODO 切换为自己的导入的包
        //        String authHeader = httpServletRequest.getHeader(this.tokenHeader);
//        String requestURI = httpServletRequest.getRequestURI();
//        if(requestURI.contains("/login") || requestURI.contains("/register")){
//            filterChain.doFilter(httpServletRequest, httpServletResponse);
//            return;
//        }
//        if (StringUtils.isNotBlank(authHeader) && authHeader.startsWith(this.tokenHead)) {
//            String authToken = authHeader.substring(this.tokenHead.length());
//            String username = JwtTokenUtil.getUserNameFromToken(authToken);
//            log.info("checking authentication " + username);
//            if (StringUtils.isNotBlank(username) && SecurityContextHolder.getContext().getAuthentication() == null) {
//                // 再次校验账号是否已被禁用或不存在
//                UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
//                // 校验token
//                if (JwtTokenUtil.validateToken(authToken, userDetails)) {
//                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
//                            userDetails, null, userDetails.getAuthorities());
//                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(
//                            httpServletRequest));
//                    log.info("authenticated user " + username + ", setting security context");
//                    SecurityContextHolder.getContext().setAuthentication(authentication);
//                }
//            }
//        }
        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }
}
