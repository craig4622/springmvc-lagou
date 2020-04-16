package com.lagou.edu.service.impl;

import com.lagou.edu.antition.LagouService;
import com.lagou.edu.service.TestService;

/**
 * @ClassName TestServiceImpl
 * @Description TODO
 * @Author xsq
 * @Date 2020/4/15 10:52
 **/
@LagouService
public class TestServiceImpl implements TestService {
    @Override
    public void test(String username) {
        System.out.println(username + "测试成功");
    }
}
