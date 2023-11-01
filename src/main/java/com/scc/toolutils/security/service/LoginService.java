package com.scc.toolutils.security.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;

@Slf4j
@Service
public class LoginService {
    @Resource
    private PasswordEncoder passwordEncoder;
    @Resource
    private UserDetailsService userDetailsService;

    @Value("${jwt.expiration}")
    private String tokenExpirationTime;


    // TODO 自己调试实现
//    public void login(String username, String password) {
//        try{
//            UserDetails userDetails = userDetailsService.loadUserByUsername(userName);
//            if (!passwordEncoder.matches(password, userDetails.getPassword())) {
//                return null;
//            }
//            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
//            SecurityContextHolder.getContext().setAuthentication(authentication);
//            JwtUser jwtUser = (JwtUser) authentication.getPrincipal();
//            // 设置在线状态
//            ChatUser userByCode = chatUserRepository.findByUserId(jwtUser.getUser().getUserId());
//            if(userByCode != null){
//                userByCode.setActive(2);
//                userByCode.setActiveTime(LocalDateTime.now());
//                chatUserRepository.saveAndFlush(userByCode);
//            }
//            String token = JwtTokenUtil.createToken(jwtUser);
//            LoginVo result = new LoginVo();
//            result.setToken(token);
//            result.setExpirationTime(tokenExpirationTime);
//            return R.ok("用户登录成功", result);
//        }catch (AuthenticationException e){
//            log.error("登录失败：{}", e.getMessage());
//            return R.error("用户名或密码错误");
//        }
//    }


}
