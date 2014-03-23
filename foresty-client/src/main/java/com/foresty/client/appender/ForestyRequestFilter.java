package com.foresty.client.appender;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * Created by EveningSun on 14-3-23.
 */
public class ForestyRequestFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        if (isEnableForesty(request, response)) {
            String eventName = forestyEventName(request, response);
            ForestyLog4j.beginEvent(eventName);
            chain.doFilter(request, response);
            ForestyLog4j.exitEvent();
        } else {
            chain.doFilter(request, response);
        }
    }

    @Override
    public void destroy() {
    }

    protected boolean isEnableForesty(ServletRequest request, ServletResponse response) {
        return request instanceof HttpServletRequest;
    }

    protected String forestyEventName(ServletRequest request, ServletResponse response) {
        return ((HttpServletRequest) request).getRequestURL().toString();
    }
}
