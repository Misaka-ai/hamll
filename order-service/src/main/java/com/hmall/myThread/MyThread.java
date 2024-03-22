package com.hmall.myThread;


public class MyThread {

    private static final ThreadLocal<Integer> localThread = new ThreadLocal<>();

    public static Integer getUserId() {
        return localThread.get();
    }

    public static void setUserId(Integer userId){
        localThread.set(userId);
    }

}
