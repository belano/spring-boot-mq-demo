package com.belano.springbootmqdemo.jms;

import com.belano.springbootmqdemo.domain.EmailConfirmation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class EmailConfirmationService {

  private final EmailCounter emailCounter;

  public EmailConfirmationService(EmailCounter emailCounter) {
    this.emailCounter = emailCounter;
  }

  @JmsListener(destination = "${demo.jms.email-confirmations-queue}")
  public void handle(EmailConfirmation emailConfirmation) {
    int count = emailCounter.incrementAndGet();
    log.info("Received email confirmation [{}] [{}]", emailConfirmation, count);
  }

}
