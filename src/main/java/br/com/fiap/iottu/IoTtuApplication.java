package br.com.fiap.iottu;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class IoTtuApplication {

    public static void main(String[] args) {
        SpringApplication.run(IoTtuApplication.class, args);
    }

}
