package com.lagou.edu.servlet;

import com.lagou.edu.antition.LagouAutowired;
import com.lagou.edu.antition.LagouController;
import com.lagou.edu.antition.LagouRequestMapping;
import com.lagou.edu.antition.LagouService;
import com.lagou.edu.handler.Handler;
import com.lagou.edu.utils.StringUtils;
import net.sf.cglib.core.CollectionUtils;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * @ClassName LagouDispatcherServlet
 * @Description TODO
 * @Author xsq
 * @Date 2020/4/13 20:25
 **/
public class LagouDispatcherServlet extends HttpServlet {

    private List<String> classptah = new ArrayList<>();

    private Properties properties = new Properties();

    private List<Handler> handlerMapping = new ArrayList<>();
    // ioc容器
    private Map<String, Object> ioc = new HashMap<String, Object>();

    @Override
    public void init(ServletConfig config) throws ServletException {
        String configInitParameter = config.getInitParameter("config");
        //加载配置文件类
        doLoadConfiguration(configInitParameter);
        //扫描包下所有的类
        doScan((String) properties.get("basescan"));
        //创建对象加入ioc(LagouController,LagouService,LagouAutowired)
        doInstance();
        //依赖注入
        doAutowired();
        //创建handermapping把url和对应的逻辑类绑定
        initHandlerMapping();
        //处理业务逻辑
        System.out.println("lagou mvc 初始化完成....");
    }


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Handler handler = getHandler(req);
        if (handler == null) {
            resp.getWriter().write("404");
            return;
        }
        Parameter[] parameters = handler.getMethod().getParameters();
        Object[] paraValues = new Object[parameters.length];
        Map<String, String[]> parameterMap = req.getParameterMap();
        for (Map.Entry<String, String[]> map : parameterMap.entrySet()) {
            String value = org.apache.commons.lang3.StringUtils.join(map.getValue(), ",");
            if (!handler.getParmarIndexMapping().containsKey(map.getKey())) {
                continue;
            }
            Integer integer = handler.getParmarIndexMapping().get(map.getKey());
            paraValues[integer] = value;
        }
        int reqIndex = handler.getParmarIndexMapping().get(HttpServletRequest.class.getSimpleName());
        paraValues[reqIndex] = req;
        int respIndex = handler.getParmarIndexMapping().get(HttpServletResponse.class.getSimpleName());
        paraValues[respIndex] = resp;

        try {
            handler.getMethod().invoke(handler.getController(), paraValues);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取对应的handler
     *
     * @param req
     */
    private Handler getHandler(HttpServletRequest req) {
        if (handlerMapping == null || handlerMapping.isEmpty()) {
            return null;
        }
        String requestURI = req.getRequestURI();
        for (Handler handler : handlerMapping) {
            Matcher matcher = handler.getPattern().matcher(requestURI);
            if (!matcher.matches()) {
                continue;
            }
            return handler;
        }
        return null;
    }

    /**
     * 创建handermapping把url和对应的逻辑类绑定
     */
    private void initHandlerMapping() {
        if (ioc.isEmpty()) {
            return;
        }
        ioc.forEach((key, value) -> {
            if (!value.getClass().isAnnotationPresent(LagouController.class)) {
                return;
            }
            String baseUrl = "";
            if (value.getClass().isAnnotationPresent(LagouRequestMapping.class)) {
                baseUrl = value.getClass().getAnnotation(LagouRequestMapping.class).value();
            }
            Method[] methods = value.getClass().getMethods();
            for (int i = 0; i < methods.length; i++) {
                if (!methods[i].isAnnotationPresent(LagouRequestMapping.class)) {
                    continue;
                }
                String methodUrl = methods[i].getAnnotation(LagouRequestMapping.class).value();
                String url = baseUrl + methodUrl;
                Handler handler = new Handler(value, methods[i], Pattern.compile(url));
                Parameter[] parameters = methods[i].getParameters();
                for (int j = 0; j < parameters.length; j++) {
                    if (parameters[j].getType() == HttpServletRequest.class || parameters[j].getType() == HttpServletResponse.class) {
                        handler.getParmarIndexMapping().put(parameters[j].getType().getSimpleName(), j);
                    } else {
                        handler.getParmarIndexMapping().put(parameters[j].getName(), j);
                    }

                }
                handlerMapping.add(handler);
            }

        });

    }

    /**
     * 依赖注入
     */
    private void doAutowired() {
        if (ioc.isEmpty()) {
            return;
        }
        ioc.forEach((key, value) -> {
            Field[] declaredFields = value.getClass().getDeclaredFields();
            for (int i = 0; i < declaredFields.length; i++) {
                if (!declaredFields[i].isAnnotationPresent(LagouAutowired.class)) {
                    continue;
                } else {
                    LagouAutowired annotation = declaredFields[i].getAnnotation(LagouAutowired.class);
                    String name = annotation.value();
                    if (org.apache.commons.lang3.StringUtils.isBlank(name)) {
                        name = declaredFields[i].getType().getName();
                        Object o = ioc.get(name);
                        declaredFields[i].setAccessible(true);
                        try {
                            declaredFields[i].set(value, o);
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
    }

    /**
     * 创建实例化对象放入ioc容器
     */
    private void doInstance() {
        if (classptah.isEmpty()) {
            return;
        }
        classptah.forEach(path -> {
            try {
                Class<?> aClass = Class.forName(path);
                //如果这个类含有LagouController注解
                if (aClass.isAnnotationPresent(LagouController.class)) {
                    //获取类名
                    String simpleName = aClass.getSimpleName();
                    //首字母小写
                    simpleName = StringUtils.toLowerCaseFirstOne(simpleName);
                    Object o = aClass.newInstance();
                    ioc.put(simpleName, o);

                } else if (aClass.isAnnotationPresent(LagouService.class)) {
                    LagouService annotation = aClass.getAnnotation(LagouService.class);
                    String name = annotation.value();
                    if (org.apache.commons.lang3.StringUtils.isBlank(name)) {
                        //获取类名
                        String simpleName = aClass.getSimpleName();
                        //首字母小写
                        name = StringUtils.toLowerCaseFirstOne(simpleName);

                    }
                    Object o = aClass.newInstance();
                    ioc.put(name, o);
                    // service层往往是有接口的，面向接口开发，此时再以接口名为id，放入一份对象到ioc中，便于后期根据接口类型注入
                    Class<?>[] interfaces = aClass.getInterfaces();
                    for (int i = 0; i < interfaces.length; i++) {
                        // 以接口的全限定类名作为id放入
                        ioc.put(interfaces[i].getName(), o);
                    }
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            }
        });
    }


    /**
     * 扫描所有的类,把类的全路径放进list集合中
     *
     * @param basescan
     */
    private void doScan(String basescan) {
        String path = Thread.currentThread().getContextClassLoader().getResource("").getPath() + basescan.replaceAll("\\.", "/");
        File file = new File(path);
        File[] files = file.listFiles();
        for (int i = 0; i < files.length; i++) {
            if (files[i].isDirectory()) {
                //递归遍历
                doScan(basescan + "." + files[i].getName());
            } else if (files[i].getName().endsWith(".class")) {
                classptah.add(basescan + "." + files[i].getName().replaceAll(".class", ""));
            }
        }
    }


    /**
     * 加载配置文件类
     */
    private void doLoadConfiguration(String configInitParameter) {
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(configInitParameter);
        try {
            properties.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
