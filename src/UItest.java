
import  java.awt.*;

public class UItest {
    public  static  void main(String[] args) throws Exception{
        //1.title
        Frame frame=new Frame("备份软件");
        //2.window size
        frame.setLocation(500,150);
        frame.setSize(1000,700);
        //3.visualize
        frame.setVisible(true);
        //panel
        Panel panel=new Panel();
        panel.add(new TextField("filepath"));
        panel.add(new Button("select"));
        //scroll
        ScrollPane sp=new ScrollPane(ScrollPane.SCROLLBARS_ALWAYS);
        sp.add(new TextField("filepath"));
        sp.add(new Button("select"));
        panel.add(sp);
        //panel to window
        frame.add(panel);
    }
}
