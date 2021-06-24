package friends2.queries;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "friends2.queries")
public class MainWithoutDataLoaders {
    public static void main(String[] args) {
        SpringApplication.run(MainWithoutDataLoaders.class, args);
    }
}