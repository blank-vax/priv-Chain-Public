package XDCIA.privchainaa.Service;

import XDCIA.privchainaa.Entity.AA;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;

/**
 * @description: AA实体支持的操作
 * @author: ManolinCoder
 * @time: 2022/5/17
 */
@Service
public class AAService implements IAAService{

    @Autowired
    private AA aa;

    @RabbitListener(queues = "ABEPubForAA")
    public void getABEPubForAA(String abePubString){
        /**
         *@描述 完成aa对象中ABEPub属性的相关设置，从队列ABEPubForAA中接收ABEPub字符串并执行相关转换
         *@参数  [abePubString]
         *@返回值  void
         *@创建人  B1ank
         *@创建时间  2022/7/12
         *@修改人和其它信息
         */
        aa.setABEPubFromString(abePubString);
    }

    @RabbitListener(queues = "ABEMsk")
    public void getABEMskForAA(String abeMskString){
        /**
         *@描述 完成aa对象中ABEMsk属性的相关设置，从队列ABEMsk中接收ABEMsk字符串并执行相关转换
         *@参数  [abeMskString]
         *@返回值  void
         *@创建人  B1ank
         *@创建时间  2022/7/12
         *@修改人和其它信息
         */
        aa.setABEMskFromString(abeMskString);
    }

    public String generateABEPrv(String[] attr) throws NoSuchAlgorithmException {
        /**
         *@描述 输入用户属性字符串，返回字符串类型的ABEPrv对象
         *@参数  [attr]
         *@返回值  java.lang.String
         *@创建人  B1ank
         *@创建时间  2022/7/14
         *@修改人和其它信息
         */
        return aa.keyGenByAttr(attr);
    }

}
