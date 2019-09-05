package com.lauvinson.source.open.smart.data.poi;

import java.lang.annotation.*;

/**
 * 列名
 * @author created by vinson on 2019/9/5
 */
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ColumnName {
    String value();
}
