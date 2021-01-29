package com.belano.springbootmqdemo.jms;

import com.belano.springbootmqdemo.domain.Email;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ScheduledEmailSender {

  private final EmailSender sender;

  public ScheduledEmailSender(EmailSender sender) {
    this.sender = sender;
  }

  @Scheduled(fixedRate = 5000)
  public void sendEmail() {
    sender.sendEmail(new Email("info@example.com", "Hello"));
  }

}
