import javax.swing.*;
import javax.swing.plaf.basic.BasicTabbedPaneUI;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.*;
import java.io.*;
import java.math.BigInteger;
import java.net.ConnectException;
import java.net.SocketException;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.io.FileDeleteStrategy;
import org.apache.commons.io.IOUtils;
import org.apache.commons.net.ftp.FTPClient;
import java.io.File;
import java.io.IOException;
import java.util.Map;

import C.*;

public class UI4 extends JPanel {

    public static void main(String[] args) {
        new UI4().init();
    }

    Font font1=new Font("幼圆",Font.BOLD,26);
    Font font2=new Font("黑体",Font.BOLD,22);
    Font font3=new Font("宋体",Font.BOLD,22);
    Font font4=new Font("幼圆",Font.BOLD,18);


    JFrame frame=new JFrame("备份软件");
    static final int frame_width=1200;
    static final int frame_height=900;

    JTabbedPane tabbedPane=new JTabbedPane();
    JPanel backup_Panel=new JPanel();

    JComponent test_panel1=addTextPanel("立即备份");
    JComponent test_panel2=addTextPanel("自动备份");
    JComponent test_panel3=addTextPanel("还原");

    JButton select_file_button=new JButton("添加文件");
    JButton set_local_button=new JButton("本地备份设置");

    JButton set_remote_button=new JButton("网盘备份设置");
    boolean login=false;
    String usrNow=null;
    String usrPwd=null;
    String fileSelected=null;
    static FTPClient ftpClient = null;
    FileInputStream fis = null;

    String[] backup_choice={"本地备份","网盘备份"};
    JComboBox<String> select_loc_rmo=new JComboBox<String>(backup_choice);
    JButton start_backup_button=new JButton("开始备份");
    JButton cancel_backup_button=new JButton("清空列表");
    String local_text="D:\\软件开发测试";

    String[] file_cols = {"名称","路径"};
    Object[][] tableValues = {};

    DefaultTableModel model = new DefaultTableModel(tableValues, file_cols);
    JTable file_table=new JTable(model){
        @Override
        public boolean isCellEditable(int row,int column){
            return false;
        }
        public String getToolTipText(MouseEvent e) {
            int row=file_table.rowAtPoint(e.getPoint());
            int col=file_table.columnAtPoint(e.getPoint());
            String tip=null;
            if(row>-1&&col>-1){
                Object value=file_table.getValueAt(row,col);
                if(value!=null&&!"".equals(value)){
                    tip=value.toString();
                }
            }
            return tip;
        }
    };
    String[] ftpCols = {"文件名","文件大小"};
    Object[][] ftpVals = {};
    DefaultTableModel ftpModel = new DefaultTableModel(ftpVals, ftpCols);
    JTable ftp_table=new JTable(ftpModel){
        @Override
        public boolean isCellEditable(int row,int column){
            return false;
        }
    };

    JScrollPane file_scrollPane = new JScrollPane(file_table);
    JScrollPane ftp_scrollPane = new JScrollPane(ftp_table);

//    JPopupMenu table_menu=new JPopupMenu();
//    ButtonGroup table_group=new ButtonGroup();
//    JRadioButtonMenuItem table_select=new JRadioButtonMenuItem("全选");
//    JRadioButtonMenuItem table_cancel=new JRadioButtonMenuItem("全不选");
//    JRadioButtonMenuItem table_delete=new JRadioButtonMenuItem("删除");
    //JRadioButtonMenuItem table_inv_select=new JRadioButtonMenuItem("反选");

    public FTPClient getFtpClient(){
        return ftpClient;
    }

