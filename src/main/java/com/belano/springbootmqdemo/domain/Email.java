package com.belano.springbootmqdemo.domain;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class Email {

  private String messageId;
  private String to;
  private String body;

  public Email(String to, String body) {
    this.messageId = UUID.randomUUID().toString();
    this.to = to;
    this.body = body;
  }

  @Override
  public String toString() {
    return String.format("Email{id=%s, to=%s, body=%s}", messageId, getTo(), getBody());
  }
}
