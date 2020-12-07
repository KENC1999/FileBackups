import com.sun.deploy.security.SelectableSecurityManager;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.io.IOUtils;
import org.apache.commons.net.ftp.FTPFile;

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
}
