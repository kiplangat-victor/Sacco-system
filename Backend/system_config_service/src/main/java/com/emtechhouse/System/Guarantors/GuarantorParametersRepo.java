package com.emtechhouse.System.Guarantors;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GuarantorParametersRepo extends JpaRepository<GuarantorsParameters,Long> {
    List<GuarantorsParameters> findByEntityId(String entityId);

    List<GuarantorsParameters> findByEntityIdAndDeletedFlag(String currentEntityId, char n);
}
