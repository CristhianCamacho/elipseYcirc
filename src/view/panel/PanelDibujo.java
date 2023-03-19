package view.panel;

import view.Ventana;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import java.util.Vector;

public class PanelDibujo extends JPanel {

    Ellipse2D elipse;
    int elipseX;
    int elipseY;
    int elipseA;
    int elipseB;
    Ellipse2D circle;
    int circleX;
    int circleY;
    int circleR;

    public static int MAX_ITERACIONES = 500;
    public static int MAX_RADIOUS = 2500;
    public static int INIT_A = 590;
    public static int INIT_B = 600;
    public int NroIteraciones = MAX_ITERACIONES;
    public boolean intersectan = false;
    public float pMax = 0;
    public Area arcMax;

    public Vector Vimagenes = new Vector();

    double a_max;
    double b_max;
    double x_max;
    double y_max;

    double R_max;
    double xc_max;
    double yc_max;

    public PanelDibujo () {

    }

    public void paint(Graphics g)
    {
        Graphics2D g2d = (Graphics2D)g;
        g2d.setStroke(new BasicStroke(2.0f));

        g2d.clearRect(0,0,getWidth(), getHeight());

        //////////////////////////
        // nro Iteraciones
        g2d.drawString("Iteracion = "+NroIteraciones, 10, 20);

        ///////////////////
        for(Object o: Vimagenes) {

            Paint old = g2d.getPaint();

            g2d.setColor(Color.gray);
/*
            g2d.setColor(new Color(
                    (int)(Math.random()*255),
                    (int)(Math.random()*255),
                    (int)(Math.random()*255)
            ));
*/
            g2d.fill((Area)o);

            g2d.setPaint(old);
        }

        if (arcMax!=null) {

            Paint old = g2d.getPaint();
            g2d.setColor(Color.RED);
            g2d.fill(arcMax);
            g2d.setPaint(old);

            g2d.drawString("P="+pMax, elipseX, elipseY-20);
        }

        ///////////////////

        if (elipse == null) {
            return;
        }

        g2d.draw(elipse);

        if (circle == null) {
           return;
        }

        g2d.draw(circle);

        AffineTransform at = new AffineTransform();
        GeneralPath pathElipse = new GeneralPath();
        pathElipse.append(elipse.getPathIterator(at), true);

        at = new AffineTransform();
        GeneralPath pathCirc = new GeneralPath();
        pathCirc.append(circle.getPathIterator(at), true);

        Area a_elip = new Area(pathElipse);
        Area a_circ = new Area(pathCirc);
        Area a_intersect = new Area(pathCirc);

        a_intersect.intersect(a_elip);





        if (!a_intersect.isEmpty() && !(a_intersect.equals(a_circ) || a_intersect.equals(a_elip)) ) {

            ///////////////////////////////////
            int lineThick = 3;
            Ellipse2D elipse2 = new Ellipse2D.Double();
            elipse2.setFrame(circleX+lineThick, circleY+lineThick, circleR-2*lineThick, circleR-2*lineThick);
            at = new AffineTransform();
            GeneralPath pathelipse2 = new GeneralPath();
            pathelipse2.append(elipse2.getPathIterator(at), true);
            Area a4 = new Area(pathelipse2);

            a_intersect.subtract(a4);
            //////////////////////////////////

            g2d.setColor(Color.BLUE);
            g2d.fill(a_intersect);
            Vimagenes.add(a_intersect);

            NroIteraciones--;
            System.out.println(String.format("NroIteraciones=%1s", NroIteraciones));

            if (NroIteraciones <= 0) {
                intersectan = true;

                System.out.println(String.format("pMax=%1s", pMax));

                /*if (arcMax!=null)*/ {

                    if (arcMax!=null) {
                        g2d.setColor(Color.RED);
                        g2d.fill(arcMax);
                    }

                    PathLength pseFinal = new PathLength(a_intersect);
                    pseFinal.initialise();

                    float arcLenghtFinal = pseFinal.lengthOfPath();

                    Ventana.getInstance().agregarAlLog(String.format(" Perimeter = %1s \n", arcLenghtFinal));

                    Ventana.getInstance().agregarAlLog(String.format("Circ x=%1s, y=%2s, R=%3s \n",
                            xc_max, yc_max, R_max));

                    //Ventana.getInstance().agregarAlLog(String.format("Elipse a=%1s, b=%2s, x=%3s, y=%4s \n",
                    //        a_max, b_max, x_max, y_max));
                }

            }

/*
            GeneralPath polygonCirc = new GeneralPath(pathCirc);
            GeneralPath polygonElip = new GeneralPath(pathElipse);


            Point2D point1 = getIntersectPoint(new Point2D.Double(
                    pathElipse.getCurrentPoint().getX(),
                    pathElipse.getCurrentPoint().getY()),
                    pathCirc,
                    pathElipse);
*/
            PathLength pse = new PathLength(a_intersect);
            pse.initialise();

            float arcLenght = pse.lengthOfPath();
            if ( arcLenght > pMax ) {
                pMax = arcLenght;

                arcMax = a_intersect;

                /*
                a_max = elipseA;
                b_max = elipseB;
                x_max = elipseX;
                y_max = elipseY;
                */
                xc_max = circleX;
                yc_max = circleY;
                R_max = circleR;

            }

            //Ventana.getInstance().agregarAlLog(String.format(" Perimeter = %1s \n", arcLenght));

            if (NroIteraciones <= 0) {
                Ventana.getInstance().hilo.stop();
            }

        }

    }

