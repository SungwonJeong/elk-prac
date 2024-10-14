package com.example.elkprac.security.jwt;

import com.example.elkprac.common.message.ErrorMessage;
import com.example.elkprac.security.exception.JwtValidationException;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Set;
import java.util.regex.Pattern;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    private static final Set<String> PUBLIC_URIS = Set.of(
            "/auth/signup",
            "/auth/login"
    );
    private static final Pattern AUTH_REQUIRED_URIS = Pattern.compile(
            "^/(product).*"
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        logRequestHeaders(request);

        String requestURI = request.getRequestURI();
        String accessToken = jwtTokenProvider.resolveToken(request, JwtTokenProvider.ACCESS_TOKEN_HEADER);

        if (isPublicUri(requestURI)) {
            filterChain.doFilter(request, response);
            return;
        }

        if (requiresAuthentication(requestURI, accessToken)) {
            authenticateUser(accessToken);
            filterChain.doFilter(request, response);
        } else {
            throw new JwtValidationException(ErrorMessage.TOKEN_ERROR);
        }
    }

    private void logRequestHeaders(HttpServletRequest request) {
        Enumeration<String> headers = request.getHeaderNames();
        while (headers.hasMoreElements()) {
            String headerName = headers.nextElement();
            log.info("Header: {} = {}", headerName, request.getHeader(headerName));
        }
        log.info("Request received. URI: {}, Method: {}, IP: {}", request.getRequestURI(), request.getMethod(), request.getRemoteAddr());
    }

    private boolean isPublicUri(String uri) {
        return PUBLIC_URIS.contains(uri);
    }

    private boolean requiresAuthentication(String uri, String accessToken) {
        if (AUTH_REQUIRED_URIS.matcher(uri).matches()) {
            return jwtTokenProvider.validateToken(accessToken, true);
        }
        return false;
    }

    private void authenticateUser(String accessToken) {
        Claims info = jwtTokenProvider.getCustomerInfoFromAccessToken(accessToken);
        setAuthentication(info.getSubject());
    }

    private void setAuthentication(String username) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        Authentication authentication = jwtTokenProvider.createAuthentication(username);
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
    }
}
