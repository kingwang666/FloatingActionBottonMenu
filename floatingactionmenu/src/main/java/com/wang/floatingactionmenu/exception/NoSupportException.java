package com.wang.floatingactionmenu.exception;

/**
 * Created on 2016/11/13.
 * Author: wang
 */

public class NoSupportException extends RuntimeException {

    public NoSupportException(){
        super("system overly dont support add text label");
    }
}
