package com.example.elkprac.common.aop;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class ReqResLogging {

    @JsonProperty("traceId")
    private String traceId;           // 추적 ID

    @JsonProperty("class")
    private String className;         // 클래스 이름

    @JsonProperty("http_method")
    private String httpMethod;        // HTTP 메서드 (GET, POST 등)

    @JsonProperty("uri")
    private String uri;               // 요청된 URI

    @JsonProperty("method")
    private String method;            // 메서드 이름

    /* 이 부분은 쿼리스트링일 경우에 사용 */
    @JsonProperty("params")
    private Map<String, String> params; // 요청 파라미터 (String -> Object로 수정)

    @JsonProperty("log_time")
    private String logTime;           // 로그 시간

    @JsonProperty("server_ip")
    private String serverIp;          // 서버 IP 주소

    @JsonProperty("device_type")
    private String deviceType;        // 장치 타입 (모바일, 데스크탑 등)

    @JsonProperty("request_body")
    private Object requestBody;       // 요청 바디

    @JsonProperty("response_body")
    private Object responseBody;      // 응답 바디

    @JsonProperty("elapsed_time")
    private String elapsedTime;       // 처리 시간
}

