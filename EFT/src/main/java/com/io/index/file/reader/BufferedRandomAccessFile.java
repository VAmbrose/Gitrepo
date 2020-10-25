package com.io.index.file.reader;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.Charset;

public class BufferedRandomAccessFile
extends RandomAccessFile {
    private static final int DEFAULT_BUFFER_SIZE = 256;
    private final int bufferSize;
    private byte[] buffer;
    private int bufferEnd = 0;
    private int bufferPos = 0;
    private long realPos = 0;

    public BufferedRandomAccessFile(File file, String mode) throws IOException {
        this(file, mode, 256);
    }

    public BufferedRandomAccessFile(File file, String mode, int bufferSize) throws IOException {
        super(file, mode);
        this.invalidate();
        this.bufferSize = bufferSize;
        this.buffer = new byte[bufferSize];
    }

    public BufferedRandomAccessFile(String filename, String mode) throws IOException {
        this(filename, mode, 256);
    }

    public BufferedRandomAccessFile(String filename, String mode, int bufsize) throws IOException {
        super(filename, mode);
        this.invalidate();
        this.bufferSize = bufsize;
        this.buffer = new byte[bufsize];
    }

    public long getFilePointer() throws IOException {
        return this.realPos - (long)this.bufferEnd + (long)this.bufferPos;
    }

    public final String getNextLine() throws IOException {
        return this.getNextLine(Charset.defaultCharset());
    }

    public final String getNextLine(Charset charset) throws IOException {
        String str = null;
        if (this.bufferEnd - this.bufferPos <= 0 && this.fillBuffer() < 0) {
            return null;
        }
        int lineEnd = -1;
        for (int i2 = this.bufferPos; i2 < this.bufferEnd; ++i2) {
            if (this.buffer[i2] != 10) continue;
            lineEnd = i2;
            break;
        }
        if (lineEnd < 0) {
            int c2;
            StringBuilder sb = new StringBuilder(256);
            while ((c2 = this.read()) != -1 && c2 != 10) {
                if ((char)c2 == '\r') continue;
                sb.append((char)c2);
            }
            if (c2 == -1 && sb.length() == 0) {
                return null;
            }
            return sb.toString();
        }
        str = lineEnd > 0 && this.buffer[lineEnd - 1] == 13 ? new String(this.buffer, this.bufferPos, lineEnd - this.bufferPos - 1, charset) : new String(this.buffer, this.bufferPos, lineEnd - this.bufferPos, charset);
        this.bufferPos = lineEnd + 1;
        return str;
    }

    public final int read() throws IOException {
        if (this.bufferPos >= this.bufferEnd && this.fillBuffer() < 0) {
            return -1;
        }
        if (this.bufferEnd == 0) {
            return -1;
        }
        return this.buffer[this.bufferPos++];
    }

    public int read(byte[] b2, int off, int len) throws IOException {
        int leftover = this.bufferEnd - this.bufferPos;
        if (len <= leftover) {
            System.arraycopy(this.buffer, this.bufferPos, b2, off, len);
            this.bufferPos += len;
            return len;
        }
        for (int i2 = 0; i2 < len; ++i2) {
            int c2 = this.read();
            if (c2 == -1) {
                return i2;
            }
            b2[off + i2] = (byte)c2;
        }
        return len;
    }

    public void seek(long pos) throws IOException {
        int n2 = (int)(this.realPos - pos);
        if (n2 >= 0 && n2 <= this.bufferEnd) {
            this.bufferPos = this.bufferEnd - n2;
        } else {
            super.seek(pos);
            this.invalidate();
        }
    }

    private int fillBuffer() throws IOException {
        int n2 = super.read(this.buffer, 0, this.bufferSize);
        if (n2 >= 0) {
            this.realPos += (long)n2;
            this.bufferEnd = n2;
            this.bufferPos = 0;
        }
        return n2;
    }

    private void invalidate() throws IOException {
        this.bufferEnd = 0;
        this.bufferPos = 0;
        this.realPos = super.getFilePointer();
    }
}

