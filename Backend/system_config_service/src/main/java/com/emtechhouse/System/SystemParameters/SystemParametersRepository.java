package com.emtechhouse.System.SystemParameters;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SystemParametersRepository extends JpaRepository<SystemParameters, Long> {
}
