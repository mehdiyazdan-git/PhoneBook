package com.pishgaman.phonebook;

import com.pishgaman.phonebook.security.auth.AuthenticationService;
import com.pishgaman.phonebook.security.auth.RegisterRequest;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import static com.pishgaman.phonebook.security.user.Role.ADMIN;

@SpringBootApplication
public class PhoneBookApplication {
    public static void main(String[] args) {
        SpringApplication.run(PhoneBookApplication.class, args);
    }
    @Bean
    public CommandLineRunner loadData(AuthenticationService service) {
        return args -> {

            var admin = RegisterRequest.builder()
                    .firstname("روزبه")
                    .lastname("کسری")
                    .username("kasra")
                    .email("r.kasra@gmail.com")
                    .password("123456")
                    .role(ADMIN)
                    .build();


            if (service.isTableEmpty()){
                System.out.println("Admin token: " + service.register(admin).getAccessToken());
            }
        };
    }
}
