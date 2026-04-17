package com.alpha.MoveBuddy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.alpha.MoveBuddy.Repository.UsersRepo;
import com.alpha.MoveBuddy.entity.Users;

@SpringBootApplication
public class MoveBuddyApplication {

	public static void main(String[] args) {
		SpringApplication.run(MoveBuddyApplication.class, args);
	}

	@Bean
	public CommandLineRunner setupAdmin(UsersRepo repo, PasswordEncoder encoder) {
		return args -> {
			if (!repo.existsByUsermobileNo(9999999999L)) {
				Users admin = new Users();
				admin.setUsermobileNo(9999999999L);
				admin.setUserPassword(encoder.encode("admin123"));
				admin.setRole("ADMIN");
				repo.save(admin);
				System.out.println(">>> DEFAULT ADMIN CREATED: 9999999999 / admin123");
			}
		};
	}
}
