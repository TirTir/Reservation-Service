package com.example.ReservationService.dto;

import lombok.Data;

import java.util.List;

@Data
public class AvailableTimes {
    private List<String> monday;
    private List<String> tuesday;
    private List<String> wednesday;
    private List<String> thursday;
    private List<String> friday;
    private List<String> saturday;
    private List<String> sunday;
    private List<String> holiday;

    private String hpid; // 병원 코드
    private String dutyName; // 병원명
    private String dutyAddr; // 주소
    private String dutyTel1; // 대표전화
    private String dutyTel3; // 응급실 전화
    private String dgidIdName; // 진료과목

    private String wgs84Lat; // 위도
    private String wgs84Lon; // 경도
}
