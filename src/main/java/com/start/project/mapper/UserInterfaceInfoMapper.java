package com.start.project.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.start.apicommon.model.entity.UserInterfaceInfo;

import java.util.List;


/**
* @author xiesipei
* @description 针对表【user_interface_info(用户调用接口关系表)】的数据库操作Mapper
* @createDate 2024-02-02 10:37:47
* @Entity com.start.project.domain.UserInterfaceInfo
*/
public interface UserInterfaceInfoMapper extends BaseMapper<UserInterfaceInfo> {

    List<UserInterfaceInfo> listTopInvokeInterfaceInfo(int limit);
}




