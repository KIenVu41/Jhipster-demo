package com.demo.service.mapper;

import com.demo.domain.Jobs;
import com.demo.service.dto.JobsDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Jobs} and its DTO {@link JobsDTO}.
 */
@Mapper(componentModel = "spring", uses = { CategoryMapper.class })
public interface JobsMapper extends EntityMapper<JobsDTO, Jobs> {
    @Mapping(target = "category", source = "category", qualifiedByName = "id")
    JobsDTO toDto(Jobs s);
}
