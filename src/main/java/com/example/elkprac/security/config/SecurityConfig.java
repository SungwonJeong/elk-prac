package com.example.elkprac.security.config;

import com.example.elkprac.security.exception.CustomAccessDeniedHandler;
import com.example.elkprac.security.exception.CustomAuthenticationEntryPoint;
import com.example.elkprac.security.exception.JwtExceptionFilter;
import com.example.elkprac.security.jwt.JwtAuthFilter;
import com.example.elkprac.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true)
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;
    private final JwtExceptionFilter jwtExceptionFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return Argon2PasswordEncoder.defaultsForSpringSecurity_v5_8();
    }

    @Bean
    public MvcRequestMatcher.Builder mvc(HandlerMappingIntrospector handlerMappingIntrospector) {
        return new MvcRequestMatcher.Builder(handlerMappingIntrospector);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity, MvcRequestMatcher.Builder mvc) throws Exception {
        httpSecurity
                .csrf(AbstractHttpConfigurer::disable)
                .cors(c -> c.configurationSource(corsConfigurationSource()));

        httpSecurity.exceptionHandling(e -> {
            e.authenticationEntryPoint(customAuthenticationEntryPoint);
            e.accessDeniedHandler(customAccessDeniedHandler);
        });

        httpSecurity.sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        httpSecurity.authorizeHttpRequests(auth -> auth
                .requestMatchers(mvc.pattern("/auth/**")).permitAll()
                .anyRequest().authenticated()
        );

        httpSecurity.addFilterBefore(new JwtAuthFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class);
        httpSecurity.addFilterBefore(jwtExceptionFilter, JwtAuthFilter.class);
        return httpSecurity.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // 허용할 출처를 명시 (보안 강화: 프로젝트 도메인만 허용)
        configuration.addAllowedOrigin("*");

        // 허용할 HTTP 메소드를 명시 (보안 강화: 필요한 메소드만 허용)
        configuration.addAllowedMethod("GET");
        configuration.addAllowedMethod("POST");
        configuration.addAllowedMethod("PUT");
        configuration.addAllowedMethod("DELETE");

        // 허용할 요청 헤더를 명시 (보안 강화: JWT 관련 필수 헤더만 허용)
        configuration.addAllowedHeader(JwtTokenProvider.ACCESS_TOKEN_HEADER); // AccessToken
        configuration.addAllowedHeader(JwtTokenProvider.REFRESH_TOKEN_HEADER);  // RefreshToken 추가
        configuration.addAllowedHeader("Content-Type");
        configuration.addAllowedHeader("Accept");

        // 브라우저에서 인증 관련 정보들을 요청에 담을 수 있도록 허용
        configuration.setAllowCredentials(true);

        // CORS 구성 정보를 3600초 동안 캐시
        configuration.setMaxAge(3600L);

        // 클라이언트에서 JWT 토큰을 포함한 특정 헤더 접근을 허용
        configuration.addExposedHeader(JwtTokenProvider.ACCESS_TOKEN_HEADER);  // AccessToken 노출
        configuration.addExposedHeader(JwtTokenProvider.REFRESH_TOKEN_HEADER);   // RefreshToken 노출

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
