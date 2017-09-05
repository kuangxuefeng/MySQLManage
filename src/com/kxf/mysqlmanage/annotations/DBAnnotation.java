package com.kxf.mysqlmanage.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented //文档  
@Retention(RetentionPolicy.RUNTIME) //在运行时可以获取  
@Target(ElementType.FIELD)
public @interface DBAnnotation {
	boolean canNull() default true;
	boolean isKey() default false;
}
