package com.start.project.service.inner;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.start.apicommon.model.entity.InterfaceInfo;
import com.start.apicommon.model.entity.User;
import com.start.apicommon.model.entity.UserInterfaceInfo;
import com.start.apicommon.service.InnerUserInterfaceInfoService;
import com.start.project.common.ErrorCode;
import com.start.project.exception.BusinessException;
import com.start.project.mapper.InterfaceInfoMapper;
import com.start.project.mapper.UserInterfaceInfoMapper;
import com.start.project.mapper.UserMapper;
import com.start.project.service.UserInterfaceInfoService;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;

@DubboService
public class InnerUserInterfaceInfoServiceImpl implements InnerUserInterfaceInfoService {

    @Resource
    private UserInterfaceInfoService userInterfaceInfoService;
    @Resource
    private UserMapper userMapper;
    @Resource
    private InterfaceInfoMapper interfaceInfoMapper;
    @Resource
    private UserInterfaceInfoMapper userInterfaceInfoMapper;

    @Override
    public User getInvokeUserKey(String accessKey) {
        if(StringUtils.isAnyBlank(accessKey)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"密钥错误");
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("accessKey",accessKey);
        return userMapper.selectOne(queryWrapper);
    }

    @Override
    public InterfaceInfo getInterfaceInfo(String path, String method) {
        if(StringUtils.isAnyBlank(path,method)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"密钥错误");
        }
        QueryWrapper<InterfaceInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("url",path);
        queryWrapper.eq("method",method);
        return interfaceInfoMapper.selectOne(queryWrapper);
    }

    @Override
    public UserInterfaceInfo getLeftNumber(long interfaceInfoId, long userId) {
        //校验
        if (interfaceInfoId <= 0 || userId <= 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"接口或者用戶不存在");
        }
        UpdateWrapper<UserInterfaceInfo> userInterfaceInfoWrapper = new UpdateWrapper<>();
        userInterfaceInfoWrapper.eq("interfaceInfoId",interfaceInfoId);
        userInterfaceInfoWrapper.eq("userId",userId);
        return userInterfaceInfoMapper.selectOne(userInterfaceInfoWrapper);
    }


    @Override
    public boolean invokeCount(long interfaceInfoId, long userId) {
        return userInterfaceInfoService.invokeCount(interfaceInfoId,userId);
    }

}
