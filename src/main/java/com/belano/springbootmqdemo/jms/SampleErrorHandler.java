package com.belano.springbootmqdemo.jms;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.ErrorHandler;

@Component
@Slf4j
public class SampleErrorHandler implements ErrorHandler {

  @Override
  public void handleError(Throwable t) {
    log.error("Error message: {}", t.getMessage());
  }
}
