package com.example.demo.filter;

import com.example.demo.util.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // 1. 从请求头中获取 Token
        String authHeader = request.getHeader("Authorization");

        // 2. 如果没有 Token，或者格式不对，直接放行（让后面的过滤器处理）
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 3. 提取 Token（去掉 "Bearer " 前缀）
        String token = authHeader.substring(7);

        // 4. 验证 Token
        try {
            if (jwtUtil.validateToken(token)) {
                // 5. 从 Token 中解析用户信息
                Claims claims = jwtUtil.parseToken(token);
                Long userId = Long.parseLong(claims.getSubject());
                String username = claims.get("username", String.class);

                // 6. 创建认证对象，存入 SecurityContext
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                username,           // principal：用户名
                                null,               // credentials：不需要密码
                                Collections.emptyList()  // authorities：权限（暂时为空）
                        );
                authentication.setDetails(userId);  // 把用户ID放到 details 中

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception e) {
            // Token 无效，不设置认证信息，后续会被拦截
            logger.warn("JWT Token 验证失败：" + e.getMessage());
        }

        // 7. 继续执行后续过滤器
        filterChain.doFilter(request, response);
    }
}
