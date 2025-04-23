package io.baji.stvh;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("io.baji.stvh.mapper")
@SpringBootApplication
public class StvhApplication {

    public static void main(String[] args) {
        SpringApplication.run(StvhApplication.class, args);
    }

}