    public void dibujarElipse(int x, int y, int a, int b) {

        elipseX = x;
        elipseY = y;
        elipseA = a;
        elipseB = b;

        elipse = new Ellipse2D.Double();
        elipse.setFrame(x, y, a, b);


        javax.swing.SwingUtilities.invokeLater(new Runnable() {

                                                   @Override
                                                   public void run() {
                                                       repaint();
                                                       Ventana.getInstance().agregarAlLog(String.format("Elipse a=%1s, b=%2s, x=%3s, y=%4s \n",
                                                               a, b, x, y));
                                                   }
                                               }
        );

    }

    public void dibujarCircunferencia(int x, int y, int R){

        circleX = x;
        circleY = y;
        circleR = R;

        circle = new Ellipse2D.Double();
        circle.setFrame(x, y, R, R);

        //javax.swing.SwingUtilities.invokeLater(new Runnable() {

        //    @Override
        //    public void run() {
                repaint();
        //    }
        //});
    }

    public static Point2D getIntersectPoint(Point2D point, GeneralPath p1,
                                            GeneralPath p2) {

        Point2D intersectionPoint, returnPoint = null;

        double x1 = 0, y1 = 0, x2 = 0, y2 = 0, closestDist = 100000;
        int index = 0;
        double seg[] = new double[6];

        for (PathIterator i = p1.getPathIterator(null); !i.isDone(); i
                .next()) {
            int segType = i.currentSegment(seg);

            if (index == 0) {
                x1 = seg[0];
                y1 = seg[1];
                x2 = x1;
                y2 = y1;
            }
            if (index > 0) {
                x1 = x2;
                y1 = y2;
                x2 = seg[0];
                y2 = seg[1];

                intersectionPoint = getIntersectPoint(x1, y1, x2, y2, p2);
                if (intersectionPoint != null) {
                    double dist = intersectionPoint.distance(point);
                    if (dist <= closestDist) {
                        returnPoint = intersectionPoint;
                        closestDist = dist;
                    }
                }
            }
            index++;
        }
        return returnPoint;
    }

    public static Point2D getIntersectPoint(double x1, double y1,
                                            double x2, double y2, GeneralPath p) {
        double x3 = 0, y3 = 0, x4 = 0, y4 = 0;
        int index = 0;
        double seg[] = new double[6];

        for (PathIterator i = p.getPathIterator(null); !i.isDone(); i
                .next()) {
            int segType = i.currentSegment(seg);

            if (index == 0) {
                x3 = seg[0];
                y3 = seg[1];
            } else if (index == 1) {
                x4 = seg[0];
                y4 = seg[1];
            } else if (index > 1) {
                x3 = x4;
                y3 = y4;
                x4 = seg[0];
                y4 = seg[1];
            }
            if (index > 0
                    && Line2D
                    .linesIntersect(x1, y1, x2, y2, x3, y3, x4, y4)) {
                double l1m, l2m;
                //if lines are vertical, cater for a divide by 0:
                if (x2 == x1) {
                    l2m = (y4 - y3) / (x4 - x3);
                    return (new Point2D.Double(x1, (l2m * x1) + y3));
                } else if (x4 == x3) {
                    l1m = (y2 - y1) / (x2 - x1);
                    return (new Point2D.Double(x3, (l1m * x3) + y1));
                } else {
                    l1m = (y2 - y1) / (x2 - x1);
                    l2m = (y4 - y3) / (x4 - x3);

                    double x = ((-l1m) * x1) + y1 + (l2m * x3) - y3;
                    x = x / (l2m - l1m);
                    double y = l1m * (x - x1) + y1;

                    return (new Point2D.Double(x, y));
                }
            }
            index++;
        }
        return null;
    }
}
