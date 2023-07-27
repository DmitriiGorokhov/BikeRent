package com.gorokhov;

import jakarta.persistence.EntityManager;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class BikeRentApplication {
    public static void main(String[] args) {
        SpringApplication.run(BikeRentApplication.class, args);
    }
}