package com.hmall.item.interceptor;

import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AuthInterceptor implements HandlerInterceptor {

    private static final ThreadLocal<String> userIdThreadLocal = new ThreadLocal<>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 获取请求头中的 authorization 信息
        String authorization = request.getHeader("Authorization");

        // 对 authorization 进行解码或解析以获取 userId（此处假设已经解码为字符串形式的 userId）
        String userId = extractUserIdFromAuthorization(authorization);

        // 将 userId 存储到 ThreadLocal 中
        if (userId != null) {
            userIdThreadLocal.set(userId);
        }

        // 如果没有获取到有效的 userId，您可以选择在此处返回 false 并终止请求处理链
        // 这里为了简化示例，直接返回 true 让请求继续
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        // 在请求处理完成后，如果不需要在当前线程后续操作中继续使用 userId，可以清除 ThreadLocal 中的数据
        // userIdThreadLocal.remove();
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // 请求完全处理完后清理 ThreadLocal，确保线程安全
        userIdThreadLocal.remove();
    }

    public static String getUserId() {
        return userIdThreadLocal.get();
    }

    // 此方法根据实际情况编写，用于从 authorization 头部提取 userId
    private String extractUserIdFromAuthorization(String authorization) {
        // 假设这是一个简单的示例，直接返回 authorization 字符串作为 userId
        // 实际应用中可能需要解码 JWT 或其他认证信息
        return authorization;
    }
}
