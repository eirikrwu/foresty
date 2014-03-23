package com.foresty.client.appender;

import com.google.common.base.Preconditions;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * Created by EveningSun on 14-3-23.
 */
public class ForestyServlet implements Servlet {
    private final Servlet servlet;

    public ForestyServlet(Servlet servlet) {
        Preconditions.checkNotNull(servlet);

        this.servlet = servlet;
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        String eventName;
        if (this.servlet.getServletConfig() == null) {
            eventName = this.servlet.getClass().getName();
        } else {
            eventName = this.servlet.getServletConfig().getServletName();
        }

        ForestyLog4j.beginEvent("Init servlet " + eventName);
        this.servlet.init(config);
        ForestyLog4j.exitEvent();
    }

    @Override
    public ServletConfig getServletConfig() {
        return this.servlet.getServletConfig();
    }

    @Override
    public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {
        String eventName;
        if (req instanceof HttpServletRequest) {
            eventName = getRelativeUrl((HttpServletRequest) req);
        } else {
            eventName = req.getServerName();
        }

        ForestyLog4j.beginEvent(eventName);
        this.servlet.service(req, res);
        ForestyLog4j.exitEvent();
    }

    @Override
    public String getServletInfo() {
        return this.servlet.getServletInfo();
    }

    @Override
    public void destroy() {
        String eventName;
        if (this.servlet.getServletConfig() == null) {
            eventName = this.servlet.getClass().getName();
        } else {
            eventName = this.servlet.getServletConfig().getServletName();
        }

        ForestyLog4j.beginEvent("Destroy servlet " + eventName);
        this.servlet.destroy();
        ForestyLog4j.exitEvent();
    }

    private static String getRelativeUrl(HttpServletRequest request) {
        String baseUrl;
        if (request.getServerPort() == 80 || request.getServerPort() == 443) {
            baseUrl = request.getScheme() + "://" + request.getServerName();
        } else {
            baseUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
        }

        StringBuffer buf = request.getRequestURL();
        if (request.getQueryString() != null) {
            buf.append("?");
            buf.append(request.getQueryString());
        }
        return buf.substring(baseUrl.length());
    }
}
