package com.emtechhouse.CollateralService.CollateralDocuments;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface CollateraldocumentRepo extends JpaRepository<Collateraldocument, Long> {

}


