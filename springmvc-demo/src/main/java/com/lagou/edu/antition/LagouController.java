package com.lagou.edu.antition;

import java.lang.annotation.*;
/**
 * @ClassName LagouController
 * @Description TODO
 * @Author xsq
 * @Date 2020/4/14 10:55
 **/
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LagouController {

    String value() default "";
}
