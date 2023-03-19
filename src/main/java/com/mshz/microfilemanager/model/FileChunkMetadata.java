package com.mshz.microfilemanager.model;

import java.io.Serializable;

public class FileChunkMetadata implements Serializable {
    String name;
    Integer chunk;
    Integer chunks;
    Long entityId;
    String entityTagName;
    Long userId;

    

    public FileChunkMetadata(String name, Integer chunk, Integer chunks, Long entityId, String entityTagName,
            Long userId) {
        this.name = name;
        this.chunk = chunk;
        this.chunks = chunks;
        this.entityId = entityId;
        this.entityTagName = entityTagName;
        this.userId = userId;
    }
    
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public Integer getChunk() {
        return chunk;
    }
    public void setChunk(Integer chunk) {
        this.chunk = chunk;
    }
    public Integer getChunks() {
        return chunks;
    }
    public void setChunks(Integer chunks) {
        this.chunks = chunks;
    }
    public Long getEntityId() {
        return entityId;
    }
    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }
    public String getEntityTagName() {
        return entityTagName;
    }
    public void setEntityTagName(String entityTagName) {
        this.entityTagName = entityTagName;
    }
    public Long getUserId() {
        return userId;
    }
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "FileChunkMetadata [chunk=" + chunk + ", chunks=" + chunks + ", entityId=" + entityId
                + ", entityTagName=" + entityTagName + ", name=" + name + ", userId=" + userId + "]";
    }

    

    
}
