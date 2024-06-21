package com.testing.core;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(
    basePackages = {
      "com.testing.core",
      "rw.eccellenza.core.identityregistration",
      "rw.eccellenza.core.infrastructure",
      "rw.eccellenza.core.objectstorage",
      "rw.eccellenza.core.notification",
      "rw.eccellenza.core.utils",
    })
public class ModuleApplication {

  public static void main(String[] args) {
    SpringApplication.run(ModuleApplication.class, args);
  }
}
