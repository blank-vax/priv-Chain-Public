package XDCIA.privchainfeign.feignInterface;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @description:
 * @author: ManolinCoder
 * @time: 2022/5/13
 */

@FeignClient(value = "privchainca")
public interface SchedualPrivchainCA {
    /**
     *@描述 Feign负载均衡功能测试接口
     *@参数
     *@返回值
     *@创建人  B1ank
     *@创建时间  2022/7/15
     *@修改人和其它信息
     */
    // 连接测试,访问页面caLinkTest测试认证中心连接性
    @RequestMapping(value = "caLinkTest", method = RequestMethod.POST)
    public String greetSb(@RequestParam(value = "name") String name);
}










