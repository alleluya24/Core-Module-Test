package com.testing.core;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(
    basePackages = {
      "rw.eccellenza.core.identityregistration",
      "rw.eccellenza.core.infrastructure",
      "rw.eccellenza.core.objectstorage",
      "com.testing.core"
    })
public class ModuleApplication {

  public static void main(String[] args) {
    SpringApplication.run(ModuleApplication.class, args);
  }
}
