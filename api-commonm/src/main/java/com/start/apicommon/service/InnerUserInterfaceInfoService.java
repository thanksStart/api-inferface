package com.start.apicommon.service;

import com.start.apicommon.model.entity.InterfaceInfo;
import com.start.apicommon.model.entity.User;
import com.start.apicommon.model.entity.UserInterfaceInfo;


/**
* @author xiesipei
* @description 针对表【user_interface_info(用户调用接口关系表)】的数据库操作Service
* @createDate 2024-02-02 10:37:47
*/
public interface InnerUserInterfaceInfoService{

    /**
     * 数据库中是否已分配给用户密钥（accessKey,secretKey）
     * @param accessKey
     * @return
     */
    User getInvokeUserKey(String accessKey);

    /**
     * 从数据库中查询模拟接口是否存在（请求路径、请求方法）
     * @param path
     * @param method
     * @return
     */
    InterfaceInfo getInterfaceInfo(String path, String method);

    /**
     * 查询用户剩余的调用次数
     * @param interfaceInfoId
     * @param userId
     * @return
     */
    UserInterfaceInfo getLeftNumber(long interfaceInfoId, long userId);

    /**
     * 用户调用接口次数 + 1
     * @param interfaceInfoId
     * @param userId
     * @return
     */
    boolean invokeCount(long interfaceInfoId, long userId);

}
