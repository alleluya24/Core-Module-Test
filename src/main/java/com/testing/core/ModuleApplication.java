package com.testing.core;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication()
@EnableJpaRepositories(basePackages = {"rw.eccellenza"})
@ComponentScan(
    basePackages = {
      "com.testing.core",
      "rw.eccellenza.core.identityregistration",
      "rw.eccellenza.core.infrastructure",
      "rw.eccellenza.core.objectstorage",
      "rw.eccellenza.core.notification",
      "rw.eccellenza.core.utils",
      "rw.eccellenza.core.blockChainIntegration",
      "rw.eccellenza.core.companyvalidation",
      "rw.eccellenza.core.payment",
      "rw.eccellenza.core.notification.dtos"
    })
@EntityScan(basePackages = {"rw.eccellenza"})
public class ModuleApplication {

  public static void main(String[] args) {
    SpringApplication.run(ModuleApplication.class, args);
  }
}
