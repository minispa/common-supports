package common.supports.web;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class StreamRequestWrapper extends HttpServletRequestWrapper {

    private ServletInputStream input;
    private ByteArrayOutputStream buffer;
    private BufferedReader reader;

    public StreamRequestWrapper(final HttpServletRequest request) {
        super(request);
        this.buffer = new ByteArrayOutputStream(request.getContentLength());
    }

    public byte[] getPayload() {
        return this.buffer.toByteArray();
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        return super.getInputStream();
    }

    @Override
    public BufferedReader getReader() throws IOException {
        return super.getReader();
    }

    private class BufferedServletInputStream extends ServletInputStream {

        private ServletInputStream stream;

        public BufferedServletInputStream(ServletInputStream servletInputStream) {
            this.stream = servletInputStream;
        }

        @Override
        public boolean isFinished() {
            return this.stream.isFinished();
        }

        @Override
        public boolean isReady() {
            return this.stream.isReady();
        }

        @Override
        public void setReadListener(final ReadListener readListener) {
            this.stream.setReadListener(readListener);
        }

        @Override
        public int read() throws IOException {
            int b = this.stream.read();
            if(b != -1) {
                StreamRequestWrapper.this.buffer.write(b);
            }
            return b;
        }

        @Override
        public int readLine(final byte[] b, final int off, final int len) throws IOException {
            int count = this.stream.readLine(b, off, len);
            this.writeToCache(b, off, count);
            return count;
        }

        @Override
        public int read(final byte[] b) throws IOException {
            int count = this.stream.read(b);
            this.writeToCache(b, 0, count);
            return count;
        }

        @Override
        public int read(final byte[] b, final int off, final int len) throws IOException {
            int count = this.stream.read(b, off, len);
            this.writeToCache(b, off, count);
            return count;
        }


        private void writeToCache(byte[] b, int off, int len) {
            if(len > 0) {
                StreamRequestWrapper.this.buffer.write(b, off, len);
            }
        }
    }

}
