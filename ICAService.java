package XDCIA.privchainca.Service;

import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * @description: CA实体Service提供两个服务接口，分别返回公开参数和主密钥
 * @author: ManolinCoder
 * @time: 2022/5/16
 */
public interface ICAService {

    // 返回公开参数，由属性认证机构AA、云服务器CS和每个新注册的用户Client调用，获取系统的全局公开参数
    void sendPubParams() throws JsonProcessingException;

    // 返回masterKey，由属性认证机构AA调用，完成AA的初始化
    void sendMasterKey() throws JsonProcessingException;

    // 返回序列化后的ABEPub字符串,用于新用户注册;
    String getSerializedABEPub();


}
