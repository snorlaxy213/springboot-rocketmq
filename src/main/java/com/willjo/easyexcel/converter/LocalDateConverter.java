package com.willjo.easyexcel.converter;

import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.metadata.GlobalConfiguration;
import com.alibaba.excel.metadata.data.ReadCellData;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.alibaba.excel.metadata.property.ExcelContentProperty;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * 日期解析成LocalDateTime
 **/
public class LocalDateConverter implements Converter<LocalDate> {

    // 将Excel日期转换为Java日期
    public static LocalDate convertExcelDateToJavaDate(long excelDate) {
        // Excel中的日期起点为1900-01-01
        Date date = new Date(((excelDate - 1) * 24 * 60 * 60 * 1000));
        // Date转化为LocalDate
        return date.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
    }

    @Override
    public Class<LocalDate> supportJavaTypeKey() {
        return LocalDate.class;
    }

    @Override
    public CellDataTypeEnum supportExcelTypeKey() {
        return CellDataTypeEnum.STRING;
    }

    @Override
    public LocalDate convertToJavaData(ReadCellData<?> cellData, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) throws Exception {
        String stringValue = cellData.getStringValue();
        BigDecimal numberValue = cellData.getNumberValue();
        return convertExcelDateToJavaDate(stringValue != null ? Long.parseLong(stringValue) : numberValue.longValue());
    }

    @Override
    public WriteCellData<?> convertToExcelData(LocalDate value, ExcelContentProperty contentProperty,
                                               GlobalConfiguration globalConfiguration) {
        return new WriteCellData<String>(value.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
    }
}