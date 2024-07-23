package com.yupi.springbootinit.model.vo;

import lombok.Data;

/**
 * @author dio哒
 * @version 1.0
 * @date 2024/6/30 20:42
 */
@Data
public class BiResponse {
    //分析数据
    private String genResult;
    //图表代码
    private String genChart;
    //生成的图表id
    private Long chartId;
}
