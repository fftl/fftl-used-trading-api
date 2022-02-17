package fftl.usedtradingapi.commons.utils;

import fftl.usedtradingapi.commons.domain.Category;
import fftl.usedtradingapi.commons.domain.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    //save
    public Category saveCategory(String categoryName){
        Category category = categoryRepository.save(Category.builder()
            .categoryName(categoryName)
            .build());

        return category;
    }

    //delete
    public void deleteCategory(Long categoryId){
        Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new RuntimeException("올바른 카테고리 Id를 입력해주세요."));
        categoryRepository.delete(category);
    }

}
