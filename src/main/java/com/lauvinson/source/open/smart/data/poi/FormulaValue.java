/*
 *
 * projectName: face-detect
 * fileName: Formula.java
 * packageName: com.wangxiaobao.facedetect.utils
 * date: 2019-11-08 15:18 PM
 * copyright(c) 2009-2019 成都旺小宝科技有限公司
 * https://www.wangxiaobao.com/
 *
 */
package com.lauvinson.source.open.smart.data.poi;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 公式值
 * @author created by vinson on 2019/11/8
 */
@Target({ElementType.ANNOTATION_TYPE, ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface FormulaValue {
}
