package com.example.ReservationService.controller;


import com.example.ReservationService.dto.AvailableTimes;
import com.example.ReservationService.dto.DutyTimes;
import com.example.ReservationService.dto.FastReservationRequest;
import com.example.ReservationService.dto.HpidRequest;
import com.example.ReservationService.service.FastReservationService;
import com.example.ReservationService.service.MypageRegisterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/reservation-service")
public class ReservationController {
    private static final Logger logger = LoggerFactory.getLogger(ReservationController.class);

    @Autowired
    private MypageRegisterService mypageRegisterService;

    @Autowired
    private FastReservationService fastReservationService;

    @PostMapping("/fastReservation") // 빠른 예약에 대한 응답 페이지
    public ResponseEntity<?> fastReservationCall(@RequestBody FastReservationRequest fastReservationRequest){
        logger.info("진료과목: " + fastReservationRequest.getOption());
        logger.info("시/도: " + fastReservationRequest.getSelectedCity());
        logger.info("시/군/구: " + fastReservationRequest.getSelectedDistrict());
        return ResponseEntity.ok(fastReservationService.fastReservation(fastReservationRequest));
    }



    @GetMapping("/toMypageForDate") // 빠른 예약 page에서나 병원찾기 페이지에서 -> 예약하기 버튼을 눌렀을 때 해당 병원에 대해 예약 가능한 시간대를 불러오는 컨트롤러
    public ResponseEntity<?> toMypageRegister(@RequestParam String hpid) {
        logger.info("Received hpid: " + hpid); // 요청받은 병원 코드를 로그에 출력
        HpidRequest newHpid = new HpidRequest();
        newHpid.setHpid(hpid);

        // 서비스에서 진료 시간과 병원 정보를 가져옴
        DutyTimes dutyTimes = mypageRegisterService.registerDate(newHpid);

        // 진료 시간을 바탕으로 예약 가능한 시간대를 생성
        AvailableTimes availableTimes = generateAvailableTimes(dutyTimes);

        // 병원 정보를 설정
        availableTimes.setHpid(dutyTimes.getHpid());
        availableTimes.setDutyName(dutyTimes.getDutyName());
        availableTimes.setDutyAddr(dutyTimes.getDutyAddr());
        availableTimes.setDutyTel1(dutyTimes.getDutyTel1());
        availableTimes.setDutyTel3(dutyTimes.getDutyTel3());
        availableTimes.setDgidIdName(dutyTimes.getDgidIdName());

        return ResponseEntity.ok(availableTimes);
    }

    // 예약 가능한 시간대를 생성하는 메서드
    private AvailableTimes generateAvailableTimes(DutyTimes dutyTimes) {
        AvailableTimes availableTimes = new AvailableTimes();

        // 각 요일에 대해 예약 가능한 시간대를 생성하여 설정
        availableTimes.setMonday(getAvailableTimeSlots(dutyTimes.getDutyTime1s(), dutyTimes.getDutyTime1c()));
        availableTimes.setTuesday(getAvailableTimeSlots(dutyTimes.getDutyTime2s(), dutyTimes.getDutyTime2c()));
        availableTimes.setWednesday(getAvailableTimeSlots(dutyTimes.getDutyTime3s(), dutyTimes.getDutyTime3c()));
        availableTimes.setThursday(getAvailableTimeSlots(dutyTimes.getDutyTime4s(), dutyTimes.getDutyTime4c()));
        availableTimes.setFriday(getAvailableTimeSlots(dutyTimes.getDutyTime5s(), dutyTimes.getDutyTime5c()));
        availableTimes.setSaturday(Collections.emptyList()); // 예약 불가
        availableTimes.setSunday(Collections.emptyList()); // 예약 불가
        availableTimes.setHoliday(Collections.emptyList()); // 예약 불가

        return availableTimes;
    }

    // 주어진 시작 시간과 종료 시간으로 예약 가능한 30분 간격의 시간대를 생성하는 메서드 + 6월 20일 추가 수정
    private List<String> getAvailableTimeSlots(String start, String end) {
        List<String> timeSlots = new ArrayList<>();
        // 시작 시간과 종료 시간이 null이거나 비어있는 경우 빈 리스트를 반환합니다.
        if (start == null || end == null || start.isEmpty() || end.isEmpty()) {
            return timeSlots;
        }
        // 시작 시간과 종료 시간을 정수로 변환합니다 (HHMM 형식).
        int startTime = Integer.parseInt(start);
        int endTime = Integer.parseInt(end);

        int lunchStart = 1300;
        int lunchEnd = 1400;
        // 시작 시간부터 종료 시간까지 30분 간격으로 시간대를 생성합니다.
        while (startTime < endTime) {
            int startHour = startTime / 100; // 시작 시간의 시(HH) 부분
            int startMinute = startTime % 100; // 시작 시간의 분(MM) 부분

            // 다음 시간대를 계산합니다.
            int nextTime = startTime + 30; // 30분 후 시간
            int nextHour = nextTime / 100; // 다음 시간대의 시(HH) 부분
            int nextMinute = nextTime % 100; // 다음 시간대의 분(MM) 부분

            // 분이 60분을 넘어갈 경우 시간을 조정합니다.
            if (nextMinute >= 60) {
                nextHour += 1;
                nextMinute -= 60;
            }

            // 다음 시간대가 종료 시간을 넘어가거나 점심 시간 동안이면 건너뜁니다.
            if (nextHour * 100 + nextMinute > endTime || (startTime >= lunchStart && startTime < lunchEnd)) {
                startTime = nextHour * 100 + nextMinute; // 다음 시간대로 이동
                continue; // 루프의 다음 반복으로 건너뜁니다.
            }

            // 시간대를 "HHMM-HHMM" 형식으로 문자열로 변환하여 리스트에 추가합니다.
            String timeSlot = String.format("%02d%02d-%02d%02d", startHour, startMinute, nextHour, nextMinute);
            timeSlots.add(timeSlot);

            // 다음 시간대로 이동합니다.
            startTime = nextHour * 100 + nextMinute;
        }

        return timeSlots;
    }
}
