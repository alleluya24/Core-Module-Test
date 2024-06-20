package com.testing.core.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Object Response DTO
 */
@Data
@AllArgsConstructor
public class ObjectResponseDto {
  private String name;
  private String size;
  private boolean isDirectory;
  private String lastModified;
}
