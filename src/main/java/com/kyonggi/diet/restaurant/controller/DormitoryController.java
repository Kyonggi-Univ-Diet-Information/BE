package com.kyonggi.diet.restaurant.controller;

import com.kyonggi.diet.diet.service.Impl.DietServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/restaurant/dormitory")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin("*")
public class DormitoryController {

    private final DietServiceImpl dietServiceImpl;


}
