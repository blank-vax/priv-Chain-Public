package XDCIA.privchainclient1.Service;

import XDCIA.privchainclient1.Entity.*;
import XDCIA.privchainclient1.ReactEntities.UserDownloadResEntry;
import XDCIA.privchainclient1.ReactEntities.UserUploadResEntry;
import com.alibaba.fastjson.JSONObject;
import it.unisa.dia.gas.jpbc.Element;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @description:
 * @author: ManolinCoder
 * @time: 2022/7/13
 */
@Slf4j
@Service
public class CService implements ICService{

    @Autowired
    Client client;
    @Autowired
    CSUploadEntry csUploadEntry;
    @Autowired
    UserUploadResEntry userUploadResEntry;
    @Autowired
    UserDownloadResEntry userDownloadResEntry;
    @Autowired
    ErrEntry errEntry;
    @Autowired
    CSDownloadEntry csDownloadEntry;



    @Override
    public void initClient(String sm2HexPub, String abePubAsString) throws Exception {
        /**
         *@描述 完成client对象中ABEPub属性和SM2HexPub属性设置,执行反序列化等操作
         *     完成区块链初始化,生成区块链对象,随后可通过智能合约与区块链交互
         *@参数  [sm2HexPub, abePubAsString]
         *@返回值  void
         *@创建人  B1ank
         *@创建时间  2022/7/25
         *@修改人和其它信息
         */
        log.info("[+] Client Init");
        client.setABEPubFromString(abePubAsString);
        client.setSm2HexPubKey(sm2HexPub);
        client.initBlockChain();
        log.info("[+] Client Init Finished");
    }

    @Override
    public void setClientABEPrv(String abePrvAsString){
        /**
         *@描述 接收ABEPrv字符串,执行反序列化并完成属性密钥设置
         *@参数  [abePrvAsString]
         *@返回值  void
         *@创建人  B1ank
         *@创建时间  2022/7/15
         *@修改人和其它信息
         */
        client.setABEPrvFromString(abePrvAsString);
    }

    // 测试类,待删除
    @Override
    public String getClientABEPrv() {
        /**
         *@描述 测试类,完成初始化操作后,获取
         *@参数  []
         *@返回值  void
         *@创建人  B1ank
         *@创建时间  2022/7/17
         *@修改人和其它信息
         */
        return client.getABEPrvAsString();
    }

    @Override
    public String getClientABEPub() {
        /**
         *@描述 测试类,完成初始化操作后,获取
         *@参数  []
         *@返回值  void
         *@创建人  B1ank
         *@创建时间  2022/7/17
         *@修改人和其它信息
         */
        return client.getABEPubAsString();
    }


    @Override
    public ABECphKey generateABECphKey(String policy, Element userKey) throws Exception {
        /**
         *@描述 面向Controller层,接收POST请求传递的Policy和userKey,调用client对象接口
         *     返回ABECphKey对象
         *@参数  [policy, userKey]
         *@返回值  XDCIA.privchainclient1.Entity.ABECphKey
         *@创建人  B1ank
         *@创建时间  2022/7/19
         *@修改人和其它信息
         */
        return client.keyEnc(policy, userKey);
    }

    @Override
    public Element generateUserKey() {
        /**
         *@描述 生成GT群上的元素,作为用户AES算法的对称密钥
         *@参数  []
         *@返回值  it.unisa.dia.gas.jpbc.Element
         *@创建人  B1ank
         *@创建时间  2022/7/19
         *@修改人和其它信息
         */
        return client.generateUserKey();
    }

    // 接收的CSResEntry JSON对象反序列化
    @Override
    public void unserializeResEntry(String jsonObject) {
        /**
         *@描述 CSResEntry对象反序列化
         *@参数  [jsonObject]
         *@返回值  void
         *@创建人  B1ank
         *@创建时间  2022/7/20
         *@修改人和其它信息
         */
        csUploadEntry = JSONObject.parseObject(jsonObject, CSUploadEntry.class);
    }

    // 接收的CSDownloadEntry JSON对象反序列化
    @Override
    public void unserializeCSDownloadEntry(String jsonObject) {
        /**
         *@描述 DownloadEntry对象反序列化
         *@参数  [jsonObject]
         *@返回值  void
         *@创建人  B1ank
         *@创建时间  2022/7/21
         *@修改人和其它信息
         */
        csDownloadEntry = JSONObject.parseObject(jsonObject, CSDownloadEntry.class);
    }

    // 获取CSDownloadEntry对象元素
    @Override
    public String getUserMsgCph() {
        /**
         *@描述 获取用户AES加密密文
         *@参数  []
         *@返回值  java.lang.String
         *@创建人  B1ank
         *@创建时间  2022/7/21
         *@修改人和其它信息
         */
        return csDownloadEntry.getUserMsgCph();
    }

    @Override
    public String getABEPreDecryptResult() {
        /**
         *@描述 获取用户AES密钥的预解密结果
         *@参数  []
         *@返回值  java.lang.String
         *@创建人  B1ank
         *@创建时间  2022/7/21
         *@修改人和其它信息
         */
        return csDownloadEntry.getPreDecryptAESKeyCph();
    }

    @Override
    public String getABECph() {
        /**
         *@描述 获取用户AES密钥的密文
         *@参数  []
         *@返回值  java.lang.String
         *@创建人  B1ank
         *@创建时间  2022/7/21
         *@修改人和其它信息
         */
        return csDownloadEntry.getAesKeyCph();
    }

    // 获取CSResEntry对象元素
    @Override
    public String getIDCS() {
        /**
         *@描述 获取云服务器ID
         *@参数  []
         *@返回值  java.lang.String
         *@创建人  B1ank
         *@创建时间  2022/7/20
         *@修改人和其它信息
         */
        return csUploadEntry.getID_CS();
    }

