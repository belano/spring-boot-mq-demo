package com.belano.springbootmqdemo.bdd;

import com.belano.springbootmqdemo.bdd.CucumberTestContextConfiguration.CustomTestExecutionListener;
import io.cucumber.spring.CucumberContextConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jms.config.JmsListenerEndpointRegistry;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.TestExecutionListener;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.TestExecutionListeners.MergeMode;
import org.springframework.test.context.TestPropertySource;

@CucumberContextConfiguration
@SpringBootTest
@TestPropertySource(properties = {"demo.scheduling.enabled=false"})
@TestExecutionListeners(
    value = {CustomTestExecutionListener.class},
    mergeMode = MergeMode.MERGE_WITH_DEFAULTS)
@Slf4j
public class CucumberTestContextConfiguration {

  @Autowired
  JmsListenerEndpointRegistry registry;

  static class CustomTestExecutionListener implements TestExecutionListener {

    @Override
    public void beforeTestClass(TestContext testContext) {
      log.info("beforeTestClass");
    }
  }

}
