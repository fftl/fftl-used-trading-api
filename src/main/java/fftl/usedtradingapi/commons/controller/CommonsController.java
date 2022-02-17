package fftl.usedtradingapi.commons.controller;

import fftl.usedtradingapi.commons.utils.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
public class CommonsController {

    private final CategoryService categoryService;

    @PostMapping("/category")
    public boolean saveCategory(@RequestParam("categoryName") String categoryName){
        return true;
    }

    @DeleteMapping("/category/{categoryId}")
    public boolean deleteCategory(@PathVariable Long categoryId){
        return true;
    }
}
