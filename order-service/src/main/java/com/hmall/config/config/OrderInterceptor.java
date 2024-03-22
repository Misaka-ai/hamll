package com.hmall.config.config;


import com.hmall.myThread.MyThread;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class OrderInterceptor
        implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        try {
            String authorization = request.getHeader("authorization");
            MyThread.setUserId(Integer.parseInt(authorization));
        } catch (NumberFormatException e) {

        }
        return true;
    }
}
