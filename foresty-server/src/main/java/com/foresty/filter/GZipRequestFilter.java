package com.foresty.filter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.zip.GZIPInputStream;

/**
 * Created by EveningSun on 14-3-29.
 */
public class GZipRequestFilter implements Filter {
    public void init(FilterConfig arg0) throws ServletException {
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        if ((request instanceof HttpServletRequest)) {
            HttpServletRequest req = (HttpServletRequest) request;
            String contentEncoding = req.getHeader("Content-Encoding");
            if ((contentEncoding != null) && (contentEncoding.toLowerCase().indexOf("gzip") > -1)) {
                request = new GZipRequestWrapper((HttpServletRequest) request);
            }
        }

        chain.doFilter(request, response);
    }

    public void destroy() {
    }

    public static class GZipRequestStream extends ServletInputStream {
        private HttpServletRequest request = null;
        private ServletInputStream inStream = null;
        private GZIPInputStream in = null;

        public GZipRequestStream(HttpServletRequest request) throws IOException {
            this.request = request;
            this.inStream = request.getInputStream();
            this.in = new GZIPInputStream(this.inStream);
        }

        public int read() throws IOException {
            return this.in.read();
        }

        public int read(byte[] b) throws IOException {
            return this.in.read(b);
        }

        public int read(byte[] b, int off, int len) throws IOException {
            return this.in.read(b, off, len);
        }

        public void close() throws IOException {
            this.in.close();
        }

        @Override
        public boolean isFinished() {
            return this.inStream.isFinished();
        }

        @Override
        public boolean isReady() {
            return this.inStream.isReady();
        }

        @Override
        public void setReadListener(ReadListener readListener) {
            throw new UnsupportedOperationException();
        }
    }

    public class GZipRequestWrapper extends HttpServletRequestWrapper {
        private HttpServletRequest origRequest = null;
        private ServletInputStream inStream = null;
        private BufferedReader reader = null;

        public GZipRequestWrapper(HttpServletRequest req) throws IOException {
            super(req);

            this.inStream = new GZipRequestStream(req);
            this.reader = new BufferedReader(new InputStreamReader(this.inStream));
        }

        public ServletInputStream getInputStream() throws IOException {
            return this.inStream;
        }

        public BufferedReader getReader() throws IOException {
            return this.reader;
        }
    }
}
