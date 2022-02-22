package com.demo.web.rest;

import static com.demo.web.rest.TestUtil.sameInstant;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.demo.IntegrationTest;
import com.demo.domain.Category;
import com.demo.domain.Jobs;
import com.demo.domain.enumeration.JobStatus;
import com.demo.repository.JobsRepository;
import com.demo.service.criteria.JobsCriteria;
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
    private static final ZonedDateTime SMALLER_VALID_FROM = ZonedDateTime.ofInstant(Instant.ofEpochMilli(-1L), ZoneOffset.UTC);

    private static final ZonedDateTime DEFAULT_VALID_THROUGH = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_VALID_THROUGH = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final ZonedDateTime SMALLER_VALID_THROUGH = ZonedDateTime.ofInstant(Instant.ofEpochMilli(-1L), ZoneOffset.UTC);

    private static final JobStatus DEFAULT_STATUS = JobStatus.DRAFT;
    private static final JobStatus UPDATED_STATUS = JobStatus.TO;

    private static final Long DEFAULT_CREATED_BY = 1L;
    private static final Long UPDATED_CREATED_BY = 2L;
    private static final Long SMALLER_CREATED_BY = 1L - 1L;

    private static final ZonedDateTime DEFAULT_CREATED_DATE = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_CREATED_DATE = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final ZonedDateTime SMALLER_CREATED_DATE = ZonedDateTime.ofInstant(Instant.ofEpochMilli(-1L), ZoneOffset.UTC);

    private static final ZonedDateTime DEFAULT_UPDATED_DATE = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_UPDATED_DATE = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final ZonedDateTime SMALLER_UPDATED_DATE = ZonedDateTime.ofInstant(Instant.ofEpochMilli(-1L), ZoneOffset.UTC);

    private static final Long DEFAULT_UPDATE_BY = 1L;
    private static final Long UPDATED_UPDATE_BY = 2L;
    private static final Long SMALLER_UPDATE_BY = 1L - 1L;

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
    void getJobsByIdFiltering() throws Exception {
        // Initialize the database
        jobsRepository.saveAndFlush(jobs);

        Long id = jobs.getId();

        defaultJobsShouldBeFound("id.equals=" + id);
        defaultJobsShouldNotBeFound("id.notEquals=" + id);

        defaultJobsShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultJobsShouldNotBeFound("id.greaterThan=" + id);

        defaultJobsShouldBeFound("id.lessThanOrEqual=" + id);
        defaultJobsShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllJobsByTitleIsEqualToSomething() throws Exception {
        // Initialize the database
        jobsRepository.saveAndFlush(jobs);

        // Get all the jobsList where title equals to DEFAULT_TITLE
        defaultJobsShouldBeFound("title.equals=" + DEFAULT_TITLE);

        // Get all the jobsList where title equals to UPDATED_TITLE
        defaultJobsShouldNotBeFound("title.equals=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    void getAllJobsByTitleIsNotEqualToSomething() throws Exception {
        // Initialize the database
        jobsRepository.saveAndFlush(jobs);

        // Get all the jobsList where title not equals to DEFAULT_TITLE
        defaultJobsShouldNotBeFound("title.notEquals=" + DEFAULT_TITLE);

        // Get all the jobsList where title not equals to UPDATED_TITLE
        defaultJobsShouldBeFound("title.notEquals=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    void getAllJobsByTitleIsInShouldWork() throws Exception {
        // Initialize the database
        jobsRepository.saveAndFlush(jobs);

        // Get all the jobsList where title in DEFAULT_TITLE or UPDATED_TITLE
        defaultJobsShouldBeFound("title.in=" + DEFAULT_TITLE + "," + UPDATED_TITLE);

        // Get all the jobsList where title equals to UPDATED_TITLE
        defaultJobsShouldNotBeFound("title.in=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    void getAllJobsByTitleIsNullOrNotNull() throws Exception {
        // Initialize the database
        jobsRepository.saveAndFlush(jobs);

        // Get all the jobsList where title is not null
        defaultJobsShouldBeFound("title.specified=true");

        // Get all the jobsList where title is null
        defaultJobsShouldNotBeFound("title.specified=false");
    }

    @Test
    @Transactional
    void getAllJobsByTitleContainsSomething() throws Exception {
        // Initialize the database
        jobsRepository.saveAndFlush(jobs);

        // Get all the jobsList where title contains DEFAULT_TITLE
        defaultJobsShouldBeFound("title.contains=" + DEFAULT_TITLE);

        // Get all the jobsList where title contains UPDATED_TITLE
        defaultJobsShouldNotBeFound("title.contains=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    void getAllJobsByTitleNotContainsSomething() throws Exception {
        // Initialize the database
        jobsRepository.saveAndFlush(jobs);

        // Get all the jobsList where title does not contain DEFAULT_TITLE
        defaultJobsShouldNotBeFound("title.doesNotContain=" + DEFAULT_TITLE);

        // Get all the jobsList where title does not contain UPDATED_TITLE
        defaultJobsShouldBeFound("title.doesNotContain=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    void getAllJobsBySlugIsEqualToSomething() throws Exception {
        // Initialize the database
        jobsRepository.saveAndFlush(jobs);

        // Get all the jobsList where slug equals to DEFAULT_SLUG
        defaultJobsShouldBeFound("slug.equals=" + DEFAULT_SLUG);

        // Get all the jobsList where slug equals to UPDATED_SLUG
        defaultJobsShouldNotBeFound("slug.equals=" + UPDATED_SLUG);
    }

    @Test
    @Transactional
    void getAllJobsBySlugIsNotEqualToSomething() throws Exception {
        // Initialize the database
        jobsRepository.saveAndFlush(jobs);

        // Get all the jobsList where slug not equals to DEFAULT_SLUG
        defaultJobsShouldNotBeFound("slug.notEquals=" + DEFAULT_SLUG);

        // Get all the jobsList where slug not equals to UPDATED_SLUG
        defaultJobsShouldBeFound("slug.notEquals=" + UPDATED_SLUG);
    }

    @Test
    @Transactional
    void getAllJobsBySlugIsInShouldWork() throws Exception {
        // Initialize the database
        jobsRepository.saveAndFlush(jobs);

        // Get all the jobsList where slug in DEFAULT_SLUG or UPDATED_SLUG
        defaultJobsShouldBeFound("slug.in=" + DEFAULT_SLUG + "," + UPDATED_SLUG);

        // Get all the jobsList where slug equals to UPDATED_SLUG
        defaultJobsShouldNotBeFound("slug.in=" + UPDATED_SLUG);
    }

    @Test
    @Transactional
    void getAllJobsBySlugIsNullOrNotNull() throws Exception {
        // Initialize the database
        jobsRepository.saveAndFlush(jobs);

        // Get all the jobsList where slug is not null
        defaultJobsShouldBeFound("slug.specified=true");

        // Get all the jobsList where slug is null
        defaultJobsShouldNotBeFound("slug.specified=false");
    }

    @Test
    @Transactional
    void getAllJobsBySlugContainsSomething() throws Exception {
        // Initialize the database
        jobsRepository.saveAndFlush(jobs);

        // Get all the jobsList where slug contains DEFAULT_SLUG
        defaultJobsShouldBeFound("slug.contains=" + DEFAULT_SLUG);

        // Get all the jobsList where slug contains UPDATED_SLUG
        defaultJobsShouldNotBeFound("slug.contains=" + UPDATED_SLUG);
    }

    @Test
    @Transactional
    void getAllJobsBySlugNotContainsSomething() throws Exception {
        // Initialize the database
        jobsRepository.saveAndFlush(jobs);

        // Get all the jobsList where slug does not contain DEFAULT_SLUG
        defaultJobsShouldNotBeFound("slug.doesNotContain=" + DEFAULT_SLUG);

        // Get all the jobsList where slug does not contain UPDATED_SLUG
        defaultJobsShouldBeFound("slug.doesNotContain=" + UPDATED_SLUG);
    }

    @Test
    @Transactional
    void getAllJobsByFeatureImageIsEqualToSomething() throws Exception {
        // Initialize the database
        jobsRepository.saveAndFlush(jobs);

        // Get all the jobsList where featureImage equals to DEFAULT_FEATURE_IMAGE
        defaultJobsShouldBeFound("featureImage.equals=" + DEFAULT_FEATURE_IMAGE);

        // Get all the jobsList where featureImage equals to UPDATED_FEATURE_IMAGE
        defaultJobsShouldNotBeFound("featureImage.equals=" + UPDATED_FEATURE_IMAGE);
    }

    @Test
    @Transactional
    void getAllJobsByFeatureImageIsNotEqualToSomething() throws Exception {
        // Initialize the database
        jobsRepository.saveAndFlush(jobs);

        // Get all the jobsList where featureImage not equals to DEFAULT_FEATURE_IMAGE
        defaultJobsShouldNotBeFound("featureImage.notEquals=" + DEFAULT_FEATURE_IMAGE);

        // Get all the jobsList where featureImage not equals to UPDATED_FEATURE_IMAGE
        defaultJobsShouldBeFound("featureImage.notEquals=" + UPDATED_FEATURE_IMAGE);
    }

    @Test
    @Transactional
    void getAllJobsByFeatureImageIsInShouldWork() throws Exception {
        // Initialize the database
        jobsRepository.saveAndFlush(jobs);

        // Get all the jobsList where featureImage in DEFAULT_FEATURE_IMAGE or UPDATED_FEATURE_IMAGE
        defaultJobsShouldBeFound("featureImage.in=" + DEFAULT_FEATURE_IMAGE + "," + UPDATED_FEATURE_IMAGE);

        // Get all the jobsList where featureImage equals to UPDATED_FEATURE_IMAGE
        defaultJobsShouldNotBeFound("featureImage.in=" + UPDATED_FEATURE_IMAGE);
    }

    @Test
    @Transactional
    void getAllJobsByFeatureImageIsNullOrNotNull() throws Exception {
        // Initialize the database
        jobsRepository.saveAndFlush(jobs);

        // Get all the jobsList where featureImage is not null
        defaultJobsShouldBeFound("featureImage.specified=true");

        // Get all the jobsList where featureImage is null
        defaultJobsShouldNotBeFound("featureImage.specified=false");
    }

    @Test
    @Transactional
    void getAllJobsByFeatureImageContainsSomething() throws Exception {
        // Initialize the database
        jobsRepository.saveAndFlush(jobs);

        // Get all the jobsList where featureImage contains DEFAULT_FEATURE_IMAGE
        defaultJobsShouldBeFound("featureImage.contains=" + DEFAULT_FEATURE_IMAGE);

        // Get all the jobsList where featureImage contains UPDATED_FEATURE_IMAGE
        defaultJobsShouldNotBeFound("featureImage.contains=" + UPDATED_FEATURE_IMAGE);
    }

    @Test
    @Transactional
    void getAllJobsByFeatureImageNotContainsSomething() throws Exception {
        // Initialize the database
        jobsRepository.saveAndFlush(jobs);

        // Get all the jobsList where featureImage does not contain DEFAULT_FEATURE_IMAGE
        defaultJobsShouldNotBeFound("featureImage.doesNotContain=" + DEFAULT_FEATURE_IMAGE);

        // Get all the jobsList where featureImage does not contain UPDATED_FEATURE_IMAGE
        defaultJobsShouldBeFound("featureImage.doesNotContain=" + UPDATED_FEATURE_IMAGE);
    }

    @Test
    @Transactional
    void getAllJobsByValidFromIsEqualToSomething() throws Exception {
        // Initialize the database
        jobsRepository.saveAndFlush(jobs);

        // Get all the jobsList where validFrom equals to DEFAULT_VALID_FROM
        defaultJobsShouldBeFound("validFrom.equals=" + DEFAULT_VALID_FROM);

        // Get all the jobsList where validFrom equals to UPDATED_VALID_FROM
        defaultJobsShouldNotBeFound("validFrom.equals=" + UPDATED_VALID_FROM);
    }

    @Test
    @Transactional
    void getAllJobsByValidFromIsNotEqualToSomething() throws Exception {
        // Initialize the database
        jobsRepository.saveAndFlush(jobs);

        // Get all the jobsList where validFrom not equals to DEFAULT_VALID_FROM
        defaultJobsShouldNotBeFound("validFrom.notEquals=" + DEFAULT_VALID_FROM);

        // Get all the jobsList where validFrom not equals to UPDATED_VALID_FROM
        defaultJobsShouldBeFound("validFrom.notEquals=" + UPDATED_VALID_FROM);
    }

    @Test
    @Transactional
    void getAllJobsByValidFromIsInShouldWork() throws Exception {
        // Initialize the database
        jobsRepository.saveAndFlush(jobs);

        // Get all the jobsList where validFrom in DEFAULT_VALID_FROM or UPDATED_VALID_FROM
        defaultJobsShouldBeFound("validFrom.in=" + DEFAULT_VALID_FROM + "," + UPDATED_VALID_FROM);

        // Get all the jobsList where validFrom equals to UPDATED_VALID_FROM
        defaultJobsShouldNotBeFound("validFrom.in=" + UPDATED_VALID_FROM);
    }

    @Test
    @Transactional
    void getAllJobsByValidFromIsNullOrNotNull() throws Exception {
        // Initialize the database
        jobsRepository.saveAndFlush(jobs);

        // Get all the jobsList where validFrom is not null
        defaultJobsShouldBeFound("validFrom.specified=true");

        // Get all the jobsList where validFrom is null
        defaultJobsShouldNotBeFound("validFrom.specified=false");
    }

    @Test
    @Transactional
    void getAllJobsByValidFromIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        jobsRepository.saveAndFlush(jobs);

        // Get all the jobsList where validFrom is greater than or equal to DEFAULT_VALID_FROM
        defaultJobsShouldBeFound("validFrom.greaterThanOrEqual=" + DEFAULT_VALID_FROM);

        // Get all the jobsList where validFrom is greater than or equal to UPDATED_VALID_FROM
        defaultJobsShouldNotBeFound("validFrom.greaterThanOrEqual=" + UPDATED_VALID_FROM);
    }

    @Test
    @Transactional
    void getAllJobsByValidFromIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        jobsRepository.saveAndFlush(jobs);

        // Get all the jobsList where validFrom is less than or equal to DEFAULT_VALID_FROM
        defaultJobsShouldBeFound("validFrom.lessThanOrEqual=" + DEFAULT_VALID_FROM);

        // Get all the jobsList where validFrom is less than or equal to SMALLER_VALID_FROM
        defaultJobsShouldNotBeFound("validFrom.lessThanOrEqual=" + SMALLER_VALID_FROM);
    }

    @Test
    @Transactional
    void getAllJobsByValidFromIsLessThanSomething() throws Exception {
        // Initialize the database
        jobsRepository.saveAndFlush(jobs);

        // Get all the jobsList where validFrom is less than DEFAULT_VALID_FROM
        defaultJobsShouldNotBeFound("validFrom.lessThan=" + DEFAULT_VALID_FROM);

        // Get all the jobsList where validFrom is less than UPDATED_VALID_FROM
        defaultJobsShouldBeFound("validFrom.lessThan=" + UPDATED_VALID_FROM);
    }

    @Test
    @Transactional
    void getAllJobsByValidFromIsGreaterThanSomething() throws Exception {
        // Initialize the database
        jobsRepository.saveAndFlush(jobs);

        // Get all the jobsList where validFrom is greater than DEFAULT_VALID_FROM
        defaultJobsShouldNotBeFound("validFrom.greaterThan=" + DEFAULT_VALID_FROM);

        // Get all the jobsList where validFrom is greater than SMALLER_VALID_FROM
        defaultJobsShouldBeFound("validFrom.greaterThan=" + SMALLER_VALID_FROM);
    }

    @Test
    @Transactional
    void getAllJobsByValidThroughIsEqualToSomething() throws Exception {
        // Initialize the database
        jobsRepository.saveAndFlush(jobs);

        // Get all the jobsList where validThrough equals to DEFAULT_VALID_THROUGH
        defaultJobsShouldBeFound("validThrough.equals=" + DEFAULT_VALID_THROUGH);

        // Get all the jobsList where validThrough equals to UPDATED_VALID_THROUGH
        defaultJobsShouldNotBeFound("validThrough.equals=" + UPDATED_VALID_THROUGH);
    }

    @Test
    @Transactional
    void getAllJobsByValidThroughIsNotEqualToSomething() throws Exception {
        // Initialize the database
        jobsRepository.saveAndFlush(jobs);

        // Get all the jobsList where validThrough not equals to DEFAULT_VALID_THROUGH
        defaultJobsShouldNotBeFound("validThrough.notEquals=" + DEFAULT_VALID_THROUGH);

        // Get all the jobsList where validThrough not equals to UPDATED_VALID_THROUGH
        defaultJobsShouldBeFound("validThrough.notEquals=" + UPDATED_VALID_THROUGH);
    }

    @Test
    @Transactional
    void getAllJobsByValidThroughIsInShouldWork() throws Exception {
        // Initialize the database
        jobsRepository.saveAndFlush(jobs);

        // Get all the jobsList where validThrough in DEFAULT_VALID_THROUGH or UPDATED_VALID_THROUGH
        defaultJobsShouldBeFound("validThrough.in=" + DEFAULT_VALID_THROUGH + "," + UPDATED_VALID_THROUGH);

        // Get all the jobsList where validThrough equals to UPDATED_VALID_THROUGH
        defaultJobsShouldNotBeFound("validThrough.in=" + UPDATED_VALID_THROUGH);
    }

    @Test
    @Transactional
    void getAllJobsByValidThroughIsNullOrNotNull() throws Exception {
        // Initialize the database
        jobsRepository.saveAndFlush(jobs);

        // Get all the jobsList where validThrough is not null
        defaultJobsShouldBeFound("validThrough.specified=true");

        // Get all the jobsList where validThrough is null
        defaultJobsShouldNotBeFound("validThrough.specified=false");
    }

    @Test
    @Transactional
    void getAllJobsByValidThroughIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        jobsRepository.saveAndFlush(jobs);

        // Get all the jobsList where validThrough is greater than or equal to DEFAULT_VALID_THROUGH
        defaultJobsShouldBeFound("validThrough.greaterThanOrEqual=" + DEFAULT_VALID_THROUGH);

        // Get all the jobsList where validThrough is greater than or equal to UPDATED_VALID_THROUGH
        defaultJobsShouldNotBeFound("validThrough.greaterThanOrEqual=" + UPDATED_VALID_THROUGH);
    }

    @Test
    @Transactional
    void getAllJobsByValidThroughIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        jobsRepository.saveAndFlush(jobs);

        // Get all the jobsList where validThrough is less than or equal to DEFAULT_VALID_THROUGH
        defaultJobsShouldBeFound("validThrough.lessThanOrEqual=" + DEFAULT_VALID_THROUGH);

        // Get all the jobsList where validThrough is less than or equal to SMALLER_VALID_THROUGH
        defaultJobsShouldNotBeFound("validThrough.lessThanOrEqual=" + SMALLER_VALID_THROUGH);
    }

    @Test
    @Transactional
    void getAllJobsByValidThroughIsLessThanSomething() throws Exception {
        // Initialize the database
        jobsRepository.saveAndFlush(jobs);

        // Get all the jobsList where validThrough is less than DEFAULT_VALID_THROUGH
        defaultJobsShouldNotBeFound("validThrough.lessThan=" + DEFAULT_VALID_THROUGH);

        // Get all the jobsList where validThrough is less than UPDATED_VALID_THROUGH
        defaultJobsShouldBeFound("validThrough.lessThan=" + UPDATED_VALID_THROUGH);
    }

    @Test
    @Transactional
    void getAllJobsByValidThroughIsGreaterThanSomething() throws Exception {
        // Initialize the database
        jobsRepository.saveAndFlush(jobs);

        // Get all the jobsList where validThrough is greater than DEFAULT_VALID_THROUGH
        defaultJobsShouldNotBeFound("validThrough.greaterThan=" + DEFAULT_VALID_THROUGH);

        // Get all the jobsList where validThrough is greater than SMALLER_VALID_THROUGH
        defaultJobsShouldBeFound("validThrough.greaterThan=" + SMALLER_VALID_THROUGH);
    }

    @Test
    @Transactional
    void getAllJobsByStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        jobsRepository.saveAndFlush(jobs);

        // Get all the jobsList where status equals to DEFAULT_STATUS
        defaultJobsShouldBeFound("status.equals=" + DEFAULT_STATUS);

        // Get all the jobsList where status equals to UPDATED_STATUS
        defaultJobsShouldNotBeFound("status.equals=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllJobsByStatusIsNotEqualToSomething() throws Exception {
        // Initialize the database
        jobsRepository.saveAndFlush(jobs);

        // Get all the jobsList where status not equals to DEFAULT_STATUS
        defaultJobsShouldNotBeFound("status.notEquals=" + DEFAULT_STATUS);

        // Get all the jobsList where status not equals to UPDATED_STATUS
        defaultJobsShouldBeFound("status.notEquals=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllJobsByStatusIsInShouldWork() throws Exception {
        // Initialize the database
        jobsRepository.saveAndFlush(jobs);

        // Get all the jobsList where status in DEFAULT_STATUS or UPDATED_STATUS
        defaultJobsShouldBeFound("status.in=" + DEFAULT_STATUS + "," + UPDATED_STATUS);

        // Get all the jobsList where status equals to UPDATED_STATUS
        defaultJobsShouldNotBeFound("status.in=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllJobsByStatusIsNullOrNotNull() throws Exception {
        // Initialize the database
        jobsRepository.saveAndFlush(jobs);

        // Get all the jobsList where status is not null
        defaultJobsShouldBeFound("status.specified=true");

        // Get all the jobsList where status is null
        defaultJobsShouldNotBeFound("status.specified=false");
    }

    @Test
    @Transactional
    void getAllJobsByCreatedByIsEqualToSomething() throws Exception {
        // Initialize the database
        jobsRepository.saveAndFlush(jobs);

        // Get all the jobsList where createdBy equals to DEFAULT_CREATED_BY
        defaultJobsShouldBeFound("createdBy.equals=" + DEFAULT_CREATED_BY);

        // Get all the jobsList where createdBy equals to UPDATED_CREATED_BY
        defaultJobsShouldNotBeFound("createdBy.equals=" + UPDATED_CREATED_BY);
    }

    @Test
    @Transactional
    void getAllJobsByCreatedByIsNotEqualToSomething() throws Exception {
        // Initialize the database
        jobsRepository.saveAndFlush(jobs);

        // Get all the jobsList where createdBy not equals to DEFAULT_CREATED_BY
        defaultJobsShouldNotBeFound("createdBy.notEquals=" + DEFAULT_CREATED_BY);

        // Get all the jobsList where createdBy not equals to UPDATED_CREATED_BY
        defaultJobsShouldBeFound("createdBy.notEquals=" + UPDATED_CREATED_BY);
    }

    @Test
    @Transactional
    void getAllJobsByCreatedByIsInShouldWork() throws Exception {
        // Initialize the database
        jobsRepository.saveAndFlush(jobs);

        // Get all the jobsList where createdBy in DEFAULT_CREATED_BY or UPDATED_CREATED_BY
        defaultJobsShouldBeFound("createdBy.in=" + DEFAULT_CREATED_BY + "," + UPDATED_CREATED_BY);

        // Get all the jobsList where createdBy equals to UPDATED_CREATED_BY
        defaultJobsShouldNotBeFound("createdBy.in=" + UPDATED_CREATED_BY);
    }

    @Test
    @Transactional
    void getAllJobsByCreatedByIsNullOrNotNull() throws Exception {
        // Initialize the database
        jobsRepository.saveAndFlush(jobs);

        // Get all the jobsList where createdBy is not null
        defaultJobsShouldBeFound("createdBy.specified=true");

        // Get all the jobsList where createdBy is null
        defaultJobsShouldNotBeFound("createdBy.specified=false");
    }

    @Test
    @Transactional
    void getAllJobsByCreatedByIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        jobsRepository.saveAndFlush(jobs);

        // Get all the jobsList where createdBy is greater than or equal to DEFAULT_CREATED_BY
        defaultJobsShouldBeFound("createdBy.greaterThanOrEqual=" + DEFAULT_CREATED_BY);

        // Get all the jobsList where createdBy is greater than or equal to UPDATED_CREATED_BY
        defaultJobsShouldNotBeFound("createdBy.greaterThanOrEqual=" + UPDATED_CREATED_BY);
    }

    @Test
    @Transactional
    void getAllJobsByCreatedByIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        jobsRepository.saveAndFlush(jobs);

        // Get all the jobsList where createdBy is less than or equal to DEFAULT_CREATED_BY
        defaultJobsShouldBeFound("createdBy.lessThanOrEqual=" + DEFAULT_CREATED_BY);

        // Get all the jobsList where createdBy is less than or equal to SMALLER_CREATED_BY
        defaultJobsShouldNotBeFound("createdBy.lessThanOrEqual=" + SMALLER_CREATED_BY);
    }

    @Test
    @Transactional
    void getAllJobsByCreatedByIsLessThanSomething() throws Exception {
        // Initialize the database
        jobsRepository.saveAndFlush(jobs);

        // Get all the jobsList where createdBy is less than DEFAULT_CREATED_BY
        defaultJobsShouldNotBeFound("createdBy.lessThan=" + DEFAULT_CREATED_BY);

        // Get all the jobsList where createdBy is less than UPDATED_CREATED_BY
        defaultJobsShouldBeFound("createdBy.lessThan=" + UPDATED_CREATED_BY);
    }

    @Test
    @Transactional
    void getAllJobsByCreatedByIsGreaterThanSomething() throws Exception {
        // Initialize the database
        jobsRepository.saveAndFlush(jobs);

        // Get all the jobsList where createdBy is greater than DEFAULT_CREATED_BY
        defaultJobsShouldNotBeFound("createdBy.greaterThan=" + DEFAULT_CREATED_BY);

        // Get all the jobsList where createdBy is greater than SMALLER_CREATED_BY
        defaultJobsShouldBeFound("createdBy.greaterThan=" + SMALLER_CREATED_BY);
    }

    @Test
    @Transactional
    void getAllJobsByCreatedDateIsEqualToSomething() throws Exception {
        // Initialize the database
        jobsRepository.saveAndFlush(jobs);

        // Get all the jobsList where createdDate equals to DEFAULT_CREATED_DATE
        defaultJobsShouldBeFound("createdDate.equals=" + DEFAULT_CREATED_DATE);

        // Get all the jobsList where createdDate equals to UPDATED_CREATED_DATE
        defaultJobsShouldNotBeFound("createdDate.equals=" + UPDATED_CREATED_DATE);
    }

    @Test
    @Transactional
    void getAllJobsByCreatedDateIsNotEqualToSomething() throws Exception {
        // Initialize the database
        jobsRepository.saveAndFlush(jobs);

        // Get all the jobsList where createdDate not equals to DEFAULT_CREATED_DATE
        defaultJobsShouldNotBeFound("createdDate.notEquals=" + DEFAULT_CREATED_DATE);

        // Get all the jobsList where createdDate not equals to UPDATED_CREATED_DATE
        defaultJobsShouldBeFound("createdDate.notEquals=" + UPDATED_CREATED_DATE);
    }

    @Test
    @Transactional
    void getAllJobsByCreatedDateIsInShouldWork() throws Exception {
        // Initialize the database
        jobsRepository.saveAndFlush(jobs);

        // Get all the jobsList where createdDate in DEFAULT_CREATED_DATE or UPDATED_CREATED_DATE
        defaultJobsShouldBeFound("createdDate.in=" + DEFAULT_CREATED_DATE + "," + UPDATED_CREATED_DATE);

        // Get all the jobsList where createdDate equals to UPDATED_CREATED_DATE
        defaultJobsShouldNotBeFound("createdDate.in=" + UPDATED_CREATED_DATE);
    }

    @Test
    @Transactional
    void getAllJobsByCreatedDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        jobsRepository.saveAndFlush(jobs);

        // Get all the jobsList where createdDate is not null
        defaultJobsShouldBeFound("createdDate.specified=true");

        // Get all the jobsList where createdDate is null
        defaultJobsShouldNotBeFound("createdDate.specified=false");
    }

    @Test
    @Transactional
    void getAllJobsByCreatedDateIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        jobsRepository.saveAndFlush(jobs);

        // Get all the jobsList where createdDate is greater than or equal to DEFAULT_CREATED_DATE
        defaultJobsShouldBeFound("createdDate.greaterThanOrEqual=" + DEFAULT_CREATED_DATE);

        // Get all the jobsList where createdDate is greater than or equal to UPDATED_CREATED_DATE
        defaultJobsShouldNotBeFound("createdDate.greaterThanOrEqual=" + UPDATED_CREATED_DATE);
    }

    @Test
    @Transactional
    void getAllJobsByCreatedDateIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        jobsRepository.saveAndFlush(jobs);

        // Get all the jobsList where createdDate is less than or equal to DEFAULT_CREATED_DATE
        defaultJobsShouldBeFound("createdDate.lessThanOrEqual=" + DEFAULT_CREATED_DATE);

        // Get all the jobsList where createdDate is less than or equal to SMALLER_CREATED_DATE
        defaultJobsShouldNotBeFound("createdDate.lessThanOrEqual=" + SMALLER_CREATED_DATE);
    }

    @Test
    @Transactional
    void getAllJobsByCreatedDateIsLessThanSomething() throws Exception {
        // Initialize the database
        jobsRepository.saveAndFlush(jobs);

        // Get all the jobsList where createdDate is less than DEFAULT_CREATED_DATE
        defaultJobsShouldNotBeFound("createdDate.lessThan=" + DEFAULT_CREATED_DATE);

        // Get all the jobsList where createdDate is less than UPDATED_CREATED_DATE
        defaultJobsShouldBeFound("createdDate.lessThan=" + UPDATED_CREATED_DATE);
    }

    @Test
    @Transactional
    void getAllJobsByCreatedDateIsGreaterThanSomething() throws Exception {
        // Initialize the database
        jobsRepository.saveAndFlush(jobs);

        // Get all the jobsList where createdDate is greater than DEFAULT_CREATED_DATE
        defaultJobsShouldNotBeFound("createdDate.greaterThan=" + DEFAULT_CREATED_DATE);

        // Get all the jobsList where createdDate is greater than SMALLER_CREATED_DATE
        defaultJobsShouldBeFound("createdDate.greaterThan=" + SMALLER_CREATED_DATE);
    }

    @Test
    @Transactional
    void getAllJobsByUpdatedDateIsEqualToSomething() throws Exception {
        // Initialize the database
        jobsRepository.saveAndFlush(jobs);

        // Get all the jobsList where updatedDate equals to DEFAULT_UPDATED_DATE
        defaultJobsShouldBeFound("updatedDate.equals=" + DEFAULT_UPDATED_DATE);

        // Get all the jobsList where updatedDate equals to UPDATED_UPDATED_DATE
        defaultJobsShouldNotBeFound("updatedDate.equals=" + UPDATED_UPDATED_DATE);
    }

    @Test
    @Transactional
    void getAllJobsByUpdatedDateIsNotEqualToSomething() throws Exception {
        // Initialize the database
        jobsRepository.saveAndFlush(jobs);

        // Get all the jobsList where updatedDate not equals to DEFAULT_UPDATED_DATE
        defaultJobsShouldNotBeFound("updatedDate.notEquals=" + DEFAULT_UPDATED_DATE);

        // Get all the jobsList where updatedDate not equals to UPDATED_UPDATED_DATE
        defaultJobsShouldBeFound("updatedDate.notEquals=" + UPDATED_UPDATED_DATE);
    }

    @Test
    @Transactional
    void getAllJobsByUpdatedDateIsInShouldWork() throws Exception {
        // Initialize the database
        jobsRepository.saveAndFlush(jobs);

        // Get all the jobsList where updatedDate in DEFAULT_UPDATED_DATE or UPDATED_UPDATED_DATE
        defaultJobsShouldBeFound("updatedDate.in=" + DEFAULT_UPDATED_DATE + "," + UPDATED_UPDATED_DATE);

        // Get all the jobsList where updatedDate equals to UPDATED_UPDATED_DATE
        defaultJobsShouldNotBeFound("updatedDate.in=" + UPDATED_UPDATED_DATE);
    }

    @Test
    @Transactional
    void getAllJobsByUpdatedDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        jobsRepository.saveAndFlush(jobs);

        // Get all the jobsList where updatedDate is not null
        defaultJobsShouldBeFound("updatedDate.specified=true");

        // Get all the jobsList where updatedDate is null
        defaultJobsShouldNotBeFound("updatedDate.specified=false");
    }

    @Test
    @Transactional
    void getAllJobsByUpdatedDateIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        jobsRepository.saveAndFlush(jobs);

        // Get all the jobsList where updatedDate is greater than or equal to DEFAULT_UPDATED_DATE
        defaultJobsShouldBeFound("updatedDate.greaterThanOrEqual=" + DEFAULT_UPDATED_DATE);

        // Get all the jobsList where updatedDate is greater than or equal to UPDATED_UPDATED_DATE
        defaultJobsShouldNotBeFound("updatedDate.greaterThanOrEqual=" + UPDATED_UPDATED_DATE);
    }

    @Test
    @Transactional
    void getAllJobsByUpdatedDateIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        jobsRepository.saveAndFlush(jobs);

        // Get all the jobsList where updatedDate is less than or equal to DEFAULT_UPDATED_DATE
        defaultJobsShouldBeFound("updatedDate.lessThanOrEqual=" + DEFAULT_UPDATED_DATE);

        // Get all the jobsList where updatedDate is less than or equal to SMALLER_UPDATED_DATE
        defaultJobsShouldNotBeFound("updatedDate.lessThanOrEqual=" + SMALLER_UPDATED_DATE);
    }

    @Test
    @Transactional
    void getAllJobsByUpdatedDateIsLessThanSomething() throws Exception {
        // Initialize the database
        jobsRepository.saveAndFlush(jobs);

        // Get all the jobsList where updatedDate is less than DEFAULT_UPDATED_DATE
        defaultJobsShouldNotBeFound("updatedDate.lessThan=" + DEFAULT_UPDATED_DATE);

        // Get all the jobsList where updatedDate is less than UPDATED_UPDATED_DATE
        defaultJobsShouldBeFound("updatedDate.lessThan=" + UPDATED_UPDATED_DATE);
    }

    @Test
    @Transactional
    void getAllJobsByUpdatedDateIsGreaterThanSomething() throws Exception {
        // Initialize the database
        jobsRepository.saveAndFlush(jobs);

        // Get all the jobsList where updatedDate is greater than DEFAULT_UPDATED_DATE
        defaultJobsShouldNotBeFound("updatedDate.greaterThan=" + DEFAULT_UPDATED_DATE);

        // Get all the jobsList where updatedDate is greater than SMALLER_UPDATED_DATE
        defaultJobsShouldBeFound("updatedDate.greaterThan=" + SMALLER_UPDATED_DATE);
    }

    @Test
    @Transactional
    void getAllJobsByUpdateByIsEqualToSomething() throws Exception {
        // Initialize the database
        jobsRepository.saveAndFlush(jobs);

        // Get all the jobsList where updateBy equals to DEFAULT_UPDATE_BY
        defaultJobsShouldBeFound("updateBy.equals=" + DEFAULT_UPDATE_BY);

        // Get all the jobsList where updateBy equals to UPDATED_UPDATE_BY
        defaultJobsShouldNotBeFound("updateBy.equals=" + UPDATED_UPDATE_BY);
    }

    @Test
    @Transactional
    void getAllJobsByUpdateByIsNotEqualToSomething() throws Exception {
        // Initialize the database
        jobsRepository.saveAndFlush(jobs);

        // Get all the jobsList where updateBy not equals to DEFAULT_UPDATE_BY
        defaultJobsShouldNotBeFound("updateBy.notEquals=" + DEFAULT_UPDATE_BY);

        // Get all the jobsList where updateBy not equals to UPDATED_UPDATE_BY
        defaultJobsShouldBeFound("updateBy.notEquals=" + UPDATED_UPDATE_BY);
    }

    @Test
    @Transactional
    void getAllJobsByUpdateByIsInShouldWork() throws Exception {
        // Initialize the database
        jobsRepository.saveAndFlush(jobs);

        // Get all the jobsList where updateBy in DEFAULT_UPDATE_BY or UPDATED_UPDATE_BY
        defaultJobsShouldBeFound("updateBy.in=" + DEFAULT_UPDATE_BY + "," + UPDATED_UPDATE_BY);

        // Get all the jobsList where updateBy equals to UPDATED_UPDATE_BY
        defaultJobsShouldNotBeFound("updateBy.in=" + UPDATED_UPDATE_BY);
    }

    @Test
    @Transactional
    void getAllJobsByUpdateByIsNullOrNotNull() throws Exception {
        // Initialize the database
        jobsRepository.saveAndFlush(jobs);

        // Get all the jobsList where updateBy is not null
        defaultJobsShouldBeFound("updateBy.specified=true");

        // Get all the jobsList where updateBy is null
        defaultJobsShouldNotBeFound("updateBy.specified=false");
    }

    @Test
    @Transactional
    void getAllJobsByUpdateByIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        jobsRepository.saveAndFlush(jobs);

        // Get all the jobsList where updateBy is greater than or equal to DEFAULT_UPDATE_BY
        defaultJobsShouldBeFound("updateBy.greaterThanOrEqual=" + DEFAULT_UPDATE_BY);

        // Get all the jobsList where updateBy is greater than or equal to UPDATED_UPDATE_BY
        defaultJobsShouldNotBeFound("updateBy.greaterThanOrEqual=" + UPDATED_UPDATE_BY);
    }

    @Test
    @Transactional
    void getAllJobsByUpdateByIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        jobsRepository.saveAndFlush(jobs);

        // Get all the jobsList where updateBy is less than or equal to DEFAULT_UPDATE_BY
        defaultJobsShouldBeFound("updateBy.lessThanOrEqual=" + DEFAULT_UPDATE_BY);

        // Get all the jobsList where updateBy is less than or equal to SMALLER_UPDATE_BY
        defaultJobsShouldNotBeFound("updateBy.lessThanOrEqual=" + SMALLER_UPDATE_BY);
    }

    @Test
    @Transactional
    void getAllJobsByUpdateByIsLessThanSomething() throws Exception {
        // Initialize the database
        jobsRepository.saveAndFlush(jobs);

        // Get all the jobsList where updateBy is less than DEFAULT_UPDATE_BY
        defaultJobsShouldNotBeFound("updateBy.lessThan=" + DEFAULT_UPDATE_BY);

        // Get all the jobsList where updateBy is less than UPDATED_UPDATE_BY
        defaultJobsShouldBeFound("updateBy.lessThan=" + UPDATED_UPDATE_BY);
    }

    @Test
    @Transactional
    void getAllJobsByUpdateByIsGreaterThanSomething() throws Exception {
        // Initialize the database
        jobsRepository.saveAndFlush(jobs);

        // Get all the jobsList where updateBy is greater than DEFAULT_UPDATE_BY
        defaultJobsShouldNotBeFound("updateBy.greaterThan=" + DEFAULT_UPDATE_BY);

        // Get all the jobsList where updateBy is greater than SMALLER_UPDATE_BY
        defaultJobsShouldBeFound("updateBy.greaterThan=" + SMALLER_UPDATE_BY);
    }

    @Test
    @Transactional
    void getAllJobsByCategoryIsEqualToSomething() throws Exception {
        // Initialize the database
        jobsRepository.saveAndFlush(jobs);
        Category category;
        if (TestUtil.findAll(em, Category.class).isEmpty()) {
            category = CategoryResourceIT.createEntity(em);
            em.persist(category);
            em.flush();
        } else {
            category = TestUtil.findAll(em, Category.class).get(0);
        }
        em.persist(category);
        em.flush();
        jobs.setCategory(category);
        jobsRepository.saveAndFlush(jobs);
        Long categoryId = category.getId();

        // Get all the jobsList where category equals to categoryId
        defaultJobsShouldBeFound("categoryId.equals=" + categoryId);

        // Get all the jobsList where category equals to (categoryId + 1)
        defaultJobsShouldNotBeFound("categoryId.equals=" + (categoryId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultJobsShouldBeFound(String filter) throws Exception {
        restJobsMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
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

        // Check, that the count call also returns 1
        restJobsMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultJobsShouldNotBeFound(String filter) throws Exception {
        restJobsMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restJobsMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
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
