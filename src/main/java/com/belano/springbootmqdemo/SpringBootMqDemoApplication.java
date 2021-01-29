package com.belano.springbootmqdemo;

import com.belano.springbootmqdemo.config.JMSProperties;
import com.belano.springbootmqdemo.domain.Email;
import com.belano.springbootmqdemo.domain.EmailConfirmation;
import java.util.Comparator;
import java.util.Map;
import java.util.stream.Stream;
import javax.jms.ConnectionFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jms.DefaultJmsListenerContainerFactoryConfigurer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerContainerFactory;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MessageType;
import org.springframework.util.ErrorHandler;

@SpringBootApplication
@EnableJms
@EnableConfigurationProperties(JMSProperties.class)
@Slf4j
public class SpringBootMqDemoApplication {

  public static void main(String[] args) {
    var ctx = SpringApplication.run(SpringBootMqDemoApplication.class, args);
    log.info("# Beans: {}", ctx.getBeanDefinitionCount());
    var names = ctx.getBeanDefinitionNames();
    Stream.of(names)
        .filter(name -> name.toLowerCase().contains("jms")).sorted(Comparator.naturalOrder())
        .forEach(name -> {
          Object bean = ctx.getBean(name);
          log.info("\tName: {}, Type: {}\n", name, bean.getClass().getSimpleName());
        });
  }

  @Bean
  public JmsListenerContainerFactory<?> myFactory(ConnectionFactory connectionFactory,
      DefaultJmsListenerContainerFactoryConfigurer configurer, ErrorHandler errorHandler) {
    DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
    // This provides all boot's default to this factory, including the message converter
    configurer.configure(factory, connectionFactory);
    // You could still override some of Boot's default if necessary.
    factory.setErrorHandler(errorHandler);
    return factory;
  }

  @Bean
  public MessageConverter jacksonJmsMessageConverter() {
    var converter = new MappingJackson2MessageConverter();
    converter.setTargetType(MessageType.TEXT);
    converter.setTypeIdPropertyName("content_type");
    converter.setTypeIdMappings(Map.of(
        "email", Email.class,
        "emailConfirmation", EmailConfirmation.class
    ));
    return converter;
  }
}