    @Override
    public String getSM2Sig() {
        /**
         *@描述 获取SM2签名结果
         *@参数  []
         *@返回值  java.lang.String
         *@创建人  B1ank
         *@创建时间  2022/7/20
         *@修改人和其它信息
         */
        return csUploadEntry.getSm2SigResult();
    }

    @Override
    public boolean sm2SigVerification(String sm2SigResult, String data) {
        /**
         *@描述 验证SM2签名
         *@参数  [sm2SigResult, data]
         *@返回值  boolean
         *@创建人  B1ank
         *@创建时间  2022/7/20
         *@修改人和其它信息
         */
        return client.sm2SigVeri(sm2SigResult, data);
    }

    @Override
    public String uploadDataToBC(byte[] userKey, String message, String policy) {
        /**
         *@描述 将数据上传至区块链
         *@参数  [userKey, message, policy]
         *@返回值  java.lang.String
         *@创建人  B1ank
         *@创建时间  2022/7/20
         *@修改人和其它信息
         */
        // 随机生成区块链ID
        // 待修改,区块链支持上传数量有限,极大限制作为数据共享传输系统的实用性
        String[] clientResponse = client.uploadDataToBC(userKey, message, policy);
        log.info("[+] Data upload finished, transaction hash is " + clientResponse[0]);
        log.info("[+] Got blockchain ID: " + clientResponse[1]);
        return clientResponse[1];
    }

    // 上传过程中前端返回结果序列化
    @Override
    public String serializeUploadResEntry(String ID_BC, String ID_CS) {
        /**
         *@描述 接收区块链标识和云服务器标识,统一序列化成JSON格式后返回前端
         *@参数  [ID_BC, ID_CS]
         *@返回值  java.lang.String
         *@创建人  B1ank
         *@创建时间  2022/7/20
         *@修改人和其它信息
         */
        userUploadResEntry.setID_BC(ID_BC);
        userUploadResEntry.setID_CS(ID_CS);
        return JSONObject.toJSONString(userUploadResEntry);
    }

    // 下载过程中前端返回结果序列化
    @Override
    public String serializeDownloadResEntry(String accessVerResult, String msgDecryptResult, String integrityVerResult) {
        /**
         *@描述 接收访问权限验证结果,数据解密结果,解密完整性验证结果,统一序列化成JSON格式后返回前端
         *@参数  [accessVerResult, msgDecryptResult, integrityVerResult]
         *@返回值  java.lang.String
         *@创建人  B1ank
         *@创建时间  2022/7/20
         *@修改人和其它信息
         */
        userDownloadResEntry.setAccessVerResult(accessVerResult);
        userDownloadResEntry.setMsgDecryptResult(msgDecryptResult);
        userDownloadResEntry.setIntegrityVerResult(integrityVerResult);
        return JSONObject.toJSONString(userDownloadResEntry);
    }

    // 验证访问是否通过
    @Override
    public boolean accessVerify(String ID_BC) {
        /**
         *@描述 接收区块链ID及部分私钥,完成验证
         *@参数  [ID_BC, comps]
         *@返回值  boolean
         *@创建人  B1ank
         *@创建时间  2022/7/20
         *@修改人和其它信息
         */
        return client.accessVerify(ID_BC);
    }

    // 构造字符串类型的ABEPrvComp对象
    @Override
    public String getSerializedABEPrvComp() {
        /**
         *@描述 获取序列化后的ABEPrvComp对象,用于后续数据发送
         *@参数  []
         *@返回值  java.lang.String
         *@创建人  B1ank
         *@创建时间  2022/7/21
         *@修改人和其它信息
         */
        byte[] serializedABEPrvCompsAsByte = client.getSerializeABEPrvComps();
        return Base64.encodeBase64String(serializedABEPrvCompsAsByte);
    }


    // 执行最终解密操作
    @Override
    public Element finalDecryption(String aesKeyABECph, String aesKeyPreDecrypt) {
        /**
         *@描述 输入预解密结果与密文,返回Element对象
         *@参数  [aesKeyABECph, aesKeyPreDecrypt]
         *@返回值  java.lang.String
         *@创建人  B1ank
         *@创建时间  2022/7/21
         *@修改人和其它信息
         */
        return client.finalDecrypt(aesKeyABECph, aesKeyPreDecrypt);
    }

    // 错误处理
    @Override
    public String serializeErrEntry(String errResponse) {
        /**
         *@描述 出现错误时,决定向前端返回的JSON信息
         *@参数  [errResponse]
         *@返回值  java.lang.String
         *@创建人  B1ank
         *@创建时间  2022/7/20
         *@修改人和其它信息
         */
        errEntry.setErrResponse(errResponse);
        return JSONObject.toJSONString(errEntry);
    }

    @Override
    public boolean userAESKeyIntegrityVerify(String ID_BC, byte[] userAESKeyDecrypted) {
        /**
         *@描述 用户AES密钥完整性验证
         *@参数  [ID_BC, userAESKeyDecrypted]
         *@返回值  boolean
         *@创建人  B1ank
         *@创建时间  2022/7/21
         *@修改人和其它信息
         */
        return client.userAESKeyIntegrityVerify(ID_BC, userAESKeyDecrypted);
    }

    @Override
    public boolean userMsgIntegrityVerify(String ID_BC, String userMsg) {
        /**
         *@描述 用户明文解密结果完整性验证
         *@参数  [ID_BC, userMsg]
         *@返回值  boolean
         *@创建人  B1ank
         *@创建时间  2022/7/21
         *@修改人和其它信息
         */
        return client.userMsgIntegrityVerify(ID_BC, userMsg);
    }
}
