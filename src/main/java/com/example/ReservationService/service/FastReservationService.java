package com.example.ReservationService.service;

import com.example.ReservationService.dto.FastReservationRequest;
import com.example.ReservationService.dto.FastReservationResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
public class FastReservationService {
    private static final Logger logger = LoggerFactory.getLogger(FastReservationService.class);

    @Autowired
    private RestTemplate restTemplate;

    private static final String PUBLIC_DATA_API_URL = "https://apis.data.go.kr/B552657/HsptlAsembySearchService/getHsptlMdcncListInfoInqire";
    private static final String SERVICE_KEY = "K9t4%2FMS1InyhHxC7oJtTEGncK1mWLav7ML0G5XcgX7k37YyN6sL7owPZDulwsO7m0jyVwvEqeoiFQp3c7C%2BKuQ%3D%3D"; // 인코딩이 된..? rest자체에서
    private static final String NUM_OF_ROWS = "3000";
    private static final String PAGE_NO = "1";


    public List<FastReservationResponse> fastReservation(FastReservationRequest request) { // api 호출 보내기 함수
        DefaultUriBuilderFactory uriBuilderFactory = new DefaultUriBuilderFactory(); // building 하는 요소들 제어
        uriBuilderFactory.setEncodingMode(DefaultUriBuilderFactory.EncodingMode.NONE); // 인코딩 자체를 멈추기 막아버리기
        restTemplate.setUriTemplateHandler(uriBuilderFactory); // 레스트 템플릿 빌딩 잡기

        String apiUrl = buildApiUrl(request);
        logger.info("Constructed API URL: " + apiUrl);
        String response = restTemplate.getForObject(apiUrl.replace("%25","%"), String.class); // 혹시 몰라서 한번 더 25 제거
        String utf8EncodedResponse = new String(response.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8); // 한글 깨지는거 잡기

        logger.info("Response: " + utf8EncodedResponse);

        return parseXmlResponse(utf8EncodedResponse);
    }

    private String buildApiUrl(FastReservationRequest request) { // api 호출 url 만들기

        UriComponents builder = UriComponentsBuilder.fromHttpUrl(PUBLIC_DATA_API_URL)
                .queryParam("pageNo", PAGE_NO)
                .queryParam("numOfRows", NUM_OF_ROWS)
                .queryParam("serviceKey", SERVICE_KEY)
                .encode()
                .build();

        StringBuilder apiUrl = new StringBuilder(builder.toString());


        if (request.getOption() != null && !request.getOption().isEmpty()) {
            apiUrl.append("&QD=").append(encodeValue(request.getOption()));
        }
        if (request.getSelectedCity() != null && !request.getSelectedCity().isEmpty()) {
            apiUrl.append("&Q0=").append(encodeValue(request.getSelectedCity()));
        }
        if (request.getSelectedDistrict() != null && !request.getSelectedDistrict().isEmpty()) {
            apiUrl.append("&Q1=").append(encodeValue(request.getSelectedDistrict()));
        }


        String apiUrlStr = apiUrl.toString();
        logger.info("Final API URL: " + apiUrlStr);
        return apiUrlStr;
    }

    private String encodeValue(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }

    private List<FastReservationResponse> parseXmlResponse(String response) {
        List<FastReservationResponse> fastResponses = new ArrayList<>();
        try {
            XmlMapper xmlMapper = new XmlMapper();
            JsonNode root = xmlMapper.readTree(response.getBytes(StandardCharsets.UTF_8));
            JsonNode items = root.path("body").path("items").path("item");

            if (items.isArray()) {
                for (JsonNode item : items) {
                    FastReservationResponse fastReservationResponse = new FastReservationResponse();
                    fastReservationResponse.setDutyAddr(item.path("dutyAddr").asText());
                    fastReservationResponse.setDutyName(item.path("dutyName").asText());
                    fastReservationResponse.setDutyTel1(item.path("dutyTel1").asText());
                    fastReservationResponse.setDutyTime1c(item.path("dutyTime1c").asText());
                    fastReservationResponse.setDutyTime1s(item.path("dutyTime1s").asText());
                    fastReservationResponse.setWgs84Lat(item.path("wgs84Lat").asText());
                    fastReservationResponse.setWgs84Lon(item.path("wgs84Lon").asText());
                    fastReservationResponse.setHpid(item.path("hpid").asText());
                    fastResponses.add(fastReservationResponse);
                }
            }
        } catch (Exception e) {
            logger.error("Error parsing XML response", e);
        }
        return fastResponses;
    }
}
