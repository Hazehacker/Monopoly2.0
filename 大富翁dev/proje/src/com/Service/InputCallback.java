package com.Service;

public interface InputCallback {
    /**
     * 阻塞等待，直到用户在 GUI 控制台里敲回车
     * 返回已 trim 的一行
     */
    String readLine();
}
