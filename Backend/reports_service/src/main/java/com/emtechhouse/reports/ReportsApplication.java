package com.emtechhouse.reports;

import com.emtechhouse.reports.Utils.CONSTANTS;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.io.File;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.Set;

@SpringBootApplication
@Slf4j
public class ReportsApplication {
	public static void main(String[] args) {
		SpringApplication.run(ReportsApplication.class, args);
	}
	@Bean
	CommandLineRunner runner() {
		return args -> {
			Set<PosixFilePermission> permissionSet = PosixFilePermissions.fromString("rwxrwxrwx");
			File reports = new File(CONSTANTS.REPORTS_DESTINATION);
			if (!reports.exists()) {
				reports.mkdirs();
			}
		};
	}
}

