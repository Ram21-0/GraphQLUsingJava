package friends.queries;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "friends.queries")
public class Main {
    public static void main(String[] args) {
        GraphQLGenerator.init();
        SpringApplication.run(Main.class, args);
    }
}
