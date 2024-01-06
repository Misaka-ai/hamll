package com.hmall.order.interceptor;

import com.hmall.context.BaseContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author liudo
 * @version 1.0
 * @project hmall
 * @description 认证拦截器
 * @date 2023/8/25 19:09:28
 */
@Component
@Slf4j
public class AuthorizationInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String userId = request.getHeader("authorization");
        if (StringUtils.isEmpty(userId)) {
            log.error("-----------登录失败-----------");
            return false;
        }
        BaseContext.setCurrentUserId(Long.parseLong(userId));
        return true;
    }
}
