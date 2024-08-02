package dev.hashnode.ishbhana.csfle;

import org.springframework.boot.SpringApplication;

public class TestCsfleSpringBootApplication {

    public static void main(String[] args) {
        SpringApplication.from(CsfleSpringBootApplication::main).with(TestcontainersConfig.class).run(args);
    }

}
