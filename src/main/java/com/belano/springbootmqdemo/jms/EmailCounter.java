package com.belano.springbootmqdemo.jms;

import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.stereotype.Component;

@Component
public class EmailCounter {

  private final AtomicInteger counter = new AtomicInteger(0);

  public int incrementAndGet() {
    return counter.incrementAndGet();
  }

  public int get() {
    return counter.get();
  }

}
