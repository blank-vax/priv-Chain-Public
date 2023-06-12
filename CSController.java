package XDCIA.privchaincs.Controller;

import XDCIA.privchaincs.Entity.CSDataEntry;
import it.unisa.dia.gas.jpbc.Element;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import XDCIA.privchaincs.Service.CSService;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * @description:
 * @author: ManolinCoder
 * @time: 2022/6/10
 */
@Slf4j
@RestController
public class CSController {

    @Autowired
    CSService csService;


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
        log.info("[+] Cloud server link test passed");
        return "hello" + name + ",CS连接测试完成!";
    }

    @RequestMapping(value = "sm2PubHex", method = RequestMethod.GET)
    public String getSM2PubHex() {
        /**
         *@描述 返回SM2验证公钥
         *@参数  []
         *@返回值  java.lang.String
         *@创建人  B1ank
         *@创建时间  2022/7/25
         *@修改人和其它信息
         */
        log.info("[+] Got SM2 verification public key in hex from cloud server");
        return csService.getSM2PubHex();
    }


    @ResponseBody
    @RequestMapping(value = "upload", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public String uploadAESandABECph(@RequestParam(value = "aesCph") String aesCph, @RequestParam(value = "abeCph") String abeCph) {
        /**
         *@描述 接收客户端传输的AES加密后的原始数据密文和利用ABE加密后的AES对称密钥
         *@参数  [aesCph, abeCph]
         *@返回值  java.lang.String
         *@创建人  B1ank
         *@创建时间  2022/7/19
         *@修改人和其它信息
         */
        // 将aesCph和abeCph存入数据库
        log.info("[+] Store AESCph and ABECph in cloud server database");
        // 数据插入是否正常
        if(csService.insert(aesCph, abeCph) == 1){
            // 若插入正常,则记录
            log.info("[+] AES and ABE ciphertext insert function works normally");
        } else {
            log.info("[-] Something error while inserting AES and ABE ciphertext");
            return null;
        }
        // 查询数据库中全部数据量,最新插入的数据主键自增,故数据量即为主键
        int ID_CS = csService.count();
        // 执行SM2签名
        // 拼接aesCph和abeCph,执行SM2签名
        log.info("[+] Generating SM2 signature");
        String sm2Signature = csService.generateSignatureWithSM2(aesCph+abeCph).convertToString();
        // 返回SM2序列化后的CSResEntry JSON对象
        log.info("[+] Generating upload return Json object");
        return csService.serializeCSUploadEntry(ID_CS, sm2Signature);
    }

    @RequestMapping(value = "uploadBlockChainID", method = RequestMethod.POST)
    public String uploadBlockChainID(@RequestParam(value = "cloudserverID") String cloudserverID, @RequestParam(value = "blockChainID") String blockChainID) {
        /**
         *@描述 更新区块链ID,便于后续显示可访问数据
         *@参数  [cloudserverID, blockChainID]
         *@返回值  java.lang.String
         *@创建人  B1ank
         *@创建时间  2022/7/25
         *@修改人和其它信息
         */
        log.info("[+] Got blockchain ID and store");
        csService.update(Integer.parseInt(cloudserverID), blockChainID);
        return "Update blockchainID finished!";
    }

    @ResponseBody
    @RequestMapping(value = "allDataID", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public String showAllDataID() {
        /**
         *@描述 接收GET请求,以JSON格式返回云服务器数据库中现存所有数据条目的区块链ID和云服务器ID,以备后续访问
         *@参数  []
         *@返回值  java.lang.String
         *@创建人  B1ank
         *@创建时间  2022/7/25
         *@修改人和其它信息
         */
        log.info("[+] Show all data ID_BC and ID_CS");
        List<CSDataEntry> csDataEntryList = csService.selectAll();
        return csService.serializeCSAllDataEntry(csDataEntryList);
    }


    @ResponseBody
    @RequestMapping(value = "download", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public String downloadOperation(@RequestParam(value = "cloudserverID") String ID_CS, @RequestParam(value = "keyForPredecrypt") String abePrvComps) {
        /**
         *@描述 接收用户上传的预解密密钥,反序列化后从数据库获取两类密文,针对AES密钥执行预解密,返回预解密结果和用户数据明文
         *@参数  [ID_CS, abePrvComps]
         *@返回值  java.lang.String
         *@创建人  B1ank
         *@创建时间  2022/7/21
         *@修改人和其它信息
         */
        // 获取AESKey的密文和用户上传数据对应密文
        log.info("[+] Got ciphertext of AESKey and userMsg");
        CSDataEntry csDataEntry = csService.selectById(Integer.parseInt(ID_CS));
        String userMsgCph = csDataEntry.getUserMsgCph();
        String userAESKeyCph = csDataEntry.getAesKeyCph();
        // 预解密
        log.info("[+] Predecrypt...");
        Element preDecryptResult = csService.preDecrypt(userAESKeyCph, abePrvComps);
        // 序列化Element对象
        if(preDecryptResult == null) {
            // 预解密失败,则返回null
            log.warn("[-] Predecrypt failed");
            return null;
        }
        // 将Element对象转换为byte[]数组,随后转换为字符串并返回
        String userAESKeyPreDecryptResult = Base64.encodeBase64String(preDecryptResult.toBytes());
        // JSON结果包含(AES密文,云服务器预解密结果,AES密钥对应ABE密文)
        log.info("[+] Generating download return Json object");
        return csService.serializeCSDownloadEntry(userMsgCph, userAESKeyPreDecryptResult, userAESKeyCph);
    }
}
