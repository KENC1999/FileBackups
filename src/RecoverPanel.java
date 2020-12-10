import C.TarArchive;
import org.apache.commons.io.FileDeleteStrategy;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class RecoverPanel extends JPanel {
    Font font1 = new Font("幼圆", Font.BOLD, 26);
    Font font2 = new Font("黑体", Font.BOLD, 22);
    Font font3 = new Font("宋体", Font.BOLD, 40);
    Font font4 = new Font("幼圆", Font.BOLD, 18);
    static final int frameWidth = 1200;
    static final int frameHeight = 900;

    JButton selectPathButton = new JButton("还原路径设置");
    String recPath="I:\\软件开发\\下载";
    String lastPath=null;
    String tmpPath=null;
    JButton recLocButton = new JButton("本地还原");
    JButton recRmoButton = new JButton("网盘还原");
    JDialog ftpFrame = new JDialog();//构造一个新的JFrame，作为新窗口。
    String[] ftpCols = {"文件名","文件大小"};
    Object[][] ftpVals = {};
    DefaultTableModel ftpModel = new DefaultTableModel(ftpVals, ftpCols);
    JTable ftp_table=new JTable(ftpModel){
        @Override
        public boolean isCellEditable(int row,int column){
            return false;
        }
    };
    JScrollPane ftp_scrollPane = new JScrollPane(ftp_table);

    void recoverFile(File file) throws IOException {
        String fileName=file.getName();

        tmpPath=recPath+"\\"+fileName.substring(0,fileName.length()-4);
        boolean flag=FileProcessEncrypt.unCompress(file.getAbsolutePath(),tmpPath);
        if(!flag)
            return;

        String des=recPath+"\\"+fileName.substring(0,fileName.length()-8);
        try { TarArchive.untar(tmpPath,des);
        }
        catch (IOException ex) { ex.printStackTrace(); }

        File file1=new File(tmpPath);
        if(file1.exists()){
            System.out.println(file1.getAbsolutePath());
            System.gc();
            try {
                FileDeleteStrategy.FORCE.delete(file1);
            }catch (Exception e){}
        }


        String[] options = {"查看文件", "退出"};
        int result = JOptionPane.showOptionDialog(null, "还原成功！",
                "提示",
                JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
        if(result==0){
            try{
                //Runtime.getRuntime().exec("cmd /c start explorer "+ recPath);
                Runtime.getRuntime().exec("cmd /c explorer.exe /select,"+ des);
            }catch (Exception ex){
            }
        }
    }

    RecoverPanel() {
        this.initFtpDesk();
        this.setLayout(null);
        JPanel subRecPanel = new JPanel();
        this.add(subRecPanel);
        subRecPanel.setBounds(420, -30, frameWidth / 4, frameHeight);

        FlowLayout flowLayout = (FlowLayout) subRecPanel.getLayout();
        flowLayout.setVgap(frameHeight / 6);
        Dimension dim = new Dimension(frameWidth / 6, frameHeight / 16);
        subRecPanel.add(selectPathButton);
        selectPathButton.setFont(font1);
        selectPathButton.setPreferredSize(dim);
        subRecPanel.add(recLocButton);
        recLocButton.setFont(font1);
        recLocButton.setPreferredSize(dim);
        subRecPanel.add(recRmoButton);
        recRmoButton.setFont(font1);
        recRmoButton.setPreferredSize(dim);

        //UIManager.put("FileChooser.text",font3);

        selectPathButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Object[] options = {"选择路径", "退出"};

                //JOptionPane.showInputDialog(null,"请输入你的爱好：\n","title",JOptionPane.PLAIN_MESSAGE,new ImageIcon(),null,"当前路径");
                while (true) {
                    JPanel panel = new JPanel();
                    JLabel label = new JLabel("当前路径：");
                    label.setFont(font3);
                    panel.add(label);
                    JTextField textField = new JTextField(recPath, 40);
                    textField.setFont(font3);
                    panel.add(textField);
                    int result = JOptionPane.showOptionDialog(null, panel, "还原路径设置",
                            JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null,
                            options, null);
                    if (result != 0)
                        break;
                    else {
                        JFileChooser filechooser = new JFileChooser(); //文件选择
                        filechooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                        filechooser.showOpenDialog(filechooser);//打开文件选择窗
                        File file = filechooser.getSelectedFile();    //获取选择的文件
                        recPath = file.getPath();
                        lastPath=recPath;
                    }
                }

            }
        });

        recLocButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser filechooser = new JFileChooser(lastPath); //文件选择
                filechooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                filechooser.showOpenDialog(filechooser);//打开文件选择窗
                File file = filechooser.getSelectedFile();    //获取选择的文件
                //System.out.println(file.getName());
                String fileName = file.getName();
                lastPath = file.getPath();
                System.out.println(file.getAbsolutePath());
                try {
                    if (!(fileName).endsWith("tar.huf")) {
                        System.out.println(fileName.substring(fileName.length() - 7));
                        JOptionPane.showMessageDialog(null, "格式不正确！后缀须为tar.huf", "提示", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "格式不正确！后缀须为tar.huf", "提示", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                try {
                    recoverFile(file);
                } catch (IOException exception) {
                    exception.printStackTrace();
                }

            }
        });

        recRmoButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ftpModel.setRowCount(0);
                ArrayList<ArrayList<String>> filelist=null;
                try { filelist=FtpCli.showList(UI4.ftpClient); }
                catch (Exception e3) {
                    JOptionPane.showMessageDialog(null,"请先登录网盘！","提示",JOptionPane.WARNING_MESSAGE);
                    return;
                }
                for(int i=0;i<filelist.size();i++)
                    ftpModel.addRow(new Object[]{filelist.get(i).get(0), filelist.get(i).get(1)});
                //ftpModel.addRow(new Object[]{file.getName(), file.getPath()});




                ftpFrame.setTitle("网盘文件");
                ftpFrame.getContentPane().add(ftp_scrollPane);

                ftpFrame.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);  // 设置模式类型。
                ftpFrame.setSize(frameWidth/2,frameHeight/2);
                ftpFrame.setLocationRelativeTo(null);
                ftpFrame.setVisible(true);
            }
        });


    }

    public void initFtpDesk(){
        ftp_table.getTableHeader().setFont(font4);
        ftp_table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        ftp_table.setRowHeight(24);
        ftp_table.setFont(font4);
        ftp_table.getColumnModel().getColumn(0).setPreferredWidth(frameWidth/3);
        ftp_table.getColumnModel().getColumn(1).setPreferredWidth(frameWidth/6);
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
                        select.setFont(font4);
                        table_menu.add(select);
                        //table_menu.addSeparator();
                        select.addActionListener(new ActionListener() {
                            public void actionPerformed(ActionEvent e) {
                                System.out.println("选择");
                                String fileSelected=ftpModel.getValueAt(row,0).toString();
                                System.out.println(fileSelected);
                                ftpFrame.setVisible(false);
                                try {
                                    FtpCli.downFile(UI4.ftpClient,fileSelected,recPath);
                                    File file=new File(recPath+"\\"+fileSelected);
                                    recoverFile(file);
                                } catch (IOException ex) {
                                    ex.printStackTrace();
                                }
                            }
                        });
                        table_menu.show(me.getComponent(), me.getX(), me.getY());
                    }
                }
            }});
    }
}
