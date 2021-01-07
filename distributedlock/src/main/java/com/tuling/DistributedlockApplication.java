package com.tuling;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@MapperScan("com.tuling.distributedlock.mapper")
@SpringBootApplication
public class DistributedlockApplication {

	public static void main(String[] args) {
		SpringApplication.run(DistributedlockApplication.class, args);
	}

}
