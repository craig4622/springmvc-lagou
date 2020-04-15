package com.lagou.edu.handler;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * @ClassName Handler
 * @Description TODO
 * @Author xsq
 * @Date 2020/4/14 20:15
 **/
public class Handler {

    private Object controller;

    private Method method;

    private Pattern pattern;

    private Map<String, Integer> parmarIndexMapping = new HashMap<>();


    public Handler(Object controller, Method method, Pattern pattern) {
        this.controller = controller;
        this.method = method;
        this.pattern = pattern;
    }

    public Object getController() {
        return controller;
    }

    public void setController(Object controller) {
        this.controller = controller;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public Pattern getPattern() {
        return pattern;
    }

    public void setPattern(Pattern pattern) {
        this.pattern = pattern;
    }

    public Map<String, Integer> getParmarIndexMapping() {
        return parmarIndexMapping;
    }

    public void setParmarIndexMapping(Map<String, Integer> parmarIndexMapping) {
        this.parmarIndexMapping = parmarIndexMapping;
    }

    @Override
    public String toString() {
        return "Handler{" +
                "controller=" + controller +
                ", method=" + method +
                ", pattern=" + pattern +
                ", parmarIndexMapping=" + parmarIndexMapping +
                '}';
    }
}
