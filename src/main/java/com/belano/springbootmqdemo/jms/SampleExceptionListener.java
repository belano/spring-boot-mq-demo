package com.belano.springbootmqdemo.jms;

import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SampleExceptionListener implements ExceptionListener {

  @Override
  public void onException(JMSException exception) {
    log.error("JMS exception", exception);
  }
}
