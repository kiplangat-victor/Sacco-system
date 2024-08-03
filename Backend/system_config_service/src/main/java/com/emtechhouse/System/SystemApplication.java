package com.emtechhouse.System;

import com.emtechhouse.System.Branches.Branch;
import com.emtechhouse.System.Branches.BranchRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Date;
@SpringBootApplication
public class SystemApplication {
	@Autowired
	BranchRepo branchRepo;
	public static void main(String[] args) {
		SpringApplication.run(SystemApplication.class, args);
	}

	@Bean
	CommandLineRunner runner() {
		return args -> {
			if (branchRepo.findAll().size()<1) {
				//Create Default Entity
				Branch branch = new Branch();
				branch.setBranchCode("001");
				branch.setEntityId("001");
				branch.setBranchDescription("Main Branch");
				branch.setLocation("Kenya");
				branch.setEmail("");
				branch.setPhoneNumber("");
				branch.setPostedBy("System");
				branch.setPostedFlag('Y');
				branch.setVerifiedBy("System");
				branch.setVerifiedFlag('Y');
				branch.setVerifiedTime(new Date());
				branch.setPostedTime(new Date());
				branchRepo.save(branch);
			}
			System.out.println("SYSTEM-CONFIG-SERVICE SUCCESSFULLY ON " + new Date());
		};
	}
}