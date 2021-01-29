package com.belano.springbootmqdemo.jms;

import com.belano.springbootmqdemo.domain.Email;
import com.belano.springbootmqdemo.domain.EmailConfirmation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class EmailService {

  @JmsListener(destination = "${demo.jms.email-queue}")
  @SendTo("${demo.jms.email-confirmations-queue}")
  public EmailConfirmation handle(Email email) {
    log.info("Received email [{}]", email);
    return new EmailConfirmation(email.getMessageId());
  }
}
