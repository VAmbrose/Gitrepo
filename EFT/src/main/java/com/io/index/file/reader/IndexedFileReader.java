package com.io.index.file.reader;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


public final class IndexedFileReader
implements Closeable {
    private static final ForkJoinPool DEFAULT_POOL = new ForkJoinPool();
    private static final long MIN_FORK_THRESHOLD = 1000000;
    private static final String READ_MODE = "r";
    private final BufferedRandomAccessFile raf;
    private final Charset charset;
    private final SortedSet<Long> index;
    private final Lock lock;
    private boolean isClosed = false;

    public IndexedFileReader(File file) throws IOException {
        this(file, Charset.defaultCharset(), 1, DEFAULT_POOL);
    }

    public IndexedFileReader(File file, Charset charset) throws IOException {
        this(file, charset, 1, DEFAULT_POOL);
    }

    public IndexedFileReader(File file, Charset charset, int splitCount) throws IOException {
        this(file, charset, splitCount, DEFAULT_POOL);
    }

    public IndexedFileReader(File file, Charset charset, int splitCount, ForkJoinPool pool) throws IOException {
        this.raf = new BufferedRandomAccessFile(file, "r");
        this.charset = charset;
        long threshold = Math.max(1000000, file.length() / (long)splitCount);
        this.index = Collections.unmodifiableSortedSet((SortedSet)pool.invoke(new IndexingTask(file, 0, file.length(), threshold)));
        this.lock = new ReentrantLock();
    }

    public IndexedFileReader(File file, int splitCount) throws IOException {
        this(file, Charset.defaultCharset(), splitCount, DEFAULT_POOL);
    }

    public void close() throws IOException {
        this.raf.close();
        this.isClosed = true;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public SortedMap<Integer, String> find(int from, int to, String regex) throws IOException {
        this.assertNotClosed();
        if (regex == null) {
            throw new NullPointerException("Regex cannot be null!");
        }
        if (from < 1) {
            throw new IllegalArgumentException("Argument 'from' must be greater than or equal to 1!");
        }
        if (to < from) {
            throw new IllegalArgumentException("Argument 'to' must be greater than or equal to 'from'!");
        }
        TreeMap<Integer, String> lines = new TreeMap<Integer, String>();
        ArrayList<Long> positions = new ArrayList<Long>(this.index);
        try {
            String line;
            this.lock.lock();
            this.raf.seek(positions.get(from - 1));
            for (int i22 = from; i22 <= to && (line = this.raf.getNextLine(this.charset)) != null; ++i22) {
                if (!line.matches(regex)) continue;
                lines.put(i22, line);
            }
            TreeMap<Integer, String> i22 = lines;
            return i22;
        }
        finally {
            this.lock.unlock();
        }
    }

    public int getLineCount() {
        return this.index.size();
    }

    public SortedMap<Integer, String> head(int n2) throws IOException {
        this.assertNotClosed();
        if (n2 < 1) {
            throw new IllegalArgumentException("Argument 'n' must be greater than or equal to 1!");
        }
        return this.readLines(1, n2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public SortedMap<Integer, String> readLines(int from, int to) throws IOException {
        this.assertNotClosed();
        if (from < 1) {
            throw new IllegalArgumentException("Argument 'from' must be greater than or equal to 1!");
        }
        if (to < from) {
            throw new IllegalArgumentException("Argument 'to' must be greater than or equal to 'from'!");
        }
        if (from > this.index.size()) {
            throw new IllegalArgumentException("Argument 'from' must be less than the file's number of lines!");
        }
        TreeMap<Integer, String> lines = new TreeMap<Integer, String>();
        ArrayList<Long> positions = new ArrayList<Long>(this.index);
        try {
            String line;
            this.lock.lock();
            this.raf.seek(positions.get(from - 1));
            for (int i22 = from; i22 <= to && (line = this.raf.getNextLine(this.charset)) != null; ++i22) {
                lines.put(i22, line);
            }
            TreeMap<Integer, String> i22 = lines;
            return i22;
        }
        finally {
            this.lock.unlock();
        }
    }

    public SortedMap<Integer, String> tail(int n2) throws IOException {
        this.assertNotClosed();
        if (n2 < 1) {
            throw new IllegalArgumentException("Argument 'n' must be greater than or equal to 1!");
        }
        int from = this.index.size() - n2;
        int to = from + n2;
        return this.readLines(from, to);
    }

    private void assertNotClosed() {
        if (this.isClosed) {
            throw new IllegalStateException("Reader is closed!");
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static final class IndexingTask
    extends RecursiveTask<SortedSet<Long>> {
        private static final long serialVersionUID = 3509549890190032574L;
        private final File file;
        private final long start;
        private final long end;
        private final long length;
        private final long threshold;

        public IndexingTask(File file, long start, long end, long threshold) {
            this.file = file;
            this.start = start;
            this.end = end;
            this.length = end - start;
            this.threshold = threshold;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        protected SortedSet<Long> compute() {
            TreeSet<Long> index;
            block9 : {
                index = new TreeSet<Long>();
                try {
                    long end1;
                    if (this.length < this.threshold) {
                        RandomAccessFile raf = null;
                        try {
                            raf = new BufferedRandomAccessFile(this.file, "r");
                            raf.seek(this.start);
                            if (raf.getFilePointer() == 0) {
                                index.add(raf.getFilePointer());
                            }
                            while (raf.getFilePointer() < this.end) {
                                ((BufferedRandomAccessFile) raf).getNextLine();
                                index.add(raf.getFilePointer());
                            }
                            break block9;
                        }
                        finally {
                            if (raf != null) {
                                raf.close();
                            }
                        }
                    }
                    long start1 = this.start;
                    long start2 = end1 = this.start + this.length / 2;
                    long end2 = this.end;
                    IndexingTask task1 = new IndexingTask(this.file, start1, end1, this.threshold);
                    task1.fork();
                    IndexingTask task2 = new IndexingTask(this.file, start2, end2, this.threshold);
                    index.addAll((Collection<Long>)task2.compute());
                    index.addAll((Collection)task1.join());
                }
                catch (IOException ex) {
                    throw new FileIndexingException(this.file, ex);
                }
            }
            return index;
        }
    }

}

