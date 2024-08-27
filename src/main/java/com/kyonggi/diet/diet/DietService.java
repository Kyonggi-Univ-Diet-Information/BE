package com.kyonggi.diet.diet;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class DietService {

    private final DietRepository dietRepository;

    @Transactional
    public void save(Diet diet) {
        dietRepository.save(diet);
    }

    public Diet findOne(Long id) {
        return dietRepository.findOne(id);
    }

    public List<Diet> findAll() {
        return dietRepository.findAll();
    }

    public void delete(Diet diet) {
        dietRepository.delete(diet);
    }

    public List<Diet> findDietsBetweenDates(LocalDate startOfWeek, LocalDate endOfWeek) {
        return dietRepository.findDietsBetweenDates(startOfWeek, endOfWeek);
    }
}
