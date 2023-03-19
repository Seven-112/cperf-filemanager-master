package com.mshz.microfilemanager.service.dto;

import java.io.Serializable;
import java.util.Objects;
import io.github.jhipster.service.Criteria;
import io.github.jhipster.service.filter.BooleanFilter;
import io.github.jhipster.service.filter.DoubleFilter;
import io.github.jhipster.service.filter.Filter;
import io.github.jhipster.service.filter.FloatFilter;
import io.github.jhipster.service.filter.IntegerFilter;
import io.github.jhipster.service.filter.LongFilter;
import io.github.jhipster.service.filter.StringFilter;
import io.github.jhipster.service.filter.InstantFilter;

/**
 * Criteria class for the {@link com.mshz.microfilemanager.domain.MshzFile} entity. This class is used
 * in {@link com.mshz.microfilemanager.web.rest.MshzFileResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /mshz-files?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class MshzFileCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter name;

    private LongFilter entityId;

    private StringFilter entityTagName;

    private LongFilter userId;

    private InstantFilter storeAt;

    public MshzFileCriteria() {
    }

    public MshzFileCriteria(MshzFileCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.name = other.name == null ? null : other.name.copy();
        this.entityId = other.entityId == null ? null : other.entityId.copy();
        this.entityTagName = other.entityTagName == null ? null : other.entityTagName.copy();
        this.userId = other.userId == null ? null : other.userId.copy();
        this.storeAt = other.storeAt == null ? null : other.storeAt.copy();
    }

    @Override
    public MshzFileCriteria copy() {
        return new MshzFileCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getName() {
        return name;
    }

    public void setName(StringFilter name) {
        this.name = name;
    }

    public LongFilter getEntityId() {
        return entityId;
    }

    public void setEntityId(LongFilter entityId) {
        this.entityId = entityId;
    }

    public StringFilter getEntityTagName() {
        return entityTagName;
    }

    public void setEntityTagName(StringFilter entityTagName) {
        this.entityTagName = entityTagName;
    }

    public LongFilter getUserId() {
        return userId;
    }

    public void setUserId(LongFilter userId) {
        this.userId = userId;
    }

    public InstantFilter getStoreAt() {
        return storeAt;
    }

    public void setStoreAt(InstantFilter storeAt) {
        this.storeAt = storeAt;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final MshzFileCriteria that = (MshzFileCriteria) o;
        return
            Objects.equals(id, that.id) &&
            Objects.equals(name, that.name) &&
            Objects.equals(entityId, that.entityId) &&
            Objects.equals(entityTagName, that.entityTagName) &&
            Objects.equals(userId, that.userId) &&
            Objects.equals(storeAt, that.storeAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
        id,
        name,
        entityId,
        entityTagName,
        userId,
        storeAt
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "MshzFileCriteria{" +
                (id != null ? "id=" + id + ", " : "") +
                (name != null ? "name=" + name + ", " : "") +
                (entityId != null ? "entityId=" + entityId + ", " : "") +
                (entityTagName != null ? "entityTagName=" + entityTagName + ", " : "") +
                (userId != null ? "userId=" + userId + ", " : "") +
                (storeAt != null ? "storeAt=" + storeAt + ", " : "") +
            "}";
    }

}
