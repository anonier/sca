package com.boredou.common.annotation;

import java.lang.annotation.*;


@Target({ElementType.METHOD,                //方法声明
        ElementType.TYPE,            //类、接口
        ElementType.FIELD,           //字段声明
        ElementType.CONSTRUCTOR,     //构造方法声明
        ElementType.LOCAL_VARIABLE,  //局部变量
        ElementType.PACKAGE,         //包声明
        ElementType.PARAMETER})      //参数声明
@Retention(RetentionPolicy.RUNTIME)    //生命周期，有SOURCE（源码）、CLASS（编译）、RUNTIME（运行时）
@Inherited                          //允许子类继承
@Documented
public @interface SysLog {
}
