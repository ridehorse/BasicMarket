package org.example.basicMarket.service.category;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.basicMarket.dto.category.CategoryCreateRequest;
import org.example.basicMarket.dto.category.CategoryDto;
import org.example.basicMarket.entity.category.Category;
import org.example.basicMarket.exception.CategoryNotFoundException;
import org.example.basicMarket.repository.member.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    public List<CategoryDto> readAll(){
        List<Category> categories = categoryRepository.findAllOrderByParentIdAscNullsFirstCategoryIdAsc();

        return CategoryDto.toDtoList(categories);
    }

    @Transactional
    public void create(CategoryCreateRequest req){
        Category parent = Optional.ofNullable(req.getParentId())
                        .map(id->categoryRepository.findById(id).orElseThrow(CategoryNotFoundException::new))
                                .orElse(null);
        categoryRepository.save(new Category(req.getName(),parent));

    }

    @Transactional
    public void delete(Long id){
        if(notExistsCategory(id)) throw new CategoryNotFoundException();
        categoryRepository.deleteById(id);
    }

    private boolean notExistsCategory(Long id){
        return !categoryRepository.existsById(id);
    }


}
