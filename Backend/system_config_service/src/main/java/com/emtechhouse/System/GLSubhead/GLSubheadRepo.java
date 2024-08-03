package com.emtechhouse.System.GLSubhead;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GLSubheadRepo extends JpaRepository<GLSubhead, Long> {
    Optional<GLSubhead> findByEntityIdAndGlSubheadCodeAndDeletedFlag(String entityId, String glSubheadCode, Character flag);

    List<GLSubhead> findByEntityIdAndDeletedFlag(String entityId, Character flag);

    @Query(value = "SELECT COUNT(*) FROM accounts WHERE accounts.gl_subhead=:gl_subhead AND accounts.delete_flag='N'", nativeQuery = true)
    Long findByGlSubhead(Long gl_subhead);

    Optional<GLSubhead> findByGlSubheadCode(String glSubheadCode);

    Optional<GLSubhead> findByEntityIdAndGlSubheadCode(String entityId, String glSubheadCode);

    List<GLSubhead> findAllByEntityIdAndDeletedFlagOrderByIdDesc(String entityId, Character deletedFlag);


}
