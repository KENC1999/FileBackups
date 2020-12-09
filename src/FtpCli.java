import com.sun.deploy.security.SelectableSecurityManager;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.io.IOUtils;
import org.apache.commons.net.ftp.FTPFile;
import sun.net.ftp.FtpClient;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class FtpCli {

    public static ArrayList<ArrayList<String>> showList(FTPClient ftpClient)throws IOException{
        ftpClient.setControlEncoding("GBK");
        FTPFile[] list = ftpClient.listFiles();
        ArrayList<ArrayList<String>> filelist= new ArrayList<ArrayList<String>>();
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        DecimalFormat    df   = new DecimalFormat("######0.00");
        for(FTPFile ftpfile:list){
            //System.out.println(file.getName());
            ArrayList<String> file=new ArrayList<String>();
            file.add(ftpfile.getName());
            long size=ftpfile.getSize();
            if(size/1048576!=0){
                file.add(df.format(size/1048576.)+" MB");
            }else if(size/1024!=0){
                file.add(df.format(size/1024.)+" KB");
            }else{
                file.add(size+"B");
            }
//            Calendar calendar = ftpfile.getTimestamp();
//            if(calendar==null){
//                file.add(ftpfile.getName());file.add("未知");
//            }else{
//                file.add(ftpfile.getName());file.add(sf.format(calendar.getTime()));
//            }
            filelist.add(file);
        }
        return filelist;
    }

    public static void deleteFile(FTPClient ftpClient,String fileName)throws IOException{
        ftpClient.deleteFile(new String(fileName.getBytes("GBK"), "ISO-8859-1"));
    }

    public static void uploadFile(FTPClient ftpClient,String fileName)throws IOException{
        //System.out.println(fileName);
        File srcFile = new File(fileName);
        FileInputStream fis = null;
        System.out.println(srcFile.getAbsolutePath());
        try {
            System.out.println(ftpClient.getPassiveLocalIPAddress());
            fis = new FileInputStream(srcFile);
            //ftpClient.setBufferSize(1024);
            ftpClient.setControlEncoding("GBK");
            //设置文件类型（二进制）
            ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
            String[] strName=fileName.split("\\\\");//文件名是path最后一段
            //System.out.println(strName[strName.length-1]);
            boolean flag=ftpClient.storeFile(new String(strName[strName.length-1].getBytes("GBK"),
                    "ISO-8859-1"), fis);
            System.out.println(flag);
            JOptionPane.showMessageDialog(null,"网盘备份成功！","提示",JOptionPane.PLAIN_MESSAGE);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static void downFile(FTPClient ftpClient,String remoteFileName,String recPath)throws IOException {
        FileOutputStream fos = new FileOutputStream(recPath+"\\"+remoteFileName);
        //ftpClient.setBufferSize(1024);
        //设置文件类型（二进制）
        ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
        boolean flag=ftpClient.retrieveFile(new String(remoteFileName.getBytes("GBK"),
                "ISO-8859-1"), fos);
        IOUtils.closeQuietly(fos);
        System.out.println(flag);
    }

    public static void showtest(FTPClient ftpClient)throws IOException{
        ftpClient.setControlEncoding("GBK");
        FTPFile[] list = ftpClient.listFiles();
        for(FTPFile ftpfile:list){
            System.out.println(ftpfile.getName());
        }
    }

}
