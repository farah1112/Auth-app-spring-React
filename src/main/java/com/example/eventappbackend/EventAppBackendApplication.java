package com.example.eventappbackend;

import com.example.eventappbackend.Repositories.RoleRepository;
import com.example.eventappbackend.user.Role;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableJpaAuditing
@EnableJpaRepositories
@EntityScan
@EnableAsync
public class EventAppBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(EventAppBackendApplication.class, args);

        }

    @Bean
    public CommandLineRunner runner(RoleRepository roleRepository) {
        return args -> {
            if (roleRepository.findByName("USER").isEmpty()){
                roleRepository.save(
                        Role.builder().name("USER").build()
                );
            }
        };
    }


    }
