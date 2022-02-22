package com.demo.service;

import com.demo.domain.*; // for static metamodels
import com.demo.domain.Jobs;
import com.demo.repository.JobsRepository;
import com.demo.service.criteria.JobsCriteria;
import com.demo.service.dto.JobsDTO;
import com.demo.service.mapper.JobsMapper;
import java.util.List;
import javax.persistence.criteria.JoinType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link Jobs} entities in the database.
 * The main input is a {@link JobsCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link JobsDTO} or a {@link Page} of {@link JobsDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class JobsQueryService extends QueryService<Jobs> {

    private final Logger log = LoggerFactory.getLogger(JobsQueryService.class);

    private final JobsRepository jobsRepository;

    private final JobsMapper jobsMapper;

    public JobsQueryService(JobsRepository jobsRepository, JobsMapper jobsMapper) {
        this.jobsRepository = jobsRepository;
        this.jobsMapper = jobsMapper;
    }

    /**
     * Return a {@link List} of {@link JobsDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<JobsDTO> findByCriteria(JobsCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Jobs> specification = createSpecification(criteria);
        return jobsMapper.toDto(jobsRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link JobsDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<JobsDTO> findByCriteria(JobsCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Jobs> specification = createSpecification(criteria);
        return jobsRepository.findAll(specification, page).map(jobsMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(JobsCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Jobs> specification = createSpecification(criteria);
        return jobsRepository.count(specification);
    }

    /**
     * Function to convert {@link JobsCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Jobs> createSpecification(JobsCriteria criteria) {
        Specification<Jobs> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Jobs_.id));
            }
            if (criteria.getTitle() != null) {
                specification = specification.and(buildStringSpecification(criteria.getTitle(), Jobs_.title));
            }
            if (criteria.getSlug() != null) {
                specification = specification.and(buildStringSpecification(criteria.getSlug(), Jobs_.slug));
            }
            if (criteria.getFeatureImage() != null) {
                specification = specification.and(buildStringSpecification(criteria.getFeatureImage(), Jobs_.featureImage));
            }
            if (criteria.getValidFrom() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getValidFrom(), Jobs_.validFrom));
            }
            if (criteria.getValidThrough() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getValidThrough(), Jobs_.validThrough));
            }
            if (criteria.getStatus() != null) {
                specification = specification.and(buildSpecification(criteria.getStatus(), Jobs_.status));
            }
            if (criteria.getCreatedBy() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getCreatedBy(), Jobs_.createdBy));
            }
            if (criteria.getCreatedDate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getCreatedDate(), Jobs_.createdDate));
            }
            if (criteria.getUpdatedDate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getUpdatedDate(), Jobs_.updatedDate));
            }
            if (criteria.getUpdateBy() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getUpdateBy(), Jobs_.updateBy));
            }
            if (criteria.getCategoryId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getCategoryId(), root -> root.join(Jobs_.category, JoinType.LEFT).get(Category_.id))
                    );
            }
        }
        return specification;
    }
}
