package XDCIA.privchaincs.DAO;

import XDCIA.privchaincs.Entity.CSDataEntry;
import org.apache.ibatis.annotations.*;


import java.util.List;


/**
 * @description:
 * @author: ManolinCoder
 * @time: 2022/7/8
 */
@Mapper
public interface CSMapper {

    int insert(@Param("userMsg") String userMsg, @Param("aesKeyCph") String aesKeyCph);

    int update(@Param("userMsg") String userMsg, @Param("aesKeyCph") String aesKeyCph, @Param("blockChainID") String blockChainID, @Param("cloudserverID") int cloudserverID);

    int delete(@Param("cloudserverID") int cloudserverID);

    List<CSDataEntry> selectAll();

    CSDataEntry selectById(@Param("cloudserverID") int cloudserverID);

    int count();

}



