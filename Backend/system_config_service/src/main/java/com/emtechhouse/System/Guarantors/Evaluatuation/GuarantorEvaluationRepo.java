package com.emtechhouse.System.Guarantors.Evaluatuation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GuarantorEvaluationRepo extends JpaRepository<GuarantorEvaluation,Long> {

}
