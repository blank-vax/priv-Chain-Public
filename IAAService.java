package XDCIA.privchainaa.Service;

import XDCIA.privchainaa.Entity.AA;
import XDCIA.privchainaa.Entity.ABEMsk;
import XDCIA.privchainaa.Entity.ABEPrv;
import XDCIA.privchainaa.Entity.ABEPub;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.web.bind.annotation.PostMapping;

import java.security.NoSuchAlgorithmException;

/**
 * @description: 定义AA实体支持的接口
 * @author: ManolinCoder
 * @time: 2022/5/17
 */
public interface IAAService {


    // 完成对象中ABEPub属性初始化
    void getABEPubForAA(String abePubAsString);

    // 完成对象中ABEMsk属性初始化
    void getABEMskForAA(String abeMskAsString);


    // 接收属性字符串，返回ABEPrv对象序列化后的字符串
    String generateABEPrv(String[] attr) throws NoSuchAlgorithmException;

}
