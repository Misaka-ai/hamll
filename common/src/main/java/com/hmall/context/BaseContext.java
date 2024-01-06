package com.hmall.context;

/**
 * @author liudo
 * @version 1.0
 * @project hmall
 * @date 2023/8/25 19:36:41
 */
public class BaseContext {
    private static final ThreadLocal<Long> LOCAL = new ThreadLocal<>();

    public static Long getCurrentUserId() {
        return LOCAL.get();
    }

    public static void setCurrentUserId(Long userId) {
        LOCAL.set(userId);
    }

    public static void remove() {
        LOCAL.remove();
    }
}
