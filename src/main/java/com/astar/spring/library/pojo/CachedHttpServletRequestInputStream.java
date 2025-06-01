package com.astar.spring.library.pojo;

import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import org.springframework.util.StreamUtils;

import java.io.*;

public class CachedHttpServletRequestInputStream extends HttpServletRequestWrapper {

    private byte[] cachedInputStream;

    public CachedHttpServletRequestInputStream(HttpServletRequest request) throws IOException {
        super(request);
        InputStream requestInputStream = request.getInputStream();
        this.cachedInputStream = StreamUtils.copyToByteArray(requestInputStream);
    }

    @Override
    public ServletInputStream getInputStream() {
        return new CachedServletInputStream(this.cachedInputStream);
    }

    @Override
    public BufferedReader getReader() throws IOException {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(
                this.cachedInputStream);
        return new BufferedReader(new InputStreamReader(byteArrayInputStream));
    }
}
