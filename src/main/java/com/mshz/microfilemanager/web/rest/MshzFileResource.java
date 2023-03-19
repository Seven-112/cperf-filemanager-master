package com.mshz.microfilemanager.web.rest;

import com.mshz.microfilemanager.domain.MshzFile;
import com.mshz.microfilemanager.model.FileChunkMetadata;
import com.mshz.microfilemanager.service.MshzFileService;
import com.mshz.microfilemanager.web.rest.errors.BadRequestAlertException;
import com.mshz.microfilemanager.service.dto.MshzFileCriteria;
import com.mshz.microfilemanager.service.MshzFileQueryService;

import io.github.jhipster.web.util.HeaderUtil;
import io.github.jhipster.web.util.PaginationUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing {@link com.mshz.microfilemanager.domain.MshzFile}.
 */
@RestController
@RequestMapping("/api")
public class MshzFileResource {

    private final Logger log = LoggerFactory.getLogger(MshzFileResource.class);

    private static final String ENTITY_NAME = "microfilemanagerMshzFile";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    @Value("${my-config.file.upload-root-path}")
    private String uploaderFolderPath;

    private final MshzFileService mshzFileService;

    private final MshzFileQueryService mshzFileQueryService;

    public MshzFileResource(MshzFileService mshzFileService, MshzFileQueryService mshzFileQueryService) {
        this.mshzFileService = mshzFileService;
        this.mshzFileQueryService = mshzFileQueryService;
    }

    /**
     * {@code POST  /mshz-files} : Create a new mshzFile.
     *
     * @param mshzFile the mshzFile to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new mshzFile, or with status {@code 400 (Bad Request)} if the mshzFile has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/mshz-files")
    public ResponseEntity<MshzFile> createMshzFile(@Valid @RequestBody MshzFile mshzFile) throws URISyntaxException {
        log.debug("REST request to save MshzFile : {}", mshzFile);
        if (mshzFile.getId() != null) {
            throw new BadRequestAlertException("A new mshzFile cannot already have an ID", ENTITY_NAME, "idexists");
        }
        MshzFile result = mshzFileService.save(mshzFile);
        return ResponseEntity.created(new URI("/api/mshz-files/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /mshz-files} : Updates an existing mshzFile.
     *
     * @param mshzFile the mshzFile to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated mshzFile,
     * or with status {@code 400 (Bad Request)} if the mshzFile is not valid,
     * or with status {@code 500 (Internal Server Error)} if the mshzFile couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/mshz-files")
    public ResponseEntity<MshzFile> updateMshzFile(@Valid @RequestBody MshzFile mshzFile) throws URISyntaxException {
        log.debug("REST request to update MshzFile : {}", mshzFile);
        if (mshzFile.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        MshzFile result = mshzFileService.save(mshzFile);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, mshzFile.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /mshz-files} : get all the mshzFiles.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of mshzFiles in body.
     */
    @GetMapping("/mshz-files")
    public ResponseEntity<List<MshzFile>> getAllMshzFiles(MshzFileCriteria criteria, Pageable pageable) {
        log.debug("REST request to get MshzFiles by criteria: {}", criteria);
        Page<MshzFile> page = mshzFileQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /mshz-files/count} : count all the mshzFiles.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/mshz-files/count")
    public ResponseEntity<Long> countMshzFiles(MshzFileCriteria criteria) {
        log.debug("REST request to count MshzFiles by criteria: {}", criteria);
        return ResponseEntity.ok().body(mshzFileQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /mshz-files/:id} : get the "id" mshzFile.
     *
     * @param id the id of the mshzFile to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the mshzFile, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/mshz-files/{id}")
    public ResponseEntity<MshzFile> getMshzFile(@PathVariable Long id) {
        log.debug("REST request to get MshzFile : {}", id);
        Optional<MshzFile> mshzFile = mshzFileService.findOne(id);
        return ResponseUtil.wrapOrNotFound(mshzFile);
    }

    /**
     * {@code DELETE  /mshz-files/:id} : delete the "id" mshzFile.
     *
     * @param id the id of the mshzFile to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/mshz-files/{id}")
    public ResponseEntity<Void> deleteMshzFile(@PathVariable Long id) {
        log.debug("REST request to delete MshzFile : {}", id);
        mshzFileService.delete(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build();
    }
    
    @GetMapping("/mshz-files/uploadFolderPath")
    public ResponseEntity<String> getUploaderFolderPath() {
        log.debug("REST request to get uploader folder");;
        return ResponseEntity.ok(uploaderFolderPath);
    }
    
    @PostMapping(value = "/mshz-files/uploadFile",  consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MshzFile> uploadFile(@RequestParam MultipartFile file) throws URISyntaxException {
        log.debug("REST request to upload file : {}", file);
        if (file == null) {
            throw new BadRequestAlertException("A new mshzFile cannot already have an ID", ENTITY_NAME, "idexists");
        }

        MshzFile result = mshzFileService.uploadFile(file);
        String id = result != null && result.getId() != null ? result.getId().toString() : "";
        return ResponseEntity.created(new URI("/api/mshz-files/uploadFile/" + id))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, id))
            .body(result);
    }

    @PostMapping(value = "/mshz-files/uploadChunk", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MshzFile> uploadChunk(
            @RequestParam("file") MultipartFile file,
            @RequestParam("name") String name,
            @RequestParam("chunk") Integer chunk,
            @RequestParam("chunks") Integer chunks,
            @RequestParam(name = "entityId", required = false) Long entityId,
            @RequestParam("entityTagName") String entityTagName,
            @RequestParam(name = "userId", required = false) Long userId
        ) throws URISyntaxException {
        log.debug("REST request upload chunk: {} for file: {}", chunk, name);
        MshzFile result = mshzFileService.uploadChunk(file,new FileChunkMetadata(name, chunk, chunks, entityId, entityTagName, userId));
        String id = result != null && result.getId() != null ? result.getId().toString() : "";
        return ResponseEntity.created(new URI("/api/mshz-files/uploadChunk/" + id))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, id))
            .body(result);
    }

    @GetMapping("/mshz-files/associateFileToEntity")
    public ResponseEntity<Integer> associateFileToEntity(@RequestParam("entityId") Long entityId, 
        @RequestParam("tag") String entityTagName, @RequestParam("userId") Long userId){
        log.debug("request to associate file to entityId: {} by tag: {} created by user: {}", entityId, entityTagName, userId);
        int result = mshzFileService.associateFileToEntity(entityId, entityTagName, userId);
        return ResponseEntity.ok().body(result);
    }

    @GetMapping("/mshz-files/getAllByEntityTagAndEntityId")
    public ResponseEntity<List<MshzFile>> getByEntityTagAndEntityId(
        @RequestParam(name = "tag", required = false) String tag,
        @RequestParam(name = "entityId", required = false) Long entityId
    ) {
        log.debug("REST request to get MshzFiles by tag: {} and entityId: criteria: {}", tag, entityId);
        List<MshzFile> result = mshzFileService.getByEntityTagAndEntityId(tag, entityId);
        return ResponseEntity.ok().body(result);
    }

    @PutMapping("/mshz-files/updateEntityId")
    public ResponseEntity<Integer> updateMshzFile(
        @RequestParam("entityId") Long entityId,
        @RequestParam("newEntityId") Long newEntityId, 
        @RequestParam("userId") Long userId) throws URISyntaxException {
        log.debug("REST request to update change entityId: {} to new enityId; {} by user: {}", entityId, newEntityId, userId);
        int result = mshzFileService.updateEntityId(entityId, newEntityId, userId);
        return ResponseEntity.ok().body(result);
    }
}
