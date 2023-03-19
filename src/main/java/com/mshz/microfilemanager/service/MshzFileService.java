package com.mshz.microfilemanager.service;

import com.mshz.microfilemanager.domain.MshzFile;
import com.mshz.microfilemanager.model.FileChunkMetadata;
import com.mshz.microfilemanager.repository.MshzFileRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLEncoder;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * Service Implementation for managing {@link MshzFile}.
 */
@Service
@Transactional
public class MshzFileService {

    private final Logger log = LoggerFactory.getLogger(MshzFileService.class);

    @Value("${my-config.file.store-mode.in-db}")
    private boolean storeModeIdDB;

    private final MshzFileRepository mshzFileRepository;

    private final FileSystemManager fileSystemManager;

    public MshzFileService(MshzFileRepository mshzFileRepository, FileSystemManager fileSystemManager) {
        this.mshzFileRepository = mshzFileRepository;
        this.fileSystemManager = fileSystemManager;
    }

    /**
     * Save a mshzFile.
     *
     * @param mshzFile the entity to save.
     * @return the persisted entity.
     */
    public MshzFile save(MshzFile mshzFile) {
        log.debug("Request to save MshzFile : {}", mshzFile);
        if(!storeModeIdDB){
            byte[] fData = mshzFile.getfData();
            fileSystemManager.upload(mshzFile.getName(), fData, null);
            mshzFile.setfData(null);
        }
        if(mshzFile.getStoreAt() != null)
            mshzFile.setStoreAt(Instant.now());
        return mshzFileRepository.save(mshzFile);
    }

    /**
     * Get all the mshzFiles.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<MshzFile> findAll(Pageable pageable) {
        log.debug("Request to get all MshzFiles");
        return mshzFileRepository.findAll(pageable);
    }


    /**
     * Get one mshzFile by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<MshzFile> findOne(Long id) {
        log.debug("Request to get MshzFile : {}", id);
        Optional<MshzFile> file = mshzFileRepository.findById(id);
        if(!storeModeIdDB && file.isPresent())
            return Optional.of(fileSystemManager.downLoadFile(file.get()));
        return file;
    }

    /**
     * Delete the mshzFile by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete MshzFile : {}", id);
        if(!storeModeIdDB){
            MshzFile file = mshzFileRepository.findById(id).orElse(null);
            if(file != null && file.getName() != null)
                fileSystemManager.deleteFile(file.getName());
        }
        mshzFileRepository.deleteById(id);
    }

    public MshzFile uploadFile(MultipartFile file) {
        MshzFile mshzFile = new MshzFile();
        try {
            byte[] data = file.getBytes();
            String fileName = URLEncoder.encode(file.getOriginalFilename(),  "UTF-8");
            if(!storeModeIdDB){
                fileSystemManager.upload(fileName, data, null);
                mshzFile.setfData(null);
            }else{
                mshzFile.setfData(data);
            }
            mshzFile.setName(fileName);
            mshzFile.setfDataContentType(file.getContentType());
           return mshzFileRepository.save(mshzFile);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    public MshzFile uploadChunk(MultipartFile file, FileChunkMetadata chunkMetadata) {
        MshzFile mshzFile = new MshzFile();
        if(chunkMetadata != null && file != null){
            Integer chunk = chunkMetadata.getChunk();
            String name = chunkMetadata.getName();
            Integer chunks = chunkMetadata.getChunks();
            mshzFile.setName(encodeFileUri(name));
            mshzFile.setfDataContentType(file.getContentType());
            mshzFile.setfData(null);
            String chunkFolder = null;
            try {     
                String chunName = "";
                if(chunks != null && chunks.intValue() > 1){
                    chunkFolder = fileSystemManager.createFolderIfNotExists(encodeFileUri(name));
                    chunName = "chunk".concat(chunk.toString()).concat("_").concat(encodeFileUri(name));
                }else{
                    chunName = encodeFileUri(name);
                }
                if(file.getSize() != 0)
                    fileSystemManager.upload(chunName, file.getBytes(), chunkFolder);
                if(chunk != null && chunk.equals(chunks)){
                    if(chunks != null && chunks.intValue() > 1)
                        fileSystemManager.combineFileChunks(chunkFolder, encodeFileUri(name));
                    mshzFile.setEntityId(chunkMetadata.getEntityId());
                    mshzFile.setEntityTagName(chunkMetadata.getEntityTagName());
                    mshzFile.setUserId(chunkMetadata.getUserId());
                    mshzFile.setStoreAt(Instant.now());
                    return mshzFileRepository.save(mshzFile);
                }
            } catch (Exception e) {
                log.error("error {}", e.getMessage());
                e.printStackTrace();
            }
        }
        return mshzFile;
    }

    public String encodeFileUri(String path){
        try {
            return  URLEncoder.encode(path, "UTF-8");
        } catch (Exception e) {
           log.error("url encoded error: {}", e.getMessage());
           e.printStackTrace();
        }
        return "";
    }

    public int associateFileToEntity(Long entityId, String entityTagName, Long userId) {
        return mshzFileRepository.associateFileToEntity(entityId, entityTagName, userId);
    }

    public List<MshzFile> getByEntityTagAndEntityId(String tag, Long entityId) {
        return mshzFileRepository.findByEntityTagNameAndEntityId(tag, entityId);
    }

    public int updateEntityId(Long entityId, Long newEntityId, Long userId){
        return mshzFileRepository.updateEntityId(entityId,newEntityId, userId);
    }
}
