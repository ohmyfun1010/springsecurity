package com.ohmyfun.csquiz.filter;

import com.ohmyfun.csquiz.dto.CustomUserDetails;
import com.ohmyfun.csquiz.util.JWTUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

//1.SecurityFilterChain 설정: 로그인 요청이 들어오면 LoginFilter 또는 UsernamePasswordAuthenticationFilter가 호출됩니다.
//2.attemptAuthentication(): 사용자의 아이디와 비밀번호를 받아 UsernamePasswordAuthenticationToken 객체에 담습니다.
//3.authenticationManager.authenticate(): 인증 요청을 처리하기 위해 authenticationManager의 authenticate() 메서드를 호출합니다.
//4.UserDetailsService.loadUserByUsername(): DaoAuthenticationProvider가 사용자 정보를 조회하기 위해 UserDetailsService의 loadUserByUsername() 메서드를 호출합니다.
//5.비밀번호 검증: DaoAuthenticationProvider가 조회한 사용자 정보와 입력된 비밀번호를 비교합니다.
//6.인증된 Authentication 객체 생성: 비밀번호 검증이 성공하면 DaoAuthenticationProvider가 인증된 Authentication 객체를 생성합니다.
//7.SecurityContext에 저장: SecurityContextHolder.getContext().setAuthentication(authentication)를 통해 인증 정보를 SecurityContext에 저장합니다.
//8.ThreadLocal 사용: SecurityContext는 ThreadLocal을 사용하여 현재 스레드에 저장되어, 같은 스레드 내에서 인증 정보를 쉽게 접근할 수 있게 합니다.
public class LoginFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    //JWTUtil 주입
    private final JWTUtil jwtUtil;

    public LoginFilter(AuthenticationManager authenticationManager, JWTUtil jwtUtil) {

        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        setFilterProcessesUrl("/api/login"); // 로그인 경로 변경

    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) {

        //클라이언트 요청에서 username, password 추출
        String username = obtainUsername(request);
        String password = obtainPassword(request);

        //스프링 시큐리티에서 username과 password를 검증하기 위해서는 token에 담아야 함
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, password, null);

        //token에 담은 검증을 위한 AuthenticationManager로 전달
        return authenticationManager.authenticate(authToken);
    }

    //로그인 성공시 실행하는 메소드 (여기서 JWT를 발급하면 됨)
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) {
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();

        String username = customUserDetails.getUsername();

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();

        String role = auth.getAuthority();

        String token = jwtUtil.createJwt(username, role, 60*60*10L);

        response.addHeader("Authorization", "Bearer " + token);
        System.out.println("성공");
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        // 인증 실패 처리 로직
        // 예를 들어, 실패한 이유를 로그에 기록하거나, 에러 메시지를 클라이언트로 반환할 수 있음.
        System.out.println("로그인 실패");
        response.setStatus(401);
    }
}
