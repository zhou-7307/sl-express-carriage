package com.sl;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
@MapperScan("com.sl.ms.carriage.mapper")
public class CarriageApplication {
    public static void main(String[] args) {
        SpringApplication.run(CarriageApplication.class, args);
    }

}
