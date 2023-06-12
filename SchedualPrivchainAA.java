package XDCIA.privchainfeign.feignInterface;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.NoSuchAlgorithmException;

/**
 * @description:
 * @author: ManolinCoder
 * @time: 2022/7/15
 */

@FeignClient(value = "privchainaa")
public interface SchedualPrivchainAA {
    /**
     *@描述 定义适用于Feign负载均衡的接口，将访问数据转移至privchainaa.controller
     *@参数
     *@返回值
     *@创建人  B1ank
     *@创建时间  2022/7/15
     *@修改人和其它信息
     */
    // 连接测试,访问页面aaLinkTest测试属性认证中心连接性
    @RequestMapping(value = "aaLinkTest", method = RequestMethod.POST)
    public String greetSb(@RequestParam(value = "name") String name);

    // 测试类，待删除
    @RequestMapping(value = "PubParams", method = RequestMethod.POST)
    String getABEPubFromAA();

    @RequestMapping(value = "MasterKey", method = RequestMethod.POST)
    String getABEMskFromAA();

    // 属性密钥注册功能
    @RequestMapping(value = "register", method = RequestMethod.POST)
    String generateABEPrv(@RequestParam(value = "attr", required = true) String userAttr) throws NoSuchAlgorithmException;
}
