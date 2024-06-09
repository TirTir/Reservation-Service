package com.example.ReservationService.dto;

import lombok.Data;

@Data
public class DutyTimes {
    private String dutyTime1c; // 월요일 종료 시간
    private String dutyTime2c; // 화요일 종료 시간
    private String dutyTime3c; // 수요일 종료 시간
    private String dutyTime4c; // 목요일 종료 시간
    private String dutyTime5c; // 금요일 종료 시간
    private String dutyTime6c; // 토요일 종료 시간
    private String dutyTime7c; // 일요일 종료 시간
    private String dutyTime8c; // 공휴일 종료 시간
    private String dutyTime1s; // 월요일 시작 시간
    private String dutyTime2s; // 화요일 시작 시간
    private String dutyTime3s; // 수요일 시작 시간
    private String dutyTime4s; // 목요일 시작 시간
    private String dutyTime5s; // 금요일 시작 시간
    private String dutyTime6s; // 토요일 시작 시간
    private String dutyTime7s; // 일요일 시작 시간
    private String dutyTime8s; // 공휴일 시작 시간

    private String hpid; // 병원 코드
    private String dutyName; // 병원명
    private String dutyAddr; // 주소
    private String dutyTel1; // 대표전화
    private String dutyTel3; // 응급실 전화
    private String dgidIdName; // 진료과목
}
