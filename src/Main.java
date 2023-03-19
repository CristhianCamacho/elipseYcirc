import view.Ventana;

import javax.swing.*;
import java.awt.*;

public class Main {

    public static void main(String[] args) {
        System.out.println("Hello World!");

        Dimension dim=Toolkit.getDefaultToolkit().getScreenSize();
        //jf_principal.setSize(new Dimension(dim.width-100,dim.height-100));
        Ventana.getInstance().setSize(new Dimension(1280+20,720+10));
        Ventana.getInstance().setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}
