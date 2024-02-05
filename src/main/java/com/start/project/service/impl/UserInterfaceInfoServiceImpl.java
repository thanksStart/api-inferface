package com.start.project.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.start.apicommon.model.entity.UserInterfaceInfo;
import com.start.project.common.ErrorCode;
import com.start.project.exception.BusinessException;
import com.start.project.service.UserInterfaceInfoService;
import com.start.project.mapper.UserInterfaceInfoMapper;
import org.springframework.stereotype.Service;

/**
* @author xiesipei
* @description 针对表【user_interface_info(用户调用接口关系表)】的数据库操作Service实现
* @createDate 2024-02-02 10:37:47
*/
@Service
public class UserInterfaceInfoServiceImpl extends ServiceImpl<UserInterfaceInfoMapper, UserInterfaceInfo>
    implements UserInterfaceInfoService {


    @Override
    public void validUserInterfaceInfo(UserInterfaceInfo userInterfaceInfo, boolean add) {
        if (userInterfaceInfo == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Long userId = userInterfaceInfo.getUserId();
        Long interfaceInfoId = userInterfaceInfo.getInterfaceInfoId();
        Integer totalNumber = userInterfaceInfo.getTotalNumber();
        Integer leftNumber = userInterfaceInfo.getLeftNumber();
        Integer status = userInterfaceInfo.getStatus();

        // 用户第一次创建时，所有参数必须非空
        if (add) {
            if (userId <= 0 || interfaceInfoId <= 0) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR,"接口或者用戶不存在");
            }
        }
        if (leftNumber <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "剩余调用次数不能小于0");
        }
    }

    @Override
    public boolean invokeCount(long interfaceInfoId, long userId) {
        //校验
        if (interfaceInfoId <= 0 || userId <= 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"接口或者用戶不存在");
        }
        UpdateWrapper<UserInterfaceInfo> userInterfaceInfoUpdateWrapper = new UpdateWrapper<>();
        userInterfaceInfoUpdateWrapper.eq("interfaceInfoId",interfaceInfoId);
        userInterfaceInfoUpdateWrapper.eq("userId",userId);
        userInterfaceInfoUpdateWrapper.setSql("leftNumber = leftNumber - 1, totalNumber = totalNumber + 1");
        return this.update(userInterfaceInfoUpdateWrapper);
    }
}




