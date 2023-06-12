package XDCIA.privchainserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class PrivchainserverApplication {

	public static void main(String[] args) {
		SpringApplication.run(PrivchainserverApplication.class, args);
	}

}

