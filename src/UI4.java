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
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.net.ftp.FTPClient;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

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
    String fileSelected=null;
    FTPClient ftpClient = new FTPClient();
    FileInputStream fis = null;

    String[] backup_choice={"本地备份","网盘备份","本地+网盘"};
    JComboBox<String> select_loc_rmo=new JComboBox<String>(backup_choice);
    JButton start_backup_button=new JButton("开始备份");
    JButton cancel_backup_button=new JButton("清空列表");
    String local_text="C:\\";

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
                        table_menu.add(select);
                        table_menu.addSeparator();
                        table_menu.add(delete);
                        select.addActionListener(new ActionListener() {
                            public void actionPerformed(ActionEvent e) {
                                System.out.println("选择");
                                fileSelected=ftpModel.getValueAt(row,0).toString();
                                System.out.println(fileSelected);
                                //model.addRow(new Object[]{"35", "Boss"});
                                //file_table.setModel(tableModel);
                            }
                        });
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
                model.addRow(new Object[]{file.getName(), file.getPath()});
                //textPath.setText(openFile.getPath());	//获取选择文件的路径
            }
        });
        set_local_button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Object[] options ={ "选择路径", "退出" };

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
                Object[] options ={ "确定", "退出" };
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
                    if(result==1)
                        return;
                    else{
                        if(textField1.getText().length()==0){
                            JOptionPane.showMessageDialog(panel,"请输入服务器IP！","提示",JOptionPane.WARNING_MESSAGE);
                            continue;
                        }
                        try {
                            frame.setFocusable(false);
                            //JOptionPane.showMessageDialog(null, "请等待.", "正在连接",JOptionPane.PLAIN_MESSAGE);
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
                                try { ftpClient.disconnect(); } catch (IOException ex) { ex.printStackTrace(); }
                            }
                            else {
                                usrNow=textField2.getText();
                                login=true;
                            }
                        } catch (IOException ioException) {}
                    }
                }
                if(login){
                    Object[] usrOptions ={ "查看网盘资源", "注销","退出" };
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
                            try { ftpClient.disconnect(); } catch (IOException ex) { ex.printStackTrace(); }
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
                Object[] startOptions ={ "开始", "取消" };
                JPanel panel = new JPanel();
                panel.setLayout(new GridLayout(2,2,4,4));
                JLabel label1=new JLabel("备份文件名：");
                JTextField textField1 = new JTextField(15);
                JLabel label3=new JLabel("密码：");
                JPasswordField textField3 = new JPasswordField(15);textField3.setEchoChar('*');
                label1.setFont(font4);label3.setFont(font4);
                textField1.setFont(font4);textField3.setFont(font4);
                panel.add(label1);panel.add(textField1);
                panel.add(label3);panel.add(textField3);
                int startResult=JOptionPane.showOptionDialog(null, panel, "备份文件设置",
                        JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null,
                        startOptions,null);
                if(startResult==1)
                    return;

                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd_HH_mm_ss");
                String tail=df.format(System.currentTimeMillis())+".tar";
                String fileName=local_text+"\\"+textField1.getText()+"_"+tail;
                File file=new File(fileName);
                if(!file.exists()){
                    try{file.createNewFile();}
                    catch (IOException ex){ex.printStackTrace();}
                }
                try{
//                    FileWriter filewriter=new FileWriter(file,true);
//                    //SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//                    filewriter.write(df.format(System.currentTimeMillis())+"\n");filewriter.flush();
//                    for(int i=0;i<model.getRowCount();i++){
//                        filewriter.write(""+model.getValueAt(i,0)+"\t"+model.getValueAt(i,1)+"\n");
//                        filewriter.flush();
//                    }
                    ArrayList<String> pathList=new ArrayList<String>();
                    for(int i=0;i<model.getRowCount();i++){
                        pathList.add(model.getValueAt(i,1).toString());
                    }
                    TarArchive.tar(pathList,fileName);
                    String compressFile=fileName+".huf";
                    FileProcess.comPress(fileName,compressFile);
                    {
                        File file2=new File(fileName);
                        if(file2.exists())
                            file2.delete();
                    }

                    String[] options = {"查看文件", "退出"};
                    int result = JOptionPane.showOptionDialog(null, "备份成功！",
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
                    JOptionPane.showMessageDialog(backup_Panel,"路径不存在，请检查路径！","提示",JOptionPane.WARNING_MESSAGE);
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
                                //model.addRow(new Object[]{"35", "Boss"});
                                //file_table.setModel(tableModel);

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
                            path+=file.getAbsolutePath();
                            name+=file.getName();
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

}


