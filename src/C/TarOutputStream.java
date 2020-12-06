package C;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class TarOutputStream extends OutputStream {

    public static final byte[] MAGIC_NUMBER = new byte[] {
            'u', 's', 't', 'a', 'r', '\040', '\040', '\0'
    };

    private DataOutputStream dos;

    private boolean mn = false;
    private int written = 0;

    public TarOutputStream(OutputStream os) {
        this.dos = new DataOutputStream(os);
    }

    public void writeHeader(Header h) throws IOException {
        int written = 0;


        byte[] header = h.getHeader();
        dos.write(header);
        written += header.length;

        if (!mn) {
            mn = true;

            dos.write(MAGIC_NUMBER);

            written += MAGIC_NUMBER.length;
        }

        dos.write(new byte[Header.BLOCKSIZE - written]);
    }

    public void closeHeader() throws IOException {
        int ok = 0;

        while ((ok += Header.BLOCKSIZE) < written);

        dos.write(new byte[ok - written]);

        written = 0;
    }

    @Override
    public void write(int b) throws IOException {
        written++;
        dos.write(b);
    }

    public void write(byte b[]) throws IOException {
        write(b, 0, b.length);
    }

    public void write(byte b[], int off, int len) throws IOException {
        written += len;
        dos.write(b, off, len);
    }

    @Override
    public void close() throws IOException {
//        byte[] eof = new byte[Header.BLOCKSIZE * 2];
//        dos.write(eof);
        dos.close();
    }
}