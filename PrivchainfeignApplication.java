package XDCIA.privchainfeign;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableEurekaClient
@EnableDiscoveryClient
// 开启Feign功能
@EnableFeignClients
public class PrivchainfeignApplication {

	public static void main(String[] args) {
		SpringApplication.run(PrivchainfeignApplication.class, args);
	}

}
