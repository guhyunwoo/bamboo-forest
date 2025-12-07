package hyunwoo.com.bambooforest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class BambooForestApplication {

    public static void main(String[] args) {
        SpringApplication.run(BambooForestApplication.class, args);
    }

}
