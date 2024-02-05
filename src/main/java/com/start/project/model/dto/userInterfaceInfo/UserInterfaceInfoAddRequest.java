package com.start.project.model.dto.userInterfaceInfo;


import lombok.Data;
import java.io.Serializable;


/**
 * 创建请求
 *
 * @TableName product
 */
@Data
public class UserInterfaceInfoAddRequest implements Serializable {

    /**
     * 调用用户 id
     */
    private Long userId;

    /**
     * 接口 id
     */
    private Long interfaceInfoId;

    /**
     * 总调用次数
     */
    private Integer totalNumber;

    /**
     * 剩余调用次数
     */
    private Integer leftNumber;


    private static final long serialVersionUID = 1L;
}