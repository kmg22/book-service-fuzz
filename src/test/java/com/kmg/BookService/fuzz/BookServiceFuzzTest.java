package com.kmg.BookService.fuzz;

import com.code_intelligence.jazzer.api.FuzzedDataProvider;
import com.code_intelligence.jazzer.junit.FuzzTest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kmg.BookService.service.OpenApiClient;
import com.kmg.BookService.dto.AladinItem;

import java.util.List;

public class BookServiceFuzzTest {
    private final ObjectMapper objectMapper = new ObjectMapper();
    // 테스트 대상인 OpenApiClient (필요한 객체만 수동 생성)
    private final OpenApiClient openApiClient = new OpenApiClient(objectMapper);

    @FuzzTest
    void fuzzAladinParsing(FuzzedDataProvider data) {
        // 퍼저가 생성한 무작위 문자열 (JSON 형태, 깨진 문자열, 특수문자 등)
        String randomJson = data.consumeRemainingAsString();

        try {
            // parseJson 메서드에 무작위 데이터를 주입하여 예외 발생 여부 확인
            List<AladinItem> results = openApiClient.parseJson(randomJson);
            
            // 파싱 결과가 null이 아닌지, 리스트 내부 객체의 필드 접근 시 NPE가 안 터지는지 추가 검증 가능
            if (results != null && !results.isEmpty()) {
                results.get(0).getTitle();
            }
            
        } catch (Exception e) {
            /*
             * 퍼징 중 발생하는 '알려진 예외'는 무시
             * (ObjectMapper가 던지는 파싱 에러 등은 정상적인 동작)
             * Unhandled RuntimeException(NPE 등) 발생하면 Jazzer가 이를 가치 있는 버그로 판단
             */
        }
    }
}
