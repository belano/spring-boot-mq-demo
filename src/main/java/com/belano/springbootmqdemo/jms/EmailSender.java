package com.belano.springbootmqdemo.jms;

import com.belano.springbootmqdemo.config.JMSProperties;
import com.belano.springbootmqdemo.domain.Email;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class EmailSender {

  private final JmsTemplate jms;
  private final JMSProperties jmsProperties;

  public EmailSender(JmsTemplate jms, JMSProperties jmsProperties) {
    this.jms = jms;
    this.jmsProperties = jmsProperties;
  }

  public void sendEmail(Email email) {
    log.info("Sending email [{}]", email);
    jms.convertAndSend(jmsProperties.getEmailQueue(), email);
  }
}
