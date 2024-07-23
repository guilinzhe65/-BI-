package com.yupi.springbootinit.utils;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.support.ExcelTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 *
 * Excel转CSV
 * @author dio哒
 * @version 1.0
 * @date 2024/6/29 23:08
 */
@Slf4j
public class ExcelUtils {
    public static String excelToCsv(MultipartFile multipartFile){
//        File file = null;
//        try {
//            file = ResourceUtils.getFile("classpath:网站数据.xlsx");
//        } catch (FileNotFoundException e) {
//            throw new RuntimeException(e);
//        }
        //读取数据
        List<Map<Integer, String>> list = null;
        try {
            list = EasyExcel.read(multipartFile.getInputStream())
                    .excelType(ExcelTypeEnum.XLSX)
                    .sheet()
                    .headRowNumber(0)
                    .doReadSync();
        } catch (IOException e) {
            log.error("表格处理错误",e);
            e.printStackTrace();
        }
        if (CollUtil.isEmpty(list)){
            return "";
        }
        //转换为CSV
        StringBuilder stringBuilder = new StringBuilder();
        //读取表头
        LinkedHashMap<Integer, String> map = (LinkedHashMap<Integer, String>) list.get(0);
        //过滤null数据
        List<String> headList = map.values().stream().filter(ObjectUtils::isNotEmpty).collect(Collectors.toList());
        //拼接
        stringBuilder.append(StringUtils.join(headList,",")).append("\n");
        //读取表中数据
        for (int i = 1; i < list.size(); i++) {
            Map<Integer, String> map1 = list.get(i);
            List<String> dataList = map1.values().stream().filter(ObjectUtils::isNotEmpty).collect(Collectors.toList());
            stringBuilder.append(StringUtils.join(dataList,",")).append("\n");

        }
        return stringBuilder.toString();
    }
}
