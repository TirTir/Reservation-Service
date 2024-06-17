package com.example.ReservationService.dto;

import lombok.Data;

@Data
public class FastReservationResponse {
    private String hpid; // 병원 코드 -> 예약하기 눌렀을 때 hpid를 예약하기로 누르기 (시간 받아오기용)
    private String dutyAddr; // 주소
    private String dutyName; // 병원명
    private String dutyTel1; // 전화번호
    private String dutyTime1s; // 시작시간
    private String dutyTime1c; // 종료시간

    private String wgs84Lat; // 위도
    private String wgs84Lon; // 경도
}
