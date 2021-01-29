package com.belano.springbootmqdemo.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class EmailConfirmation {

  private String messageId;

  @Override
  public String toString() {
    return String.format("Email confirmation [messageId='%s']", messageId);
  }
}
