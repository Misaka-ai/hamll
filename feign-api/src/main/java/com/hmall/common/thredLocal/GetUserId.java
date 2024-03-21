package com.hmall.common.thredLocal;

public class GetUserId {
    public static ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    public static void setId(Long id){
        threadLocal.set(id);
    }

    public static Long getId(){
        return threadLocal.get();
    }
}