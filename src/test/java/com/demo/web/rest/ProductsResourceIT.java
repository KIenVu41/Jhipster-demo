package com.demo.web.rest;

import static com.demo.web.rest.TestUtil.sameInstant;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.demo.IntegrationTest;
import com.demo.domain.Category;
import com.demo.domain.Products;
import com.demo.repository.ProductsRepository;
import com.demo.service.criteria.ProductsCriteria;
import com.demo.service.dto.ProductsDTO;
import com.demo.service.mapper.ProductsMapper;
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
import org.springframework.util.Base64Utils;

/**
 * Integration tests for the {@link ProductsResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ProductsResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final Integer DEFAULT_PRICE = 1;
    private static final Integer UPDATED_PRICE = 2;
    private static final Integer SMALLER_PRICE = 1 - 1;

    private static final Integer DEFAULT_QUANTITY = 1;
    private static final Integer UPDATED_QUANTITY = 2;
    private static final Integer SMALLER_QUANTITY = 1 - 1;

    private static final byte[] DEFAULT_IMAGE_URL = TestUtil.createByteArray(1, "0");
    private static final byte[] UPDATED_IMAGE_URL = TestUtil.createByteArray(1, "1");
    private static final String DEFAULT_IMAGE_URL_CONTENT_TYPE = "image/jpg";
    private static final String UPDATED_IMAGE_URL_CONTENT_TYPE = "image/png";

    private static final ZonedDateTime DEFAULT_CREATED_DATE = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_CREATED_DATE = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final ZonedDateTime SMALLER_CREATED_DATE = ZonedDateTime.ofInstant(Instant.ofEpochMilli(-1L), ZoneOffset.UTC);

    private static final ZonedDateTime DEFAULT_UPDATED_DATE = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_UPDATED_DATE = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final ZonedDateTime SMALLER_UPDATED_DATE = ZonedDateTime.ofInstant(Instant.ofEpochMilli(-1L), ZoneOffset.UTC);

    private static final String ENTITY_API_URL = "/api/products";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ProductsRepository productsRepository;

    @Autowired
    private ProductsMapper productsMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restProductsMockMvc;

    private Products products;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Products createEntity(EntityManager em) {
        Products products = new Products()
            .name(DEFAULT_NAME)
            .price(DEFAULT_PRICE)
            .quantity(DEFAULT_QUANTITY)
            .imageURL(DEFAULT_IMAGE_URL)
            .imageURLContentType(DEFAULT_IMAGE_URL_CONTENT_TYPE)
            .createdDate(DEFAULT_CREATED_DATE)
            .updatedDate(DEFAULT_UPDATED_DATE);
        return products;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Products createUpdatedEntity(EntityManager em) {
        Products products = new Products()
            .name(UPDATED_NAME)
            .price(UPDATED_PRICE)
            .quantity(UPDATED_QUANTITY)
            .imageURL(UPDATED_IMAGE_URL)
            .imageURLContentType(UPDATED_IMAGE_URL_CONTENT_TYPE)
            .createdDate(UPDATED_CREATED_DATE)
            .updatedDate(UPDATED_UPDATED_DATE);
        return products;
    }

    @BeforeEach
    public void initTest() {
        products = createEntity(em);
    }

    @Test
    @Transactional
    void createProducts() throws Exception {
        int databaseSizeBeforeCreate = productsRepository.findAll().size();
        // Create the Products
        ProductsDTO productsDTO = productsMapper.toDto(products);
        restProductsMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(productsDTO)))
            .andExpect(status().isCreated());

        // Validate the Products in the database
        List<Products> productsList = productsRepository.findAll();
        assertThat(productsList).hasSize(databaseSizeBeforeCreate + 1);
        Products testProducts = productsList.get(productsList.size() - 1);
        assertThat(testProducts.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testProducts.getPrice()).isEqualTo(DEFAULT_PRICE);
        assertThat(testProducts.getQuantity()).isEqualTo(DEFAULT_QUANTITY);
        assertThat(testProducts.getImageURL()).isEqualTo(DEFAULT_IMAGE_URL);
        assertThat(testProducts.getImageURLContentType()).isEqualTo(DEFAULT_IMAGE_URL_CONTENT_TYPE);
        assertThat(testProducts.getCreatedDate()).isEqualTo(DEFAULT_CREATED_DATE);
        assertThat(testProducts.getUpdatedDate()).isEqualTo(DEFAULT_UPDATED_DATE);
    }

    @Test
    @Transactional
    void createProductsWithExistingId() throws Exception {
        // Create the Products with an existing ID
        products.setId(1L);
        ProductsDTO productsDTO = productsMapper.toDto(products);

        int databaseSizeBeforeCreate = productsRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restProductsMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(productsDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Products in the database
        List<Products> productsList = productsRepository.findAll();
        assertThat(productsList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = productsRepository.findAll().size();
        // set the field null
        products.setName(null);

        // Create the Products, which fails.
        ProductsDTO productsDTO = productsMapper.toDto(products);

        restProductsMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(productsDTO)))
            .andExpect(status().isBadRequest());

        List<Products> productsList = productsRepository.findAll();
        assertThat(productsList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkPriceIsRequired() throws Exception {
        int databaseSizeBeforeTest = productsRepository.findAll().size();
        // set the field null
        products.setPrice(null);

        // Create the Products, which fails.
        ProductsDTO productsDTO = productsMapper.toDto(products);

        restProductsMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(productsDTO)))
            .andExpect(status().isBadRequest());

        List<Products> productsList = productsRepository.findAll();
        assertThat(productsList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkQuantityIsRequired() throws Exception {
        int databaseSizeBeforeTest = productsRepository.findAll().size();
        // set the field null
        products.setQuantity(null);

        // Create the Products, which fails.
        ProductsDTO productsDTO = productsMapper.toDto(products);

        restProductsMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(productsDTO)))
            .andExpect(status().isBadRequest());

        List<Products> productsList = productsRepository.findAll();
        assertThat(productsList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllProducts() throws Exception {
        // Initialize the database
        productsRepository.saveAndFlush(products);

        // Get all the productsList
        restProductsMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(products.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].price").value(hasItem(DEFAULT_PRICE)))
            .andExpect(jsonPath("$.[*].quantity").value(hasItem(DEFAULT_QUANTITY)))
            .andExpect(jsonPath("$.[*].imageURLContentType").value(hasItem(DEFAULT_IMAGE_URL_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].imageURL").value(hasItem(Base64Utils.encodeToString(DEFAULT_IMAGE_URL))))
            .andExpect(jsonPath("$.[*].createdDate").value(hasItem(sameInstant(DEFAULT_CREATED_DATE))))
            .andExpect(jsonPath("$.[*].updatedDate").value(hasItem(sameInstant(DEFAULT_UPDATED_DATE))));
    }

    @Test
    @Transactional
    void getProducts() throws Exception {
        // Initialize the database
        productsRepository.saveAndFlush(products);

        // Get the products
        restProductsMockMvc
            .perform(get(ENTITY_API_URL_ID, products.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(products.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.price").value(DEFAULT_PRICE))
            .andExpect(jsonPath("$.quantity").value(DEFAULT_QUANTITY))
            .andExpect(jsonPath("$.imageURLContentType").value(DEFAULT_IMAGE_URL_CONTENT_TYPE))
            .andExpect(jsonPath("$.imageURL").value(Base64Utils.encodeToString(DEFAULT_IMAGE_URL)))
            .andExpect(jsonPath("$.createdDate").value(sameInstant(DEFAULT_CREATED_DATE)))
            .andExpect(jsonPath("$.updatedDate").value(sameInstant(DEFAULT_UPDATED_DATE)));
    }

    @Test
    @Transactional
    void getProductsByIdFiltering() throws Exception {
        // Initialize the database
        productsRepository.saveAndFlush(products);

        Long id = products.getId();

        defaultProductsShouldBeFound("id.equals=" + id);
        defaultProductsShouldNotBeFound("id.notEquals=" + id);

        defaultProductsShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultProductsShouldNotBeFound("id.greaterThan=" + id);

        defaultProductsShouldBeFound("id.lessThanOrEqual=" + id);
        defaultProductsShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllProductsByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        productsRepository.saveAndFlush(products);

        // Get all the productsList where name equals to DEFAULT_NAME
        defaultProductsShouldBeFound("name.equals=" + DEFAULT_NAME);

        // Get all the productsList where name equals to UPDATED_NAME
        defaultProductsShouldNotBeFound("name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllProductsByNameIsNotEqualToSomething() throws Exception {
        // Initialize the database
        productsRepository.saveAndFlush(products);

        // Get all the productsList where name not equals to DEFAULT_NAME
        defaultProductsShouldNotBeFound("name.notEquals=" + DEFAULT_NAME);

        // Get all the productsList where name not equals to UPDATED_NAME
        defaultProductsShouldBeFound("name.notEquals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllProductsByNameIsInShouldWork() throws Exception {
        // Initialize the database
        productsRepository.saveAndFlush(products);

        // Get all the productsList where name in DEFAULT_NAME or UPDATED_NAME
        defaultProductsShouldBeFound("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME);

        // Get all the productsList where name equals to UPDATED_NAME
        defaultProductsShouldNotBeFound("name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllProductsByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        productsRepository.saveAndFlush(products);

        // Get all the productsList where name is not null
        defaultProductsShouldBeFound("name.specified=true");

        // Get all the productsList where name is null
        defaultProductsShouldNotBeFound("name.specified=false");
    }

    @Test
    @Transactional
    void getAllProductsByNameContainsSomething() throws Exception {
        // Initialize the database
        productsRepository.saveAndFlush(products);

        // Get all the productsList where name contains DEFAULT_NAME
        defaultProductsShouldBeFound("name.contains=" + DEFAULT_NAME);

        // Get all the productsList where name contains UPDATED_NAME
        defaultProductsShouldNotBeFound("name.contains=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllProductsByNameNotContainsSomething() throws Exception {
        // Initialize the database
        productsRepository.saveAndFlush(products);

        // Get all the productsList where name does not contain DEFAULT_NAME
        defaultProductsShouldNotBeFound("name.doesNotContain=" + DEFAULT_NAME);

        // Get all the productsList where name does not contain UPDATED_NAME
        defaultProductsShouldBeFound("name.doesNotContain=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllProductsByPriceIsEqualToSomething() throws Exception {
        // Initialize the database
        productsRepository.saveAndFlush(products);

        // Get all the productsList where price equals to DEFAULT_PRICE
        defaultProductsShouldBeFound("price.equals=" + DEFAULT_PRICE);

        // Get all the productsList where price equals to UPDATED_PRICE
        defaultProductsShouldNotBeFound("price.equals=" + UPDATED_PRICE);
    }

    @Test
    @Transactional
    void getAllProductsByPriceIsNotEqualToSomething() throws Exception {
        // Initialize the database
        productsRepository.saveAndFlush(products);

        // Get all the productsList where price not equals to DEFAULT_PRICE
        defaultProductsShouldNotBeFound("price.notEquals=" + DEFAULT_PRICE);

        // Get all the productsList where price not equals to UPDATED_PRICE
        defaultProductsShouldBeFound("price.notEquals=" + UPDATED_PRICE);
    }

    @Test
    @Transactional
    void getAllProductsByPriceIsInShouldWork() throws Exception {
        // Initialize the database
        productsRepository.saveAndFlush(products);

        // Get all the productsList where price in DEFAULT_PRICE or UPDATED_PRICE
        defaultProductsShouldBeFound("price.in=" + DEFAULT_PRICE + "," + UPDATED_PRICE);

        // Get all the productsList where price equals to UPDATED_PRICE
        defaultProductsShouldNotBeFound("price.in=" + UPDATED_PRICE);
    }

    @Test
    @Transactional
    void getAllProductsByPriceIsNullOrNotNull() throws Exception {
        // Initialize the database
        productsRepository.saveAndFlush(products);

        // Get all the productsList where price is not null
        defaultProductsShouldBeFound("price.specified=true");

        // Get all the productsList where price is null
        defaultProductsShouldNotBeFound("price.specified=false");
    }

    @Test
    @Transactional
    void getAllProductsByPriceIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        productsRepository.saveAndFlush(products);

        // Get all the productsList where price is greater than or equal to DEFAULT_PRICE
        defaultProductsShouldBeFound("price.greaterThanOrEqual=" + DEFAULT_PRICE);

        // Get all the productsList where price is greater than or equal to UPDATED_PRICE
        defaultProductsShouldNotBeFound("price.greaterThanOrEqual=" + UPDATED_PRICE);
    }

    @Test
    @Transactional
    void getAllProductsByPriceIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        productsRepository.saveAndFlush(products);

        // Get all the productsList where price is less than or equal to DEFAULT_PRICE
        defaultProductsShouldBeFound("price.lessThanOrEqual=" + DEFAULT_PRICE);

        // Get all the productsList where price is less than or equal to SMALLER_PRICE
        defaultProductsShouldNotBeFound("price.lessThanOrEqual=" + SMALLER_PRICE);
    }

    @Test
    @Transactional
    void getAllProductsByPriceIsLessThanSomething() throws Exception {
        // Initialize the database
        productsRepository.saveAndFlush(products);

        // Get all the productsList where price is less than DEFAULT_PRICE
        defaultProductsShouldNotBeFound("price.lessThan=" + DEFAULT_PRICE);

        // Get all the productsList where price is less than UPDATED_PRICE
        defaultProductsShouldBeFound("price.lessThan=" + UPDATED_PRICE);
    }

    @Test
    @Transactional
    void getAllProductsByPriceIsGreaterThanSomething() throws Exception {
        // Initialize the database
        productsRepository.saveAndFlush(products);

        // Get all the productsList where price is greater than DEFAULT_PRICE
        defaultProductsShouldNotBeFound("price.greaterThan=" + DEFAULT_PRICE);

        // Get all the productsList where price is greater than SMALLER_PRICE
        defaultProductsShouldBeFound("price.greaterThan=" + SMALLER_PRICE);
    }

    @Test
    @Transactional
    void getAllProductsByQuantityIsEqualToSomething() throws Exception {
        // Initialize the database
        productsRepository.saveAndFlush(products);

        // Get all the productsList where quantity equals to DEFAULT_QUANTITY
        defaultProductsShouldBeFound("quantity.equals=" + DEFAULT_QUANTITY);

        // Get all the productsList where quantity equals to UPDATED_QUANTITY
        defaultProductsShouldNotBeFound("quantity.equals=" + UPDATED_QUANTITY);
    }

    @Test
    @Transactional
    void getAllProductsByQuantityIsNotEqualToSomething() throws Exception {
        // Initialize the database
        productsRepository.saveAndFlush(products);

        // Get all the productsList where quantity not equals to DEFAULT_QUANTITY
        defaultProductsShouldNotBeFound("quantity.notEquals=" + DEFAULT_QUANTITY);

        // Get all the productsList where quantity not equals to UPDATED_QUANTITY
        defaultProductsShouldBeFound("quantity.notEquals=" + UPDATED_QUANTITY);
    }

    @Test
    @Transactional
    void getAllProductsByQuantityIsInShouldWork() throws Exception {
        // Initialize the database
        productsRepository.saveAndFlush(products);

        // Get all the productsList where quantity in DEFAULT_QUANTITY or UPDATED_QUANTITY
        defaultProductsShouldBeFound("quantity.in=" + DEFAULT_QUANTITY + "," + UPDATED_QUANTITY);

        // Get all the productsList where quantity equals to UPDATED_QUANTITY
        defaultProductsShouldNotBeFound("quantity.in=" + UPDATED_QUANTITY);
    }

    @Test
    @Transactional
    void getAllProductsByQuantityIsNullOrNotNull() throws Exception {
        // Initialize the database
        productsRepository.saveAndFlush(products);

        // Get all the productsList where quantity is not null
        defaultProductsShouldBeFound("quantity.specified=true");

        // Get all the productsList where quantity is null
        defaultProductsShouldNotBeFound("quantity.specified=false");
    }

    @Test
    @Transactional
    void getAllProductsByQuantityIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        productsRepository.saveAndFlush(products);

        // Get all the productsList where quantity is greater than or equal to DEFAULT_QUANTITY
        defaultProductsShouldBeFound("quantity.greaterThanOrEqual=" + DEFAULT_QUANTITY);

        // Get all the productsList where quantity is greater than or equal to UPDATED_QUANTITY
        defaultProductsShouldNotBeFound("quantity.greaterThanOrEqual=" + UPDATED_QUANTITY);
    }

    @Test
    @Transactional
    void getAllProductsByQuantityIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        productsRepository.saveAndFlush(products);

        // Get all the productsList where quantity is less than or equal to DEFAULT_QUANTITY
        defaultProductsShouldBeFound("quantity.lessThanOrEqual=" + DEFAULT_QUANTITY);

        // Get all the productsList where quantity is less than or equal to SMALLER_QUANTITY
        defaultProductsShouldNotBeFound("quantity.lessThanOrEqual=" + SMALLER_QUANTITY);
    }

    @Test
    @Transactional
    void getAllProductsByQuantityIsLessThanSomething() throws Exception {
        // Initialize the database
        productsRepository.saveAndFlush(products);

        // Get all the productsList where quantity is less than DEFAULT_QUANTITY
        defaultProductsShouldNotBeFound("quantity.lessThan=" + DEFAULT_QUANTITY);

        // Get all the productsList where quantity is less than UPDATED_QUANTITY
        defaultProductsShouldBeFound("quantity.lessThan=" + UPDATED_QUANTITY);
    }

    @Test
    @Transactional
    void getAllProductsByQuantityIsGreaterThanSomething() throws Exception {
        // Initialize the database
        productsRepository.saveAndFlush(products);

        // Get all the productsList where quantity is greater than DEFAULT_QUANTITY
        defaultProductsShouldNotBeFound("quantity.greaterThan=" + DEFAULT_QUANTITY);

        // Get all the productsList where quantity is greater than SMALLER_QUANTITY
        defaultProductsShouldBeFound("quantity.greaterThan=" + SMALLER_QUANTITY);
    }

    @Test
    @Transactional
    void getAllProductsByCreatedDateIsEqualToSomething() throws Exception {
        // Initialize the database
        productsRepository.saveAndFlush(products);

        // Get all the productsList where createdDate equals to DEFAULT_CREATED_DATE
        defaultProductsShouldBeFound("createdDate.equals=" + DEFAULT_CREATED_DATE);

        // Get all the productsList where createdDate equals to UPDATED_CREATED_DATE
        defaultProductsShouldNotBeFound("createdDate.equals=" + UPDATED_CREATED_DATE);
    }

    @Test
    @Transactional
    void getAllProductsByCreatedDateIsNotEqualToSomething() throws Exception {
        // Initialize the database
        productsRepository.saveAndFlush(products);

        // Get all the productsList where createdDate not equals to DEFAULT_CREATED_DATE
        defaultProductsShouldNotBeFound("createdDate.notEquals=" + DEFAULT_CREATED_DATE);

        // Get all the productsList where createdDate not equals to UPDATED_CREATED_DATE
        defaultProductsShouldBeFound("createdDate.notEquals=" + UPDATED_CREATED_DATE);
    }

    @Test
    @Transactional
    void getAllProductsByCreatedDateIsInShouldWork() throws Exception {
        // Initialize the database
        productsRepository.saveAndFlush(products);

        // Get all the productsList where createdDate in DEFAULT_CREATED_DATE or UPDATED_CREATED_DATE
        defaultProductsShouldBeFound("createdDate.in=" + DEFAULT_CREATED_DATE + "," + UPDATED_CREATED_DATE);

        // Get all the productsList where createdDate equals to UPDATED_CREATED_DATE
        defaultProductsShouldNotBeFound("createdDate.in=" + UPDATED_CREATED_DATE);
    }

    @Test
    @Transactional
    void getAllProductsByCreatedDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        productsRepository.saveAndFlush(products);

        // Get all the productsList where createdDate is not null
        defaultProductsShouldBeFound("createdDate.specified=true");

        // Get all the productsList where createdDate is null
        defaultProductsShouldNotBeFound("createdDate.specified=false");
    }

    @Test
    @Transactional
    void getAllProductsByCreatedDateIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        productsRepository.saveAndFlush(products);

        // Get all the productsList where createdDate is greater than or equal to DEFAULT_CREATED_DATE
        defaultProductsShouldBeFound("createdDate.greaterThanOrEqual=" + DEFAULT_CREATED_DATE);

        // Get all the productsList where createdDate is greater than or equal to UPDATED_CREATED_DATE
        defaultProductsShouldNotBeFound("createdDate.greaterThanOrEqual=" + UPDATED_CREATED_DATE);
    }

    @Test
    @Transactional
    void getAllProductsByCreatedDateIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        productsRepository.saveAndFlush(products);

        // Get all the productsList where createdDate is less than or equal to DEFAULT_CREATED_DATE
        defaultProductsShouldBeFound("createdDate.lessThanOrEqual=" + DEFAULT_CREATED_DATE);

        // Get all the productsList where createdDate is less than or equal to SMALLER_CREATED_DATE
        defaultProductsShouldNotBeFound("createdDate.lessThanOrEqual=" + SMALLER_CREATED_DATE);
    }

    @Test
    @Transactional
    void getAllProductsByCreatedDateIsLessThanSomething() throws Exception {
        // Initialize the database
        productsRepository.saveAndFlush(products);

        // Get all the productsList where createdDate is less than DEFAULT_CREATED_DATE
        defaultProductsShouldNotBeFound("createdDate.lessThan=" + DEFAULT_CREATED_DATE);

        // Get all the productsList where createdDate is less than UPDATED_CREATED_DATE
        defaultProductsShouldBeFound("createdDate.lessThan=" + UPDATED_CREATED_DATE);
    }

    @Test
    @Transactional
    void getAllProductsByCreatedDateIsGreaterThanSomething() throws Exception {
        // Initialize the database
        productsRepository.saveAndFlush(products);

        // Get all the productsList where createdDate is greater than DEFAULT_CREATED_DATE
        defaultProductsShouldNotBeFound("createdDate.greaterThan=" + DEFAULT_CREATED_DATE);

        // Get all the productsList where createdDate is greater than SMALLER_CREATED_DATE
        defaultProductsShouldBeFound("createdDate.greaterThan=" + SMALLER_CREATED_DATE);
    }

    @Test
    @Transactional
    void getAllProductsByUpdatedDateIsEqualToSomething() throws Exception {
        // Initialize the database
        productsRepository.saveAndFlush(products);

        // Get all the productsList where updatedDate equals to DEFAULT_UPDATED_DATE
        defaultProductsShouldBeFound("updatedDate.equals=" + DEFAULT_UPDATED_DATE);

        // Get all the productsList where updatedDate equals to UPDATED_UPDATED_DATE
        defaultProductsShouldNotBeFound("updatedDate.equals=" + UPDATED_UPDATED_DATE);
    }

    @Test
    @Transactional
    void getAllProductsByUpdatedDateIsNotEqualToSomething() throws Exception {
        // Initialize the database
        productsRepository.saveAndFlush(products);

        // Get all the productsList where updatedDate not equals to DEFAULT_UPDATED_DATE
        defaultProductsShouldNotBeFound("updatedDate.notEquals=" + DEFAULT_UPDATED_DATE);

        // Get all the productsList where updatedDate not equals to UPDATED_UPDATED_DATE
        defaultProductsShouldBeFound("updatedDate.notEquals=" + UPDATED_UPDATED_DATE);
    }

    @Test
    @Transactional
    void getAllProductsByUpdatedDateIsInShouldWork() throws Exception {
        // Initialize the database
        productsRepository.saveAndFlush(products);

        // Get all the productsList where updatedDate in DEFAULT_UPDATED_DATE or UPDATED_UPDATED_DATE
        defaultProductsShouldBeFound("updatedDate.in=" + DEFAULT_UPDATED_DATE + "," + UPDATED_UPDATED_DATE);

        // Get all the productsList where updatedDate equals to UPDATED_UPDATED_DATE
        defaultProductsShouldNotBeFound("updatedDate.in=" + UPDATED_UPDATED_DATE);
    }

    @Test
    @Transactional
    void getAllProductsByUpdatedDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        productsRepository.saveAndFlush(products);

        // Get all the productsList where updatedDate is not null
        defaultProductsShouldBeFound("updatedDate.specified=true");

        // Get all the productsList where updatedDate is null
        defaultProductsShouldNotBeFound("updatedDate.specified=false");
    }

    @Test
    @Transactional
    void getAllProductsByUpdatedDateIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        productsRepository.saveAndFlush(products);

        // Get all the productsList where updatedDate is greater than or equal to DEFAULT_UPDATED_DATE
        defaultProductsShouldBeFound("updatedDate.greaterThanOrEqual=" + DEFAULT_UPDATED_DATE);

        // Get all the productsList where updatedDate is greater than or equal to UPDATED_UPDATED_DATE
        defaultProductsShouldNotBeFound("updatedDate.greaterThanOrEqual=" + UPDATED_UPDATED_DATE);
    }

    @Test
    @Transactional
    void getAllProductsByUpdatedDateIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        productsRepository.saveAndFlush(products);

        // Get all the productsList where updatedDate is less than or equal to DEFAULT_UPDATED_DATE
        defaultProductsShouldBeFound("updatedDate.lessThanOrEqual=" + DEFAULT_UPDATED_DATE);

        // Get all the productsList where updatedDate is less than or equal to SMALLER_UPDATED_DATE
        defaultProductsShouldNotBeFound("updatedDate.lessThanOrEqual=" + SMALLER_UPDATED_DATE);
    }

    @Test
    @Transactional
    void getAllProductsByUpdatedDateIsLessThanSomething() throws Exception {
        // Initialize the database
        productsRepository.saveAndFlush(products);

        // Get all the productsList where updatedDate is less than DEFAULT_UPDATED_DATE
        defaultProductsShouldNotBeFound("updatedDate.lessThan=" + DEFAULT_UPDATED_DATE);

        // Get all the productsList where updatedDate is less than UPDATED_UPDATED_DATE
        defaultProductsShouldBeFound("updatedDate.lessThan=" + UPDATED_UPDATED_DATE);
    }

    @Test
    @Transactional
    void getAllProductsByUpdatedDateIsGreaterThanSomething() throws Exception {
        // Initialize the database
        productsRepository.saveAndFlush(products);

        // Get all the productsList where updatedDate is greater than DEFAULT_UPDATED_DATE
        defaultProductsShouldNotBeFound("updatedDate.greaterThan=" + DEFAULT_UPDATED_DATE);

        // Get all the productsList where updatedDate is greater than SMALLER_UPDATED_DATE
        defaultProductsShouldBeFound("updatedDate.greaterThan=" + SMALLER_UPDATED_DATE);
    }

    @Test
    @Transactional
    void getAllProductsByCategoryIsEqualToSomething() throws Exception {
        // Initialize the database
        productsRepository.saveAndFlush(products);
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
        products.setCategory(category);
        productsRepository.saveAndFlush(products);
        Long categoryId = category.getId();

        // Get all the productsList where category equals to categoryId
        defaultProductsShouldBeFound("categoryId.equals=" + categoryId);

        // Get all the productsList where category equals to (categoryId + 1)
        defaultProductsShouldNotBeFound("categoryId.equals=" + (categoryId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultProductsShouldBeFound(String filter) throws Exception {
        restProductsMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(products.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].price").value(hasItem(DEFAULT_PRICE)))
            .andExpect(jsonPath("$.[*].quantity").value(hasItem(DEFAULT_QUANTITY)))
            .andExpect(jsonPath("$.[*].imageURLContentType").value(hasItem(DEFAULT_IMAGE_URL_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].imageURL").value(hasItem(Base64Utils.encodeToString(DEFAULT_IMAGE_URL))))
            .andExpect(jsonPath("$.[*].createdDate").value(hasItem(sameInstant(DEFAULT_CREATED_DATE))))
            .andExpect(jsonPath("$.[*].updatedDate").value(hasItem(sameInstant(DEFAULT_UPDATED_DATE))));

        // Check, that the count call also returns 1
        restProductsMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultProductsShouldNotBeFound(String filter) throws Exception {
        restProductsMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restProductsMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingProducts() throws Exception {
        // Get the products
        restProductsMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewProducts() throws Exception {
        // Initialize the database
        productsRepository.saveAndFlush(products);

        int databaseSizeBeforeUpdate = productsRepository.findAll().size();

        // Update the products
        Products updatedProducts = productsRepository.findById(products.getId()).get();
        // Disconnect from session so that the updates on updatedProducts are not directly saved in db
        em.detach(updatedProducts);
        updatedProducts
            .name(UPDATED_NAME)
            .price(UPDATED_PRICE)
            .quantity(UPDATED_QUANTITY)
            .imageURL(UPDATED_IMAGE_URL)
            .imageURLContentType(UPDATED_IMAGE_URL_CONTENT_TYPE)
            .createdDate(UPDATED_CREATED_DATE)
            .updatedDate(UPDATED_UPDATED_DATE);
        ProductsDTO productsDTO = productsMapper.toDto(updatedProducts);

        restProductsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, productsDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(productsDTO))
            )
            .andExpect(status().isOk());

        // Validate the Products in the database
        List<Products> productsList = productsRepository.findAll();
        assertThat(productsList).hasSize(databaseSizeBeforeUpdate);
        Products testProducts = productsList.get(productsList.size() - 1);
        assertThat(testProducts.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testProducts.getPrice()).isEqualTo(UPDATED_PRICE);
        assertThat(testProducts.getQuantity()).isEqualTo(UPDATED_QUANTITY);
        assertThat(testProducts.getImageURL()).isEqualTo(UPDATED_IMAGE_URL);
        assertThat(testProducts.getImageURLContentType()).isEqualTo(UPDATED_IMAGE_URL_CONTENT_TYPE);
        assertThat(testProducts.getCreatedDate()).isEqualTo(UPDATED_CREATED_DATE);
        assertThat(testProducts.getUpdatedDate()).isEqualTo(UPDATED_UPDATED_DATE);
    }

    @Test
    @Transactional
    void putNonExistingProducts() throws Exception {
        int databaseSizeBeforeUpdate = productsRepository.findAll().size();
        products.setId(count.incrementAndGet());

        // Create the Products
        ProductsDTO productsDTO = productsMapper.toDto(products);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restProductsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, productsDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(productsDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Products in the database
        List<Products> productsList = productsRepository.findAll();
        assertThat(productsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchProducts() throws Exception {
        int databaseSizeBeforeUpdate = productsRepository.findAll().size();
        products.setId(count.incrementAndGet());

        // Create the Products
        ProductsDTO productsDTO = productsMapper.toDto(products);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProductsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(productsDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Products in the database
        List<Products> productsList = productsRepository.findAll();
        assertThat(productsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamProducts() throws Exception {
        int databaseSizeBeforeUpdate = productsRepository.findAll().size();
        products.setId(count.incrementAndGet());

        // Create the Products
        ProductsDTO productsDTO = productsMapper.toDto(products);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProductsMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(productsDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Products in the database
        List<Products> productsList = productsRepository.findAll();
        assertThat(productsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateProductsWithPatch() throws Exception {
        // Initialize the database
        productsRepository.saveAndFlush(products);

        int databaseSizeBeforeUpdate = productsRepository.findAll().size();

        // Update the products using partial update
        Products partialUpdatedProducts = new Products();
        partialUpdatedProducts.setId(products.getId());

        partialUpdatedProducts.name(UPDATED_NAME).quantity(UPDATED_QUANTITY);

        restProductsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedProducts.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedProducts))
            )
            .andExpect(status().isOk());

        // Validate the Products in the database
        List<Products> productsList = productsRepository.findAll();
        assertThat(productsList).hasSize(databaseSizeBeforeUpdate);
        Products testProducts = productsList.get(productsList.size() - 1);
        assertThat(testProducts.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testProducts.getPrice()).isEqualTo(DEFAULT_PRICE);
        assertThat(testProducts.getQuantity()).isEqualTo(UPDATED_QUANTITY);
        assertThat(testProducts.getImageURL()).isEqualTo(DEFAULT_IMAGE_URL);
        assertThat(testProducts.getImageURLContentType()).isEqualTo(DEFAULT_IMAGE_URL_CONTENT_TYPE);
        assertThat(testProducts.getCreatedDate()).isEqualTo(DEFAULT_CREATED_DATE);
        assertThat(testProducts.getUpdatedDate()).isEqualTo(DEFAULT_UPDATED_DATE);
    }

    @Test
    @Transactional
    void fullUpdateProductsWithPatch() throws Exception {
        // Initialize the database
        productsRepository.saveAndFlush(products);

        int databaseSizeBeforeUpdate = productsRepository.findAll().size();

        // Update the products using partial update
        Products partialUpdatedProducts = new Products();
        partialUpdatedProducts.setId(products.getId());

        partialUpdatedProducts
            .name(UPDATED_NAME)
            .price(UPDATED_PRICE)
            .quantity(UPDATED_QUANTITY)
            .imageURL(UPDATED_IMAGE_URL)
            .imageURLContentType(UPDATED_IMAGE_URL_CONTENT_TYPE)
            .createdDate(UPDATED_CREATED_DATE)
            .updatedDate(UPDATED_UPDATED_DATE);

        restProductsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedProducts.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedProducts))
            )
            .andExpect(status().isOk());

        // Validate the Products in the database
        List<Products> productsList = productsRepository.findAll();
        assertThat(productsList).hasSize(databaseSizeBeforeUpdate);
        Products testProducts = productsList.get(productsList.size() - 1);
        assertThat(testProducts.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testProducts.getPrice()).isEqualTo(UPDATED_PRICE);
        assertThat(testProducts.getQuantity()).isEqualTo(UPDATED_QUANTITY);
        assertThat(testProducts.getImageURL()).isEqualTo(UPDATED_IMAGE_URL);
        assertThat(testProducts.getImageURLContentType()).isEqualTo(UPDATED_IMAGE_URL_CONTENT_TYPE);
        assertThat(testProducts.getCreatedDate()).isEqualTo(UPDATED_CREATED_DATE);
        assertThat(testProducts.getUpdatedDate()).isEqualTo(UPDATED_UPDATED_DATE);
    }

    @Test
    @Transactional
    void patchNonExistingProducts() throws Exception {
        int databaseSizeBeforeUpdate = productsRepository.findAll().size();
        products.setId(count.incrementAndGet());

        // Create the Products
        ProductsDTO productsDTO = productsMapper.toDto(products);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restProductsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, productsDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(productsDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Products in the database
        List<Products> productsList = productsRepository.findAll();
        assertThat(productsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchProducts() throws Exception {
        int databaseSizeBeforeUpdate = productsRepository.findAll().size();
        products.setId(count.incrementAndGet());

        // Create the Products
        ProductsDTO productsDTO = productsMapper.toDto(products);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProductsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(productsDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Products in the database
        List<Products> productsList = productsRepository.findAll();
        assertThat(productsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamProducts() throws Exception {
        int databaseSizeBeforeUpdate = productsRepository.findAll().size();
        products.setId(count.incrementAndGet());

        // Create the Products
        ProductsDTO productsDTO = productsMapper.toDto(products);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProductsMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(productsDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Products in the database
        List<Products> productsList = productsRepository.findAll();
        assertThat(productsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteProducts() throws Exception {
        // Initialize the database
        productsRepository.saveAndFlush(products);

        int databaseSizeBeforeDelete = productsRepository.findAll().size();

        // Delete the products
        restProductsMockMvc
            .perform(delete(ENTITY_API_URL_ID, products.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Products> productsList = productsRepository.findAll();
        assertThat(productsList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
