package com.demo.web.rest;

import static com.demo.web.rest.TestUtil.sameInstant;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.demo.IntegrationTest;
import com.demo.domain.Category;
import com.demo.domain.Jobs;
import com.demo.domain.Products;
import com.demo.repository.CategoryRepository;
import com.demo.service.criteria.CategoryCriteria;
import com.demo.service.dto.CategoryDTO;
import com.demo.service.mapper.CategoryMapper;
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
 * Integration tests for the {@link CategoryResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class CategoryResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

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

    private static final String ENTITY_API_URL = "/api/categories";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restCategoryMockMvc;

    private Category category;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Category createEntity(EntityManager em) {
        Category category = new Category()
            .name(DEFAULT_NAME)
            .createdBy(DEFAULT_CREATED_BY)
            .createdDate(DEFAULT_CREATED_DATE)
            .updatedDate(DEFAULT_UPDATED_DATE)
            .updateBy(DEFAULT_UPDATE_BY);
        return category;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Category createUpdatedEntity(EntityManager em) {
        Category category = new Category()
            .name(UPDATED_NAME)
            .createdBy(UPDATED_CREATED_BY)
            .createdDate(UPDATED_CREATED_DATE)
            .updatedDate(UPDATED_UPDATED_DATE)
            .updateBy(UPDATED_UPDATE_BY);
        return category;
    }

    @BeforeEach
    public void initTest() {
        category = createEntity(em);
    }

    @Test
    @Transactional
    void createCategory() throws Exception {
        int databaseSizeBeforeCreate = categoryRepository.findAll().size();
        // Create the Category
        CategoryDTO categoryDTO = categoryMapper.toDto(category);
        restCategoryMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(categoryDTO)))
            .andExpect(status().isCreated());

        // Validate the Category in the database
        List<Category> categoryList = categoryRepository.findAll();
        assertThat(categoryList).hasSize(databaseSizeBeforeCreate + 1);
        Category testCategory = categoryList.get(categoryList.size() - 1);
        assertThat(testCategory.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testCategory.getCreatedBy()).isEqualTo(DEFAULT_CREATED_BY);
        assertThat(testCategory.getCreatedDate()).isEqualTo(DEFAULT_CREATED_DATE);
        assertThat(testCategory.getUpdatedDate()).isEqualTo(DEFAULT_UPDATED_DATE);
        assertThat(testCategory.getUpdateBy()).isEqualTo(DEFAULT_UPDATE_BY);
    }

    @Test
    @Transactional
    void createCategoryWithExistingId() throws Exception {
        // Create the Category with an existing ID
        category.setId(1L);
        CategoryDTO categoryDTO = categoryMapper.toDto(category);

        int databaseSizeBeforeCreate = categoryRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restCategoryMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(categoryDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Category in the database
        List<Category> categoryList = categoryRepository.findAll();
        assertThat(categoryList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = categoryRepository.findAll().size();
        // set the field null
        category.setName(null);

        // Create the Category, which fails.
        CategoryDTO categoryDTO = categoryMapper.toDto(category);

        restCategoryMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(categoryDTO)))
            .andExpect(status().isBadRequest());

        List<Category> categoryList = categoryRepository.findAll();
        assertThat(categoryList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllCategories() throws Exception {
        // Initialize the database
        categoryRepository.saveAndFlush(category);

        // Get all the categoryList
        restCategoryMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(category.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].createdBy").value(hasItem(DEFAULT_CREATED_BY.intValue())))
            .andExpect(jsonPath("$.[*].createdDate").value(hasItem(sameInstant(DEFAULT_CREATED_DATE))))
            .andExpect(jsonPath("$.[*].updatedDate").value(hasItem(sameInstant(DEFAULT_UPDATED_DATE))))
            .andExpect(jsonPath("$.[*].updateBy").value(hasItem(DEFAULT_UPDATE_BY.intValue())));
    }

    @Test
    @Transactional
    void getCategory() throws Exception {
        // Initialize the database
        categoryRepository.saveAndFlush(category);

        // Get the category
        restCategoryMockMvc
            .perform(get(ENTITY_API_URL_ID, category.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(category.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.createdBy").value(DEFAULT_CREATED_BY.intValue()))
            .andExpect(jsonPath("$.createdDate").value(sameInstant(DEFAULT_CREATED_DATE)))
            .andExpect(jsonPath("$.updatedDate").value(sameInstant(DEFAULT_UPDATED_DATE)))
            .andExpect(jsonPath("$.updateBy").value(DEFAULT_UPDATE_BY.intValue()));
    }

    @Test
    @Transactional
    void getCategoriesByIdFiltering() throws Exception {
        // Initialize the database
        categoryRepository.saveAndFlush(category);

        Long id = category.getId();

        defaultCategoryShouldBeFound("id.equals=" + id);
        defaultCategoryShouldNotBeFound("id.notEquals=" + id);

        defaultCategoryShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultCategoryShouldNotBeFound("id.greaterThan=" + id);

        defaultCategoryShouldBeFound("id.lessThanOrEqual=" + id);
        defaultCategoryShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllCategoriesByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        categoryRepository.saveAndFlush(category);

        // Get all the categoryList where name equals to DEFAULT_NAME
        defaultCategoryShouldBeFound("name.equals=" + DEFAULT_NAME);

        // Get all the categoryList where name equals to UPDATED_NAME
        defaultCategoryShouldNotBeFound("name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllCategoriesByNameIsNotEqualToSomething() throws Exception {
        // Initialize the database
        categoryRepository.saveAndFlush(category);

        // Get all the categoryList where name not equals to DEFAULT_NAME
        defaultCategoryShouldNotBeFound("name.notEquals=" + DEFAULT_NAME);

        // Get all the categoryList where name not equals to UPDATED_NAME
        defaultCategoryShouldBeFound("name.notEquals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllCategoriesByNameIsInShouldWork() throws Exception {
        // Initialize the database
        categoryRepository.saveAndFlush(category);

        // Get all the categoryList where name in DEFAULT_NAME or UPDATED_NAME
        defaultCategoryShouldBeFound("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME);

        // Get all the categoryList where name equals to UPDATED_NAME
        defaultCategoryShouldNotBeFound("name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllCategoriesByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        categoryRepository.saveAndFlush(category);

        // Get all the categoryList where name is not null
        defaultCategoryShouldBeFound("name.specified=true");

        // Get all the categoryList where name is null
        defaultCategoryShouldNotBeFound("name.specified=false");
    }

    @Test
    @Transactional
    void getAllCategoriesByNameContainsSomething() throws Exception {
        // Initialize the database
        categoryRepository.saveAndFlush(category);

        // Get all the categoryList where name contains DEFAULT_NAME
        defaultCategoryShouldBeFound("name.contains=" + DEFAULT_NAME);

        // Get all the categoryList where name contains UPDATED_NAME
        defaultCategoryShouldNotBeFound("name.contains=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllCategoriesByNameNotContainsSomething() throws Exception {
        // Initialize the database
        categoryRepository.saveAndFlush(category);

        // Get all the categoryList where name does not contain DEFAULT_NAME
        defaultCategoryShouldNotBeFound("name.doesNotContain=" + DEFAULT_NAME);

        // Get all the categoryList where name does not contain UPDATED_NAME
        defaultCategoryShouldBeFound("name.doesNotContain=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllCategoriesByCreatedByIsEqualToSomething() throws Exception {
        // Initialize the database
        categoryRepository.saveAndFlush(category);

        // Get all the categoryList where createdBy equals to DEFAULT_CREATED_BY
        defaultCategoryShouldBeFound("createdBy.equals=" + DEFAULT_CREATED_BY);

        // Get all the categoryList where createdBy equals to UPDATED_CREATED_BY
        defaultCategoryShouldNotBeFound("createdBy.equals=" + UPDATED_CREATED_BY);
    }

    @Test
    @Transactional
    void getAllCategoriesByCreatedByIsNotEqualToSomething() throws Exception {
        // Initialize the database
        categoryRepository.saveAndFlush(category);

        // Get all the categoryList where createdBy not equals to DEFAULT_CREATED_BY
        defaultCategoryShouldNotBeFound("createdBy.notEquals=" + DEFAULT_CREATED_BY);

        // Get all the categoryList where createdBy not equals to UPDATED_CREATED_BY
        defaultCategoryShouldBeFound("createdBy.notEquals=" + UPDATED_CREATED_BY);
    }

    @Test
    @Transactional
    void getAllCategoriesByCreatedByIsInShouldWork() throws Exception {
        // Initialize the database
        categoryRepository.saveAndFlush(category);

        // Get all the categoryList where createdBy in DEFAULT_CREATED_BY or UPDATED_CREATED_BY
        defaultCategoryShouldBeFound("createdBy.in=" + DEFAULT_CREATED_BY + "," + UPDATED_CREATED_BY);

        // Get all the categoryList where createdBy equals to UPDATED_CREATED_BY
        defaultCategoryShouldNotBeFound("createdBy.in=" + UPDATED_CREATED_BY);
    }

    @Test
    @Transactional
    void getAllCategoriesByCreatedByIsNullOrNotNull() throws Exception {
        // Initialize the database
        categoryRepository.saveAndFlush(category);

        // Get all the categoryList where createdBy is not null
        defaultCategoryShouldBeFound("createdBy.specified=true");

        // Get all the categoryList where createdBy is null
        defaultCategoryShouldNotBeFound("createdBy.specified=false");
    }

    @Test
    @Transactional
    void getAllCategoriesByCreatedByIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        categoryRepository.saveAndFlush(category);

        // Get all the categoryList where createdBy is greater than or equal to DEFAULT_CREATED_BY
        defaultCategoryShouldBeFound("createdBy.greaterThanOrEqual=" + DEFAULT_CREATED_BY);

        // Get all the categoryList where createdBy is greater than or equal to UPDATED_CREATED_BY
        defaultCategoryShouldNotBeFound("createdBy.greaterThanOrEqual=" + UPDATED_CREATED_BY);
    }

    @Test
    @Transactional
    void getAllCategoriesByCreatedByIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        categoryRepository.saveAndFlush(category);

        // Get all the categoryList where createdBy is less than or equal to DEFAULT_CREATED_BY
        defaultCategoryShouldBeFound("createdBy.lessThanOrEqual=" + DEFAULT_CREATED_BY);

        // Get all the categoryList where createdBy is less than or equal to SMALLER_CREATED_BY
        defaultCategoryShouldNotBeFound("createdBy.lessThanOrEqual=" + SMALLER_CREATED_BY);
    }

    @Test
    @Transactional
    void getAllCategoriesByCreatedByIsLessThanSomething() throws Exception {
        // Initialize the database
        categoryRepository.saveAndFlush(category);

        // Get all the categoryList where createdBy is less than DEFAULT_CREATED_BY
        defaultCategoryShouldNotBeFound("createdBy.lessThan=" + DEFAULT_CREATED_BY);

        // Get all the categoryList where createdBy is less than UPDATED_CREATED_BY
        defaultCategoryShouldBeFound("createdBy.lessThan=" + UPDATED_CREATED_BY);
    }

    @Test
    @Transactional
    void getAllCategoriesByCreatedByIsGreaterThanSomething() throws Exception {
        // Initialize the database
        categoryRepository.saveAndFlush(category);

        // Get all the categoryList where createdBy is greater than DEFAULT_CREATED_BY
        defaultCategoryShouldNotBeFound("createdBy.greaterThan=" + DEFAULT_CREATED_BY);

        // Get all the categoryList where createdBy is greater than SMALLER_CREATED_BY
        defaultCategoryShouldBeFound("createdBy.greaterThan=" + SMALLER_CREATED_BY);
    }

    @Test
    @Transactional
    void getAllCategoriesByCreatedDateIsEqualToSomething() throws Exception {
        // Initialize the database
        categoryRepository.saveAndFlush(category);

        // Get all the categoryList where createdDate equals to DEFAULT_CREATED_DATE
        defaultCategoryShouldBeFound("createdDate.equals=" + DEFAULT_CREATED_DATE);

        // Get all the categoryList where createdDate equals to UPDATED_CREATED_DATE
        defaultCategoryShouldNotBeFound("createdDate.equals=" + UPDATED_CREATED_DATE);
    }

    @Test
    @Transactional
    void getAllCategoriesByCreatedDateIsNotEqualToSomething() throws Exception {
        // Initialize the database
        categoryRepository.saveAndFlush(category);

        // Get all the categoryList where createdDate not equals to DEFAULT_CREATED_DATE
        defaultCategoryShouldNotBeFound("createdDate.notEquals=" + DEFAULT_CREATED_DATE);

        // Get all the categoryList where createdDate not equals to UPDATED_CREATED_DATE
        defaultCategoryShouldBeFound("createdDate.notEquals=" + UPDATED_CREATED_DATE);
    }

    @Test
    @Transactional
    void getAllCategoriesByCreatedDateIsInShouldWork() throws Exception {
        // Initialize the database
        categoryRepository.saveAndFlush(category);

        // Get all the categoryList where createdDate in DEFAULT_CREATED_DATE or UPDATED_CREATED_DATE
        defaultCategoryShouldBeFound("createdDate.in=" + DEFAULT_CREATED_DATE + "," + UPDATED_CREATED_DATE);

        // Get all the categoryList where createdDate equals to UPDATED_CREATED_DATE
        defaultCategoryShouldNotBeFound("createdDate.in=" + UPDATED_CREATED_DATE);
    }

    @Test
    @Transactional
    void getAllCategoriesByCreatedDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        categoryRepository.saveAndFlush(category);

        // Get all the categoryList where createdDate is not null
        defaultCategoryShouldBeFound("createdDate.specified=true");

        // Get all the categoryList where createdDate is null
        defaultCategoryShouldNotBeFound("createdDate.specified=false");
    }

    @Test
    @Transactional
    void getAllCategoriesByCreatedDateIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        categoryRepository.saveAndFlush(category);

        // Get all the categoryList where createdDate is greater than or equal to DEFAULT_CREATED_DATE
        defaultCategoryShouldBeFound("createdDate.greaterThanOrEqual=" + DEFAULT_CREATED_DATE);

        // Get all the categoryList where createdDate is greater than or equal to UPDATED_CREATED_DATE
        defaultCategoryShouldNotBeFound("createdDate.greaterThanOrEqual=" + UPDATED_CREATED_DATE);
    }

    @Test
    @Transactional
    void getAllCategoriesByCreatedDateIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        categoryRepository.saveAndFlush(category);

        // Get all the categoryList where createdDate is less than or equal to DEFAULT_CREATED_DATE
        defaultCategoryShouldBeFound("createdDate.lessThanOrEqual=" + DEFAULT_CREATED_DATE);

        // Get all the categoryList where createdDate is less than or equal to SMALLER_CREATED_DATE
        defaultCategoryShouldNotBeFound("createdDate.lessThanOrEqual=" + SMALLER_CREATED_DATE);
    }

    @Test
    @Transactional
    void getAllCategoriesByCreatedDateIsLessThanSomething() throws Exception {
        // Initialize the database
        categoryRepository.saveAndFlush(category);

        // Get all the categoryList where createdDate is less than DEFAULT_CREATED_DATE
        defaultCategoryShouldNotBeFound("createdDate.lessThan=" + DEFAULT_CREATED_DATE);

        // Get all the categoryList where createdDate is less than UPDATED_CREATED_DATE
        defaultCategoryShouldBeFound("createdDate.lessThan=" + UPDATED_CREATED_DATE);
    }

    @Test
    @Transactional
    void getAllCategoriesByCreatedDateIsGreaterThanSomething() throws Exception {
        // Initialize the database
        categoryRepository.saveAndFlush(category);

        // Get all the categoryList where createdDate is greater than DEFAULT_CREATED_DATE
        defaultCategoryShouldNotBeFound("createdDate.greaterThan=" + DEFAULT_CREATED_DATE);

        // Get all the categoryList where createdDate is greater than SMALLER_CREATED_DATE
        defaultCategoryShouldBeFound("createdDate.greaterThan=" + SMALLER_CREATED_DATE);
    }

    @Test
    @Transactional
    void getAllCategoriesByUpdatedDateIsEqualToSomething() throws Exception {
        // Initialize the database
        categoryRepository.saveAndFlush(category);

        // Get all the categoryList where updatedDate equals to DEFAULT_UPDATED_DATE
        defaultCategoryShouldBeFound("updatedDate.equals=" + DEFAULT_UPDATED_DATE);

        // Get all the categoryList where updatedDate equals to UPDATED_UPDATED_DATE
        defaultCategoryShouldNotBeFound("updatedDate.equals=" + UPDATED_UPDATED_DATE);
    }

    @Test
    @Transactional
    void getAllCategoriesByUpdatedDateIsNotEqualToSomething() throws Exception {
        // Initialize the database
        categoryRepository.saveAndFlush(category);

        // Get all the categoryList where updatedDate not equals to DEFAULT_UPDATED_DATE
        defaultCategoryShouldNotBeFound("updatedDate.notEquals=" + DEFAULT_UPDATED_DATE);

        // Get all the categoryList where updatedDate not equals to UPDATED_UPDATED_DATE
        defaultCategoryShouldBeFound("updatedDate.notEquals=" + UPDATED_UPDATED_DATE);
    }

    @Test
    @Transactional
    void getAllCategoriesByUpdatedDateIsInShouldWork() throws Exception {
        // Initialize the database
        categoryRepository.saveAndFlush(category);

        // Get all the categoryList where updatedDate in DEFAULT_UPDATED_DATE or UPDATED_UPDATED_DATE
        defaultCategoryShouldBeFound("updatedDate.in=" + DEFAULT_UPDATED_DATE + "," + UPDATED_UPDATED_DATE);

        // Get all the categoryList where updatedDate equals to UPDATED_UPDATED_DATE
        defaultCategoryShouldNotBeFound("updatedDate.in=" + UPDATED_UPDATED_DATE);
    }

    @Test
    @Transactional
    void getAllCategoriesByUpdatedDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        categoryRepository.saveAndFlush(category);

        // Get all the categoryList where updatedDate is not null
        defaultCategoryShouldBeFound("updatedDate.specified=true");

        // Get all the categoryList where updatedDate is null
        defaultCategoryShouldNotBeFound("updatedDate.specified=false");
    }

    @Test
    @Transactional
    void getAllCategoriesByUpdatedDateIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        categoryRepository.saveAndFlush(category);

        // Get all the categoryList where updatedDate is greater than or equal to DEFAULT_UPDATED_DATE
        defaultCategoryShouldBeFound("updatedDate.greaterThanOrEqual=" + DEFAULT_UPDATED_DATE);

        // Get all the categoryList where updatedDate is greater than or equal to UPDATED_UPDATED_DATE
        defaultCategoryShouldNotBeFound("updatedDate.greaterThanOrEqual=" + UPDATED_UPDATED_DATE);
    }

    @Test
    @Transactional
    void getAllCategoriesByUpdatedDateIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        categoryRepository.saveAndFlush(category);

        // Get all the categoryList where updatedDate is less than or equal to DEFAULT_UPDATED_DATE
        defaultCategoryShouldBeFound("updatedDate.lessThanOrEqual=" + DEFAULT_UPDATED_DATE);

        // Get all the categoryList where updatedDate is less than or equal to SMALLER_UPDATED_DATE
        defaultCategoryShouldNotBeFound("updatedDate.lessThanOrEqual=" + SMALLER_UPDATED_DATE);
    }

    @Test
    @Transactional
    void getAllCategoriesByUpdatedDateIsLessThanSomething() throws Exception {
        // Initialize the database
        categoryRepository.saveAndFlush(category);

        // Get all the categoryList where updatedDate is less than DEFAULT_UPDATED_DATE
        defaultCategoryShouldNotBeFound("updatedDate.lessThan=" + DEFAULT_UPDATED_DATE);

        // Get all the categoryList where updatedDate is less than UPDATED_UPDATED_DATE
        defaultCategoryShouldBeFound("updatedDate.lessThan=" + UPDATED_UPDATED_DATE);
    }

    @Test
    @Transactional
    void getAllCategoriesByUpdatedDateIsGreaterThanSomething() throws Exception {
        // Initialize the database
        categoryRepository.saveAndFlush(category);

        // Get all the categoryList where updatedDate is greater than DEFAULT_UPDATED_DATE
        defaultCategoryShouldNotBeFound("updatedDate.greaterThan=" + DEFAULT_UPDATED_DATE);

        // Get all the categoryList where updatedDate is greater than SMALLER_UPDATED_DATE
        defaultCategoryShouldBeFound("updatedDate.greaterThan=" + SMALLER_UPDATED_DATE);
    }

    @Test
    @Transactional
    void getAllCategoriesByUpdateByIsEqualToSomething() throws Exception {
        // Initialize the database
        categoryRepository.saveAndFlush(category);

        // Get all the categoryList where updateBy equals to DEFAULT_UPDATE_BY
        defaultCategoryShouldBeFound("updateBy.equals=" + DEFAULT_UPDATE_BY);

        // Get all the categoryList where updateBy equals to UPDATED_UPDATE_BY
        defaultCategoryShouldNotBeFound("updateBy.equals=" + UPDATED_UPDATE_BY);
    }

    @Test
    @Transactional
    void getAllCategoriesByUpdateByIsNotEqualToSomething() throws Exception {
        // Initialize the database
        categoryRepository.saveAndFlush(category);

        // Get all the categoryList where updateBy not equals to DEFAULT_UPDATE_BY
        defaultCategoryShouldNotBeFound("updateBy.notEquals=" + DEFAULT_UPDATE_BY);

        // Get all the categoryList where updateBy not equals to UPDATED_UPDATE_BY
        defaultCategoryShouldBeFound("updateBy.notEquals=" + UPDATED_UPDATE_BY);
    }

    @Test
    @Transactional
    void getAllCategoriesByUpdateByIsInShouldWork() throws Exception {
        // Initialize the database
        categoryRepository.saveAndFlush(category);

        // Get all the categoryList where updateBy in DEFAULT_UPDATE_BY or UPDATED_UPDATE_BY
        defaultCategoryShouldBeFound("updateBy.in=" + DEFAULT_UPDATE_BY + "," + UPDATED_UPDATE_BY);

        // Get all the categoryList where updateBy equals to UPDATED_UPDATE_BY
        defaultCategoryShouldNotBeFound("updateBy.in=" + UPDATED_UPDATE_BY);
    }

    @Test
    @Transactional
    void getAllCategoriesByUpdateByIsNullOrNotNull() throws Exception {
        // Initialize the database
        categoryRepository.saveAndFlush(category);

        // Get all the categoryList where updateBy is not null
        defaultCategoryShouldBeFound("updateBy.specified=true");

        // Get all the categoryList where updateBy is null
        defaultCategoryShouldNotBeFound("updateBy.specified=false");
    }

    @Test
    @Transactional
    void getAllCategoriesByUpdateByIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        categoryRepository.saveAndFlush(category);

        // Get all the categoryList where updateBy is greater than or equal to DEFAULT_UPDATE_BY
        defaultCategoryShouldBeFound("updateBy.greaterThanOrEqual=" + DEFAULT_UPDATE_BY);

        // Get all the categoryList where updateBy is greater than or equal to UPDATED_UPDATE_BY
        defaultCategoryShouldNotBeFound("updateBy.greaterThanOrEqual=" + UPDATED_UPDATE_BY);
    }

    @Test
    @Transactional
    void getAllCategoriesByUpdateByIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        categoryRepository.saveAndFlush(category);

        // Get all the categoryList where updateBy is less than or equal to DEFAULT_UPDATE_BY
        defaultCategoryShouldBeFound("updateBy.lessThanOrEqual=" + DEFAULT_UPDATE_BY);

        // Get all the categoryList where updateBy is less than or equal to SMALLER_UPDATE_BY
        defaultCategoryShouldNotBeFound("updateBy.lessThanOrEqual=" + SMALLER_UPDATE_BY);
    }

    @Test
    @Transactional
    void getAllCategoriesByUpdateByIsLessThanSomething() throws Exception {
        // Initialize the database
        categoryRepository.saveAndFlush(category);

        // Get all the categoryList where updateBy is less than DEFAULT_UPDATE_BY
        defaultCategoryShouldNotBeFound("updateBy.lessThan=" + DEFAULT_UPDATE_BY);

        // Get all the categoryList where updateBy is less than UPDATED_UPDATE_BY
        defaultCategoryShouldBeFound("updateBy.lessThan=" + UPDATED_UPDATE_BY);
    }

    @Test
    @Transactional
    void getAllCategoriesByUpdateByIsGreaterThanSomething() throws Exception {
        // Initialize the database
        categoryRepository.saveAndFlush(category);

        // Get all the categoryList where updateBy is greater than DEFAULT_UPDATE_BY
        defaultCategoryShouldNotBeFound("updateBy.greaterThan=" + DEFAULT_UPDATE_BY);

        // Get all the categoryList where updateBy is greater than SMALLER_UPDATE_BY
        defaultCategoryShouldBeFound("updateBy.greaterThan=" + SMALLER_UPDATE_BY);
    }

    @Test
    @Transactional
    void getAllCategoriesByProductsIsEqualToSomething() throws Exception {
        // Initialize the database
        categoryRepository.saveAndFlush(category);
        Products products;
        if (TestUtil.findAll(em, Products.class).isEmpty()) {
            products = ProductsResourceIT.createEntity(em);
            em.persist(products);
            em.flush();
        } else {
            products = TestUtil.findAll(em, Products.class).get(0);
        }
        em.persist(products);
        em.flush();
        category.addProducts(products);
        categoryRepository.saveAndFlush(category);
        Long productsId = products.getId();

        // Get all the categoryList where products equals to productsId
        defaultCategoryShouldBeFound("productsId.equals=" + productsId);

        // Get all the categoryList where products equals to (productsId + 1)
        defaultCategoryShouldNotBeFound("productsId.equals=" + (productsId + 1));
    }

    @Test
    @Transactional
    void getAllCategoriesByJobsIsEqualToSomething() throws Exception {
        // Initialize the database
        categoryRepository.saveAndFlush(category);
        Jobs jobs;
        if (TestUtil.findAll(em, Jobs.class).isEmpty()) {
            jobs = JobsResourceIT.createEntity(em);
            em.persist(jobs);
            em.flush();
        } else {
            jobs = TestUtil.findAll(em, Jobs.class).get(0);
        }
        em.persist(jobs);
        em.flush();
        category.addJobs(jobs);
        categoryRepository.saveAndFlush(category);
        Long jobsId = jobs.getId();

        // Get all the categoryList where jobs equals to jobsId
        defaultCategoryShouldBeFound("jobsId.equals=" + jobsId);

        // Get all the categoryList where jobs equals to (jobsId + 1)
        defaultCategoryShouldNotBeFound("jobsId.equals=" + (jobsId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultCategoryShouldBeFound(String filter) throws Exception {
        restCategoryMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(category.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].createdBy").value(hasItem(DEFAULT_CREATED_BY.intValue())))
            .andExpect(jsonPath("$.[*].createdDate").value(hasItem(sameInstant(DEFAULT_CREATED_DATE))))
            .andExpect(jsonPath("$.[*].updatedDate").value(hasItem(sameInstant(DEFAULT_UPDATED_DATE))))
            .andExpect(jsonPath("$.[*].updateBy").value(hasItem(DEFAULT_UPDATE_BY.intValue())));

        // Check, that the count call also returns 1
        restCategoryMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultCategoryShouldNotBeFound(String filter) throws Exception {
        restCategoryMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restCategoryMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingCategory() throws Exception {
        // Get the category
        restCategoryMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewCategory() throws Exception {
        // Initialize the database
        categoryRepository.saveAndFlush(category);

        int databaseSizeBeforeUpdate = categoryRepository.findAll().size();

        // Update the category
        Category updatedCategory = categoryRepository.findById(category.getId()).get();
        // Disconnect from session so that the updates on updatedCategory are not directly saved in db
        em.detach(updatedCategory);
        updatedCategory
            .name(UPDATED_NAME)
            .createdBy(UPDATED_CREATED_BY)
            .createdDate(UPDATED_CREATED_DATE)
            .updatedDate(UPDATED_UPDATED_DATE)
            .updateBy(UPDATED_UPDATE_BY);
        CategoryDTO categoryDTO = categoryMapper.toDto(updatedCategory);

        restCategoryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, categoryDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(categoryDTO))
            )
            .andExpect(status().isOk());

        // Validate the Category in the database
        List<Category> categoryList = categoryRepository.findAll();
        assertThat(categoryList).hasSize(databaseSizeBeforeUpdate);
        Category testCategory = categoryList.get(categoryList.size() - 1);
        assertThat(testCategory.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testCategory.getCreatedBy()).isEqualTo(UPDATED_CREATED_BY);
        assertThat(testCategory.getCreatedDate()).isEqualTo(UPDATED_CREATED_DATE);
        assertThat(testCategory.getUpdatedDate()).isEqualTo(UPDATED_UPDATED_DATE);
        assertThat(testCategory.getUpdateBy()).isEqualTo(UPDATED_UPDATE_BY);
    }

    @Test
    @Transactional
    void putNonExistingCategory() throws Exception {
        int databaseSizeBeforeUpdate = categoryRepository.findAll().size();
        category.setId(count.incrementAndGet());

        // Create the Category
        CategoryDTO categoryDTO = categoryMapper.toDto(category);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCategoryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, categoryDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(categoryDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Category in the database
        List<Category> categoryList = categoryRepository.findAll();
        assertThat(categoryList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchCategory() throws Exception {
        int databaseSizeBeforeUpdate = categoryRepository.findAll().size();
        category.setId(count.incrementAndGet());

        // Create the Category
        CategoryDTO categoryDTO = categoryMapper.toDto(category);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCategoryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(categoryDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Category in the database
        List<Category> categoryList = categoryRepository.findAll();
        assertThat(categoryList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamCategory() throws Exception {
        int databaseSizeBeforeUpdate = categoryRepository.findAll().size();
        category.setId(count.incrementAndGet());

        // Create the Category
        CategoryDTO categoryDTO = categoryMapper.toDto(category);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCategoryMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(categoryDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Category in the database
        List<Category> categoryList = categoryRepository.findAll();
        assertThat(categoryList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateCategoryWithPatch() throws Exception {
        // Initialize the database
        categoryRepository.saveAndFlush(category);

        int databaseSizeBeforeUpdate = categoryRepository.findAll().size();

        // Update the category using partial update
        Category partialUpdatedCategory = new Category();
        partialUpdatedCategory.setId(category.getId());

        partialUpdatedCategory.name(UPDATED_NAME).updatedDate(UPDATED_UPDATED_DATE).updateBy(UPDATED_UPDATE_BY);

        restCategoryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCategory.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedCategory))
            )
            .andExpect(status().isOk());

        // Validate the Category in the database
        List<Category> categoryList = categoryRepository.findAll();
        assertThat(categoryList).hasSize(databaseSizeBeforeUpdate);
        Category testCategory = categoryList.get(categoryList.size() - 1);
        assertThat(testCategory.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testCategory.getCreatedBy()).isEqualTo(DEFAULT_CREATED_BY);
        assertThat(testCategory.getCreatedDate()).isEqualTo(DEFAULT_CREATED_DATE);
        assertThat(testCategory.getUpdatedDate()).isEqualTo(UPDATED_UPDATED_DATE);
        assertThat(testCategory.getUpdateBy()).isEqualTo(UPDATED_UPDATE_BY);
    }

    @Test
    @Transactional
    void fullUpdateCategoryWithPatch() throws Exception {
        // Initialize the database
        categoryRepository.saveAndFlush(category);

        int databaseSizeBeforeUpdate = categoryRepository.findAll().size();

        // Update the category using partial update
        Category partialUpdatedCategory = new Category();
        partialUpdatedCategory.setId(category.getId());

        partialUpdatedCategory
            .name(UPDATED_NAME)
            .createdBy(UPDATED_CREATED_BY)
            .createdDate(UPDATED_CREATED_DATE)
            .updatedDate(UPDATED_UPDATED_DATE)
            .updateBy(UPDATED_UPDATE_BY);

        restCategoryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCategory.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedCategory))
            )
            .andExpect(status().isOk());

        // Validate the Category in the database
        List<Category> categoryList = categoryRepository.findAll();
        assertThat(categoryList).hasSize(databaseSizeBeforeUpdate);
        Category testCategory = categoryList.get(categoryList.size() - 1);
        assertThat(testCategory.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testCategory.getCreatedBy()).isEqualTo(UPDATED_CREATED_BY);
        assertThat(testCategory.getCreatedDate()).isEqualTo(UPDATED_CREATED_DATE);
        assertThat(testCategory.getUpdatedDate()).isEqualTo(UPDATED_UPDATED_DATE);
        assertThat(testCategory.getUpdateBy()).isEqualTo(UPDATED_UPDATE_BY);
    }

    @Test
    @Transactional
    void patchNonExistingCategory() throws Exception {
        int databaseSizeBeforeUpdate = categoryRepository.findAll().size();
        category.setId(count.incrementAndGet());

        // Create the Category
        CategoryDTO categoryDTO = categoryMapper.toDto(category);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCategoryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, categoryDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(categoryDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Category in the database
        List<Category> categoryList = categoryRepository.findAll();
        assertThat(categoryList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchCategory() throws Exception {
        int databaseSizeBeforeUpdate = categoryRepository.findAll().size();
        category.setId(count.incrementAndGet());

        // Create the Category
        CategoryDTO categoryDTO = categoryMapper.toDto(category);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCategoryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(categoryDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Category in the database
        List<Category> categoryList = categoryRepository.findAll();
        assertThat(categoryList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamCategory() throws Exception {
        int databaseSizeBeforeUpdate = categoryRepository.findAll().size();
        category.setId(count.incrementAndGet());

        // Create the Category
        CategoryDTO categoryDTO = categoryMapper.toDto(category);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCategoryMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(categoryDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Category in the database
        List<Category> categoryList = categoryRepository.findAll();
        assertThat(categoryList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteCategory() throws Exception {
        // Initialize the database
        categoryRepository.saveAndFlush(category);

        int databaseSizeBeforeDelete = categoryRepository.findAll().size();

        // Delete the category
        restCategoryMockMvc
            .perform(delete(ENTITY_API_URL_ID, category.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Category> categoryList = categoryRepository.findAll();
        assertThat(categoryList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
