package com.example.mroojBE;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

// FIX: BaseEntity relies on @CreatedDate/@LastModifiedDate via
// AuditingEntityListener, but that listener does nothing unless JPA
// auditing is explicitly enabled. Without @EnableJpaAuditing, createdAt/
// updatedAt stay null on every entity, and both are declared
// nullable = false — the very first save() throws a constraint violation.
@SpringBootApplication
@EnableJpaAuditing
public class MroojBeApplication {

	public static void main(String[] args) {
		SpringApplication.run(MroojBeApplication.class, args);
	}

}