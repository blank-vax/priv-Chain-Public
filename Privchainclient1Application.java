package XDCIA.privchainclient1;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

import java.security.Security;


@SpringBootApplication
// 注册中心为eureka
@EnableEurekaClient
// 其他注册中心如zookeeper, consul等
@EnableDiscoveryClient
@EnableRabbit

public class Privchainclient1Application {

	static {
		Security.removeProvider("SunEC");
	}

	public static void main(String[] args) {
		SpringApplication.run(Privchainclient1Application.class, args);
	}

}
