package com.example.ReservationService.dto;

import lombok.Data;

@Data
public class HpidRequest {
    private String hpid = ""; // FE 쪽에서 보내는 병원 코드
}
