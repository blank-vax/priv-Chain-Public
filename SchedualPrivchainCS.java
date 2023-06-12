package XDCIA.privchainfeign.feignInterface;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @description:
 * @author: ManolinCoder
 * @time: 2022/7/19
 */

@FeignClient(value = "privchaincs")
public interface SchedualPrivchainCS {
    /**
     *@描述 定义适用于Feign负载均衡的接口,将访问数据转移至privchaincs.controller
     *@参数
     *@返回值
     *@创建人  B1ank
     *@创建时间  2022/7/19
     *@修改人和其它信息
     */
    // 连接测试,访问页面csLinkTest测试云服务器连接性
    @RequestMapping(value = "csLinkTest", method = RequestMethod.POST)
    String greetSb(@RequestParam(value = "name") String name);

    // 数据上传功能,返回结果为JSON字符串
    @ResponseBody
    @RequestMapping(value = "upload", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    String uploadAndStore(@RequestParam(value = "aesCph") String aesCph, @RequestParam(value = "abeCph") String abeCph);

    // 数据下载功能,返回结果为字符串
    @ResponseBody
    @RequestMapping(value = "download", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    String downloadOperation(@RequestParam(value = "cloudserverID") String ID_CS, @RequestParam(value = "keyForPredecrypt") String abePrvComps);

    @RequestMapping(value = "uploadBlockChainID", method = RequestMethod.POST)
    String uploadBlockChainID(@RequestParam(value = "cloudserverID") String cloudserverID, @RequestParam(value = "blockChainID") String blockChainID);

    @ResponseBody
    @RequestMapping(value = "allDataID", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    String showAllDataID();


}


