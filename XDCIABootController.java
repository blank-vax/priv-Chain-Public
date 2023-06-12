package XDCIA.privchainfeign.Controller;

import XDCIA.privchainfeign.feignInterface.SchedualPrivchainAA;
import XDCIA.privchainfeign.feignInterface.SchedualPrivchainCA;
import XDCIA.privchainfeign.feignInterface.SchedualPrivchainCS;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.security.NoSuchAlgorithmException;

/**
 * @description:
 * @author: ManolinCoder
 * @time: 2022/5/13
 */
@Slf4j
@RestController
public class XDCIABootController {

    @Autowired
    SchedualPrivchainCA schedualPrivchainCA;

    @Autowired
    SchedualPrivchainAA schedualPrivchainAA;

    @Autowired
    SchedualPrivchainCS schedualPrivchainCS;


    @RequestMapping(value = "PubParams", method = RequestMethod.POST)
    public String getABEPubFromAA(){
        /**
         *@描述 测试-ABEPub from AA
         *@参数  []
         *@返回值  java.lang.String
         *@创建人  B1ank
         *@创建时间  2022/7/15
         *@修改人和其它信息
         */
        log.info("[+] Get ABEPub from AA");
        return schedualPrivchainAA.getABEPubFromAA();
    }

    @RequestMapping(value = "MasterKey", method = RequestMethod.POST)
    public String getABEMskFromAA(){
        /**
         *@描述 测试-ABEMsk from AA
         *@参数  []
         *@返回值  java.lang.String
         *@创建人  B1ank
         *@创建时间  2022/7/15
         *@修改人和其它信息
         */
        log.info("[+] Get ABEMsk from AA");
        return schedualPrivchainAA.getABEMskFromAA();
    }

    // 注册阶段
    @RequestMapping(value = "register", method = RequestMethod.POST)
    public String getABEPrvFromAA(@RequestParam(value = "attr", required = true) String userAttr) throws NoSuchAlgorithmException {
        /**
         *@描述 接收使用+分割的属性字符串,返回ABEPrv
         *@参数  [userAttr]
         *@返回值  java.lang.String
         *@创建人  B1ank
         *@创建时间  2022/7/15
         *@修改人和其它信息
         */
        log.info("[+] Generate ABEPrv according to attributes");
        return schedualPrivchainAA.generateABEPrv(userAttr);
    }

    // 用户数据上传阶段
    @ResponseBody
    @RequestMapping(value = "upload", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public String uploadAndStore(@RequestParam(value = "aesCph") String aesCph, @RequestParam(value = "abeCph") String abeCph) {
        /**
         *@描述 转发上传请求至CSController
         *@参数  [aesCph, abeCph]
         *@返回值  java.lang.String
         *@创建人  B1ank
         *@创建时间  2022/7/19
         *@修改人和其它信息
         */
        log.info("[+] Uploading data...");
        return schedualPrivchainCS.uploadAndStore(aesCph, abeCph);
    }


    // 用户数据下载阶段
    @ResponseBody
    @RequestMapping(value = "download", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public String downloadData(@RequestParam(value = "cloudserverID") String ID_CS, @RequestParam(value = "keyForPredecrypt") String abePrvComps) {
        /**
         *@描述 转发下载访问请求至CSController
         *@参数  [ID_CS, abePrvComps]
         *@返回值  java.lang.String
         *@创建人  B1ank
         *@创建时间  2022/7/21
         *@修改人和其它信息
         */
        log.info("[+] Verifying and downloading data...");
        return schedualPrivchainCS.downloadOperation(ID_CS, abePrvComps);
    }


    // 更新区块链ID
    @RequestMapping(value = "uploadBlockChainID", method = RequestMethod.POST)
    public String uploadBlockChainID(@RequestParam(value = "cloudserverID") String cloudserverID, @RequestParam(value = "blockChainID") String blockChainID) {
        /**
         *@描述 转发更新ID_BC请求至CSController
         *@参数  [cloudserverID, blockChainID]
         *@返回值  java.lang.String
         *@创建人  B1ank
         *@创建时间  2022/7/25
         *@修改人和其它信息
         */
        log.info("[+] Uploading blockchain ID");
        return schedualPrivchainCS.uploadBlockChainID(cloudserverID, blockChainID);
    }

    // 显示所有可访问数据的区块链ID与云服务器ID
    @ResponseBody
    @RequestMapping(value = "allDataID", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public String showAllDataID() {
        /**
         *@描述 转发至CSController
         *@参数  []
         *@返回值  java.lang.String
         *@创建人  B1ank
         *@创建时间  2022/7/25
         *@修改人和其它信息
         */
        log.info("[+] Request all data ID");
        return schedualPrivchainCS.showAllDataID();
    }

}
