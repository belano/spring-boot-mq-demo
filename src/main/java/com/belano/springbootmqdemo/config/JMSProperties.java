package com.belano.springbootmqdemo.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "demo.jms")
public class JMSProperties {

  private String emailQueue;
  private String emailConfirmationsQueue;

  public String getEmailQueue() {
    return emailQueue;
  }

  public void setEmailQueue(String emailQueue) {
    this.emailQueue = emailQueue;
  }

  public String getEmailConfirmationsQueue() {
    return emailConfirmationsQueue;
  }

  public void setEmailConfirmationsQueue(String emailConfirmationsQueue) {
    this.emailConfirmationsQueue = emailConfirmationsQueue;
  }
}
