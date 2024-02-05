package com.start.project.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.start.apicommon.model.entity.InterfaceInfo;
import com.start.apicommon.model.entity.UserInterfaceInfo;
import com.start.project.annotation.AuthCheck;
import com.start.project.common.BaseResponse;
import com.start.project.common.ErrorCode;
import com.start.project.common.ResultUtils;
import com.start.project.exception.BusinessException;
import com.start.project.mapper.UserInterfaceInfoMapper;
import com.start.project.model.vo.InterfaceAnalysisVO;
import com.start.project.service.InterfaceInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.start.project.constant.UserConstant.ADMIN_ROLE;

/**
 * 分析控制器
 *
 * @author start
 */
@RestController
@RequestMapping("/analysis")
@Slf4j
public class AnalysisController {

    @Resource
    private UserInterfaceInfoMapper userInterfaceInfoMapper;
    @Resource
    private InterfaceInfoService interfaceInfoService;

    @GetMapping("/top/interface/invoke")
    @AuthCheck(mustRole = ADMIN_ROLE)
    public BaseResponse<List<InterfaceAnalysisVO>> listTopInvokeInterfaceInfo(){
        List<UserInterfaceInfo> userInterfaceInfoList = userInterfaceInfoMapper.listTopInvokeInterfaceInfo(3);
        Map<Long, List<UserInterfaceInfo>> interfaceInfoIdObjMap = userInterfaceInfoList
                .stream()
                .collect(Collectors.groupingBy(UserInterfaceInfo::getInterfaceInfoId));
        QueryWrapper<InterfaceInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("id",interfaceInfoIdObjMap.keySet());
        List<InterfaceInfo> interfaceInfoList = interfaceInfoService.list(queryWrapper);
        if(CollectionUtils.isEmpty(interfaceInfoList)){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
        List<InterfaceAnalysisVO> interfaceAnalysisVOList = interfaceInfoList.stream().map(interfaceInfo -> {
            InterfaceAnalysisVO interfaceAnalysisVO = new InterfaceAnalysisVO();
            BeanUtils.copyProperties(interfaceInfo, interfaceAnalysisVO);
            Integer totalNumber = interfaceInfoIdObjMap.get(interfaceInfo.getId()).get(0).getTotalNumber();
            interfaceAnalysisVO.setTotalNumber(totalNumber);
            return interfaceAnalysisVO;
        }).collect(Collectors.toList());
        return ResultUtils.success(interfaceAnalysisVOList);
    }
}
