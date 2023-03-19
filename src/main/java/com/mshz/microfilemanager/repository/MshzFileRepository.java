package com.mshz.microfilemanager.repository;

import com.mshz.microfilemanager.domain.MshzFile;

import java.util.List;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data  repository for the MshzFile entity.
 */
@SuppressWarnings("unused")
@Repository
public interface MshzFileRepository extends JpaRepository<MshzFile, Long>, JpaSpecificationExecutor<MshzFile> {
    @Modifying(flushAutomatically = true)
    @Query("update MshzFile f set f.entityId =:entityId "
           +"where f.entityTagName =:tag and f.userId =:uid and f.entityId=null")
    int associateFileToEntity(@Param("entityId") Long entityId,
         @Param("tag") String entityTagName, @Param("uid") Long userId);

    List<MshzFile> findByEntityTagNameAndEntityId(String tag, Long entityId);
    
    @Modifying(flushAutomatically = true)
    @Query("update MshzFile f set f.entityId =:newEntityId "
            +"where f.entityId =:entityId and f.userId =:uid")
    int updateEntityId(@Param("entityId") Long entityId,
            @Param("newEntityId") Long newEntityId, @Param("uid") Long userId);
}
