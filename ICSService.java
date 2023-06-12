package XDCIA.privchaincs.Service;

import XDCIA.privchaincs.Entity.CSDataEntry;
import it.unisa.dia.gas.jpbc.Element;
import org.fisco.bcos.sdk.crypto.signature.SM2SignatureResult;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @description:
 * @author: ManolinCoder
 * @time: 2022/6/10
 */
public interface ICSService {

    // CS对象初始化
    void initForCS(String abePubString);

    String getSM2PubHex();

    // 数据库插入
    int insert(String userMsgCph, String aesKeyCph);

    // 待删除
    // 数据库删除
    int delete(int id);

    // 数据库更新,主要用于更新区块链ID
    int update(int id, String blockChainID);

    // 待删除
    // 数据库全选
    List<CSDataEntry> selectAll();

    // 返回序列化后的所有可下载对象的(ID_BC,ID_CS)标识CSDatabaseEntry
    String serializeCSAllDataEntry(List<CSDataEntry> csDataEntries);

    CSDataEntry selectById(int id);

    // 统计当前数据库中数据量,返回刚刚插入的数据条目序号(ID_CS)
    int count();

    // 生成SM2签名，数据最终由Controller层打包发送
    SM2SignatureResult generateSignatureWithSM2(String data);

    // 返回序列化后的上传数据返回对象CSUploadEntry
    String serializeCSUploadEntry(int id, String sm2Signature);

    // 3. 预解密相关操作
    // 预解密操作
    Element preDecrypt(String aesKeyCph, String keyForPredecrypt);

    // 返回序列化后的下载数据返回对象CSDownloadEntry
    String serializeCSDownloadEntry(String userMsgAESCph, String preDecryptResult, String userAESKeyCph);

}
