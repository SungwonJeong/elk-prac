package com.example.elkprac.common.aop;

import com.example.elkprac.common.exception.CustomException;
import com.example.elkprac.common.message.ErrorMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class ApiReqResFilter extends OncePerRequestFilter {

    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String traceId = UUID.randomUUID().toString(); // traceId 생성
        try {
            CachedBodyHttpServletRequest wrappedRequest = new CachedBodyHttpServletRequest(request);
            wrappedRequest.setAttribute("traceId", traceId);
            filterChain.doFilter(wrappedRequest, response);


        } catch (Exception e) {
            log.error("Error occurred with traceId {}: {}", traceId, e.getMessage(), e); // 스택 트레이스도 포함하여 로그 기록

            // 응답 설정
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            CustomException customException = new CustomException(ErrorMessage.SYSTEM_ERROR, traceId);

            // 응답에 CustomException 출력 및 flush 호출
            response.getWriter().write(objectMapper.writeValueAsString(customException));
            response.getWriter().flush();
        }
    }
}
