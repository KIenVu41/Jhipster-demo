package com.demo.web.rest;

import static com.demo.web.rest.TestUtil.sameInstant;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.demo.IntegrationTest;
import com.demo.domain.Jobs;
import com.demo.domain.enumeration.JobStatus;
import com.demo.repository.JobsRepository;
import com.demo.service.dto.JobsDTO;
import com.demo.service.mapper.JobsMapper;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link JobsResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class JobsResourceIT {

    private static final String DEFAULT_TITLE = "AAAAAAAAAA";
    private static final String UPDATED_TITLE = "BBBBBBBBBB";

    private static final String DEFAULT_SLUG = "AAAAAAAAAA";
    private static final String UPDATED_SLUG = "BBBBBBBBBB";

    private static final String DEFAULT_FEATURE_IMAGE = "AAAAAAAAAA";
    private static final String UPDATED_FEATURE_IMAGE = "BBBBBBBBBB";

    private static final ZonedDateTime DEFAULT_VALID_FROM = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_VALID_FROM = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final ZonedDateTime DEFAULT_VALID_THROUGH = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_VALID_THROUGH = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final JobStatus DEFAULT_STATUS = JobStatus.DRAFT;
    private static final JobStatus UPDATED_STATUS = JobStatus.TO;

    private static final Long DEFAULT_CREATED_BY = 1L;
    private static final Long UPDATED_CREATED_BY = 2L;

    private static final ZonedDateTime DEFAULT_CREATED_DATE = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_CREATED_DATE = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final ZonedDateTime DEFAULT_UPDATED_DATE = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_UPDATED_DATE = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final Long DEFAULT_UPDATE_BY = 1L;
    private static final Long UPDATED_UPDATE_BY = 2L;

    private static final String ENTITY_API_URL = "/api/jobs";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private JobsRepository jobsRepository;

    @Autowired
    private JobsMapper jobsMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restJobsMockMvc;

    private Jobs jobs;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Jobs createEntity(EntityManager em) {
        Jobs jobs = new Jobs()
            .title(DEFAULT_TITLE)
            .slug(DEFAULT_SLUG)
            .featureImage(DEFAULT_FEATURE_IMAGE)
            .validFrom(DEFAULT_VALID_FROM)
            .validThrough(DEFAULT_VALID_THROUGH)
            .status(DEFAULT_STATUS)
            .createdBy(DEFAULT_CREATED_BY)
            .createdDate(DEFAULT_CREATED_DATE)
            .updatedDate(DEFAULT_UPDATED_DATE)
            .updateBy(DEFAULT_UPDATE_BY);
        return jobs;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Jobs createUpdatedEntity(EntityManager em) {
        Jobs jobs = new Jobs()
            .title(UPDATED_TITLE)
            .slug(UPDATED_SLUG)
            .featureImage(UPDATED_FEATURE_IMAGE)
            .validFrom(UPDATED_VALID_FROM)
            .validThrough(UPDATED_VALID_THROUGH)
            .status(UPDATED_STATUS)
            .createdBy(UPDATED_CREATED_BY)
            .createdDate(UPDATED_CREATED_DATE)
            .updatedDate(UPDATED_UPDATED_DATE)
            .updateBy(UPDATED_UPDATE_BY);
        return jobs;
    }

    @BeforeEach
    public void initTest() {
        jobs = createEntity(em);
    }

    @Test
    @Transactional
    void createJobs() throws Exception {
        int databaseSizeBeforeCreate = jobsRepository.findAll().size();
        // Create the Jobs
        JobsDTO jobsDTO = jobsMapper.toDto(jobs);
        restJobsMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(jobsDTO)))
            .andExpect(status().isCreated());

        // Validate the Jobs in the database
        List<Jobs> jobsList = jobsRepository.findAll();
        assertThat(jobsList).hasSize(databaseSizeBeforeCreate + 1);
        Jobs testJobs = jobsList.get(jobsList.size() - 1);
        assertThat(testJobs.getTitle()).isEqualTo(DEFAULT_TITLE);
        assertThat(testJobs.getSlug()).isEqualTo(DEFAULT_SLUG);
        assertThat(testJobs.getFeatureImage()).isEqualTo(DEFAULT_FEATURE_IMAGE);
        assertThat(testJobs.getValidFrom()).isEqualTo(DEFAULT_VALID_FROM);
        assertThat(testJobs.getValidThrough()).isEqualTo(DEFAULT_VALID_THROUGH);
        assertThat(testJobs.getStatus()).isEqualTo(DEFAULT_STATUS);
        assertThat(testJobs.getCreatedBy()).isEqualTo(DEFAULT_CREATED_BY);
        assertThat(testJobs.getCreatedDate()).isEqualTo(DEFAULT_CREATED_DATE);
        assertThat(testJobs.getUpdatedDate()).isEqualTo(DEFAULT_UPDATED_DATE);
        assertThat(testJobs.getUpdateBy()).isEqualTo(DEFAULT_UPDATE_BY);
    }

    @Test
    @Transactional
    void createJobsWithExistingId() throws Exception {
        // Create the Jobs with an existing ID
        jobs.setId(1L);
        JobsDTO jobsDTO = jobsMapper.toDto(jobs);

        int databaseSizeBeforeCreate = jobsRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restJobsMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(jobsDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Jobs in the database
        List<Jobs> jobsList = jobsRepository.findAll();
        assertThat(jobsList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkTitleIsRequired() throws Exception {
        int databaseSizeBeforeTest = jobsRepository.findAll().size();
        // set the field null
        jobs.setTitle(null);

        // Create the Jobs, which fails.
        JobsDTO jobsDTO = jobsMapper.toDto(jobs);

        restJobsMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(jobsDTO)))
            .andExpect(status().isBadRequest());

        List<Jobs> jobsList = jobsRepository.findAll();
        assertThat(jobsList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkSlugIsRequired() throws Exception {
        int databaseSizeBeforeTest = jobsRepository.findAll().size();
        // set the field null
        jobs.setSlug(null);

        // Create the Jobs, which fails.
        JobsDTO jobsDTO = jobsMapper.toDto(jobs);

        restJobsMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(jobsDTO)))
            .andExpect(status().isBadRequest());

        List<Jobs> jobsList = jobsRepository.findAll();
        assertThat(jobsList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllJobs() throws Exception {
        // Initialize the database
        jobsRepository.saveAndFlush(jobs);

        // Get all the jobsList
        restJobsMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(jobs.getId().intValue())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].slug").value(hasItem(DEFAULT_SLUG)))
            .andExpect(jsonPath("$.[*].featureImage").value(hasItem(DEFAULT_FEATURE_IMAGE)))
            .andExpect(jsonPath("$.[*].validFrom").value(hasItem(sameInstant(DEFAULT_VALID_FROM))))
            .andExpect(jsonPath("$.[*].validThrough").value(hasItem(sameInstant(DEFAULT_VALID_THROUGH))))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].createdBy").value(hasItem(DEFAULT_CREATED_BY.intValue())))
            .andExpect(jsonPath("$.[*].createdDate").value(hasItem(sameInstant(DEFAULT_CREATED_DATE))))
            .andExpect(jsonPath("$.[*].updatedDate").value(hasItem(sameInstant(DEFAULT_UPDATED_DATE))))
            .andExpect(jsonPath("$.[*].updateBy").value(hasItem(DEFAULT_UPDATE_BY.intValue())));
    }

    @Test
    @Transactional
    void getJobs() throws Exception {
        // Initialize the database
        jobsRepository.saveAndFlush(jobs);

        // Get the jobs
        restJobsMockMvc
            .perform(get(ENTITY_API_URL_ID, jobs.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(jobs.getId().intValue()))
            .andExpect(jsonPath("$.title").value(DEFAULT_TITLE))
            .andExpect(jsonPath("$.slug").value(DEFAULT_SLUG))
            .andExpect(jsonPath("$.featureImage").value(DEFAULT_FEATURE_IMAGE))
            .andExpect(jsonPath("$.validFrom").value(sameInstant(DEFAULT_VALID_FROM)))
            .andExpect(jsonPath("$.validThrough").value(sameInstant(DEFAULT_VALID_THROUGH)))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.createdBy").value(DEFAULT_CREATED_BY.intValue()))
            .andExpect(jsonPath("$.createdDate").value(sameInstant(DEFAULT_CREATED_DATE)))
            .andExpect(jsonPath("$.updatedDate").value(sameInstant(DEFAULT_UPDATED_DATE)))
            .andExpect(jsonPath("$.updateBy").value(DEFAULT_UPDATE_BY.intValue()));
    }

    @Test
    @Transactional
    void getNonExistingJobs() throws Exception {
        // Get the jobs
        restJobsMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewJobs() throws Exception {
        // Initialize the database
        jobsRepository.saveAndFlush(jobs);

        int databaseSizeBeforeUpdate = jobsRepository.findAll().size();

        // Update the jobs
        Jobs updatedJobs = jobsRepository.findById(jobs.getId()).get();
        // Disconnect from session so that the updates on updatedJobs are not directly saved in db
        em.detach(updatedJobs);
        updatedJobs
            .title(UPDATED_TITLE)
            .slug(UPDATED_SLUG)
            .featureImage(UPDATED_FEATURE_IMAGE)
            .validFrom(UPDATED_VALID_FROM)
            .validThrough(UPDATED_VALID_THROUGH)
            .status(UPDATED_STATUS)
            .createdBy(UPDATED_CREATED_BY)
            .createdDate(UPDATED_CREATED_DATE)
            .updatedDate(UPDATED_UPDATED_DATE)
            .updateBy(UPDATED_UPDATE_BY);
        JobsDTO jobsDTO = jobsMapper.toDto(updatedJobs);

        restJobsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, jobsDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(jobsDTO))
            )
            .andExpect(status().isOk());

        // Validate the Jobs in the database
        List<Jobs> jobsList = jobsRepository.findAll();
        assertThat(jobsList).hasSize(databaseSizeBeforeUpdate);
        Jobs testJobs = jobsList.get(jobsList.size() - 1);
        assertThat(testJobs.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testJobs.getSlug()).isEqualTo(UPDATED_SLUG);
        assertThat(testJobs.getFeatureImage()).isEqualTo(UPDATED_FEATURE_IMAGE);
        assertThat(testJobs.getValidFrom()).isEqualTo(UPDATED_VALID_FROM);
        assertThat(testJobs.getValidThrough()).isEqualTo(UPDATED_VALID_THROUGH);
        assertThat(testJobs.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testJobs.getCreatedBy()).isEqualTo(UPDATED_CREATED_BY);
        assertThat(testJobs.getCreatedDate()).isEqualTo(UPDATED_CREATED_DATE);
        assertThat(testJobs.getUpdatedDate()).isEqualTo(UPDATED_UPDATED_DATE);
        assertThat(testJobs.getUpdateBy()).isEqualTo(UPDATED_UPDATE_BY);
    }

    @Test
    @Transactional
    void putNonExistingJobs() throws Exception {
        int databaseSizeBeforeUpdate = jobsRepository.findAll().size();
        jobs.setId(count.incrementAndGet());

        // Create the Jobs
        JobsDTO jobsDTO = jobsMapper.toDto(jobs);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restJobsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, jobsDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(jobsDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Jobs in the database
        List<Jobs> jobsList = jobsRepository.findAll();
        assertThat(jobsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchJobs() throws Exception {
        int databaseSizeBeforeUpdate = jobsRepository.findAll().size();
        jobs.setId(count.incrementAndGet());

        // Create the Jobs
        JobsDTO jobsDTO = jobsMapper.toDto(jobs);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restJobsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(jobsDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Jobs in the database
        List<Jobs> jobsList = jobsRepository.findAll();
        assertThat(jobsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamJobs() throws Exception {
        int databaseSizeBeforeUpdate = jobsRepository.findAll().size();
        jobs.setId(count.incrementAndGet());

        // Create the Jobs
        JobsDTO jobsDTO = jobsMapper.toDto(jobs);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restJobsMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(jobsDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Jobs in the database
        List<Jobs> jobsList = jobsRepository.findAll();
        assertThat(jobsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateJobsWithPatch() throws Exception {
        // Initialize the database
        jobsRepository.saveAndFlush(jobs);

        int databaseSizeBeforeUpdate = jobsRepository.findAll().size();

        // Update the jobs using partial update
        Jobs partialUpdatedJobs = new Jobs();
        partialUpdatedJobs.setId(jobs.getId());

        partialUpdatedJobs
            .title(UPDATED_TITLE)
            .slug(UPDATED_SLUG)
            .validThrough(UPDATED_VALID_THROUGH)
            .createdBy(UPDATED_CREATED_BY)
            .createdDate(UPDATED_CREATED_DATE)
            .updatedDate(UPDATED_UPDATED_DATE)
            .updateBy(UPDATED_UPDATE_BY);

        restJobsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedJobs.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedJobs))
            )
            .andExpect(status().isOk());

        // Validate the Jobs in the database
        List<Jobs> jobsList = jobsRepository.findAll();
        assertThat(jobsList).hasSize(databaseSizeBeforeUpdate);
        Jobs testJobs = jobsList.get(jobsList.size() - 1);
        assertThat(testJobs.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testJobs.getSlug()).isEqualTo(UPDATED_SLUG);
        assertThat(testJobs.getFeatureImage()).isEqualTo(DEFAULT_FEATURE_IMAGE);
        assertThat(testJobs.getValidFrom()).isEqualTo(DEFAULT_VALID_FROM);
        assertThat(testJobs.getValidThrough()).isEqualTo(UPDATED_VALID_THROUGH);
        assertThat(testJobs.getStatus()).isEqualTo(DEFAULT_STATUS);
        assertThat(testJobs.getCreatedBy()).isEqualTo(UPDATED_CREATED_BY);
        assertThat(testJobs.getCreatedDate()).isEqualTo(UPDATED_CREATED_DATE);
        assertThat(testJobs.getUpdatedDate()).isEqualTo(UPDATED_UPDATED_DATE);
        assertThat(testJobs.getUpdateBy()).isEqualTo(UPDATED_UPDATE_BY);
    }

    @Test
    @Transactional
    void fullUpdateJobsWithPatch() throws Exception {
        // Initialize the database
        jobsRepository.saveAndFlush(jobs);

        int databaseSizeBeforeUpdate = jobsRepository.findAll().size();

        // Update the jobs using partial update
        Jobs partialUpdatedJobs = new Jobs();
        partialUpdatedJobs.setId(jobs.getId());

        partialUpdatedJobs
            .title(UPDATED_TITLE)
            .slug(UPDATED_SLUG)
            .featureImage(UPDATED_FEATURE_IMAGE)
            .validFrom(UPDATED_VALID_FROM)
            .validThrough(UPDATED_VALID_THROUGH)
            .status(UPDATED_STATUS)
            .createdBy(UPDATED_CREATED_BY)
            .createdDate(UPDATED_CREATED_DATE)
            .updatedDate(UPDATED_UPDATED_DATE)
            .updateBy(UPDATED_UPDATE_BY);

        restJobsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedJobs.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedJobs))
            )
            .andExpect(status().isOk());

        // Validate the Jobs in the database
        List<Jobs> jobsList = jobsRepository.findAll();
        assertThat(jobsList).hasSize(databaseSizeBeforeUpdate);
        Jobs testJobs = jobsList.get(jobsList.size() - 1);
        assertThat(testJobs.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testJobs.getSlug()).isEqualTo(UPDATED_SLUG);
        assertThat(testJobs.getFeatureImage()).isEqualTo(UPDATED_FEATURE_IMAGE);
        assertThat(testJobs.getValidFrom()).isEqualTo(UPDATED_VALID_FROM);
        assertThat(testJobs.getValidThrough()).isEqualTo(UPDATED_VALID_THROUGH);
        assertThat(testJobs.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testJobs.getCreatedBy()).isEqualTo(UPDATED_CREATED_BY);
        assertThat(testJobs.getCreatedDate()).isEqualTo(UPDATED_CREATED_DATE);
        assertThat(testJobs.getUpdatedDate()).isEqualTo(UPDATED_UPDATED_DATE);
        assertThat(testJobs.getUpdateBy()).isEqualTo(UPDATED_UPDATE_BY);
    }

    @Test
    @Transactional
    void patchNonExistingJobs() throws Exception {
        int databaseSizeBeforeUpdate = jobsRepository.findAll().size();
        jobs.setId(count.incrementAndGet());

        // Create the Jobs
        JobsDTO jobsDTO = jobsMapper.toDto(jobs);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restJobsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, jobsDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(jobsDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Jobs in the database
        List<Jobs> jobsList = jobsRepository.findAll();
        assertThat(jobsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchJobs() throws Exception {
        int databaseSizeBeforeUpdate = jobsRepository.findAll().size();
        jobs.setId(count.incrementAndGet());

        // Create the Jobs
        JobsDTO jobsDTO = jobsMapper.toDto(jobs);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restJobsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(jobsDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Jobs in the database
        List<Jobs> jobsList = jobsRepository.findAll();
        assertThat(jobsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamJobs() throws Exception {
        int databaseSizeBeforeUpdate = jobsRepository.findAll().size();
        jobs.setId(count.incrementAndGet());

        // Create the Jobs
        JobsDTO jobsDTO = jobsMapper.toDto(jobs);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restJobsMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(jobsDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Jobs in the database
        List<Jobs> jobsList = jobsRepository.findAll();
        assertThat(jobsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteJobs() throws Exception {
        // Initialize the database
        jobsRepository.saveAndFlush(jobs);

        int databaseSizeBeforeDelete = jobsRepository.findAll().size();

        // Delete the jobs
        restJobsMockMvc
            .perform(delete(ENTITY_API_URL_ID, jobs.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Jobs> jobsList = jobsRepository.findAll();
        assertThat(jobsList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
