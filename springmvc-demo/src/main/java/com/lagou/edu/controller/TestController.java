package com.lagou.edu.controller;

import com.lagou.edu.antition.LagouAutowired;
import com.lagou.edu.antition.LagouController;
import com.lagou.edu.antition.LagouRequestMapping;
import com.lagou.edu.service.TestService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @ClassName TestController
 * @Description TODO
 * @Author xsq
 * @Date 2020/4/14 21:13
 **/
@LagouController
@LagouRequestMapping(value = "/testController")
public class TestController {

    @LagouAutowired
    private TestService testService;

    @LagouRequestMapping(value = "/test")
    public void test(HttpServletRequest request, HttpServletResponse response, String name) {
        System.out.println("我的名次" + name);
        testService.test();
    }
}
