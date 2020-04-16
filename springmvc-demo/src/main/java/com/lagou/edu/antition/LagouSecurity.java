package com.lagou.edu.antition;

import java.lang.annotation.*;

/**
 * @ClassName LagouSecurity
 * @Description TODO
 * @Author xsq
 * @Date 2020/4/14 10:55
 **/
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LagouSecurity {

    String[] value() default "";
}
