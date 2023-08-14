package com.eus;

import com.eus.auth.dto.AuthenticationRequest;
import com.eus.auth.dto.RegisterRequest;
import com.eus.auth.service.AuthenticationService;
import com.eus.entity.UserProfile;
import com.eus.service.KeyGeneratorService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class EnhancedUrlShortenerApplication {

	public static void main(String[] args) {
		SpringApplication.run(EnhancedUrlShortenerApplication.class, args);
	}

	@Bean
	public CommandLineRunner commandLineRunner(
			AuthenticationService service,
			KeyGeneratorService keyGeneratorService
	) {
		return args -> {
//			var register = RegisterRequest.builder()
//					.firstName("abcd")
//					.lastName("xyz")
//					.email("dummy@gmail.com")
//					.password("pass@123")
//					.build();
//			System.out.println("Token: " + service.authenticate(register).getAccessToken());
//			AuthenticationRequest authenticate = AuthenticationRequest.builder().email("dummy@gmail.com").password("pass@123").build();
//			System.out.println("Token: " + service.authenticate(authenticate).getAccessToken());

//			keyGeneratorService.generateKey();
		};
	}

}
