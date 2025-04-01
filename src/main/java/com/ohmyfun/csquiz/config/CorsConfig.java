package com.ohmyfun.csquiz.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

//security 에서 cors 설정하기 떄문에 빈 등록 제거
//@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        // CORS 설정 객체
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.addAllowedOrigin("http://localhost:3000"); // 모든 오리진 허용 (보안을 위해 구체적으로 설정하는 것이 좋음) -> * 사용불가
        corsConfiguration.addAllowedMethod("*"); // 모든 HTTP 메소드 허용
        corsConfiguration.addAllowedHeader("*"); // 모든 헤더 허용
        corsConfiguration.setAllowCredentials(true); // 쿠키 포함 허용

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration); // 모든 엔드포인트에 대해 CORS 설정

        return new CorsFilter(source);
    }
}