package com.start.project.model.dto.interfaceinfo;

import lombok.Data;

import java.io.Serializable;

/**
 * 接口调用请求
 *
 * @TableName product
 */
@Data
public class InterfaceInfoInvokeRequest implements Serializable {

    /**
     * id
     */
    private Long id;


    /**
     * 请求参数
     */
    private String requestParams;

    private static final long serialVersionUID = 1L;
}