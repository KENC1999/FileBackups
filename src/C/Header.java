package C;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.Arrays;

public class Header {

    public static final int BLOCKSIZE = 512;

    private byte[] name = new byte[100];
    private byte[] mode = new byte[8];
    private byte[] uid = new byte[8];
    private byte[] gid = new byte[8];
    private byte[] size = new byte[12];
    private byte[] mtime = new byte[12];
    private byte[] chksum = new byte[8];
    private byte[] typeflag = new byte[1];
    private byte[] linkname = new byte[100];

    private byte[][] order = new byte[][]{
            name, mode,uid, gid, size, mtime, chksum, typeflag, linkname
    };

    private int offset;

    public Header() {}

    public Header(byte[] bytes) {
        System.arraycopy(this.name, 0, bytes, 0, 100);
        System.arraycopy(this.mode, 0, bytes, 100, 8);
        System.arraycopy(this.uid, 0, bytes, 108, 8);
        System.arraycopy(this.gid, 0, bytes, 116, 8);
        System.arraycopy(this.size, 0, bytes, 124, 12);
        System.arraycopy(this.mtime, 0, bytes, 136, 12);
        System.arraycopy(this.chksum, 0, bytes, 148, 8);
        System.arraycopy(this.typeflag, 0, bytes, 156, 1);
        System.arraycopy(this.linkname, 0, bytes, 157, 100);

    }
    public void readRecord(DataInputStream dis) throws IOException {
        int read = 0;
        int id = 0;

        for (byte[] arr : order) {
            read += arr.length;
            dis.readFully(arr);
            order[id++] = arr;
        }
        name = order[0];
        mode = order[1];
        uid = order[2];
        gid = order[3];
        size = order[4];
        mtime = order[5];
        chksum = order[6];
        typeflag = order[7];
        linkname = order[8];

        int toSkip = Header.BLOCKSIZE - read;
        int skipped = 0;

        while ((skipped += dis.skipBytes(toSkip)) < toSkip);
    }

    public byte[] getHeader() {
        byte[] header = new byte[257];
        System.arraycopy(name, 0, header, 0, 100);
        System.arraycopy(mode, 0, header, 100, 8);
        System.arraycopy(uid, 0, header, 108, 8);
        System.arraycopy(gid, 0, header, 116, 8);
        System.arraycopy(size, 0, header, 124, 12);
        System.arraycopy(mtime, 0, header, 136, 12);
        System.arraycopy(chksum, 0, header, 148, 8);
        System.arraycopy(typeflag, 0, header, 156, 1);
        System.arraycopy(linkname, 0, header, 157, 100);

        return header;
    }

    public byte[][] getFields() {
        return order;
    }

    public String getName() {
        return new String(name).trim();
    }

    public void setName(String name) {
        this.name = Utils.makeCstr(name, 100);
    }

    public byte[] getMode() {
        return mode;
    }

    public void setMode(byte[] mode) {
        this.mode = mode;
    }

    public String getUid() {
        return new String(uid).trim();
    }

    public void setUid(byte[] uid) {
        this.uid = uid;
    }

    public String getGid() {
        return new String(gid).trim();
    }

    public void setGid(byte[] gid) {
        this.gid = gid;
    }

    public int getSize() {
        return Utils.octalToDec(size);
    }

    public void setSize(int size) {
        byte[] a = Utils.decToOctal(size).getBytes();
        this.size = Arrays.copyOf(a, 12);
    }

    public int getMtime() {
        return Utils.octalToDec(mtime);
    }

    public void setMtime(int mtime) {
        this.mtime = Utils.decToOctal(mtime).getBytes();
    }

    public byte[] getChksum() {
        return chksum;
    }

    public void setChksum(byte[] chksum) {
        this.chksum = chksum;
    }

    public byte[] getTypeflag() {
        return typeflag;
    }

    public void setTypeflag(byte[] typeflag) {
        this.typeflag = typeflag;
    }

    public String getLinkname() {
        return new String(linkname).trim();
    }

    public void setLinkname(String linkname) {
        this.linkname = Utils.makeCstr(linkname, 100);
    }

    public int getDataOffset() {
        return offset;
    }

    public void setDataOffset(int dataOffset) {
        this.offset = dataOffset;
    }

    @Override
    public String toString() {
        return "Name " + getName() + "\nMode " + getMode() + "\nOwner UID " + getUid() + "\nGroup UID " + getGid() + "\nSize " + getSize() + "\nLast Modified " + getMtime() + "\nChecksum " + getChksum() + "\nLink Indicator " + getTypeflag() + "\nLinked Name " + getLinkname();
    }
}