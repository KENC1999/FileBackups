import C.TarArchive;
import org.apache.commons.io.FileDeleteStrategy;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

public class RecoverPanel extends JPanel {
    Font font1 = new Font("幼圆", Font.BOLD, 26);
    Font font2 = new Font("黑体", Font.BOLD, 22);
    Font font3 = new Font("宋体", Font.BOLD, 40);
    Font font4 = new Font("幼圆", Font.BOLD, 18);
    static final int frameWidth = 1200;
    static final int frameHeight = 900;

    JButton selectPathButton = new JButton("还原路径设置");
    String recPath="C:";
    String lastPath=null;
    String tmpPath=null;
    JButton recLocButton = new JButton("本地还原");
    JButton recRmoButton = new JButton("网盘还原");

    void recoverFile(File file) throws IOException {
        String fileName=file.getName();
        JOptionPane.showMessageDialog(null,"开始还原！","提示",JOptionPane.PLAIN_MESSAGE);
        tmpPath=recPath+"\\"+fileName.substring(0,fileName.length()-4);
        FileProcess.unCompress(file.getAbsolutePath(),tmpPath);
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
                    int result = JOptionPane.showOptionDialog(null, panel, "本地备份设置",
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
    }
}
