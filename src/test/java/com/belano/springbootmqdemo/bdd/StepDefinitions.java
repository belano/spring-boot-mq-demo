package com.belano.springbootmqdemo.bdd;

import static org.awaitility.Awaitility.await;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import com.belano.springbootmqdemo.domain.Email;
import com.belano.springbootmqdemo.jms.EmailCounter;
import com.belano.springbootmqdemo.jms.EmailSender;
import io.cucumber.java8.En;
import lombok.extern.slf4j.Slf4j;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public class StepDefinitions implements En {

  @Autowired
  private EmailSender sender;

  @Autowired
  private EmailCounter emailCounter;

  public StepDefinitions() {

    Before(() -> log.info("before scenario"));

    Given("^something exists$", () -> log.info("Something exists"));

    When("^I send an email to (.+)$",
        (String name) -> sender.sendEmail(new Email(name, "Hello " + name)));

    Then("^something happens$", () -> {
      log.info("Something happens");
      await()
          .atMost(5, TimeUnit.SECONDS)
          .untilAsserted(() -> assertThat(emailCounter.get(), is(1)));
    });

    After(() -> log.info("after scenario"));

  }
}
