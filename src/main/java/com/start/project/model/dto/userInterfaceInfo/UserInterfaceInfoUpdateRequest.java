package com.start.project.model.dto.userInterfaceInfo;


import lombok.Data;
import java.io.Serializable;


/**
 * 更新请求
 *
 * @TableName product
 */
@Data
public class UserInterfaceInfoUpdateRequest implements Serializable {

    /**
     * id
     */
    private Long id;


    /**
     * 总调用次数
     */
    private Integer totalNumber;

    /**
     * 剩余调用次数
     */
    private Integer leftNumber;

    /**
     * 0-正常 1-禁用
     */
    private Integer status;


    private static final long serialVersionUID = 1L;
}