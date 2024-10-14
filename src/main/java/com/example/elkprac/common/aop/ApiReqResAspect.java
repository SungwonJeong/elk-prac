package com.example.elkprac.common.aop;

import com.example.elkprac.common.exception.CustomException;
import com.example.elkprac.common.message.ErrorMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.net.InetAddress;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
@Aspect
@RequiredArgsConstructor
public class ApiReqResAspect {

    private final ObjectMapper objectMapper;

    // RestController에만 적용되도록 포인트컷 범위를 축소
    @Pointcut("@within(org.springframework.web.bind.annotation.RestController)")
    public void restControllerPointCut() {}

    @Around("restControllerPointCut()")
    public Object logRequestAndResponse(ProceedingJoinPoint joinPoint) throws Throwable {
        HttpServletRequest request = getCurrentHttpRequest();
        String traceId = getTraceId(request);

        // 요청 정보 생성
        ReqResLogging logging = createOrUpdateLog(null, joinPoint, request, traceId, null, null);
        long startTime = System.currentTimeMillis(); // 시작 시간 기록

        try {
            // 메서드 실행 및 성공 로깅
            Object result = joinPoint.proceed();
            return logSuccess(logging, result, joinPoint, request, traceId, startTime);
        } catch (CustomException e) {
            // 중복된 이름과 같은 CustomException을 그대로 던집니다
            logError(logging, joinPoint, request, traceId, startTime, e);
            throw e; // SYSTEM_ERROR로 재발생하지 않고 원래의 예외를 그대로 던짐
        } catch (Exception e) {
            // 에러 발생 시 로깅
            logError(logging, joinPoint, request, traceId, startTime, e);
            throw new CustomException(ErrorMessage.SYSTEM_ERROR, traceId); // 에러 재발생
        }
    }

    // 현재 HTTP 요청을 얻어오는 메서드
    private HttpServletRequest getCurrentHttpRequest() {
        return ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
    }

    // 요청에서 traceId를 가져오는 메서드
    private String getTraceId(HttpServletRequest request) {
        return (String) request.getAttribute("traceId");
    }

    // 요청 정보 생성 또는 업데이트 메서드
    private ReqResLogging createOrUpdateLog(ReqResLogging logging, ProceedingJoinPoint joinPoint, HttpServletRequest request, String traceId, Object responseBody, String elapsedTime) throws Exception {
        String className = joinPoint.getSignature().getDeclaringTypeName();
        String methodName = joinPoint.getSignature().getName();
        Map<String, String> params = getRequestParams(request); // 점(.)을 변환하는 메서드 사용
        String deviceType = request.getHeader("x-custom-device-type");
        String serverIp = InetAddress.getLocalHost().getHostAddress();

        if (logging == null) {
            // 최초 객체 생성
            return ReqResLogging.builder()
                    .traceId(traceId)
                    .className(className)
                    .httpMethod(request.getMethod())
                    .uri(request.getRequestURI())
                    .method(methodName)
                    .params(params)
                    .logTime(LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME)) // 최초 생성 시 logTime 설정
                    .serverIp(serverIp)
                    .deviceType(deviceType)
                    .requestBody(objectMapper.readTree(request.getInputStream().readAllBytes())) // 요청 본문 설정
                    .responseBody(responseBody) // 초기값은 null일 수 있음
                    .elapsedTime(elapsedTime) // 초기값은 null일 수 있음
                    .build();
        }

        // 객체 업데이트
        return ReqResLogging.builder()
                .traceId(traceId)
                .className(className)
                .httpMethod(request.getMethod())
                .uri(request.getRequestURI())
                .method(methodName)
                .params(params)
                .logTime(logging.getLogTime()) // 기존 logTime 유지
                .serverIp(serverIp)
                .deviceType(deviceType)
                .requestBody(logging.getRequestBody()) // 기존 requestBody 유지
                .responseBody(responseBody != null ? responseBody : logging.getResponseBody()) // responseBody 업데이트
                .elapsedTime(elapsedTime != null ? elapsedTime : logging.getElapsedTime()) // elapsedTime 업데이트
                .build();
    }

    // 성공적인 요청과 응답 로깅
    private Object logSuccess(ReqResLogging logging, Object result, ProceedingJoinPoint joinPoint, HttpServletRequest request, String traceId, long startTime) throws Exception {
        long elapsedTime = System.currentTimeMillis() - startTime; // 경과 시간 계산
        String elapsedTimeStr = elapsedTime + "ms";

        // 성공 로그 업데이트
        logging = createOrUpdateLog(logging, joinPoint, request, traceId, result instanceof ResponseEntity<?> ? ((ResponseEntity<?>) result).getBody() : "{}", elapsedTimeStr);

        // JSON 형식으로 성공 로그 기록
        log.info(objectMapper.writeValueAsString(logging));

        return result; // 메서드의 결과 반환
    }

    // 에러 발생 시 로깅, JSON 형식 및 시간 기록 추가
    private void logError(ReqResLogging logging, ProceedingJoinPoint joinPoint, HttpServletRequest request, String traceId, long startTime, Exception e) throws Exception {
        long elapsedTime = System.currentTimeMillis() - startTime; // 에러 발생 시 경과 시간 계산
        String elapsedTimeStr = elapsedTime + "ms";

        // 에러 로그 업데이트
        logging = createOrUpdateLog(logging, joinPoint, request, traceId, "Error: " + e.getMessage(), elapsedTimeStr);

        // JSON으로 에러 로그 기록
        log.error("Exception occurred: {}", objectMapper.writeValueAsString(logging), e);
    }

    // 점(.)을 대시(-)로 변환하여 요청 파라미터 추출
    private Map<String, String> getRequestParams(HttpServletRequest request) {
        return request.getParameterMap().entrySet().stream()
                .collect(Collectors.toMap(
                        entry -> entry.getKey().replace(".", "-"), // 점(.)을 대시(-)로 변환
                        entry -> String.join(",", entry.getValue()) // 배열을 ','로 연결
                ));
    }
}
