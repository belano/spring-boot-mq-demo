package com.belano.springbootmqdemo;

import static org.awaitility.Awaitility.await;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertNotNull;

import com.belano.springbootmqdemo.domain.Email;
import com.belano.springbootmqdemo.jms.EmailCounter;
import com.belano.springbootmqdemo.jms.EmailSender;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jms.config.JmsListenerEndpointRegistry;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@SpringBootTest
@ActiveProfiles("test")
@Testcontainers
class SpringBootMqDemoApplicationTests {

  public static final DockerImageName MQ_IMAGE = DockerImageName.parse("ibmcom/mq:latest");
  @Container
  public static GenericContainer<?> mq =
      new GenericContainer<>(MQ_IMAGE)
          .withEnv("LICENSE", "accept")
          .withEnv("MQ_QMGR_NAME", "QM1")
          .withEnv("MQ_APP_PASSWORD", "passw0rd")
          .withExposedPorts(1414);
  @Autowired
  JmsListenerEndpointRegistry registry;
  @Autowired
  private EmailSender sender;
  @Autowired
  private EmailCounter emailCounter;
  @Value("${demo.scheduling.enabled}")
  private boolean schedulingEnabled;

  @DynamicPropertySource
  static void mqProperties(DynamicPropertyRegistry registry) {
    registry.add("ibm.mq.connName", () -> "localhost(" + mq.getFirstMappedPort() + ")");
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
