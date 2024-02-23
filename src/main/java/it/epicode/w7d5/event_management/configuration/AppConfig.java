package it.epicode.w7d5.event_management.configuration;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Properties;

@Configuration
@PropertySource("application.properties")
public class AppConfig {

    @Bean
    public JavaMailSenderImpl getMailSender(
            @Value("${mail.smtp.host}") String smtpHost,
            @Value("${mail.smtp.port}") String port,
            @Value("${mail.from}") String from,
            @Value("${mail.password}") String password,
            @Value("${mail.transport.protocol}") String protocol,
            @Value("${mail.smtp.auth}") String auth,
            @Value("${mail.smtp.starttls.enable}") String starttls,
            @Value("${mail.debug}") String debug,
            @Value("${mail.ssl.enable}") String sslEnable
    ) {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(smtpHost);
        mailSender.setPort(Integer.parseInt(port));
        mailSender.setUsername(from);
        mailSender.setPassword(password);
        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", protocol);
        props.put("mail.smtp.auth", auth);
        props.put("mail.smtp.starttls.enable", starttls);
        props.put("mail.debug", debug);
        props.put("smtp.ssl.enable", sslEnable);
        return mailSender;
    }

    @Bean
    public PasswordEncoder encoder() {
        return new BCryptPasswordEncoder();

    }

}
