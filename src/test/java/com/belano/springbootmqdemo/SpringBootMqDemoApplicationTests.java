package com.belano.springbootmqdemo;

import static org.awaitility.Awaitility.await;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jms.config.JmsListenerEndpointRegistry;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.Container.ExecResult;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import com.belano.springbootmqdemo.domain.Email;
import com.belano.springbootmqdemo.jms.EmailCounter;
import com.belano.springbootmqdemo.jms.EmailSender;
import org.testcontainers.utility.MountableFile;

@SpringBootTest
@ActiveProfiles("test")
@Testcontainers
@Slf4j
class SpringBootMqDemoApplicationTests {

  public static final DockerImageName MQ_IMAGE = DockerImageName.parse("icr.io/ibm-messaging/mq:latest");
  @Container
  public static GenericContainer<?> mq =
      new GenericContainer<>(MQ_IMAGE)
          .withEnv("LICENSE", "accept")
          .withEnv("MQ_QMGR_NAME", "QM1")
          .withEnv("MQ_APP_PASSWORD", "passw0rd")
          .withExposedPorts(1414)
          .withCopyFileToContainer(
              MountableFile.forClasspathResource("/docker/mq-setup.sh", 0744),
              "/tmp/mq-setup.sh");

  private static final String EMAIL_QUEUE_NAME = RandomStringUtils.randomAlphanumeric(10).toUpperCase();
  private static final String EMAIL_CONFIRMATIONS_QUEUE_NAME = RandomStringUtils.randomAlphanumeric(10).toUpperCase();

  @Autowired
  JmsListenerEndpointRegistry registry;
  @Autowired
  private EmailSender sender;
  @Autowired
  private EmailCounter emailCounter;
  @Value("${demo.scheduling.enabled}")
  private boolean schedulingEnabled;

  @BeforeAll
  static void beforeAll() {
    execWithLogging(new String[] {"bin/sh", "/tmp/mq-setup.sh", "QM1", EMAIL_QUEUE_NAME});
    execWithLogging(new String[] {"bin/sh", "/tmp/mq-setup.sh", "QM1", EMAIL_CONFIRMATIONS_QUEUE_NAME});
  }

  static void execWithLogging(String[] command) {
    try {
      ExecResult execResult = mq.execInContainer(command);
      int exitCode = execResult.getExitCode();
      String stdout = execResult.getStdout();
      log.info("STDOUT {}", stdout);
      log.info("Exit code {}", exitCode);
    } catch (IOException | InterruptedException e) {
      fail(e);
    }
  }

  @DynamicPropertySource
  static void mqProperties(DynamicPropertyRegistry registry) {
    registry.add("ibm.mq.connName", () -> "localhost(" + mq.getFirstMappedPort() + ")");
    registry.add("demo.jms.email-queue", () -> EMAIL_QUEUE_NAME);
    registry.add("demo.jms.email-confirmations-queue", () -> EMAIL_CONFIRMATIONS_QUEUE_NAME);
  }

  @Test
  void sendEmail() {
    assertThat(schedulingEnabled, is(false));
    assertNotNull(sender);
    sender.sendEmail(new Email("jdoe@foo.bar", "Hello"));
    await()
        .atMost(5, TimeUnit.SECONDS)
        .untilAsserted(() -> assertThat(emailCounter.get(), is(1)));
  }

}
