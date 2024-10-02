package com.kyonggi.diet.restaurant.controller;

import com.kyonggi.diet.dietContent.service.Impl.DietContentServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/restaurant/dormitory")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin("*")
public class DormitoryController {

    private final DietContentServiceImpl dietContentServiceImpl;

}
