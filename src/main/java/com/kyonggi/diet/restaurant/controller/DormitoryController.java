package com.kyonggi.diet.restaurant.controller;

import com.kyonggi.diet.diet.Diet;
import com.kyonggi.diet.diet.DietService;
import com.kyonggi.diet.restaurant.RestaurantService;
import com.kyonggi.diet.review.DTO.ReviewDTO;
import com.kyonggi.diet.review.DTO.ReviewListDTO;
import com.kyonggi.diet.review.Review;
import com.kyonggi.diet.review.ReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.time.*;

@Controller
@RequestMapping("/restaurant/dormitory")
@RequiredArgsConstructor
@Slf4j
public class DormitoryController {

    private final ReviewService reviewService;
    private final RestaurantService restaurantService;
    private final DietService dietService;

    @GetMapping("")
    public String dormitoryHome(Model model) {
        LocalDate today = LocalDate.now();
        LocalDate startOfWeek = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate endOfWeek = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));

        List<Diet> diets = dietService.findDietsBetweenDates(startOfWeek, endOfWeek);

        Diet mondayDiet = null;
        Diet tuesdayDiet = null;
        Diet wednesdayDiet = null;
        Diet thursdayDiet = null;
        Diet fridayDiet = null;

        for (Diet diet : diets) {
            LocalDate localDate = LocalDate.parse(diet.getDate());
            DayOfWeek dayOfWeek = localDate.getDayOfWeek();

            switch (dayOfWeek) {
                case MONDAY:
                    mondayDiet = diet;
                    break;
                case TUESDAY:
                    tuesdayDiet = diet;
                    break;
                case WEDNESDAY:
                    wednesdayDiet = diet;
                    break;
                case THURSDAY:
                    thursdayDiet = diet;
                    break;
                case FRIDAY:
                    fridayDiet = diet;
                    break;
                default:
                    break;
            }
        }

        model.addAttribute("monday", mondayDiet);
        log.info("monday {} =  ", mondayDiet);
        model.addAttribute("tuesday", tuesdayDiet);
        log.info("tuesday {} =  ", tuesdayDiet);
        model.addAttribute("wednesday", wednesdayDiet);
        log.info("wednesday {} =  ", wednesdayDiet);
        model.addAttribute("thursday", thursdayDiet);
        log.info("thursday {} =  ", thursdayDiet);
        model.addAttribute("friday", fridayDiet);
        log.info("friday {} =  ", fridayDiet);

        return "restaurant/dormitory";
    }


    @GetMapping("/reviews")
    public String dormitoryReviews(Model model) {
        List<ReviewListDTO> reviewList = new ArrayList<>();
        for (Review review : restaurantService.findDormitory().getReviews()) {
            reviewList.add(new ReviewListDTO(review));
        }
        model.addAttribute("reviews",reviewList);
        return "restaurant/dormitory/dormitoryReview";
    }

    @GetMapping("/reviews/{id}")
    public String detailReview(@PathVariable("id") Long id, Model model) {
        ReviewDTO reviewDTO = new ReviewDTO();
        Review review =reviewService.findReview(id);
        reviewDTO.setTitle(review.getTitle());
        reviewDTO.setContent(review.getContent());
        reviewDTO.setRating(review.getRating());
        reviewDTO.setMemberName(review.getMember().getName());
        model.addAttribute("review",reviewDTO);
        return "restaurant/dormitory/dormitoryDetailReview";
    }

    @GetMapping("/reviews/{id}/delete")
    public String deleteReview(@PathVariable("id") Long id, Model model) {
        Review review = reviewService.findReview(id);
        reviewService.deleteReview(review);
        return "redirect:/restaurant/dormitory/dormitoryReview";
    }


}
