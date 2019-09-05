package com.lauvinson.source.open.smart.data.poi;

import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.xssf.usermodel.*;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.compile;


/**
 * 导出
 * @author created by vinson on 2019/9/5
 */
public class Export<T> {

    /**
     * <p>
     * 通用Excel导出方法,利用反射机制遍历对象的所有字段，将数据写入Excel文件中 <br>
     * 此版本生成2007以上版本的文件 (文件后缀：xlsx)
     * </p>
     *
     * @param title   表格标题名
     * @param dataset 需要显示的数据集合,集合中一定要放置符合JavaBean风格的类的对象。
     *                此方法支持的JavaBean属性的数据类型有基本数据类型及String,Date
     * @param out     与输出设备关联的流对象，可以将EXCEL文档导出到本地文件或者网络中
     * @param pattern 如果有时间数据，设定输出格式。默认为"yyyy-MM-dd hh:mm:ss"
     * @author created by vinson on 2019/9/5
     */
    @SuppressWarnings({"unchecked", "rawtypes", "AlibabaMethodTooLong"})
    public void exportExcel2007(String title, Collection<T> dataset, OutputStream out, String pattern) {
        //数据格式声明同一类型对象，抽其一个生成取值模板
        Optional<T> flagOptional = dataset.stream().findAny();
        if (!flagOptional.isPresent()) {
            return;
        }
        T flag = flagOptional.get();
        //获取列名与取值模板
        Field[] fieldsUnsort = flag.getClass().getDeclaredFields();
        Map<Integer, Field> fields = new TreeMap<>();
        for (Field f : fieldsUnsort) {
            if (f.isAnnotationPresent(ColumnPosition.class)) {
                ColumnPosition position = f.getAnnotation(ColumnPosition.class);
                fields.put(position.value(), f);
            }
        }
        List<Map.Entry<Integer, Field>> sortedColumn = new ArrayList<>(fields.entrySet());
        sortedColumn.sort(Comparator.comparing(Map.Entry::getKey));
        Field field;
        String fieldName;
        String[] headers = new String[sortedColumn.size()];
        String[] methods = new String[sortedColumn.size()];
        for (int i = 0; i < sortedColumn.size(); i++) {
            field = sortedColumn.get(i).getValue();
            String name;
            if (field.isAnnotationPresent(ColumnName.class)) {
                name = field.getAnnotation(ColumnName.class).value();
            } else {
                name = field.getName();
            }
            headers[i] = name;
            fieldName = field.getName();
            methods[i] = "get" + fieldName.substring(0, 1).toUpperCase()
                    + fieldName.substring(1);
        }
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet(title);
        sheet.setDefaultColumnWidth(20);
        XSSFCellStyle style = workbook.createCellStyle();
        style.setFillForegroundColor(XSSFColor.toXSSFColor(HSSFColor.HSSFColorPredefined.GREY_50_PERCENT.getColor()));
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setAlignment(HorizontalAlignment.CENTER);
        // 生成一个字体
        XSSFFont font = workbook.createFont();
        font.setBold(true);
        font.setFontName("宋体");
        font.setColor(XSSFColor.toXSSFColor(HSSFColor.HSSFColorPredefined.BLACK.getColor()));
        font.setFontHeightInPoints((short) 11);
        // 把字体应用到当前的样式
        style.setFont(font);
        // 生成并设置另一个样式
        XSSFCellStyle style2 = workbook.createCellStyle();
        style2.setFillForegroundColor(XSSFColor.toXSSFColor(HSSFColor.HSSFColorPredefined.WHITE.getColor()));
        style2.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style2.setBorderBottom(BorderStyle.THIN);
        style2.setBorderLeft(BorderStyle.THIN);
        style2.setBorderRight(BorderStyle.THIN);
        style2.setBorderTop(BorderStyle.THIN);
        style2.setAlignment(HorizontalAlignment.CENTER);
        style2.setVerticalAlignment(VerticalAlignment.CENTER);
        XSSFFont font2 = workbook.createFont();
        font2.setBold(true);
        style2.setFont(font2);
        XSSFRow row = sheet.createRow(0);
        XSSFCell cellHeader;
        for (int i = 0; i < headers.length; i++) {
            cellHeader = row.createCell(i);
            cellHeader.setCellStyle(style);
            cellHeader.setCellValue(new XSSFRichTextString(headers[i]));
        }
        Iterator<T> it = dataset.iterator();
        int index = 0;
        T t;
        XSSFRichTextString richString;
        Pattern p = compile("^//d+(//.//d+)?$");
        Matcher matcher;
        XSSFCell cell;
        Class tCls;
        Method getMethod;
        Object value;
        String textValue;
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        while (it.hasNext()) {
            index++;
            row = sheet.createRow(index);
            t = it.next();
            for (int i = 0; i < sortedColumn.size(); i++) {
                cell = row.createCell(i);
                cell.setCellStyle(style2);
                try {
                    tCls = t.getClass();
                    getMethod = tCls.getMethod(methods[i]);
                    value = getMethod.invoke(t);
                    textValue = null;
                    if (value instanceof Integer) {
                        cell.setCellValue((Integer) value);
                    } else if (value instanceof Float) {
                        textValue = String.valueOf(value);
                        cell.setCellValue(textValue);
                    } else if (value instanceof Double) {
                        textValue = String.valueOf(value);
                        cell.setCellValue(textValue);
                    } else if (value instanceof Long) {
                        cell.setCellValue((Long) value);
                    }
                    if (value instanceof Boolean) {
                        textValue = "是";
                        if (!(Boolean) value) {
                            textValue = "否";
                        }
                    } else if (value instanceof Date) {
                        textValue = sdf.format((Date) value);
                    } else if (value instanceof Enum && value.getClass().isAnnotationPresent(EnumDesc.class)) {
                        Object o = null;
                        try {
                            o = value.getClass().getMethod(value.getClass().getAnnotation(EnumDesc.class).descMethod()).invoke(value);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        textValue = Optional.ofNullable(o).map(Object::toString).orElse("");
                    } else {
                        if (value != null) {
                            textValue = value.toString();
                        }
                    }
                    if (textValue != null) {
                        matcher = p.matcher(textValue);
                        if (matcher.matches()) {
                            cell.setCellValue(Double.parseDouble(textValue));
                        } else {
                            richString = new XSSFRichTextString(textValue);
                            cell.setCellValue(richString);
                        }
                    }
                } catch (SecurityException | NoSuchMethodException | IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
        try {
            workbook.write(out);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
