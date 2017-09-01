package com.kxf.mysqlmanage.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** 
 * 定义一个用户名的自定义注解 
 */  
@Documented //文档  
@Retention(RetentionPolicy.RUNTIME) //在运行时可以获取  
@Target(ElementType.TYPE) //作用到类，接口上等  
@Inherited //子类会继承 
public @interface Table {
	String name();
}
