package lms;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.mail.javamail.JavaMailSender;

@SpringBootApplication
public class LmsB12Application {

    public static void main(String[] args) {
        SpringApplication.run(LmsB12Application.class, args);
    }


}
