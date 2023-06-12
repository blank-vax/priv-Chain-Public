package XDCIA.privchaincs;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
// 注册中心为eureka
@EnableEurekaClient
// 其他注册中心如zookeeper, consul等
@EnableDiscoveryClient
@EnableRabbit
public class PrivchaincsApplication {

	public static void main(String[] args) {
		SpringApplication.run(PrivchaincsApplication.class, args);
	}
}
