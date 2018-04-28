package commerce.cloud.trainings.webapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.PropertySource;


/**
 * @author Nick Mandrik
 */


@SpringBootApplication
@PropertySource("classpath:commerce.properties")
@EnableCaching
public class ExternalCommerceApplication {
    public static void main(String[] args) {
        SpringApplication.run(ExternalCommerceApplication.class, args);
    }
}
