package com.start.project.model.vo;

import com.start.apicommon.model.entity.InterfaceInfo;
import com.start.project.model.entity.Post;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 接口分析视图
 *
 * @author start
 * @TableName product
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class InterfaceAnalysisVO extends InterfaceInfo {

    /**
     * 接口总调用次数
     */
    private Integer totalNumber;

    private static final long serialVersionUID = 1L;
}