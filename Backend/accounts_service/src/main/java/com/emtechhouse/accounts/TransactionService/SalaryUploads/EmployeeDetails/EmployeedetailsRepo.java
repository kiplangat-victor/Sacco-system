package com.emtechhouse.accounts.TransactionService.SalaryUploads.EmployeeDetails;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeedetailsRepo extends JpaRepository<Employeedetails, Long> {
}
