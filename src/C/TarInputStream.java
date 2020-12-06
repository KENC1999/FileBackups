package C;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class TarInputStream extends InputStream {

    public static final int BUFFER_SIZE = 1024 * 1024;

    private DataInputStream dis;
    private Header activeHeader;
    private int read;

    public TarInputStream(InputStream is) throws IOException {
        this.dis = new DataInputStream(new BufferedInputStream(is, BUFFER_SIZE));
        this.dis.mark(Integer.MAX_VALUE);
    }

    public List<Header> getHeaders() throws IOException {
        dis.reset();
        List<Header> headers = new ArrayList<Header>();

        int read = 0;
        boolean running = true;

        while (running) {
            Header h = new Header();
            h.readRecord(dis);
            read += Header.BLOCKSIZE;

            if (h.getName().length() == 0) {
                break;
            }

            h.setDataOffset(read);

            for (int i = 0; i < h.getSize(); i += Header.BLOCKSIZE) {
                long skipped = dis.skip(Header.BLOCKSIZE);

                if (skipped == -1) {
                    running = false;
                    break;
                } else {
                    read += Header.BLOCKSIZE;
                }
            }

            headers.add(h);
        }

        return headers;
    }

    public void openEntry(Header h) throws IOException {
        dis.reset();
        dis.skipBytes(h.getDataOffset());
        activeHeader = h;
    }

    public void closeEntry() throws IOException {
        dis.reset();
        activeHeader = null;
        read = 0;
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        int total = read + len;

//        while (total > activeHeader.getSize()) {
//            len--;
//            total = read + len;
//        }
//        len = activeHeader.getSize() - read;
        read += len;

        if (len <= 0)
            return -1;

        return dis.read(b, off, len);
    }

    @Override
    public int read(byte[] b) throws IOException {
        return read(b, 0, b.length);
    }

    @Override
    public int read() throws IOException {
        read++;
        return dis.read();
    }

    public final void readFully(byte b[], int off, int len) throws IOException {
        if (len < 0)
            throw new IndexOutOfBoundsException();
        int n = 0;
        while (n < len) {
            int count = read(b, off + n, len - n);
            if (count < 0)
                throw new EOFException();
            n += count;
        }
    }

    @Override
    public void close() throws IOException {
        dis.close();
    }
}