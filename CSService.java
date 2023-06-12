package XDCIA.privchaincs.Service;

import XDCIA.privchaincs.DAO.CSMapper;
import XDCIA.privchaincs.Entity.*;
import com.alibaba.fastjson.JSONObject;
import it.unisa.dia.gas.jpbc.Element;
import lombok.extern.slf4j.Slf4j;
import org.fisco.bcos.sdk.crypto.hash.SM3Hash;
import org.fisco.bcos.sdk.crypto.signature.SM2Signature;
import org.fisco.bcos.sdk.crypto.signature.SM2SignatureResult;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.ArrayList;
import java.util.List;


/**
 * @description: 云服务器包含以下功能：
 * 1. 存储密文字符串数据并返回云服务器数据库id：ID_CS
 * 2. 针对密文数据执行签名操作并返回签名
 * 3. 接收部分属性密钥，执行预解密操作
 * @author: ManolinCoder
 * @time: 2022/6/10
 */
@Slf4j
@Service
public class CSService implements ICSService{

    @Autowired
    private CSMapper csMapper;
    @Autowired
    private CS cs;
    @Autowired
    private CSUploadEntry csUploadEntry;
    @Autowired
    private CSDownloadEntry csDownloadEntry;
    @Autowired
    private CSDatabaseEntry csDatabaseEntry;

    @Override
    @RabbitListener(queues = "ABEPubForCS")
    public void initForCS(String abePubString){
        /**
         *@描述 完成cs对象中ABEPub属性的相关设置，从队列ABEPubForCS中接收ABEPub字符串并执行相关转换，
         *     针对CSService对象中的cs属性，生成对应SM2密钥，利用rabbitTemplate将十六进制公钥信息发送至XDCIAprivChainSM2Pub对应路由
         *@参数  [abePubString]
         *@返回值  void
         *@创建人  B1ank
         *@创建时间  2022/7/12
         *@修改人和其它信息
         */
        cs.setABEPubFromString(abePubString);
        // 初始化CSService对象，随后向rabbitMQ对应路由和队列中发送SM2签名公钥
        log.info("[+] SM2SignatureKeyPair Generating....");
        cs.generateKeyPair();
        log.info("Broadcasting SM2PublicKeys...");
        // 向RabbitMQ中发送，指定路由名称为XDCIAXDCIAprivChainSM2Pub
        // rabbitTemplate.convertAndSend("XDCIAprivChainSM2Pub", "", cs.sendSM2PublicKeyByHex());
    }

    @Override
    public String getSM2PubHex() {
        /**
         *@描述 以字符串的形式返回SM2公钥的十六进制表示
         *@参数  []
         *@返回值  java.lang.String
         *@创建人  B1ank
         *@创建时间  2022/7/25
         *@修改人和其它信息
         */
        return cs.sendSM2PublicKeyByHex();
    }


    // 数据库相关操作
    @Override
    public int insert(String userMsgCph, String aesKeyCph){ return csMapper.insert(userMsgCph, aesKeyCph); }

    // 待删除
    @Override
    public int delete(int id){ return csMapper.delete(id); }

    @Override
    public int update(int id, String blockChainID){ return csMapper.update(null, null, blockChainID, id); }

    // 待删除
    @Override
    public List<CSDataEntry> selectAll() { return csMapper.selectAll(); }

    @Override
    public CSDataEntry selectById(int id) { return csMapper.selectById(id); }

    // 统计当前云服务器数据库中数据量
    @Override
    public int count() { return csMapper.count(); }


    // 2. 执行数据签名并返回签名至用户侧

    public SM2SignatureResult generateSignatureWithSM2(String data) {
        /**
         *@描述 输入sm2密钥对与待签名数据，返回SM2签名
         *@参数  [sm2KeyPair, data]
         *@返回值  org.fisco.bcos.sdk.crypto.signature.SM2SignatureResult
         *@创建人  B1ank
         *@创建时间  2022/6/10
         *@修改人和其它信息
         */
        SM2Signature signer = new SM2Signature();
        // 计算data的SM3哈希值
        SM3Hash hasher = new SM3Hash();
        String hashData = hasher.hash(data);
        // 签名方即云服务器此处必须提供CryptoKeyPair密钥对象，该对象可通过以下方法获取其中的公钥和私钥
        return (SM2SignatureResult) signer.sign(hashData, cs.getSm2KeyPair());
    }

    // 返回结果序列化
    public String serializeCSUploadEntry(int id, String sm2Signature) {
        /**
         *@描述 接收数据库返回的数据条目ID(int类型)和SM2签名(String类型),返回序列化完成后的JSON字符串
         *@参数  [id, sm2Signature]
         *@返回值  java.lang.String
         *@创建人  B1ank
         *@创建时间  2022/7/20
         *@修改人和其它信息
         */
        csUploadEntry.setID_CS(Integer.toString(id));
        csUploadEntry.setSm2SigResult(sm2Signature);
        String csUploadEntryJson = JSONObject.toJSONString(csUploadEntry);
        return csUploadEntryJson;
    }

    // 可访问数据查看序列化
    public String serializeCSAllDataEntry(List<CSDataEntry> csDataEntries) {
        /**
         *@描述 返回所有可访问对象的ID_BC和ID_CS
         *@参数  []
         *@返回值  java.lang.String
         *@创建人  B1ank
         *@创建时间  2022/7/25
         *@修改人和其它信息
         */
        List<CSDatabaseList> lists = new ArrayList<>();
        for (CSDataEntry csDataEntry : csDataEntries) {
            CSDatabaseList csDatabaseList = new CSDatabaseList();
            csDatabaseList.setID_BC(csDataEntry.getBlockChainID());
            csDatabaseList.setID_CS(Integer.toString(csDataEntry.getCloudserverID()));
            lists.add(csDatabaseList);
        }
        csDatabaseEntry.setCsDatabaseListList(lists);
        return JSONObject.toJSONString(csDatabaseEntry);
    }



    // 3. 预解密
    @Override
    public Element preDecrypt(String aesKeyCph, String keyForPredecrypt) {
        /**
         *@描述 执行预解密操作
         *@参数  [aesKeyCph, keyForPredecrypt]
         *@返回值  java.lang.String
         *@创建人  B1ank
         *@创建时间  2022/7/21
         *@修改人和其它信息
         */
        return cs.preDecrypt(aesKeyCph, keyForPredecrypt);
    }

    // 下载结果序列化
    @Override
    public String serializeCSDownloadEntry(String userMsgAESCph, String preDecryptResult, String userAESKeyCph) {
        /**
         *@描述 序列化下载JSON字符串,依次包含AES密文,预解密结果,AES密钥对应ABE密文
         *@参数  [userMsgAESCph, preDecryptResult, userAESKeyCph]
         *@返回值  java.lang.String
         *@创建人  B1ank
         *@创建时间  2022/7/21
         *@修改人和其它信息
         */
        csDownloadEntry.setUserMsgCph(userMsgAESCph);
        csDownloadEntry.setPreDecryptAESKeyCph(preDecryptResult);
        csDownloadEntry.setAesKeyCph(userAESKeyCph);
        String csDownloadEntryJson = JSONObject.toJSONString(csDownloadEntry);
        return csDownloadEntryJson;
    }
}
