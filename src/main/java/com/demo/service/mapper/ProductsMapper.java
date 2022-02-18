package com.demo.service.mapper;

import com.demo.domain.Products;
import com.demo.service.dto.ProductsDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Products} and its DTO {@link ProductsDTO}.
 */
@Mapper(componentModel = "spring", uses = { CategoryMapper.class })
public interface ProductsMapper extends EntityMapper<ProductsDTO, Products> {
    @Mapping(target = "category", source = "category", qualifiedByName = "id")
    ProductsDTO toDto(Products s);
}
