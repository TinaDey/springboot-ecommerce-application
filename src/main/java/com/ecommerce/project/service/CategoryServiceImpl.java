package com.ecommerce.project.service;

import com.ecommerce.project.exceptions.APIException;
import com.ecommerce.project.exceptions.ResourceNotFoundException;
import com.ecommerce.project.model.Category;
import com.ecommerce.project.payload.CategoryDTO;
import com.ecommerce.project.payload.CategoryResponse;
import com.ecommerce.project.repositories.CategoryRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class  CategoryServiceImpl implements CategoryService{
    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public CategoryResponse getAllCategories(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {

        Sort sortByAndOrder= sortOrder.equalsIgnoreCase("asc")? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageDetails= PageRequest.of(pageNumber, pageSize, sortByAndOrder);

        //categoryPage is a page object that contains the list of categories.
        // We are getting this from the JPA repository based on the page number and page size.
        Page<Category> categoryPage= categoryRepository.findAll(pageDetails);
        List<Category> categories= categoryPage.getContent();
        if(categories.isEmpty()){
            throw new APIException("No categories found");
        }
        List<CategoryDTO> categoryDTO= categories .stream()
                .map((category)->this.modelMapper.map(category, CategoryDTO.class))
                .toList();

        CategoryResponse categoryResponse= new CategoryResponse();
        categoryResponse.setContent(categoryDTO);
        categoryResponse.setPageNumber(categoryPage.getNumber());
        categoryResponse.setPageSize(categoryPage.getSize());
        categoryResponse.setTotalElements(categoryPage.getTotalElements());
        categoryResponse.setTotalPages(categoryPage.getTotalPages());
        categoryResponse.setLast(categoryPage.isLast());
        return categoryResponse;
    }

    @Override
    public CategoryDTO createCategory(CategoryDTO categoryDTO) {
        Category savedCategory= categoryRepository.findByCategoryName(categoryDTO.getCategoryName());
        if(savedCategory != null){
            throw new APIException("Category already exists");
        }
        Category category=this.modelMapper.map(categoryDTO, Category.class);
        categoryRepository.save(category);
        return this.modelMapper.map(category, CategoryDTO.class);
    }

    @Override
    public CategoryDTO deleteCategory(Long categoryId) {

        Category category=categoryRepository.findById(categoryId)
                .orElseThrow(()-> new ResourceNotFoundException("Category","CategoryId",categoryId));

        CategoryDTO deletedCategory= this.modelMapper.map(category,CategoryDTO.class);
        categoryRepository.delete(category);
        return deletedCategory;
    }

    @Override
    public CategoryDTO updateCategory(CategoryDTO categoryDTO, Long categoryId) {

        Category savedCategory=categoryRepository.findById(categoryId)
                .orElseThrow(()->new ResourceNotFoundException("Category","CategoryId",categoryId));

        Category category=this.modelMapper.map(categoryDTO, Category.class);

        category.setCategoryID(categoryId);
        savedCategory=categoryRepository.save(category);
        return this.modelMapper.map(savedCategory, CategoryDTO.class);
    }
}
