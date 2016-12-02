package com.guaigou.cd.minutestohome.http;

public class ServerException extends RuntimeException {
    public int code;
    public String message;
}
