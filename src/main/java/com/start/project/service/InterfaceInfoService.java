package com.start.project.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.start.apicommon.model.entity.InterfaceInfo;


/**
* @author xiesipei
* @description 针对表【interface_info(接口信息)】的数据库操作Service
* @createDate 2024-01-22 12:34:47
*/
public interface InterfaceInfoService extends IService<InterfaceInfo> {
    /**
     * 校验
     *
     * @param interfaceInfo
     * @param add 是否为创建校验
     */
    void validInterfaceInfo(InterfaceInfo interfaceInfo, boolean add);
}
