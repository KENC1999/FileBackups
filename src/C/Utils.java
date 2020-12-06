package C;

import java.util.Arrays;

public class Utils {

    public static byte[] makeCstr(String s, int len) {
        byte[] b = s.getBytes();
        byte[] b1 = Arrays.copyOf(b, len);

        return b1;
    }

    public static int octalToDec(byte[] octal) {
        return Integer.parseInt(new String(octal).trim(), 8);
    }

    public static String decToOctal(int dec) {
        return Integer.toOctalString(dec);
    }

}