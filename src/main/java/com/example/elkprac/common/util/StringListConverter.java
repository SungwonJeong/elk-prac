package com.example.elkprac.common.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.extern.slf4j.Slf4j;

import java.util.List;


@Slf4j
@Converter
public class StringListConverter implements AttributeConverter<List<String>, String> {

    private static final ObjectMapper objectMapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false)
            .configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, false);

    // DB에 저장될 때 사용: List<String> -> String (JSON)
    @Override
    public String convertToDatabaseColumn(List<String> stringList) {
        try {
            return objectMapper.writeValueAsString(stringList);  // List를 JSON으로 변환
        } catch (JsonProcessingException e) {
            log.error("StringListConverter.convertToDatabaseColumn error: {}", stringList, e);
            throw new RuntimeException("List를 JSON으로 변환할 수 없습니다.", e);
        }
    }

    // DB에서 데이터를 불러올 때 사용: String (JSON) -> List<String>
    @Override
    public List<String> convertToEntityAttribute(String jsonData) {
        try {
            return objectMapper.readValue(jsonData, List.class);  // JSON을 List로 변환
        } catch (JsonProcessingException e) {
            log.error("StringListConverter.convertToEntityAttribute error: {}", jsonData, e);
            throw new RuntimeException("JSON을 List로 변환할 수 없습니다.", e);
        }
    }
}
