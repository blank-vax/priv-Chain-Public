package XDCIA.privchainclient1.Controller;

import XDCIA.privchainclient1.Entity.ABECph;
import XDCIA.privchainclient1.Entity.ABECphKey;
import XDCIA.privchainclient1.Entity.CSDatabaseEntry;
import XDCIA.privchainclient1.Entity.CSDatabaseList;
import XDCIA.privchainclient1.ReactEntities.*;
import XDCIA.privchainclient1.Service.CService;
import XDCIA.privchainclient1.Util.AESUtil;
import XDCIA.privchainclient1.Util.CommuniteUtil;
import XDCIA.privchainclient1.Util.SerializeUtils;
import com.alibaba.fastjson.JSONObject;
import it.unisa.dia.gas.jpbc.Element;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.fisco.bcos.sdk.crypto.hash.SM3Hash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.Objects;


/**
 * @description:
 * @author: ManolinCoder
 * @time: 2022/7/14
 */
@Slf4j
@Controller
public class ClientController {

    @Autowired
    CService cService;

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
        return "hello" + name + ",Client连接测试完成!";
    }

    // 设置主页面
    @RequestMapping(value = "/main", method = RequestMethod.GET)
    public String mainPage() {
        return "mainPage";
    }

    // 设置错误页面
    @RequestMapping(value = "/error", method = RequestMethod.GET)
    public String errorPage() {
        return "errorPage";
    }


    @RequestMapping(value = "/register", method = RequestMethod.GET)
    public String getUserInfo(Model model) {
        model.addAttribute("userRegisterEntry", new UserRegisterEntry());
        return "register";
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public ModelAndView userRegister(@ModelAttribute UserRegisterEntry userRegisterEntry) throws Exception {
        /**
         *@描述 接收前端页面上传的用户属性字符串,完成注册操作后返回html页面registerResult
         *@参数  [userRegisterEntry]
         *@返回值  java.lang.String
         *@创建人  B1ank
         *@创建时间  2022/9/15
         *@修改人和其它信息
         */
        // 完成初始化,直接向BOOT-CS和BOOT-CA发送请求
        // 访问BOOT-CS的发送SM2公钥链接
        String urlInitCS = "http://127.0.0.1:8764/sm2PubHex";
        String sm2PubKeyHex = CommuniteUtil.sendGetRequest(urlInitCS);
        // 访问BOOT-CA的发送
        String urlInitCA = "http://127.0.0.1:8762/getABEPub";
        String abePubAsString = CommuniteUtil.sendGetRequest(urlInitCA);
        // 完成初始化
        cService.initClient(sm2PubKeyHex, abePubAsString);
        // 注册仅进行一次
        // 构造k-v对,发送给数据映射封装函数CommuniteUtil.generateDataMap()
        String[] keySet2 = new String[1];
        String[] valueSet2 = new String[1];
        keySet2[0] = "attr";
        valueSet2[0] = userRegisterEntry.getUserAttr();
        log.info("!Test:" + valueSet2[0]);
        // valueSet2[0] = userAttr;
        log.info("[+] Starting ABEPrv Generating....");
        // 指定发送POST请求的URL,测试过程中可修改
        String url = "http://127.0.0.1:8765/register";
        // 将userAttr封装进POST请求,发送至Feign对应接口,接收序列化后的ABEPrv
        MultiValueMap<String, String> dataMap = CommuniteUtil.generateDataMap(keySet2, valueSet2);
        // 获取AA序列化生成的ABEPrv字符串
        String abePrvAsString = CommuniteUtil.sendPOSTRequest(url, dataMap);
        // 反序列化处理ABEPrv,初始化至cs对象的ABEPrv属性
        cService.setClientABEPrv(abePrvAsString);
        log.info("[+] Finish ABEPrv Generating...");
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("registerResult", "Attribute key generation finish according to the input attributes!");
        modelAndView.setViewName("registerResult");
        return modelAndView;
        // return "Attribute key generation finish according to the input attributes!";
    }

    @RequestMapping(value = "/upload", method = RequestMethod.GET)
    public String uploadUserData(Model model) {
        model.addAttribute("userUploadEntry", new UserUploadEntry());
        return "upload";
    }

    @ResponseBody
    @RequestMapping(value = "/upload", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public ModelAndView encryptMsg(@ModelAttribute UserUploadEntry userUploadEntry)throws Exception {
        /**
         *@描述 接收前端传来的String类型userMsg和String类型userPolicy,执行加密及其他操作
         *@参数  [userMsg, userPolicy]
         *@返回值  java.lang.String
         *@创建人  B1ank
         *@创建时间  2022/7/19
         *@修改人和其它信息
         */
        String userMsg = userUploadEntry.getUserMsg();
        String userPolicy = userUploadEntry.getUserPolicy();
        log.info("[+]User message: " + userMsg);
        log.info("[+]User policy: " + userPolicy);
        ModelAndView modelAndView = new ModelAndView();
        String[] keySet = new String[2];
        String[] valueSet = new String[2];
        // 加密
        // 生成AES算法密钥
        Element userKey = cService.generateUserKey();
        log.info("[+] Generate AES userKey from GT");
        // 将GT上的随机元素导出为对称密钥
        SM3Hash hash = new SM3Hash();
        byte[] userAESKey = hash.hash(userKey.toBytes());
        // 生成用于AES密钥完整性验证的SM3散列
        log.info("[+] Got AES userKey as byte arrays using SM3");
        // 执行AES加密操作
        byte[] aesEncryptedMsg = AESUtil.encrypt(userAESKey, userMsg.getBytes(StandardCharsets.UTF_8));
        log.info("[+] AES encryption finished");
        // 针对AES密钥userKey执行属性加密
        ABECphKey userKeyAndCp = cService.generateABECphKey(userPolicy, userKey);
        ABECph userKeyCp = userKeyAndCp.getCph();
        log.info("[+] AES key encapsulation finished");
        // 向云服务器发送加密后的数据与封装后的AES密钥
        // 构造{"aesCph": "xxx", "abeCph": "xxx"}
        keySet[0] = "aesCph";
        String aesCph = Base64.encodeBase64String(aesEncryptedMsg);
        valueSet[0] = aesCph;
        keySet[1] = "abeCph";
        String abeCph = Base64.encodeBase64String(SerializeUtils.serializeABECph(userKeyCp));
        valueSet[1] = abeCph;
        log.info("[+] Sending AES and ABE ciphertext to CS");
        // 指定发送POST请求的URL
        // 接收Json字符串,分别代表(ID_CS,SM2Signature_toString)
        String url = "http://127.0.0.1:8765/upload";
        MultiValueMap<String, String> dataMap = CommuniteUtil.generateDataMap(keySet, valueSet);
        // 获取Json格式的(ID_CS, SM2Signature_toString)
        String csResEntryAsJson = CommuniteUtil.sendPOSTRequest(url, dataMap);
        // 反序列化,结果存储与cService.csResEntry中
        if(csResEntryAsJson == null) {
            log.info("[-] Something error while inserting ABE and AES ciphertext in cloud server");
            modelAndView.addObject("error","Something error while inserting ABE and AES ciphertext in cloud server");
            modelAndView.setViewName("errorPage");
            return modelAndView;
//            return cService.serializeErrEntry("Something error while inserting ABE and AES ciphertext in cloud server");
        }
        cService.unserializeResEntry(csResEntryAsJson);
        log.info("[+] Data upload finished");
        // 验证SM2签名
        boolean SM2Ver = cService.sm2SigVerification(cService.getSM2Sig(), aesCph+abeCph);
        if(SM2Ver) {
            log.info("[+] SM2 verification passed, cloud server work normally");
        } else {
            // SM2签名验证不通过则报错
            log.warn("[-] SM2 verification failed, cloud server work abnormally");
            modelAndView.addObject("error", "Upload failed, something error in cloud server");
            modelAndView.setViewName("errorPage");
            return modelAndView;
//            return cService.serializeErrEntry("Upload failed, something error in cloud server");
        }
        // 上传userKey散列至区块链
        log.info("[+] Uploading resource to remote blockchain");
        String ID_BC = cService.uploadDataToBC(userKey.toBytes(), userMsg, userPolicy);

        // 更新云服务器数据库中对应数据条目的ID_BC
        String url2 = "http://127.0.0.1:8765/uploadBlockChainID";
        keySet[0] = "cloudserverID";
        valueSet[0] = cService.getIDCS();
        keySet[1] = "blockChainID";
        valueSet[1] = ID_BC;
        MultiValueMap<String, String> dataMap2 = CommuniteUtil.generateDataMap(keySet, valueSet);
        String updateResult = CommuniteUtil.sendPOSTRequest(url2, dataMap2);
        log.info(updateResult);
        if(Objects.equals(updateResult, "Update blockchainID finished!")){
            // 区块链ID更新成功
            log.info("[+] Blockchain ID update success");
        } else {
            log.warn("[-] Blockchain ID update failed, something error!");
        }
        log.info("[+] Generating ClientController upload Json object");

        UserUploadResEntry userUploadResEntry = new UserUploadResEntry();
        userUploadResEntry.setID_BC(ID_BC);
        userUploadResEntry.setID_CS(cService.getIDCS());
        modelAndView.addObject("userUploadResEntry", userUploadResEntry);
        modelAndView.setViewName("uploadResult");
        return modelAndView;
//        return cService.serializeUploadResEntry(ID_BC, cService.getIDCS());
    }

    // @ResponseBody
    @RequestMapping(value = "/allEntry", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public String showAllDataEntryID(Model model) throws IOException {
        /**
         *@描述 以Json格式展示所有可访问数据对应ID_CS和ID_BC
         *@参数  []
         *@返回值  java.lang.String
         *@创建人  B1ank
         *@创建时间  2022/7/25
         *@修改人和其它信息
         */
        // 向对应接口发送请求
        log.info("[+] Show the ID_CS and ID_BC of all data entries");
        String url = "http://127.0.0.1:8765/allDataID";
        String IDjson =  CommuniteUtil.sendGetRequest(url);
        CSDatabaseEntry csDatabaseEntry = JSONObject.parseObject(IDjson, CSDatabaseEntry.class);
        List<CSDatabaseList> csDatabaseListList = csDatabaseEntry.getCsDatabaseListList();
        model.addAttribute("csDatabaseEntries", csDatabaseListList);
        return "allEntry";
    }


    @RequestMapping(value = "/download", method = RequestMethod.GET)
    public String getData(Model model) {
        model.addAttribute("userDownloadEntry", new UserDownloadEntry());
        return "download";
    }

    @ResponseBody
    @RequestMapping(value = "/download", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public ModelAndView decryptMsg(@ModelAttribute UserDownloadEntry userDownloadEntry) throws GeneralSecurityException {
        /**
         *@描述 接收前端传递的ID_BC和ID_CS字符串,依次完成访问控制验证,原始数据解密,解密结果完整性验证并输出结果
         *@参数  [ID_BC, ID_CS]
         *@返回值  java.lang.String
         *@创建人  B1ank
         *@创建时间  2022/7/20
         *@修改人和其它信息
         */
        SM3Hash hash = new SM3Hash();
        String accessVerResult;
        String msgDecryptResult;
        String integrityVerResult;
        ModelAndView modelAndView = new ModelAndView();

        String ID_BC = userDownloadEntry.getID_BC();
        String ID_CS = userDownloadEntry.getID_CS();
        log.info("[+]ID_BC " + ID_BC);
        // 验证访问策略
        boolean accessVeriResult = cService.accessVerify(ID_BC);
        if(accessVeriResult) {
            accessVerResult = "Attribute verification passed, you could access the resource";
            log.info("[+] Attribute verification passed, next step is go on");
        } else {
            accessVerResult = "Attribute verification failed, you could not access the resource";
            log.warn("[-] Attribute verification failed, exit..");
            modelAndView.addObject("error", accessVerResult);
            modelAndView.setViewName("errorPage");
            return modelAndView;
            // return cService.serializeErrEntry(accessVerResult);
        }
        // 若验证通过,则向云服务器发送请求,执行解密操作
        // 向云服务器发送的数据为: ID_CS, 部分私钥String
        String[] keySet = new String[2];
        String[] valueSet = new String[2];

        keySet[0] = "cloudserverID";
        log.info("ID_CS: " + ID_CS);
        valueSet[0] = ID_CS;
        keySet[1] = "keyForPredecrypt";
        valueSet[1] = cService.getSerializedABEPrvComp();
        log.info("[+] Sending cloudserverID and predecryptKey to CS");
        String url = "http://127.0.0.1:8765/download";
        MultiValueMap<String, String> dataMap = CommuniteUtil.generateDataMap(keySet, valueSet);
        String downloadCSResponse = CommuniteUtil.sendPOSTRequest(url, dataMap);
        if(downloadCSResponse == null) {
            // 身份验证失败,返回null
            msgDecryptResult = "Warning!! The verification result from cloud server is not consistent with that from blockchain, please watch out the security of cloud server!";
            log.warn("[-] Access denied, something error in cloud server, exit...");
            modelAndView.addObject("error", msgDecryptResult);
            modelAndView.setViewName("errorPage");
            return modelAndView;
            // return cService.serializeErrEntry(msgDecryptResult);
        }
        // 若返回非空,则序列化返回的JSON字符串
        cService.unserializeCSDownloadEntry(downloadCSResponse);
        log.info("[+] Final decryption..");
        // 从云服务器获取字符串形式的AES密文
        String aesKeyCph = cService.getABECph();
        // 从云服务器获取AES密钥预解密结果
        String aesKeyCphPreDecrypt = cService.getABEPreDecryptResult();
        // 获取AES对称密钥,即加密过程中生成的userKey
        Element userAESKey = cService.finalDecryption(aesKeyCph, aesKeyCphPreDecrypt);

        log.info("[+] Got AES key for user message");
        byte[] userAESKeyByte = hash.hash(userAESKey.toBytes());
        // 获取AES密文并Base64解码为byte[]
        log.info("[+] Got AES ciphertext for user message");
        log.info("[+] AES key integrity verification");
        // 验证解密后获取的用户AES密钥完整性
        if(cService.userAESKeyIntegrityVerify(ID_BC, userAESKey.toBytes())){
            // 验证通过,AES解密密钥无误
            log.info("[+] AES key decryption success, AES key is correct!");
        } else {
            log.warn("[-] Key integrity verification failed");
            modelAndView.addObject("error", "Sorry, AES Key decryption failed!");
            modelAndView.setViewName("errorPage");
            return modelAndView;
            // return cService.serializeErrEntry("Sorry, AES key decryption failed!");
        }
        String userMsgCph = cService.getUserMsgCph();
        byte[] userMsgCphAsByte = Base64.decodeBase64(userMsgCph);
        // 执行AES解密操作
        log.info("[+] AES decryption...");

        byte[] userMsgByte = AESUtil.decrypt(userAESKeyByte, userMsgCphAsByte);
        // 从字节数组还原为原始字符串
        msgDecryptResult = new String(userMsgByte, StandardCharsets.UTF_8);
        log.info("[+] AES decryption result integrity verification");
        if(cService.userMsgIntegrityVerify(ID_BC, msgDecryptResult)) {
            // AES解密结果完整性验证通过
            log.info("[+] User message decryption success, decryption result is correct!");
            integrityVerResult = "Message integrity verification passed";
        } else {
            log.warn("[-] Something error during AES decryption!");
            modelAndView.addObject("error", "Sorry, something error while AES decryption!");
            modelAndView.setViewName("errorPage");
            return modelAndView;
            // return cService.serializeErrEntry("Sorry, something error while AES decryption!");
        }
        // 返回最终解密结果与完整性检验结果
        log.info("[+] Generating ClientController download Json object");
        UserDownloadResEntry userDownloadResEntry = new UserDownloadResEntry();
        userDownloadResEntry.setAccessVerResult(accessVerResult);
        userDownloadResEntry.setMsgDecryptResult(msgDecryptResult);
        userDownloadResEntry.setIntegrityVerResult(integrityVerResult);
        modelAndView.addObject("userDownloadResEntry", userDownloadResEntry);
        modelAndView.setViewName("downloadResult");
        return modelAndView;
        // return cService.serializeDownloadResEntry(accessVerResult, msgDecryptResult, integrityVerResult);
    }

//    // 测试接收的ABEPrv是否成功还原
//    @RequestMapping(value = "PrvKey", method = RequestMethod.POST)
//    public String abePrvGenerationTest() {
//        /**
//         *@描述 测试ABEPrv传输与序列化
//         *@参数  []
//         *@返回值  java.lang.String
//         *@创建人  B1ank
//         *@创建时间  2022/7/17
//         *@修改人和其它信息
//         */
//        return cService.getClientABEPrv();
//    }
//
//    // 测试是否成功接收ABEPub对象
//    @RequestMapping(value = "PubKey", method = RequestMethod.POST)
//    public String abePubGenerationTest() {
//        /**
//         *@描述 测试ABEPub传输与序列化
//         *@参数  []
//         *@返回值  java.lang.String
//         *@创建人  B1ank
//         *@创建时间  2022/7/17
//         *@修改人和其它信息
//         */
//        return cService.getClientABEPub();
//    }

    //    @RequestMapping(value = "register", method = RequestMethod.POST)
//    public String getABEPrvFromAA(@RequestParam(value = "attr", required = true) String userAttr) throws Exception {
//        /**
//         *@描述 接收访问本地register的字符串,向Feign服务器发送POST请求,接收对应结果并解析,初始化cService.cs.ABEPrv
//         *@参数  [userAttr]
//         *@返回值  void
//         *@创建人  B1ank
//         *@创建时间  2022/7/15
//         *@修改人和其它信息
//         */
//        // 完成初始化,直接向BOOT-CS和BOOT-CA发送请求
//        // 访问BOOT-CS的发送SM2公钥链接
//        String urlInitCS = "http://127.0.0.1:8764/sm2PubHex";
//        String sm2PubKeyHex = CommuniteUtil.sendGetRequest(urlInitCS);
//        // 访问BOOT-CA的发送
//        String urlInitCA = "http://127.0.0.1:8762/getABEPub";
//        String abePubAsString = CommuniteUtil.sendGetRequest(urlInitCA);
//        // 完成初始化
//        cService.initClient(sm2PubKeyHex, abePubAsString);
//        // 注册仅进行一次
//        // 构造k-v对,发送给数据映射封装函数CommuniteUtil.generateDataMap()
//        String[] keySet2 = new String[1];
//        String[] valueSet2 = new String[1];
//        keySet2[0] = "attr";
//        valueSet2[0] = userAttr;
//        log.info("[+] Starting ABEPrv Generating....");
//        // 指定发送POST请求的URL,测试过程中可修改
//        String url = "http://127.0.0.1:8765/register";
//        // 将userAttr封装进POST请求,发送至Feign对应接口,接收序列化后的ABEPrv
//        MultiValueMap<String, String> dataMap = CommuniteUtil.generateDataMap(keySet2, valueSet2);
//        // 获取AA序列化生成的ABEPrv字符串
//        String abePrvAsString = CommuniteUtil.sendPOSTRequest(url, dataMap);
//        // 反序列化处理ABEPrv,初始化至cs对象的ABEPrv属性
//        cService.setClientABEPrv(abePrvAsString);
//        log.info("[+] Finish ABEPrv Generating...");
//        return "Attribute key generation finish according to the input attributes!";
//    }
    //    @ResponseBody
//    @RequestMapping(value = "upload", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
//    public String encryptMsg(@RequestParam(value = "msg", required = true) String userMsg, @RequestParam(value = "policy", required = true) String userPolicy) throws Exception {
//        /**
//         *@描述 接收前端传来的String类型userMsg和String类型userPolicy,执行加密及其他操作
//         *@参数  [userMsg, userPolicy]
//         *@返回值  java.lang.String
//         *@创建人  B1ank
//         *@创建时间  2022/7/19
//         *@修改人和其它信息
//         */
//        String[] keySet = new String[2];
//        String[] valueSet = new String[2];
//        // 加密
//        // 生成AES算法密钥
//        Element userKey = cService.generateUserKey();
//        log.info("[+] Generate AES userKey from GT");
//        // 将GT上的随机元素导出为对称密钥
//        SM3Hash hash = new SM3Hash();
//        byte[] userAESKey = hash.hash(userKey.toBytes());
//        // 生成用于AES密钥完整性验证的SM3散列
//        log.info("[+] Got AES userKey as byte arrays using SM3");
//        // 执行AES加密操作
//        byte[] aesEncryptedMsg = AESUtil.encrypt(userAESKey, userMsg.getBytes(StandardCharsets.UTF_8));
//        log.info("[+] AES encryption finished");
//        // 针对AES密钥userKey执行属性加密
//        ABECphKey userKeyAndCp = cService.generateABECphKey(userPolicy, userKey);
//        ABECph userKeyCp = userKeyAndCp.getCph();
//        log.info("[+] AES key encapsulation finished");
//        // 向云服务器发送加密后的数据与封装后的AES密钥
//        // 构造{"aesCph": "xxx", "abeCph": "xxx"}
//        keySet[0] = "aesCph";
//        String aesCph = Base64.encodeBase64String(aesEncryptedMsg);
//        valueSet[0] = aesCph;
//        keySet[1] = "abeCph";
//        String abeCph = Base64.encodeBase64String(SerializeUtils.serializeABECph(userKeyCp));
//        valueSet[1] = abeCph;
//        log.info("[+] Sending AES and ABE ciphertext to CS");
//        // 指定发送POST请求的URL
//        // 接收Json字符串,分别代表(ID_CS,SM2Signature_toString)
//        String url = "http://127.0.0.1:8765/upload";
//        MultiValueMap<String, String> dataMap = CommuniteUtil.generateDataMap(keySet, valueSet);
//        // 获取Json格式的(ID_CS, SM2Signature_toString)
//        String csResEntryAsJson = CommuniteUtil.sendPOSTRequest(url, dataMap);
//        // 反序列化,结果存储与cService.csResEntry中
//        if(csResEntryAsJson == null) {
//            log.info("[-] Something error while inserting ABE and AES ciphertext in cloud server");
//            return cService.serializeErrEntry("Something error while inserting ABE and AES ciphertext in cloud server");
//        }
//        cService.unserializeResEntry(csResEntryAsJson);
//        log.info("[+] Data upload finished");
//        // 验证SM2签名
//        boolean SM2Ver = cService.sm2SigVerification(cService.getSM2Sig(), aesCph+abeCph);
//        if(SM2Ver) {
//            log.info("[+] SM2 verification passed, cloud server work normally");
//        } else {
//            // SM2签名验证不通过则报错
//            log.warn("[-] SM2 verification failed, cloud server work abnormally");
//            return cService.serializeErrEntry("Upload failed, something error in cloud server");
//        }
//        // 上传userKey散列至区块链
//        log.info("[+] Uploading resource to remote blockchain");
//        String ID_BC = cService.uploadDataToBC(userKey.toBytes(), userMsg, userPolicy);
//
//        // 更新云服务器数据库中对应数据条目的ID_BC
//        String url2 = "http://127.0.0.1:8765/uploadBlockChainID";
//        keySet[0] = "cloudserverID";
//        valueSet[0] = cService.getIDCS();
//        keySet[1] = "blockChainID";
//        valueSet[1] = ID_BC;
//        MultiValueMap<String, String> dataMap2 = CommuniteUtil.generateDataMap(keySet, valueSet);
//        String updateResult = CommuniteUtil.sendPOSTRequest(url2, dataMap2);
//        log.info(updateResult);
//        if(Objects.equals(updateResult, "Update blockchainID finished!")){
//            // 区块链ID更新成功
//            log.info("[+] Blockchain ID update success");
//        } else {
//            log.warn("[-] Blockchain ID update failed, something error!");
//        }
//        log.info("[+] Generating ClientController upload Json object");
//        return cService.serializeUploadResEntry(ID_BC, cService.getIDCS());
//    }

//    @ResponseBody
//    @RequestMapping(value = "allDataID", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
//    public String showAllDataEntryID() throws IOException {
//        /**
//         *@描述 以Json格式展示所有可访问数据对应ID_CS和ID_BC
//         *@参数  []
//         *@返回值  java.lang.String
//         *@创建人  B1ank
//         *@创建时间  2022/7/25
//         *@修改人和其它信息
//         */
//        // 向对应接口发送请求
//        log.info("[+] Show the ID_CS and ID_BC of all data entries");
//        String url = "http://127.0.0.1:8765/allDataID";
//        return CommuniteUtil.sendGetRequest(url);
//    }

}
