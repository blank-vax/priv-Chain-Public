package XDCIA.privchainca.Service;

import XDCIA.privchainca.Entity.CA;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @description:
 * @author: ManolinCoder
 * @time: 2022/5/16
 */
@Slf4j
@Service
public class CAService implements ICAService{
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private CA ca;

    @Override
    public void sendPubParams() {
        /**
         *@描述 向rabbitMQ中XDCIAprivChainPub路由中存入公开参数
         *@参数  [ca]
         *@返回值  void
         *@创建人  B1ank
         *@创建时间  2022/5/29
         *@修改人和其它信息
         */
        log.info("[+] Initialization... Broadcasting Publicparameters...");
        String ABEPubString = ca.getABEPubAsString();
        // 日志输出
        // 向RabbitMQ中发送,指定路由名称为XDCIAprivChainPub
        rabbitTemplate.convertAndSend("XDCIAprivChainPub", "", ABEPubString);
    }

    @Override
    public String getSerializedABEPub() {
        /**
         *@描述 用于接收新用户注册时的请求,返回ABE公钥参数
         *@参数  []
         *@返回值  java.lang.String
         *@创建人  B1ank
         *@创建时间  2022/7/25
         *@修改人和其它信息
         */
        return ca.getABEPubAsString();
    }


    @Override
    public void sendMasterKey() {
        /**
         *@描述 向rabbitMQ中ABEMsk队列存入公开参数
         *@参数  [ca]
         *@返回值  void
         *@创建人  B1ank
         *@创建时间  2022/5/29
         *@修改人和其它信息
         */
        log.info("[+] Initialization.... Broadcasting masterKey...");
        String ABEMskString = ca.getABEMskAsString();
        // 日志输出
        // 向RabbitMQ中发送，指定队列名称为ABEMsk
        rabbitTemplate.convertAndSend("ABEMsk", ABEMskString);
    }
}
