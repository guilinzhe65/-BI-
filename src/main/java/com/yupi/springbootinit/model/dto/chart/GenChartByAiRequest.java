package com.yupi.springbootinit.model.dto.chart;

import lombok.Data;

import java.io.Serializable;

/**
 * @author dio哒
 * @version 1.0
 * @date 2024/6/29 22:11
 */
@Data
public class GenChartByAiRequest implements Serializable {
    /**
     * 名称
     */
    private String name;
    /**
     * 目标
     */
    private String goal;

    private String chartType;

    private static final long serialVersionUID = 1L;
}
