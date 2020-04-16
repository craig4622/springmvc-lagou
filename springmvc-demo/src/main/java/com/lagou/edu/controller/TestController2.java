package com.lagou.edu.controller;

import com.lagou.edu.antition.LagouAutowired;
import com.lagou.edu.antition.LagouController;
import com.lagou.edu.antition.LagouRequestMapping;
import com.lagou.edu.antition.LagouSecurity;
import com.lagou.edu.service.TestService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @ClassName TestController
 * @Description 手写MVC框架基础上增加如下功能
 * 1）定义注解@Security（有value属性，接收String数组），该注解用于添加在Controller类或者Handler方法上，
 * 表明哪些用户拥有访问该Handler方法的权限（注解配置用户名）
 * 2）访问Handler时，用户名直接以参数名username紧跟在请求的url后面即可，
 * 比如http://localhost:8080/demo/handle01?username=zhangsan
 * 3）程序要进行验证，有访问权限则放行，没有访问权限在页面上输出
 * 注意：自己造几个用户以及url，上交作业时，文档提供哪个用户有哪个url的访问权限
 * @Author xsq
 * @Date 2020/4/14 21:13
 **/
@LagouController
@LagouRequestMapping(value = "/testController2")
public class TestController2 {

    @LagouAutowired
    private TestService testService;

    /**
     * zhangsan有权限访问
     *
     * @param request
     * @param response
     * @param username
     */
    @LagouRequestMapping(value = "/test1")
    @LagouSecurity({"zhangsan"})
    public void test1(HttpServletRequest request, HttpServletResponse response, String username) {
        System.out.println("我的名字" + username);
        testService.test(username);
    }

    /**
     * lisi有权限访问
     *
     * @param request
     * @param response
     * @param username
     */
    @LagouRequestMapping(value = "/test2")
    @LagouSecurity({"lisi"})
    public void test2(HttpServletRequest request, HttpServletResponse response, String username) {
        System.out.println("我的名字" + username);
        testService.test(username);
    }


}
