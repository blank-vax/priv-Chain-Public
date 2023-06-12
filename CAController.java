package XDCIA.privchainca.Controller;

import XDCIA.privchainca.Service.CAService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.*;

/**
 * @description:
 * @author: ManolinCoder
 * @time: 2022/5/16
 */

@Slf4j
@RestController
public class CAController {

    @Autowired
    CAService caService;

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
        return "hello" + name + ",CA连接测试完成!";
    }


    @RequestMapping(value = "getABEPub", method = RequestMethod.GET)
    public String getABEPubAsString() {
        /**
         *@描述 新用户注册时,返回CA分发的ABE系统参数ABEPub
         *@参数  []
         *@返回值  java.lang.String
         *@创建人  B1ank
         *@创建时间  2022/7/25
         *@修改人和其它信息
         */
        log.info("[+] New User Register Finished!");
        return caService.getSerializedABEPub();
    }

//    @RequestMapping("PublicParams")
//    public String getPublicParams() throws JsonProcessingException {
//        log.warn("[-] Illegal Access!This interface should not be exposed!");
//        return "You could not access the public parameters directly!";
//    }
//
//    @RequestMapping("MasterKey")
//    public String getMasterKey() throws JsonProcessingException {
//        log.warn("[-] Illegal Access!This interface should not be exposed!");
//        return "You could not access the master key directly!";
//    }
}
