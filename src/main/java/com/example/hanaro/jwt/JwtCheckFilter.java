package com.example.hanaro.jwt;

import com.example.hanaro.service.CustomUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtCheckFilter extends OncePerRequestFilter {
    private final JwtTokenProvider jwtTokenProvider;
    private final CustomUserDetailsService customUserDetailsService;

    @Override
    protected boolean shouldNotFilter(@NonNull HttpServletRequest request) {
        // Preflight 요청은 체크하지 않음
        return request.getMethod().equals("OPTIONS");
    }
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");

        // Authorization 헤더가 없거나 "Bearer "로 시작하지 않으면 필터를 통과시킴
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String token = authHeader.substring(7);
            Map<String, Object> claims = jwtTokenProvider.validateToken(token);
            String email = (String) claims.get("email");

            UserDetails userDetails = customUserDetailsService.loadUserByUsername(email);
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities());

            SecurityContextHolder.getContext().setAuthentication(authentication);


        } catch (Exception e) {
            // 토큰 검증 실패 시 (유효기간 만료, 서명 불일치 등)
            log.error("Could not set user authentication in security context", e);
            // 명시적으로 SecurityContext를 비워줌
            SecurityContextHolder.clearContext();
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Invalid or Expired JWT Token");
            return;
        }

        filterChain.doFilter(request, response);
    }
}
