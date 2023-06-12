package XDCIA.privchainca;


import XDCIA.privchainca.Service.CAService;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.ConfigurableApplicationContext;


import java.io.IOException;

@SpringBootApplication
// 注册中心为eureka
@EnableEurekaClient
// 其他注册中心如zookeeper, consul等
@EnableDiscoveryClient
@EnableRabbit
public class PrivchaincaApplication {

	public static void main(String[] args) throws IOException {

		// 无需Controller层直接调用@Service注解的Bean
		ConfigurableApplicationContext run = SpringApplication.run(PrivchaincaApplication.class);
		CAService caService = (CAService)run.getBean("CAService");

		// 初始化CAService对象，随后向rabbitMQ对应队列和路由中发送公钥对象ABEPub和私钥对象ABEMsk
		caService.sendPubParams();
		caService.sendMasterKey();
	}

}
