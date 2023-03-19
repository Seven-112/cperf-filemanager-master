package com.mshz.microfilemanager.service;

import java.util.List;

import javax.persistence.criteria.JoinType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.github.jhipster.service.QueryService;

import com.mshz.microfilemanager.domain.MshzFile;
import com.mshz.microfilemanager.domain.*; // for static metamodels
import com.mshz.microfilemanager.repository.MshzFileRepository;
import com.mshz.microfilemanager.service.dto.MshzFileCriteria;

/**
 * Service for executing complex queries for {@link MshzFile} entities in the database.
 * The main input is a {@link MshzFileCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link MshzFile} or a {@link Page} of {@link MshzFile} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class MshzFileQueryService extends QueryService<MshzFile> {

    private final Logger log = LoggerFactory.getLogger(MshzFileQueryService.class);

    private final MshzFileRepository mshzFileRepository;

    public MshzFileQueryService(MshzFileRepository mshzFileRepository) {
        this.mshzFileRepository = mshzFileRepository;
    }

    /**
     * Return a {@link List} of {@link MshzFile} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<MshzFile> findByCriteria(MshzFileCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<MshzFile> specification = createSpecification(criteria);
        return mshzFileRepository.findAll(specification);
    }

    /**
     * Return a {@link Page} of {@link MshzFile} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<MshzFile> findByCriteria(MshzFileCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<MshzFile> specification = createSpecification(criteria);
        return mshzFileRepository.findAll(specification, page);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(MshzFileCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<MshzFile> specification = createSpecification(criteria);
        return mshzFileRepository.count(specification);
    }

    /**
     * Function to convert {@link MshzFileCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<MshzFile> createSpecification(MshzFileCriteria criteria) {
        Specification<MshzFile> specification = Specification.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), MshzFile_.id));
            }
            if (criteria.getName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getName(), MshzFile_.name));
            }
            if (criteria.getEntityId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getEntityId(), MshzFile_.entityId));
            }
            if (criteria.getEntityTagName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getEntityTagName(), MshzFile_.entityTagName));
            }
            if (criteria.getUserId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getUserId(), MshzFile_.userId));
            }
            if (criteria.getStoreAt() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getStoreAt(), MshzFile_.storeAt));
            }
        }
        return specification;
    }
}
