package com.example.hanaro.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtCheckFilter extends OncePerRequestFilter {
    private final JwtTokenProvider jwtTokenProvider;

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
            // "Bearer " 접두사를 제거하여 순수 토큰만 추출
            String token = authHeader.substring(7);

            // validateToken을 호출하여 토큰 검증 및 claims 추출
            Map<String, Object> claims = jwtTokenProvider.validateToken(token);

            // claims에서 사용자 이메일과 권한 정보 추출
            String email = (String) claims.get("email"); // 토큰 생성 시 넣었던 key 값
            List<String> roles = (List<String>) claims.get("role"); // 토큰 생성 시 넣었던 key 값과 일치하도록 수정생성 시 넣었던 key 값

            // 추출한 권한 정보(String 리스트)를 Spring Security가 사용하는 GrantedAuthority 리스트로 변환
            List<SimpleGrantedAuthority> authorities = roles.stream()
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());

            //생성된 claims와 권한 정보를 바탕으로 인증 객체(Authentication) 생성
            Authentication authentication = new UsernamePasswordAuthenticationToken(email, null, authorities);

            //SecurityContext에 인증 정보 저장
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
