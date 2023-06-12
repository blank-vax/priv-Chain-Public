package XDCIA.privchainclient1.Service;

import XDCIA.privchainclient1.Entity.ABECphKey;
import it.unisa.dia.gas.jpbc.Element;


/**
 * @description:
 * @author: ManolinCoder
 * @time: 2022/7/13
 */
public interface ICService {

    // 注册,Client对象初始化
    void initClient(String sm2HexPub, String abePubAsString) throws Exception;

    // 申请属性密钥,完成client对象的abePrv属性赋值
    void setClientABEPrv(String abePrvAsString);

    // 测试client对象初始化后的abePrv对象
    String getClientABEPrv();

    String getClientABEPub();

    // ABE相关操作
    ABECphKey generateABECphKey(String policy, Element userKey) throws Exception;

    Element generateUserKey();

    // CSResEntry相关操作
    String getSM2Sig();

    String getIDCS();

    void unserializeResEntry(String jsonObject);

    // UploadResEntry相关操作
    String serializeUploadResEntry(String ID_BC, String ID_CS);

    // SM2签名验证
    boolean sm2SigVerification(String sm2SigResult, String data);

    // 数据上链
    String uploadDataToBC(byte[] userKey, String message, String policy);

    // 访问验证
    boolean accessVerify(String ID_BC);

    // DownloadResEntry相关操作
    String serializeDownloadResEntry(String accessVerResult, String msgDecryptResult, String integrityVerResult);

    // 错误处理
    String serializeErrEntry(String errResponse);

    // 数据下载
    // 部分私钥对象ABEPrvComp对象构造
    String getSerializedABEPrvComp();

    // CSDownloadEntry相关操作

    void unserializeCSDownloadEntry(String jsonObject);

    String getABEPreDecryptResult();

    String getUserMsgCph();

    String getABECph();

    // 最终解密操作
    Element finalDecryption(String aesKeyABECph, String aesKeyPreDecrypt);

    // AES私钥解密结果验证
    boolean userAESKeyIntegrityVerify(String ID_BC, byte[] userAESKeyDecrypted);

    // AES明文结果验证
    boolean userMsgIntegrityVerify(String ID_BC, String userMsg);

}
