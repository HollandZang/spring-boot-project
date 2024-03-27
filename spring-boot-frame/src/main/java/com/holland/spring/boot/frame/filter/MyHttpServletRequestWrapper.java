package com.holland.spring.boot.frame.filter;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.IOException;

public class MyHttpServletRequestWrapper extends HttpServletRequestWrapper {
    String bodyStr;
    MyServletInputStream inputStream;

    public MyHttpServletRequestWrapper(HttpServletRequest request, String bodyStr) {
        super(request);
        this.bodyStr = bodyStr;
        this.inputStream = new MyServletInputStream(bodyStr);
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        return inputStream;
    }
}
