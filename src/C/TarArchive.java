package C;

import java.io.*;

public class TarArchive {
    public static void main(String[] args) throws Exception {
        String sourceFolder = "I:\\软件开发\\测试\\1";
        String tarFilepath = "I:\\软件开发\\测试\\test1.tar";
        String desFilepath = "I:\\软件开发\\测试\\解压\\1";
//        String sourceFolder = "C:\\works\\学科\\软件开发综合实验\\Test\\1.txt";
//        String tarFilepath = "C:\\works\\学科\\软件开发综合实验\\Test\\2.tar";
//        String desFilepath = "C:\\works\\学科\\软件开发综合实验\\Test\\3";
        try {
            tar(sourceFolder, tarFilepath);
            untar(tarFilepath, desFilepath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * @param sourceFolder the file folder for all files that need to be packaged.
     * @param tarFilePath the .tar file.
     * @throws Exception
     */
    public static void tar(String sourceFolder, String tarFilePath) throws Exception {
        OutputStream out = new FileOutputStream(tarFilePath);
        BufferedOutputStream bos = new BufferedOutputStream(out);
        TarOutputStream tos = new TarOutputStream(bos);
        File file = new File(sourceFolder);
        String basePath = null;
        if (file.isDirectory()) {
            basePath = file.getPath();
        } else {
            basePath = file.getParent();
        }
        tarFile(file, basePath, tos);
        bos.close();
        out.close();
    }

    /**
     *
     * @param File the .tar file.
     * @param descDir unpack folder.
     * @throws IOException
     */
    public static void untar(String File, String descDir) throws IOException {
        File pathFile = new File(descDir);
        if (!pathFile.exists()) {
            pathFile.mkdirs();
        }
        File file = new File(File);
        String basePath = null;
        if (file.isDirectory()) {
            basePath = file.getPath();
        } else {
            basePath = file.getParent();
        }
        untarFile(file, basePath, descDir);

    }

    private static void tarFile(File parentFile, String basePath, TarOutputStream tos)
            throws Exception {
        File[] files = new File[0];
        if (parentFile.isDirectory()) {
            files = parentFile.listFiles();
        } else {
            files = new File[1];
            files[0] = parentFile;
        }
        String pathName;
        InputStream is;
        BufferedInputStream bis;
        byte[] cache = new byte[512];
        for (File file : files) {
            if (file.isDirectory()) {
                tarFile(file, basePath, tos);
            } else {
                is = new FileInputStream(file);
                bis = new BufferedInputStream(is);
                Header h = new Header();
                h.setName(file.getPath().substring(basePath.length()+1));
                h.setSize((int)file.length());
                tos.writeHeader(h);
                int nRead = 0;
                while ((nRead = bis.read(cache, 0, 512)) != -1) {
                    tos.write(cache, 0, nRead);
                }
                byte[] eof = new byte[Header.BLOCKSIZE * 2];
                tos.write(eof);
                bis.close();
                is.close();
            }
        }
    }

    public static void untarFile(File parentFile, String basePath, String descDir) {
        FileInputStream is;
        BufferedInputStream bis;
        byte[] cache = new byte[512];

        try {
            is = new FileInputStream(parentFile);
            bis = new BufferedInputStream(is);
            TarInputStream tis = new TarInputStream(bis);
            finish: while (true) {
                int numOfHeader = tis.read(cache);
                if (numOfHeader == -1) {
                    break finish;
                }
                byte[] name1 = new byte[100];
                byte[] size1 = new byte[12];
                System.arraycopy(cache, 0, name1, 0, 100);
                System.arraycopy(cache, 124, size1, 0, 12);
                String descDir1 = descDir + "\\" + new String(name1).trim();
                int size = Utils.octalToDec(size1);
                File descFile = new File(descDir1);
                File parentFile1 = new File(descFile.getParent());
                if(!parentFile1.exists()) {
                    try {
                        parentFile1.mkdirs();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (!descFile.exists()){
                    try {
                        descFile.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                FileOutputStream os = new FileOutputStream(descFile);
                BufferedOutputStream bos = new BufferedOutputStream(os);
                TarOutputStream tos = new TarOutputStream(bos);
                int nRead = 0;
                int sumRead = 0;
                while (true) {
                    cache = new byte[512];
                    if (sumRead + 512 >= size) {
                        int a = tis.read(cache, 0, size - sumRead);
                        tos.write(cache, 0, a);
                        tis.read(cache, 0, 512);
                        tis.read(cache, 0, 512);
                        tos.flush();
                        tos.close();
                        continue finish;
                    }
                    nRead = tis.read(cache, 0, 512);
                    sumRead += nRead;
                    if (nRead == -1) {
                        break finish;
                    }
                    tos.write(cache, 0, nRead);
                }
            }
            tis.close();
            bis.close();
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
