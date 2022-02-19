package fftl.usedtradingapi.commons.utils;

import fftl.usedtradingapi.commons.domain.Category;
import fftl.usedtradingapi.commons.domain.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    /**
     * 신규 카테고리 저장
     * */
    public Category saveCategory(String categoryName){
        Category category = categoryRepository.save(Category.builder()
            .categoryName(categoryName)
            .build());

        return category;
    }

    /**
     * 카테고리 삭제
     * */
    public void deleteCategory(Long categoryId){
        Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new RuntimeException("올바른 카테고리 Id를 입력해주세요."));
        categoryRepository.delete(category);
    }

    /**
     * 카테고리 조회
     * */
    public Category getOneCategory(Long categoryId){
        Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new RuntimeException("올바른 카테고리 Id를 입력해주세요."));
        return category;
    }

    /**
     * 모든 카테고리 조회
     * */
    public List<Category> getAllCategory(){
        List<Category> categories = categoryRepository.findAll();
        return categories;
    }

}
