package com.mshz.microfilemanager.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;

import java.io.Serializable;
import java.time.Instant;

/**
 * A MshzFile.
 */
@Entity
@Table(name = "mshz_file")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class MshzFile implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @Column(name = "name")
    private String name;

    @Lob
    @Column(name = "f_data")
    private byte[] fData;

    @Column(name = "f_data_content_type")
    private String fDataContentType;

    @Column(name = "entity_id")
    private Long entityId;

    @Column(name = "entity_tag_name")
    private String entityTagName;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "store_at")
    private Instant storeAt;

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public MshzFile name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public byte[] getfData() {
        return fData;
    }

    public MshzFile fData(byte[] fData) {
        this.fData = fData;
        return this;
    }

    public void setfData(byte[] fData) {
        this.fData = fData;
    }

    public String getfDataContentType() {
        return fDataContentType;
    }

    public MshzFile fDataContentType(String fDataContentType) {
        this.fDataContentType = fDataContentType;
        return this;
    }

    public void setfDataContentType(String fDataContentType) {
        this.fDataContentType = fDataContentType;
    }

    public Long getEntityId() {
        return entityId;
    }

    public MshzFile entityId(Long entityId) {
        this.entityId = entityId;
        return this;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }

    public String getEntityTagName() {
        return entityTagName;
    }

    public MshzFile entityTagName(String entityTagName) {
        this.entityTagName = entityTagName;
        return this;
    }

    public void setEntityTagName(String entityTagName) {
        this.entityTagName = entityTagName;
    }

    public Long getUserId() {
        return userId;
    }

    public MshzFile userId(Long userId) {
        this.userId = userId;
        return this;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Instant getStoreAt() {
        return storeAt;
    }

    public MshzFile storeAt(Instant storeAt) {
        this.storeAt = storeAt;
        return this;
    }

    public void setStoreAt(Instant storeAt) {
        this.storeAt = storeAt;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MshzFile)) {
            return false;
        }
        return id != null && id.equals(((MshzFile) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "MshzFile{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", fData='" + getfData() + "'" +
            ", fDataContentType='" + getfDataContentType() + "'" +
            ", entityId=" + getEntityId() +
            ", entityTagName='" + getEntityTagName() + "'" +
            ", userId=" + getUserId() +
            ", storeAt='" + getStoreAt() + "'" +
            "}";
    }
}