    public void initFtpDesk(){
        ftp_table.getTableHeader().setFont(font4);
        ftp_table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        ftp_table.setRowHeight(24);
        ftp_table.setFont(font4);
        ftp_table.getColumnModel().getColumn(0).setPreferredWidth(frame_width/3);
        ftp_table.getColumnModel().getColumn(1).setPreferredWidth(frame_width/6);
        ftp_table.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent me) {
                if (SwingUtilities.isRightMouseButton(me)) {
                    final int row = ftp_table.rowAtPoint(me.getPoint());
                    System.out.println("row:"+row);
                    if(row!=-1){

                        final int col = ftp_table.columnAtPoint(me.getPoint());
                        ftp_table.setRowSelectionInterval(row, row);
                        final JPopupMenu table_menu = new JPopupMenu();
                        JMenuItem select = new JMenuItem("选择");
                        JMenuItem delete = new JMenuItem("删除");
                        select.setFont(font4);
                        delete.setFont(font4);
                        //table_menu.add(select);
                        //table_menu.addSeparator();
                        table_menu.add(delete);
//                        select.addActionListener(new ActionListener() {
//                            public void actionPerformed(ActionEvent e) {
//                                System.out.println("选择");
//                                fileSelected=ftpModel.getValueAt(row,0).toString();
//                                System.out.println(fileSelected);
//                                //model.addRow(new Object[]{"35", "Boss"});
//                                //file_table.setModel(tableModel);
//                            }
//                        });
                        delete.addActionListener(new ActionListener() {
                            public void actionPerformed(ActionEvent e) {
                                System.out.println("删除");
                                //DefaultTableModel tableModel = (DefaultTableModel) file_table.getModel();
                                JLabel  info_act  =  new  JLabel("确定删除文件吗？删除后将无法恢复");info_act.setFont(font3);
                                int n=JOptionPane.showConfirmDialog(null,info_act,"警告",JOptionPane.YES_NO_OPTION);
                                if(n==1)
                                    return;
                                try {
                                    FtpCli.deleteFile(ftpClient,ftpModel.getValueAt(row,0).toString());
                                    ftpModel.removeRow(row);
                                } catch (IOException ex) { ex.printStackTrace(); }
                                //file_table.setModel(tableModel);
                                //popup.setVisible(false);
                            }
                        });
                        table_menu.show(me.getComponent(), me.getX(), me.getY());
                }
            }
        }});
    }





    public void init_backups(){
        //备份界面布局
        backup_Panel.setLayout(null);

        JPanel sub_backup_Panel_1=new JPanel();
        backup_Panel.add(sub_backup_Panel_1);
        sub_backup_Panel_1.setBounds(50,-30,frame_width/4,frame_height);

        FlowLayout flowLayout=(FlowLayout)sub_backup_Panel_1.getLayout();
        flowLayout.setVgap(frame_height/12);

        Dimension dim=new Dimension(frame_width/6,frame_height/16);
        sub_backup_Panel_1.add(select_file_button);select_file_button.setFont(font1);select_file_button.setPreferredSize(dim);
        sub_backup_Panel_1.add(set_local_button);set_local_button.setFont(font1);set_local_button.setPreferredSize(dim);
        sub_backup_Panel_1.add(set_remote_button); set_remote_button.setFont(font1);set_remote_button.setPreferredSize(dim);
        select_file_button.setToolTipText("可直接拖动文件到右侧列表");


        JPanel select_start_panel=new JPanel();
        //select_start_panel.setBorder(new EmptyBorder(0,0,0,0));
        JLabel select_start_text=new JLabel("选择备份方式：");
        select_start_panel.add(select_start_text);select_start_text.setFont(font2);
        select_start_panel.add(select_loc_rmo);select_loc_rmo.setFont(font2);
        sub_backup_Panel_1.add(select_start_panel);

        sub_backup_Panel_1.add(start_backup_button);start_backup_button.setFont(font1);start_backup_button.setPreferredSize(dim);
        sub_backup_Panel_1.add(cancel_backup_button); cancel_backup_button.setFont(font1);cancel_backup_button.setPreferredSize(dim);


        //button监听

        select_file_button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser filechooser = new JFileChooser(); //文件选择
                filechooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
                filechooser.showOpenDialog(filechooser);//打开文件选择窗
                File file = filechooser.getSelectedFile();  	//获取选择的文件
                boolean flag=true;
                for(int i=0;i< model.getRowCount();i++){
                    if(file.getName().equals(model.getValueAt(i,0))){
                        JOptionPane.showMessageDialog(null,"文件名不能重复！","提示",JOptionPane.ERROR_MESSAGE);
                        flag=false;
                        break;
                    }
                }
                if(flag)
                    model.addRow(new Object[]{file.getName(), file.getPath()});
                //textPath.setText(openFile.getPath());	//获取选择文件的路径
            }
        });
        set_local_button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Object[] options ={ "选择路径", "返回" };

                //JOptionPane.showInputDialog(null,"请输入你的爱好：\n","title",JOptionPane.PLAIN_MESSAGE,new ImageIcon(),null,"当前路径");
                while(true){
                    JPanel panel = new JPanel();
                    JLabel label=new JLabel("当前路径：");
                    label.setFont(font3);
                    panel.add(label);
                    JTextField textField = new JTextField(local_text,40);
                    textField.setFont(font3);
                    panel.add(textField);
                    int result=JOptionPane.showOptionDialog(null, panel, "本地备份设置",
                            JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null,
                            options,null);
                    if(result!=0)
                        break;
                    else{
                        JFileChooser filechooser = new JFileChooser(); //文件选择
                        filechooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                        filechooser.showOpenDialog(filechooser);//打开文件选择窗
                        File file = filechooser.getSelectedFile();  	//获取选择的文件
                        local_text=file.getPath();
                    }
                }

            }
        });

        set_remote_button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Object[] options ={ "确定", "返回" };
                JPanel panel = new JPanel();
                panel.setLayout(new GridLayout(3,2,4,4));
                JLabel label1=new JLabel("服务器IP：");
                JTextField textField1 = new JTextField(15);textField1.setText("192.168.1.102");
                JLabel label2=new JLabel("用户名：");
                JTextField textField2 = new JTextField(15);
                JLabel label3=new JLabel("密码：");
                JPasswordField textField3 = new JPasswordField(15);textField3.setEchoChar('*');
                label1.setFont(font4);label2.setFont(font4);label3.setFont(font4);
                textField1.setFont(font4);textField2.setFont(font4);textField3.setFont(font4);
                panel.add(label1);panel.add(textField1);
                panel.add(label2);panel.add(textField2);
                panel.add(label3);panel.add(textField3);
                while (!login){
                    textField2.setText("");textField3.setText("");
                    int result=JOptionPane.showOptionDialog(null, panel, "登录网盘",
                            JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null,
                            options,null);
                    if(result!=0)
                        return;
                    else{
                        if(textField1.getText().length()==0){
                            JOptionPane.showMessageDialog(panel,"请输入服务器IP！","提示",JOptionPane.WARNING_MESSAGE);
                            continue;
                        }
                        try {
                            frame.setFocusable(false);
                            //JOptionPane.showMessageDialog(null, "请等待.", "正在连接",JOptionPane.PLAIN_MESSAGE);
                            ftpClient=new FTPClient();//重新创建一个，不然上传文件会出错，不知道为什么
                            ftpClient.connect(textField1.getText());
                        } catch (IOException ioException) {
                            JOptionPane.showMessageDialog(panel,"无法连接服务器！请检查IP是否正确","提示",JOptionPane.WARNING_MESSAGE);
                            frame.setFocusable(true);
                            continue;
                        }
                        frame.setFocusable(true);
                        try{
                            boolean usrlogin=ftpClient.login(textField2.getText(), textField3.getText());
                            if(!usrlogin){
                                JOptionPane.showMessageDialog(panel,"用户名或密码错误！","提示",JOptionPane.WARNING_MESSAGE);
                                try { ftpClient.disconnect();} catch (IOException ex) { ex.printStackTrace(); }
                            }
                            else {
                                usrNow=textField2.getText();
                                usrPwd=textField3.getPassword().toString();
                                login=true;
                            }
                        } catch (IOException ioException) {}
                    }
                }
                if(login){
                    System.out.println(ftpClient.getPassiveLocalIPAddress());
                    Object[] usrOptions ={ "查看网盘资源", "注销","返回" };
                    JPanel usrPanel = new JPanel();
                    JLabel usrLabel=new JLabel("当前用户："+usrNow);
                    usrLabel.setFont(font3);
                    usrPanel.add(usrLabel);
                    while (true){
                        int result=JOptionPane.showOptionDialog(backup_Panel, usrPanel, "网盘管理",
                                JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null,
                                usrOptions,null);
                        if(result==0){
//                            try { FtpCli.showList(ftpClient); }
//                            catch (IOException ioException) { ioException.printStackTrace(); }
                            ftpModel.setRowCount(0);
                            ArrayList<ArrayList<String>> filelist=null;
                            try { filelist=FtpCli.showList(ftpClient); } catch (IOException e3) { }
                            for(int i=0;i<filelist.size();i++)
                                ftpModel.addRow(new Object[]{filelist.get(i).get(0), filelist.get(i).get(1)});
                            //ftpModel.addRow(new Object[]{file.getName(), file.getPath()});



                            JDialog ftpFrame = new JDialog();//构造一个新的JFrame，作为新窗口。
                            ftpFrame.setTitle("网盘文件");
                            ftpFrame.getContentPane().add(ftp_scrollPane);



                            ftpFrame.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);  // 设置模式类型。
                            ftpFrame.setSize(frame_width/2,frame_height/2);
                            ftpFrame.setLocationRelativeTo(null);
                            ftpFrame.setVisible(true);
//                            JLabel jl = new JLabel();// 注意类名别写错了。
//                            ftpFrame.getContentPane().add(jl);
//                            jl.setText("这是新窗口");
//                            jl.setVerticalAlignment(JLabel.CENTER);
//                            jl.setHorizontalAlignment(JLabel.CENTER);// 注意方法名别写错了。
                            // 参数 APPLICATION_MODAL：阻塞同一 Java 应用程序中的所有顶层窗口（它自己的子层次

                        }
                        if(result==1){
                            try {
                                ftpClient.logout();
                                ftpClient.disconnect();
                            } catch (IOException ex) { ex.printStackTrace(); }
                            login=false;
                            return;
                        }
                        if(result==2)
                            return;
                    }
                }


            }
        });




        start_backup_button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(select_loc_rmo.getSelectedIndex()==1&&!ftpClient.isConnected()){
                    JOptionPane.showMessageDialog(backup_Panel,"请先登录网盘！","提示",JOptionPane.WARNING_MESSAGE);
                    return;
                }
                if(file_table.getRowCount()==0){
                    JOptionPane.showMessageDialog(backup_Panel,"请先添加文件！","提示",JOptionPane.WARNING_MESSAGE);
                    return;
                }
                Object[] startOptions ={ "开始", "取消" };
                JPanel panel = new JPanel();
                panel.setLayout(new GridLayout(3,2,4,4));
                JLabel label1=new JLabel("备份文件名：");
                JTextField textField1 = new JTextField(15);
                JLabel label3=new JLabel("密码：");
                JPasswordField textField3 = new JPasswordField(15);textField3.setEchoChar('*');
                JCheckBox checkFile=new JCheckBox("进行文件校验");
                textField1.addKeyListener(new KeyAdapter() {
                    public void keyTyped(KeyEvent e) {
                        char c = e.getKeyChar();
                        if (c=='\\'||c=='.'||c=='*') {
                            e.consume();  // ignore event
                        }
                    }});

                label1.setFont(font4);label3.setFont(font4);
                textField1.setFont(font4);textField3.setFont(font4);
                checkFile.setFont(font4);

                panel.add(label1);panel.add(textField1);
                panel.add(label3);panel.add(textField3);
                panel.add(checkFile);
                int startResult=JOptionPane.showOptionDialog(null, panel, "备份文件设置",
                        JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null,
                        startOptions,null);
                if(startResult==1)
                    return;
                //System.out.println(checkFile.isSelected());
                //System.out.println(ftpClient.isConnected());
                //System.out.println(ftpClient.getPassiveLocalIPAddress());

                String ftpPathName=null;

                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd_HH_mm_ss");
                String tail=df.format(System.currentTimeMillis())+".tar";
                String fileName=local_text+"\\"+textField1.getText()+"_"+tail;
                File file=new File(fileName);
                if(!file.exists()){
                    try{file.createNewFile();}
                    catch (IOException ex){ex.printStackTrace();}
                }
                try{
                    ArrayList<String> pathList=new ArrayList<String>();
                    for(int i=0;i<model.getRowCount();i++){
                        pathList.add(model.getValueAt(i,1).toString());
                    }
                    Map<String,String> fileRelate= new HashMap<String,String>();//拷贝文件和原文件的映射
                    if(checkFile.isSelected()){//如果要校验先拷贝到指定文件夹下
                        String dstPath=local_text+"\\.MyTempFileBackup";
                        File dst=new File(dstPath);
                        if(!dst.exists())
                            dst.mkdir();
                        for(String filePath:pathList){//依次建立映射
                            relateDir(dstPath.length(),filePath,dstPath+"\\"+filePath.substring(filePath.lastIndexOf('\\')+1),fileRelate);
                        }
//                        for(String key:fileRelate.keySet()) {//打印还原文件的hash
//                            System.out.println(key + ":" + fileRelate.get(key));
//                        }
                    }

                    TarArchive.tar(pathList,fileName);
                    String compressFile=fileName+".huf";
                    ftpPathName=compressFile;
                    FileProcessEncrypt.comPress(fileName,compressFile,textField3.getText());
                    //FileProcess.comPress(fileName,compressFile);
                    //System.out.println(textField3.getText());

                    if(checkFile.isSelected()){//进行文件校验
                        Map<String,String> postFileHash= new HashMap<String,String>();
                        String recPath=local_text+"\\.MyTempFileBackup\\"+fileName.substring(fileName.lastIndexOf('\\'));
                        recoverTemp(fileName,recPath);
                        addFileHash2Map(recPath.length(),new File(recPath),postFileHash);//计算还原文件的hash
                        for(String key:postFileHash.keySet()){//打印还原文件的hash
                            System.out.println(key+":"+postFileHash.get(key));
                        }
                        JPanel vpanel = new JPanel();
                        JTextArea vtext=new JTextArea();
                        vtext.setFont(font3);
                        vpanel.add(vtext);
                        boolean vflag=true;
                        String vinfo="";
                        System.out.println("完成！");
                        Thread.sleep(10000);//停10秒去改文件
                        for(String key:fileRelate.keySet()){//计算原文件的hash
                            String orgHash=getFileHash(fileRelate.get(key));
                            if(postFileHash.get(key)==null){
                                System.out.println(fileRelate.get(key)+"未备份成功！");
                                vinfo+=fileRelate.get(key)+"未备份成功！\n";
                                vflag=false;
                                continue;
                            }
                            System.out.println(orgHash+"____"+postFileHash.get(key));
                            if(!orgHash.equals(postFileHash.get(key))){//文件hash不一致
                                System.out.println(fileRelate.get(key)+"发生改变！");
                                vinfo+=fileRelate.get(key)+"发生改变！\n";
                                vflag=false;
                            }
                        }
                        if(vflag){
                            System.out.println("校验完成！所有文件均一致！");
                            vinfo+="校验完成！所有文件均一致\n";
                        }
                        vtext.setText(vinfo);
                        JOptionPane.showMessageDialog(null, vpanel, "文件校验",JOptionPane.PLAIN_MESSAGE);


                        //删除测试文件夹
                        deleteDir(new File(local_text+"\\.MyTempFileBackup"));
                    }
                    File file2=new File(fileName);
                    if(file2.exists())
                        file2.delete();

                    String[] options = {"查看文件", "返回"};
                    int result = JOptionPane.showOptionDialog(null, "本地备份成功！",
                            "提示",
                            JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
                    model.setRowCount(0);
                    if(result==0){
                        try{
                            //Runtime.getRuntime().exec("cmd /c start explorer "+ local_text);
                            Runtime.getRuntime().exec("cmd /c explorer.exe /select,"+ compressFile);
                        }catch (Exception ex){
                        }
                    }
                }catch (Exception ex){
                    ex.printStackTrace();
                }

                if(select_loc_rmo.getSelectedIndex()==1){
                    try {
                        FtpCli.uploadFile(ftpClient,ftpPathName);
                        //FtpCli.uploadFile(ftpClient,ftpPathName);
                        FtpCli.showtest(ftpClient);
                    }
                    catch (IOException ex) { ex.printStackTrace(); }
                }


            }
        });

        cancel_backup_button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JLabel  info_act  =  new  JLabel("确定清空列表吗？");
                info_act.setFont(font3);
                int n=JOptionPane.showConfirmDialog(null,info_act,"提示",JOptionPane.YES_NO_OPTION);
                if(n==0)
                    model.setRowCount(0);
            }
        });



        //设置Jtable
        backup_Panel.add(file_scrollPane);
        file_table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        file_scrollPane.setBounds(frame_width*2/5,30,frame_width/2,frame_height*4/5);
        file_table.getTableHeader().setFont(font2);
        file_table.setRowHeight(30);
        file_table.setFont(font3);

        file_table.getColumnModel().getColumn(0).setPreferredWidth(300);
        file_table.getColumnModel().getColumn(1).setPreferredWidth(400);


        file_table.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent me) {
                if (SwingUtilities.isRightMouseButton(me)) {
                    final int row = file_table.rowAtPoint(me.getPoint());
                    System.out.println("row:"+row);
                    if(row!=-1){
                        final int col = file_table.columnAtPoint(me.getPoint());
                        file_table.setRowSelectionInterval(row, row);

                        final JPopupMenu table_menu = new JPopupMenu();
                        JMenuItem select = new JMenuItem("打开所在路径");
                        JMenuItem delete = new JMenuItem("删除");
                        //JMenuItem delete_all = new JMenuItem("清空");
                        select.setFont(font4);
                        delete.setFont(font4);
                        table_menu.add(select);
                        table_menu.addSeparator();
                        table_menu.add(delete);
                        select.addActionListener(new ActionListener() {
                            public void actionPerformed(ActionEvent e) {
                                System.out.println("打开所在路径");
                                //file_table.setRowSelectionInterval(row, row); //高亮选择指定的行
                                // System.out.println(" "+file_table.getValueAt(row,0));
                                //DefaultTableModel tableModel = (DefaultTableModel) file_table.getModel();
                                try {
                                    String path=model.getValueAt(row,1).toString();
                                    System.out.println(path);
                                    String subpath=path.substring(0,path.lastIndexOf("\\"));
                                    Runtime.getRuntime().exec("cmd /c start explorer "+ subpath);
                                    Runtime.getRuntime().exec("cmd /c explorer.exe /select,"+ path);
                                } catch (IOException ioException) {
                                    ioException.printStackTrace();
                                }
                                System.out.println("成功");
                            }
                        });
                        delete.addActionListener(new ActionListener() {
                            public void actionPerformed(ActionEvent e) {
                                System.out.println("删除");
                                //DefaultTableModel tableModel = (DefaultTableModel) file_table.getModel();
                                model.removeRow(row);
                                //file_table.setModel(tableModel);
                                //popup.setVisible(false);
                            }
                        });

                        table_menu.show(me.getComponent(), me.getX(), me.getY());
                    }
                }
            }
        });

    }


    public void init(){

        //tabbedPane.setUI(new my_PaneUI());

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(tabbedPane,BorderLayout.CENTER);

        initFtpDesk();
        //FtpCli.initFtpDesk(1,ftpClient,ftp_table,ftpModel);

        init_backups();
        drag();



        tabbedPane.addTab("备份",backup_Panel);
        //tabbedPane.addTab("自动备份",test_panel2);
        //tabbedPane.addTab("还原",test_panel3);
        JPanel recoverPanel=new RecoverPanel();
        tabbedPane.addTab("还原",recoverPanel);
        tabbedPane.setFont(font3);

        frame.pack();
        frame.setResizable(false);
        frame.setSize(frame_width,frame_height);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }



    protected JComponent addTextPanel(String text){
        JPanel panel=new JPanel(new GridLayout(1, 1),false);
        JLabel label=new JLabel(text);
        //label.setFont(new Font("Georgia", Font.PLAIN, 14));
        label.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(label);
        return panel;
    }

    public void drag(){
        new DropTarget(file_scrollPane, DnDConstants.ACTION_COPY_OR_MOVE, new DropTargetAdapter() {
            @Override
            public void drop(DropTargetDropEvent dtde) {
                try {
                    if(dtde.isDataFlavorSupported(DataFlavor.javaFileListFlavor)){//格式支持
                        dtde.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);//接收数据
                        List<File> list=(List<File>)(dtde.getTransferable().getTransferData(DataFlavor.javaFileListFlavor));
                        String path="";
                        String name="";
                        for(File file:list){
                            path=file.getAbsolutePath();
                            name=file.getName();
                            boolean flag=true;
                            for(int i=0;i< model.getRowCount();i++){
                                if(name.equals(model.getValueAt(i,0))){
                                    JOptionPane.showMessageDialog(null,"文件名不能重复！","提示",JOptionPane.ERROR_MESSAGE);
                                    flag=false;
                                    break;
                                }
                            }
                            if(flag)
                                model.addRow(new Object[]{name, path});
                        }
                        dtde.dropComplete(true);
                    }
                    else{
                        dtde.rejectDrop();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }

    public static void addFile2List(File file,ArrayList<String> list){//递归添加目录下的文件到list中
        if(file.isFile()){
            //System.out.println(file.getAbsolutePath());
            String path=file.getAbsolutePath();
            list.add(path);
            return;
        }
        else{
            File[] subFiles=file.listFiles();
            for(File f:subFiles){
                addFile2List(f,list);
            }
        }
    }

    public static void addFileHash2Map(int PreLen,File file,Map<String,String> map){//递归添加目录下的文件hash到map中
        if(file.isFile()){
            String path=file.getAbsolutePath();
            map.put(path.substring(PreLen),getFileHash(path));
            return;
        }
        else{
            File[] subFiles=file.listFiles();
            for(File f:subFiles){
                addFileHash2Map(PreLen,f,map);
            }
        }
    }

    public static String getFileHash(String filePath){
        try{
            InputStream fis=new FileInputStream(filePath);
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            byte buffer[] = new byte[1024];
            int length=-1;
            while ((length = fis.read(buffer, 0, 1024)) != -1) {
                md5.update(buffer, 0, length);
            }
            fis.close();
            //转换并返回包含16个元素字节数组,返回数值范围为-128到127
            byte[] md5Bytes  = md5.digest();
            BigInteger bigInt = new BigInteger(1, md5Bytes);//1代表绝对值
            return bigInt.toString(16);//转换为16进制
        }
        catch (Exception e){
            e.printStackTrace();
            return "";
        }
    }

    public static void relateDir(int preLen,String sourcePath, String dstPath,Map<String,String> relate){//递归建立映射关系
        File file = new File(sourcePath);
        if(file.isFile()){//一开始就是文件那就直接建立映射
            relate.put(dstPath.substring(preLen+1),sourcePath);//把前缀去掉做键
            return;
        }
        String[] filePath = file.list();
        if (!(new File(dstPath)).exists()) {
            (new File(dstPath)).mkdir();
        }
        for (int i = 0; i < filePath.length; i++) {
            if ((new File(sourcePath + file.separator + filePath[i])).isDirectory()) {
                relateDir(preLen,sourcePath  + file.separator  + filePath[i], dstPath  + file.separator + filePath[i],relate);
            }
            if (new File(sourcePath  + file.separator + filePath[i]).isFile()) {
                String dst=dstPath + file.separator + filePath[i];
                relate.put(dst.substring(preLen+1),sourcePath + file.separator + filePath[i]);//把前缀去掉做键
            }
        }
    }


    void recoverTemp(String org,String dst) {//临时恢复做数据检验
        try { TarArchive.untar(org,dst); }
        catch (IOException ex) { ex.printStackTrace(); }
    }

    static boolean deleteDir(File dir) {//递归删除目录
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i=0; i<children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        // 目录此时为空，可以删除
        return dir.delete();
    }

}


