package com.demo.service.impl;

import com.demo.domain.Jobs;
import com.demo.repository.JobsRepository;
import com.demo.service.JobsService;
import com.demo.service.dto.JobsDTO;
import com.demo.service.mapper.JobsMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Jobs}.
 */
@Service
@Transactional
public class JobsServiceImpl implements JobsService {

    private final Logger log = LoggerFactory.getLogger(JobsServiceImpl.class);

    private final JobsRepository jobsRepository;

    private final JobsMapper jobsMapper;

    public JobsServiceImpl(JobsRepository jobsRepository, JobsMapper jobsMapper) {
        this.jobsRepository = jobsRepository;
        this.jobsMapper = jobsMapper;
    }

    @Override
    public JobsDTO save(JobsDTO jobsDTO) {
        log.debug("Request to save Jobs : {}", jobsDTO);
        Jobs jobs = jobsMapper.toEntity(jobsDTO);
        jobs = jobsRepository.save(jobs);
        return jobsMapper.toDto(jobs);
    }

    @Override
    public Optional<JobsDTO> partialUpdate(JobsDTO jobsDTO) {
        log.debug("Request to partially update Jobs : {}", jobsDTO);

        return jobsRepository
            .findById(jobsDTO.getId())
            .map(existingJobs -> {
                jobsMapper.partialUpdate(existingJobs, jobsDTO);

                return existingJobs;
            })
            .map(jobsRepository::save)
            .map(jobsMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<JobsDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Jobs");
        return jobsRepository.findAll(pageable).map(jobsMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<JobsDTO> findOne(Long id) {
        log.debug("Request to get Jobs : {}", id);
        return jobsRepository.findById(id).map(jobsMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Jobs : {}", id);
        jobsRepository.deleteById(id);
    }
}
