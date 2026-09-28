package com.rephone;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan("com.rephone")
@MapperScan("com.rephone.mapper")
public class RePhoneApplication {

    public static void main(String[] args) {
        SpringApplication.run(RePhoneApplication.class, args);
    }
}
