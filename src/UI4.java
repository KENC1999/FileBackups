import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;

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
    String[] backup_choice={"本地备份","网盘备份"};
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
    };
    JScrollPane file_scrollPane = new JScrollPane(file_table);

//    JPopupMenu table_menu=new JPopupMenu();
//    ButtonGroup table_group=new ButtonGroup();
//    JRadioButtonMenuItem table_select=new JRadioButtonMenuItem("全选");
//    JRadioButtonMenuItem table_cancel=new JRadioButtonMenuItem("全不选");
//    JRadioButtonMenuItem table_delete=new JRadioButtonMenuItem("删除");
    //JRadioButtonMenuItem table_inv_select=new JRadioButtonMenuItem("反选");






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
                    if(result==1)
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




        start_backup_button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JLabel  info_act  =  new  JLabel("确定开始备份吗？");
                info_act.setFont(font3);
                int n=JOptionPane.showConfirmDialog(null,info_act,"提示",JOptionPane.YES_NO_CANCEL_OPTION);
                if(n!=0)
                    return;
                if(select_loc_rmo.getSelectedIndex()==0){
                    File file=new File(local_text+"\\log.txt");
                    if(!file.exists()){
                        try{file.createNewFile();}
                        catch (IOException ex){ex.printStackTrace();}
                    }
                    try{
                        FileWriter filewriter=new FileWriter(file,true);
                        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        filewriter.write(df.format(System.currentTimeMillis())+"\n");filewriter.flush();
                        for(int i=0;i<model.getRowCount();i++){
                            filewriter.write(""+model.getValueAt(i,0)+"\t"+model.getValueAt(i,1)+"\n");
                            filewriter.flush();
                        }
                        String[] options = {"查看文件", "退出"};
                        int result = JOptionPane.showOptionDialog(null, "备份成功！",
                                    "提示",
                                JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
                        model.setRowCount(0);
                        if(result==0){
                            try{
                                Runtime.getRuntime().exec("cmd /c start explorer "+ local_text);
                                Runtime.getRuntime().exec("cmd /c explorer.exe /select,"+ local_text+"\\log.txt");
                            }catch (Exception ex){

                            }
                        }

                    }catch (Exception ex){
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(backup_Panel,"路径不存在，请检查路径！","提示",JOptionPane.WARNING_MESSAGE);
                    }
                }
                else {

                }
            }
        });

        cancel_backup_button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JLabel  info_act  =  new  JLabel("确定清空列表吗？");
                info_act.setFont(font3);
                int n=JOptionPane.showConfirmDialog(null,info_act,"提示",JOptionPane.YES_NO_CANCEL_OPTION);
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

        init_backups();




        tabbedPane.addTab("立即备份",backup_Panel);
        tabbedPane.addTab("自动备份",test_panel2);
        tabbedPane.addTab("还原",test_panel3);
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

}


