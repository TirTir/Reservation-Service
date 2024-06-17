package com.example.ReservationService.dto;

import lombok.Data;

@Data
public class FastReservationRequest {
    private String option = ""; // 진료과목
    private String selectedCity = "";  // 시
    private String selectedDistrict = ""; // 시/군/구

}
