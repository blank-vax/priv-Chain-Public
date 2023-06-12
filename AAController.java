package XDCIA.privchainaa.Controller;

import XDCIA.privchainaa.Service.AAService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.NoSuchAlgorithmException;

/**
 * @description:
 * @author: ManolinCoder
 * @time: 2022/6/9
 */
@Slf4j
@RestController
// 测试版本
public class AAController {

    @Autowired
    AAService aaService;

    @RequestMapping(value = "", method = RequestMethod.POST)
    public String greetSb(@RequestParam(value = "name") String name){
        /**
         *@描述 连接测试
         *@参数  [name]
         *@返回值  java.lang.String
         *@创建人  B1ank
         *@创建时间  2022/7/14
         *@修改人和其它信息
         */
        return "hello" + name + ",AA连接测试完成!";
    }

    @RequestMapping(value = "register", method = RequestMethod.POST)
    public String generateABEPrv(@RequestParam(value = "attr", required = true)String userAttr) throws NoSuchAlgorithmException {
        /**
         *@描述 接收用户端传输的属性字符串,不同属性采用+分割,返回序列化后的ABEPrv对象
         *@参数  [userAttr]
         *@返回值  java.lang.String
         *@创建人  B1ank
         *@创建时间  2022/7/14
         *@修改人和其它信息
         */
        log.info("[+] Receiving attributes and generate ABEPrv ... ");
        String[] userAttrArray = userAttr.split("[+]");
        log.info("[+] Received attributes strings");
        for(int i = 0;i< userAttrArray.length;i++){
            log.info("[+] Attribute:" + userAttrArray[i]);
        }
        String abePrvGenerated = aaService.generateABEPrv(userAttrArray);
        log.info("[+] PrvKeyFrom AA : " + abePrvGenerated);
        return abePrvGenerated;
    }

}
