package com.example.ReservationService.controller;

import com.example.ReservationService.dto.FastReservationRequest;
import com.example.ReservationService.service.FastReservationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/reservation-service")
public class FastReservationController {
    private static final Logger logger = LoggerFactory.getLogger(FastReservationController.class);

    @Autowired
    private FastReservationService fastReservationService;

    @PostMapping("/fastReservation") // 빠른 예약에 대한 응답 페이지
    public ResponseEntity<?> fastReservationCall(@RequestBody FastReservationRequest fastReservationRequest){
        logger.info("진료과목: " + fastReservationRequest.getOption());
        logger.info("시/도: " + fastReservationRequest.getSelectedCity());
        logger.info("시/군/구: " + fastReservationRequest.getSelectedDistrict());
        return ResponseEntity.ok(fastReservationService.fastReservation(fastReservationRequest));
    }


}
