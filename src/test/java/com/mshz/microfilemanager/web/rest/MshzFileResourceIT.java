package com.mshz.microfilemanager.web.rest;

import com.mshz.microfilemanager.MicrofilemanagerApp;
import com.mshz.microfilemanager.domain.MshzFile;
import com.mshz.microfilemanager.repository.MshzFileRepository;
import com.mshz.microfilemanager.service.MshzFileService;
import com.mshz.microfilemanager.service.dto.MshzFileCriteria;
import com.mshz.microfilemanager.service.MshzFileQueryService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Base64Utils;
import javax.persistence.EntityManager;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the {@link MshzFileResource} REST controller.
 */
@SpringBootTest(classes = MicrofilemanagerApp.class)
@AutoConfigureMockMvc
@WithMockUser
public class MshzFileResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final byte[] DEFAULT_F_DATA = TestUtil.createByteArray(1, "0");
    private static final byte[] UPDATED_F_DATA = TestUtil.createByteArray(1, "1");
    private static final String DEFAULT_F_DATA_CONTENT_TYPE = "image/jpg";
    private static final String UPDATED_F_DATA_CONTENT_TYPE = "image/png";

    private static final Long DEFAULT_ENTITY_ID = 1L;
    private static final Long UPDATED_ENTITY_ID = 2L;
    private static final Long SMALLER_ENTITY_ID = 1L - 1L;

    private static final String DEFAULT_ENTITY_TAG_NAME = "AAAAAAAAAA";
    private static final String UPDATED_ENTITY_TAG_NAME = "BBBBBBBBBB";

    private static final Long DEFAULT_USER_ID = 1L;
    private static final Long UPDATED_USER_ID = 2L;
    private static final Long SMALLER_USER_ID = 1L - 1L;

    private static final Instant DEFAULT_STORE_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_STORE_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    @Autowired
    private MshzFileRepository mshzFileRepository;

    @Autowired
    private MshzFileService mshzFileService;

    @Autowired
    private MshzFileQueryService mshzFileQueryService;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restMshzFileMockMvc;

    private MshzFile mshzFile;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static MshzFile createEntity(EntityManager em) {
        MshzFile mshzFile = new MshzFile()
            .name(DEFAULT_NAME)
            .fData(DEFAULT_F_DATA)
            .fDataContentType(DEFAULT_F_DATA_CONTENT_TYPE)
            .entityId(DEFAULT_ENTITY_ID)
            .entityTagName(DEFAULT_ENTITY_TAG_NAME)
            .userId(DEFAULT_USER_ID)
            .storeAt(DEFAULT_STORE_AT);
        return mshzFile;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static MshzFile createUpdatedEntity(EntityManager em) {
        MshzFile mshzFile = new MshzFile()
            .name(UPDATED_NAME)
            .fData(UPDATED_F_DATA)
            .fDataContentType(UPDATED_F_DATA_CONTENT_TYPE)
            .entityId(UPDATED_ENTITY_ID)
            .entityTagName(UPDATED_ENTITY_TAG_NAME)
            .userId(UPDATED_USER_ID)
            .storeAt(UPDATED_STORE_AT);
        return mshzFile;
    }

    @BeforeEach
    public void initTest() {
        mshzFile = createEntity(em);
    }

    @Test
    @Transactional
    public void createMshzFile() throws Exception {
        int databaseSizeBeforeCreate = mshzFileRepository.findAll().size();
        // Create the MshzFile
        restMshzFileMockMvc.perform(post("/api/mshz-files")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(mshzFile)))
            .andExpect(status().isCreated());

        // Validate the MshzFile in the database
        List<MshzFile> mshzFileList = mshzFileRepository.findAll();
        assertThat(mshzFileList).hasSize(databaseSizeBeforeCreate + 1);
        MshzFile testMshzFile = mshzFileList.get(mshzFileList.size() - 1);
        assertThat(testMshzFile.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testMshzFile.getfData()).isEqualTo(DEFAULT_F_DATA);
        assertThat(testMshzFile.getfDataContentType()).isEqualTo(DEFAULT_F_DATA_CONTENT_TYPE);
        assertThat(testMshzFile.getEntityId()).isEqualTo(DEFAULT_ENTITY_ID);
        assertThat(testMshzFile.getEntityTagName()).isEqualTo(DEFAULT_ENTITY_TAG_NAME);
        assertThat(testMshzFile.getUserId()).isEqualTo(DEFAULT_USER_ID);
        assertThat(testMshzFile.getStoreAt()).isEqualTo(DEFAULT_STORE_AT);
    }

    @Test
    @Transactional
    public void createMshzFileWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = mshzFileRepository.findAll().size();

        // Create the MshzFile with an existing ID
        mshzFile.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restMshzFileMockMvc.perform(post("/api/mshz-files")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(mshzFile)))
            .andExpect(status().isBadRequest());

        // Validate the MshzFile in the database
        List<MshzFile> mshzFileList = mshzFileRepository.findAll();
        assertThat(mshzFileList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    @Transactional
    public void getAllMshzFiles() throws Exception {
        // Initialize the database
        mshzFileRepository.saveAndFlush(mshzFile);

        // Get all the mshzFileList
        restMshzFileMockMvc.perform(get("/api/mshz-files?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(mshzFile.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].fDataContentType").value(hasItem(DEFAULT_F_DATA_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].fData").value(hasItem(Base64Utils.encodeToString(DEFAULT_F_DATA))))
            .andExpect(jsonPath("$.[*].entityId").value(hasItem(DEFAULT_ENTITY_ID.intValue())))
            .andExpect(jsonPath("$.[*].entityTagName").value(hasItem(DEFAULT_ENTITY_TAG_NAME)))
            .andExpect(jsonPath("$.[*].userId").value(hasItem(DEFAULT_USER_ID.intValue())))
            .andExpect(jsonPath("$.[*].storeAt").value(hasItem(DEFAULT_STORE_AT.toString())));
    }
    
    @Test
    @Transactional
    public void getMshzFile() throws Exception {
        // Initialize the database
        mshzFileRepository.saveAndFlush(mshzFile);

        // Get the mshzFile
        restMshzFileMockMvc.perform(get("/api/mshz-files/{id}", mshzFile.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(mshzFile.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.fDataContentType").value(DEFAULT_F_DATA_CONTENT_TYPE))
            .andExpect(jsonPath("$.fData").value(Base64Utils.encodeToString(DEFAULT_F_DATA)))
            .andExpect(jsonPath("$.entityId").value(DEFAULT_ENTITY_ID.intValue()))
            .andExpect(jsonPath("$.entityTagName").value(DEFAULT_ENTITY_TAG_NAME))
            .andExpect(jsonPath("$.userId").value(DEFAULT_USER_ID.intValue()))
            .andExpect(jsonPath("$.storeAt").value(DEFAULT_STORE_AT.toString()));
    }


    @Test
    @Transactional
    public void getMshzFilesByIdFiltering() throws Exception {
        // Initialize the database
        mshzFileRepository.saveAndFlush(mshzFile);

        Long id = mshzFile.getId();

        defaultMshzFileShouldBeFound("id.equals=" + id);
        defaultMshzFileShouldNotBeFound("id.notEquals=" + id);

        defaultMshzFileShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultMshzFileShouldNotBeFound("id.greaterThan=" + id);

        defaultMshzFileShouldBeFound("id.lessThanOrEqual=" + id);
        defaultMshzFileShouldNotBeFound("id.lessThan=" + id);
    }


    @Test
    @Transactional
    public void getAllMshzFilesByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        mshzFileRepository.saveAndFlush(mshzFile);

        // Get all the mshzFileList where name equals to DEFAULT_NAME
        defaultMshzFileShouldBeFound("name.equals=" + DEFAULT_NAME);

        // Get all the mshzFileList where name equals to UPDATED_NAME
        defaultMshzFileShouldNotBeFound("name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllMshzFilesByNameIsNotEqualToSomething() throws Exception {
        // Initialize the database
        mshzFileRepository.saveAndFlush(mshzFile);

        // Get all the mshzFileList where name not equals to DEFAULT_NAME
        defaultMshzFileShouldNotBeFound("name.notEquals=" + DEFAULT_NAME);

        // Get all the mshzFileList where name not equals to UPDATED_NAME
        defaultMshzFileShouldBeFound("name.notEquals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllMshzFilesByNameIsInShouldWork() throws Exception {
        // Initialize the database
        mshzFileRepository.saveAndFlush(mshzFile);

        // Get all the mshzFileList where name in DEFAULT_NAME or UPDATED_NAME
        defaultMshzFileShouldBeFound("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME);

        // Get all the mshzFileList where name equals to UPDATED_NAME
        defaultMshzFileShouldNotBeFound("name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllMshzFilesByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        mshzFileRepository.saveAndFlush(mshzFile);

        // Get all the mshzFileList where name is not null
        defaultMshzFileShouldBeFound("name.specified=true");

        // Get all the mshzFileList where name is null
        defaultMshzFileShouldNotBeFound("name.specified=false");
    }
                @Test
    @Transactional
    public void getAllMshzFilesByNameContainsSomething() throws Exception {
        // Initialize the database
        mshzFileRepository.saveAndFlush(mshzFile);

        // Get all the mshzFileList where name contains DEFAULT_NAME
        defaultMshzFileShouldBeFound("name.contains=" + DEFAULT_NAME);

        // Get all the mshzFileList where name contains UPDATED_NAME
        defaultMshzFileShouldNotBeFound("name.contains=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllMshzFilesByNameNotContainsSomething() throws Exception {
        // Initialize the database
        mshzFileRepository.saveAndFlush(mshzFile);

        // Get all the mshzFileList where name does not contain DEFAULT_NAME
        defaultMshzFileShouldNotBeFound("name.doesNotContain=" + DEFAULT_NAME);

        // Get all the mshzFileList where name does not contain UPDATED_NAME
        defaultMshzFileShouldBeFound("name.doesNotContain=" + UPDATED_NAME);
    }


    @Test
    @Transactional
    public void getAllMshzFilesByEntityIdIsEqualToSomething() throws Exception {
        // Initialize the database
        mshzFileRepository.saveAndFlush(mshzFile);

        // Get all the mshzFileList where entityId equals to DEFAULT_ENTITY_ID
        defaultMshzFileShouldBeFound("entityId.equals=" + DEFAULT_ENTITY_ID);

        // Get all the mshzFileList where entityId equals to UPDATED_ENTITY_ID
        defaultMshzFileShouldNotBeFound("entityId.equals=" + UPDATED_ENTITY_ID);
    }

    @Test
    @Transactional
    public void getAllMshzFilesByEntityIdIsNotEqualToSomething() throws Exception {
        // Initialize the database
        mshzFileRepository.saveAndFlush(mshzFile);

        // Get all the mshzFileList where entityId not equals to DEFAULT_ENTITY_ID
        defaultMshzFileShouldNotBeFound("entityId.notEquals=" + DEFAULT_ENTITY_ID);

        // Get all the mshzFileList where entityId not equals to UPDATED_ENTITY_ID
        defaultMshzFileShouldBeFound("entityId.notEquals=" + UPDATED_ENTITY_ID);
    }

    @Test
    @Transactional
    public void getAllMshzFilesByEntityIdIsInShouldWork() throws Exception {
        // Initialize the database
        mshzFileRepository.saveAndFlush(mshzFile);

        // Get all the mshzFileList where entityId in DEFAULT_ENTITY_ID or UPDATED_ENTITY_ID
        defaultMshzFileShouldBeFound("entityId.in=" + DEFAULT_ENTITY_ID + "," + UPDATED_ENTITY_ID);

        // Get all the mshzFileList where entityId equals to UPDATED_ENTITY_ID
        defaultMshzFileShouldNotBeFound("entityId.in=" + UPDATED_ENTITY_ID);
    }

    @Test
    @Transactional
    public void getAllMshzFilesByEntityIdIsNullOrNotNull() throws Exception {
        // Initialize the database
        mshzFileRepository.saveAndFlush(mshzFile);

        // Get all the mshzFileList where entityId is not null
        defaultMshzFileShouldBeFound("entityId.specified=true");

        // Get all the mshzFileList where entityId is null
        defaultMshzFileShouldNotBeFound("entityId.specified=false");
    }

    @Test
    @Transactional
    public void getAllMshzFilesByEntityIdIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        mshzFileRepository.saveAndFlush(mshzFile);

        // Get all the mshzFileList where entityId is greater than or equal to DEFAULT_ENTITY_ID
        defaultMshzFileShouldBeFound("entityId.greaterThanOrEqual=" + DEFAULT_ENTITY_ID);

        // Get all the mshzFileList where entityId is greater than or equal to UPDATED_ENTITY_ID
        defaultMshzFileShouldNotBeFound("entityId.greaterThanOrEqual=" + UPDATED_ENTITY_ID);
    }

    @Test
    @Transactional
    public void getAllMshzFilesByEntityIdIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        mshzFileRepository.saveAndFlush(mshzFile);

        // Get all the mshzFileList where entityId is less than or equal to DEFAULT_ENTITY_ID
        defaultMshzFileShouldBeFound("entityId.lessThanOrEqual=" + DEFAULT_ENTITY_ID);

        // Get all the mshzFileList where entityId is less than or equal to SMALLER_ENTITY_ID
        defaultMshzFileShouldNotBeFound("entityId.lessThanOrEqual=" + SMALLER_ENTITY_ID);
    }

    @Test
    @Transactional
    public void getAllMshzFilesByEntityIdIsLessThanSomething() throws Exception {
        // Initialize the database
        mshzFileRepository.saveAndFlush(mshzFile);

        // Get all the mshzFileList where entityId is less than DEFAULT_ENTITY_ID
        defaultMshzFileShouldNotBeFound("entityId.lessThan=" + DEFAULT_ENTITY_ID);

        // Get all the mshzFileList where entityId is less than UPDATED_ENTITY_ID
        defaultMshzFileShouldBeFound("entityId.lessThan=" + UPDATED_ENTITY_ID);
    }

    @Test
    @Transactional
    public void getAllMshzFilesByEntityIdIsGreaterThanSomething() throws Exception {
        // Initialize the database
        mshzFileRepository.saveAndFlush(mshzFile);

        // Get all the mshzFileList where entityId is greater than DEFAULT_ENTITY_ID
        defaultMshzFileShouldNotBeFound("entityId.greaterThan=" + DEFAULT_ENTITY_ID);

        // Get all the mshzFileList where entityId is greater than SMALLER_ENTITY_ID
        defaultMshzFileShouldBeFound("entityId.greaterThan=" + SMALLER_ENTITY_ID);
    }


    @Test
    @Transactional
    public void getAllMshzFilesByEntityTagNameIsEqualToSomething() throws Exception {
        // Initialize the database
        mshzFileRepository.saveAndFlush(mshzFile);

        // Get all the mshzFileList where entityTagName equals to DEFAULT_ENTITY_TAG_NAME
        defaultMshzFileShouldBeFound("entityTagName.equals=" + DEFAULT_ENTITY_TAG_NAME);

        // Get all the mshzFileList where entityTagName equals to UPDATED_ENTITY_TAG_NAME
        defaultMshzFileShouldNotBeFound("entityTagName.equals=" + UPDATED_ENTITY_TAG_NAME);
    }

    @Test
    @Transactional
    public void getAllMshzFilesByEntityTagNameIsNotEqualToSomething() throws Exception {
        // Initialize the database
        mshzFileRepository.saveAndFlush(mshzFile);

        // Get all the mshzFileList where entityTagName not equals to DEFAULT_ENTITY_TAG_NAME
        defaultMshzFileShouldNotBeFound("entityTagName.notEquals=" + DEFAULT_ENTITY_TAG_NAME);

        // Get all the mshzFileList where entityTagName not equals to UPDATED_ENTITY_TAG_NAME
        defaultMshzFileShouldBeFound("entityTagName.notEquals=" + UPDATED_ENTITY_TAG_NAME);
    }

    @Test
    @Transactional
    public void getAllMshzFilesByEntityTagNameIsInShouldWork() throws Exception {
        // Initialize the database
        mshzFileRepository.saveAndFlush(mshzFile);

        // Get all the mshzFileList where entityTagName in DEFAULT_ENTITY_TAG_NAME or UPDATED_ENTITY_TAG_NAME
        defaultMshzFileShouldBeFound("entityTagName.in=" + DEFAULT_ENTITY_TAG_NAME + "," + UPDATED_ENTITY_TAG_NAME);

        // Get all the mshzFileList where entityTagName equals to UPDATED_ENTITY_TAG_NAME
        defaultMshzFileShouldNotBeFound("entityTagName.in=" + UPDATED_ENTITY_TAG_NAME);
    }

    @Test
    @Transactional
    public void getAllMshzFilesByEntityTagNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        mshzFileRepository.saveAndFlush(mshzFile);

        // Get all the mshzFileList where entityTagName is not null
        defaultMshzFileShouldBeFound("entityTagName.specified=true");

        // Get all the mshzFileList where entityTagName is null
        defaultMshzFileShouldNotBeFound("entityTagName.specified=false");
    }
                @Test
    @Transactional
    public void getAllMshzFilesByEntityTagNameContainsSomething() throws Exception {
        // Initialize the database
        mshzFileRepository.saveAndFlush(mshzFile);

        // Get all the mshzFileList where entityTagName contains DEFAULT_ENTITY_TAG_NAME
        defaultMshzFileShouldBeFound("entityTagName.contains=" + DEFAULT_ENTITY_TAG_NAME);

        // Get all the mshzFileList where entityTagName contains UPDATED_ENTITY_TAG_NAME
        defaultMshzFileShouldNotBeFound("entityTagName.contains=" + UPDATED_ENTITY_TAG_NAME);
    }

    @Test
    @Transactional
    public void getAllMshzFilesByEntityTagNameNotContainsSomething() throws Exception {
        // Initialize the database
        mshzFileRepository.saveAndFlush(mshzFile);

        // Get all the mshzFileList where entityTagName does not contain DEFAULT_ENTITY_TAG_NAME
        defaultMshzFileShouldNotBeFound("entityTagName.doesNotContain=" + DEFAULT_ENTITY_TAG_NAME);

        // Get all the mshzFileList where entityTagName does not contain UPDATED_ENTITY_TAG_NAME
        defaultMshzFileShouldBeFound("entityTagName.doesNotContain=" + UPDATED_ENTITY_TAG_NAME);
    }


    @Test
    @Transactional
    public void getAllMshzFilesByUserIdIsEqualToSomething() throws Exception {
        // Initialize the database
        mshzFileRepository.saveAndFlush(mshzFile);

        // Get all the mshzFileList where userId equals to DEFAULT_USER_ID
        defaultMshzFileShouldBeFound("userId.equals=" + DEFAULT_USER_ID);

        // Get all the mshzFileList where userId equals to UPDATED_USER_ID
        defaultMshzFileShouldNotBeFound("userId.equals=" + UPDATED_USER_ID);
    }

    @Test
    @Transactional
    public void getAllMshzFilesByUserIdIsNotEqualToSomething() throws Exception {
        // Initialize the database
        mshzFileRepository.saveAndFlush(mshzFile);

        // Get all the mshzFileList where userId not equals to DEFAULT_USER_ID
        defaultMshzFileShouldNotBeFound("userId.notEquals=" + DEFAULT_USER_ID);

        // Get all the mshzFileList where userId not equals to UPDATED_USER_ID
        defaultMshzFileShouldBeFound("userId.notEquals=" + UPDATED_USER_ID);
    }

    @Test
    @Transactional
    public void getAllMshzFilesByUserIdIsInShouldWork() throws Exception {
        // Initialize the database
        mshzFileRepository.saveAndFlush(mshzFile);

        // Get all the mshzFileList where userId in DEFAULT_USER_ID or UPDATED_USER_ID
        defaultMshzFileShouldBeFound("userId.in=" + DEFAULT_USER_ID + "," + UPDATED_USER_ID);

        // Get all the mshzFileList where userId equals to UPDATED_USER_ID
        defaultMshzFileShouldNotBeFound("userId.in=" + UPDATED_USER_ID);
    }

    @Test
    @Transactional
    public void getAllMshzFilesByUserIdIsNullOrNotNull() throws Exception {
        // Initialize the database
        mshzFileRepository.saveAndFlush(mshzFile);

        // Get all the mshzFileList where userId is not null
        defaultMshzFileShouldBeFound("userId.specified=true");

        // Get all the mshzFileList where userId is null
        defaultMshzFileShouldNotBeFound("userId.specified=false");
    }

    @Test
    @Transactional
    public void getAllMshzFilesByUserIdIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        mshzFileRepository.saveAndFlush(mshzFile);

        // Get all the mshzFileList where userId is greater than or equal to DEFAULT_USER_ID
        defaultMshzFileShouldBeFound("userId.greaterThanOrEqual=" + DEFAULT_USER_ID);

        // Get all the mshzFileList where userId is greater than or equal to UPDATED_USER_ID
        defaultMshzFileShouldNotBeFound("userId.greaterThanOrEqual=" + UPDATED_USER_ID);
    }

    @Test
    @Transactional
    public void getAllMshzFilesByUserIdIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        mshzFileRepository.saveAndFlush(mshzFile);

        // Get all the mshzFileList where userId is less than or equal to DEFAULT_USER_ID
        defaultMshzFileShouldBeFound("userId.lessThanOrEqual=" + DEFAULT_USER_ID);

        // Get all the mshzFileList where userId is less than or equal to SMALLER_USER_ID
        defaultMshzFileShouldNotBeFound("userId.lessThanOrEqual=" + SMALLER_USER_ID);
    }

    @Test
    @Transactional
    public void getAllMshzFilesByUserIdIsLessThanSomething() throws Exception {
        // Initialize the database
        mshzFileRepository.saveAndFlush(mshzFile);

        // Get all the mshzFileList where userId is less than DEFAULT_USER_ID
        defaultMshzFileShouldNotBeFound("userId.lessThan=" + DEFAULT_USER_ID);

        // Get all the mshzFileList where userId is less than UPDATED_USER_ID
        defaultMshzFileShouldBeFound("userId.lessThan=" + UPDATED_USER_ID);
    }

    @Test
    @Transactional
    public void getAllMshzFilesByUserIdIsGreaterThanSomething() throws Exception {
        // Initialize the database
        mshzFileRepository.saveAndFlush(mshzFile);

        // Get all the mshzFileList where userId is greater than DEFAULT_USER_ID
        defaultMshzFileShouldNotBeFound("userId.greaterThan=" + DEFAULT_USER_ID);

        // Get all the mshzFileList where userId is greater than SMALLER_USER_ID
        defaultMshzFileShouldBeFound("userId.greaterThan=" + SMALLER_USER_ID);
    }


    @Test
    @Transactional
    public void getAllMshzFilesByStoreAtIsEqualToSomething() throws Exception {
        // Initialize the database
        mshzFileRepository.saveAndFlush(mshzFile);

        // Get all the mshzFileList where storeAt equals to DEFAULT_STORE_AT
        defaultMshzFileShouldBeFound("storeAt.equals=" + DEFAULT_STORE_AT);

        // Get all the mshzFileList where storeAt equals to UPDATED_STORE_AT
        defaultMshzFileShouldNotBeFound("storeAt.equals=" + UPDATED_STORE_AT);
    }

    @Test
    @Transactional
    public void getAllMshzFilesByStoreAtIsNotEqualToSomething() throws Exception {
        // Initialize the database
        mshzFileRepository.saveAndFlush(mshzFile);

        // Get all the mshzFileList where storeAt not equals to DEFAULT_STORE_AT
        defaultMshzFileShouldNotBeFound("storeAt.notEquals=" + DEFAULT_STORE_AT);

        // Get all the mshzFileList where storeAt not equals to UPDATED_STORE_AT
        defaultMshzFileShouldBeFound("storeAt.notEquals=" + UPDATED_STORE_AT);
    }

    @Test
    @Transactional
    public void getAllMshzFilesByStoreAtIsInShouldWork() throws Exception {
        // Initialize the database
        mshzFileRepository.saveAndFlush(mshzFile);

        // Get all the mshzFileList where storeAt in DEFAULT_STORE_AT or UPDATED_STORE_AT
        defaultMshzFileShouldBeFound("storeAt.in=" + DEFAULT_STORE_AT + "," + UPDATED_STORE_AT);

        // Get all the mshzFileList where storeAt equals to UPDATED_STORE_AT
        defaultMshzFileShouldNotBeFound("storeAt.in=" + UPDATED_STORE_AT);
    }

    @Test
    @Transactional
    public void getAllMshzFilesByStoreAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        mshzFileRepository.saveAndFlush(mshzFile);

        // Get all the mshzFileList where storeAt is not null
        defaultMshzFileShouldBeFound("storeAt.specified=true");

        // Get all the mshzFileList where storeAt is null
        defaultMshzFileShouldNotBeFound("storeAt.specified=false");
    }
    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultMshzFileShouldBeFound(String filter) throws Exception {
        restMshzFileMockMvc.perform(get("/api/mshz-files?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(mshzFile.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].fDataContentType").value(hasItem(DEFAULT_F_DATA_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].fData").value(hasItem(Base64Utils.encodeToString(DEFAULT_F_DATA))))
            .andExpect(jsonPath("$.[*].entityId").value(hasItem(DEFAULT_ENTITY_ID.intValue())))
            .andExpect(jsonPath("$.[*].entityTagName").value(hasItem(DEFAULT_ENTITY_TAG_NAME)))
            .andExpect(jsonPath("$.[*].userId").value(hasItem(DEFAULT_USER_ID.intValue())))
            .andExpect(jsonPath("$.[*].storeAt").value(hasItem(DEFAULT_STORE_AT.toString())));

        // Check, that the count call also returns 1
        restMshzFileMockMvc.perform(get("/api/mshz-files/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultMshzFileShouldNotBeFound(String filter) throws Exception {
        restMshzFileMockMvc.perform(get("/api/mshz-files?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restMshzFileMockMvc.perform(get("/api/mshz-files/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    public void getNonExistingMshzFile() throws Exception {
        // Get the mshzFile
        restMshzFileMockMvc.perform(get("/api/mshz-files/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateMshzFile() throws Exception {
        // Initialize the database
        mshzFileService.save(mshzFile);

        int databaseSizeBeforeUpdate = mshzFileRepository.findAll().size();

        // Update the mshzFile
        MshzFile updatedMshzFile = mshzFileRepository.findById(mshzFile.getId()).get();
        // Disconnect from session so that the updates on updatedMshzFile are not directly saved in db
        em.detach(updatedMshzFile);
        updatedMshzFile
            .name(UPDATED_NAME)
            .fData(UPDATED_F_DATA)
            .fDataContentType(UPDATED_F_DATA_CONTENT_TYPE)
            .entityId(UPDATED_ENTITY_ID)
            .entityTagName(UPDATED_ENTITY_TAG_NAME)
            .userId(UPDATED_USER_ID)
            .storeAt(UPDATED_STORE_AT);

        restMshzFileMockMvc.perform(put("/api/mshz-files")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(updatedMshzFile)))
            .andExpect(status().isOk());

        // Validate the MshzFile in the database
        List<MshzFile> mshzFileList = mshzFileRepository.findAll();
        assertThat(mshzFileList).hasSize(databaseSizeBeforeUpdate);
        MshzFile testMshzFile = mshzFileList.get(mshzFileList.size() - 1);
        assertThat(testMshzFile.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testMshzFile.getfData()).isEqualTo(UPDATED_F_DATA);
        assertThat(testMshzFile.getfDataContentType()).isEqualTo(UPDATED_F_DATA_CONTENT_TYPE);
        assertThat(testMshzFile.getEntityId()).isEqualTo(UPDATED_ENTITY_ID);
        assertThat(testMshzFile.getEntityTagName()).isEqualTo(UPDATED_ENTITY_TAG_NAME);
        assertThat(testMshzFile.getUserId()).isEqualTo(UPDATED_USER_ID);
        assertThat(testMshzFile.getStoreAt()).isEqualTo(UPDATED_STORE_AT);
    }

    @Test
    @Transactional
    public void updateNonExistingMshzFile() throws Exception {
        int databaseSizeBeforeUpdate = mshzFileRepository.findAll().size();

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restMshzFileMockMvc.perform(put("/api/mshz-files")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(mshzFile)))
            .andExpect(status().isBadRequest());

        // Validate the MshzFile in the database
        List<MshzFile> mshzFileList = mshzFileRepository.findAll();
        assertThat(mshzFileList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteMshzFile() throws Exception {
        // Initialize the database
        mshzFileService.save(mshzFile);

        int databaseSizeBeforeDelete = mshzFileRepository.findAll().size();

        // Delete the mshzFile
        restMshzFileMockMvc.perform(delete("/api/mshz-files/{id}", mshzFile.getId())
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<MshzFile> mshzFileList = mshzFileRepository.findAll();
        assertThat(mshzFileList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
