package XDCIA.privchainaa;


import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
// 注册中心为eureka
@EnableEurekaClient
// 其他注册中心如zookeeper, consul等
@EnableDiscoveryClient
@EnableRabbit
public class PrivchainaaApplication {

	public static void main(String[] args) {
		// 正常启动类
		SpringApplication.run(PrivchainaaApplication.class, args);
	}

}
