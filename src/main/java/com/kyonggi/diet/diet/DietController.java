package com.kyonggi.diet.diet;

import com.kyonggi.diet.dietFood.DietFood;
import com.kyonggi.diet.dietFood.DietFoodService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/restaurant/dormitory/diet")
@Slf4j
public class DietController {

    private final DietService dietService;
    private final DietFoodService dietFoodService;

    @GetMapping("/new")
    public String newDiet(Model model) {
        CreateNewDietForm createNewDietForm = new CreateNewDietForm();
        model.addAttribute("createNewDietForm", createNewDietForm);
        model.addAttribute("rices", dietFoodService.findRice());
        model.addAttribute("sides", dietFoodService.findSide());
        model.addAttribute("soups", dietFoodService.findSoup());
        model.addAttribute("deserts", dietFoodService.findDesert());

        return "restaurant/createNewDietForm";
    }

    @PostMapping("/new")
        public String createDiet(@ModelAttribute("createNewDietForm") CreateNewDietForm form) {
            log.info("ㅎㅇ");
            List<DietFood> dietFoods = new ArrayList<>();
            Diet diet = new Diet();

            dietFoods.add(dietFoodService.findOne(form.getRice()));
            dietFoods.addAll(dietFoodService.findDietFoodsByIdList(form.getSide()));
            dietFoods.add(dietFoodService.findOne(form.getSoup()));
            dietFoods.addAll(dietFoodService.findDietFoodsByIdList(form.getDesert()));
            dietService.save(diet.createDiet(form.getDate(), DietTime.LUNCH, dietFoods));

            return "redirect:/restaurant/dormitory";
        }


}
