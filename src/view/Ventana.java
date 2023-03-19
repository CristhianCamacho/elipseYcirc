package view;

import view.panel.PanelDibujo;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;
import java.util.concurrent.*;

public class Ventana extends JFrame implements ActionListener {

    public static Ventana instance;

    public JLabel jl_elipse;
    public JTextArea jta_a;
    public JTextArea jta_b;

    public JButton b_dibujar_elipse;
    public JButton b_dibujar_circunferencia;

    public JSplitPane splitPane;
    PanelDibujo p_dibujo;

    public JTextArea jta_logs;
    JScrollPane scrollLogs;

    public Thread hilo;



    int _x ;
    int _y ;
    int _R ;

    public Ventana(){

        super("Venta De Elipse Circ");
        initialize();

        this.setVisible(true);
    }

    public static Ventana getInstance()
    {
        if(instance == null)
        {
            instance = new Ventana();
        }

        return instance;
    }

    private void initialize()
    {
        setLayout( new BoxLayout( this.getContentPane(), BoxLayout.X_AXIS));

        JPanel p_controles = new JPanel();
        p_controles.setLayout( new BoxLayout( p_controles, BoxLayout.Y_AXIS));
        jl_elipse = new JLabel("Elipse");
        p_controles.add(jl_elipse);

        JPanel panelAB = new JPanel();
        panelAB.setLayout( new BoxLayout( panelAB, BoxLayout.X_AXIS));
        TitledBorder titleRotar;
        titleRotar = BorderFactory.createTitledBorder("valor de a y b");
        panelAB.setBorder(titleRotar);

        jta_a = new JTextArea(""+PanelDibujo.INIT_A);
        jta_a.setSize(new Dimension(100, 25));
        jta_a.setBorder(BorderFactory.createTitledBorder("a"));
        panelAB.add(jta_a);
        jta_b = new JTextArea(""+PanelDibujo.INIT_B);
        jta_b.setSize(new Dimension(100, 25));
        jta_b.setBorder(BorderFactory.createTitledBorder("b"));
        panelAB.add(jta_b);

        p_controles.add(panelAB);

        b_dibujar_elipse = new JButton("Dibujar Elipse");
        p_controles.add(b_dibujar_elipse);
        b_dibujar_elipse.addActionListener(this);

        b_dibujar_circunferencia = new JButton("Dibujar Circunferencia");
        p_controles.add(b_dibujar_circunferencia);
        b_dibujar_circunferencia.addActionListener(this);

        jta_logs = new JTextArea("Logs \n");
        jta_logs.setSize(new Dimension(100, 500));
        jta_logs.setPreferredSize(new Dimension(100, 500));
        scrollLogs = new JScrollPane(jta_logs);
        scrollLogs.setSize(new Dimension(100, 500));
        scrollLogs.setPreferredSize(new Dimension(100, 500));
        scrollLogs.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        p_controles.add(scrollLogs);

        p_dibujo = new PanelDibujo();
        //p_dibujo.setSize(new Dimension((1280+20)/2, 720+10 ));
        p_dibujo.setBackground(Color.white);


        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);

        splitPane.setLeftComponent(p_controles);
        splitPane.setRightComponent(p_dibujo);

        add(splitPane);
    }


    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == b_dibujar_elipse) {
            double w = p_dibujo.getSize().getWidth();
            double h = p_dibujo.getSize().getHeight();

            p_dibujo.dibujarElipse(
                    (int)(w/2) - Integer.parseInt(jta_a.getText())/2,
                    (int)(h/2) - Integer.parseInt(jta_b.getText())/2,
                    Integer.parseInt(jta_a.getText()),
                    Integer.parseInt(jta_b.getText()) );

            p_dibujo.Vimagenes = new Vector();
            p_dibujo.arcMax = null;
            p_dibujo.pMax = 0;
            p_dibujo.NroIteraciones = PanelDibujo.MAX_ITERACIONES;

            p_dibujo.repaint();
        }

        if (e.getSource() == b_dibujar_circunferencia) {
            p_dibujo.intersectan = false;
            hilo = new Thread() {
                @Override
                public void run() {
                    super.run();
                    while (!p_dibujo.intersectan) {

                        try {
                            Thread.sleep(10);
                        }
                        catch (InterruptedException ex) {
                            System.out.println(ex);
                        }

                        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
            executor.schedule(() -> {

                int max = PanelDibujo.MAX_RADIOUS;
                _x = (Math.random()>0.5? -1 : 1 ) * (int)(Math.random()*max);
                _y = (Math.random()>0.5? -1 : 1 ) * (int)(Math.random()*max);
                _R = (int)(Math.random()*max);

                p_dibujo.dibujarCircunferencia(
                        _x,//p_dibujo.getWidth()),
                        _y,//p_dibujo.getHeight()),
                        _R//p_dibujo.getWidth())
                );
                p_dibujo.repaint();

            }, 50, TimeUnit.MILLISECONDS);
 /*
                        p_dibujo.dibujarCircunferencia(
                                (int)(Math.random()*p_dibujo.getWidth()),
                                (int)(Math.random()*p_dibujo.getHeight()),
                                (int)(Math.random()*p_dibujo.getWidth())
                        );
                        p_dibujo.repaint();

*/                    }
                }
            };

            hilo.start();

        }


    }

    public void agregarAlLog (String s) {
        jta_logs.append(s);
        System.out.print(s);
    }

    public void agregarAlLog () {
        jta_logs.append(String.format("(%1s, %2s), R=%3s \n",_x,_y,_R));
        System.out.print(String.format("(%1s, %2s), R=%3s \n",_x,_y,_R));
    }


}


