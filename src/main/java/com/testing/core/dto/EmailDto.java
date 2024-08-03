package com.testing.core.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * @Author -- Richard Mazimpaka
 */
@Getter
@Setter
public class EmailDto {

  private String recipientEmailAddress;
  private String messageBodyText;
  private String subject;
}
