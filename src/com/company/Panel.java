package com.company;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.awt.Graphics2D;
import java.util.Random;

public class Panel extends JPanel
{
    private final ArrayList<Kula> listaKul;
    private int size = 50;
    Timer timer;
    public Panel()
    {
        listaKul = new ArrayList<>();
        setBackground(Color.black);

        addMouseListener(new Listener());
        addMouseWheelListener(new Listener());
        addMouseMotionListener(new Listener());
        int DELAY = 16;
        timer = new Timer(DELAY, new Listener());
        timer.start();
    }

    @Override
    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);

        for(Kula k:listaKul)
        {
            ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setColor(k.kolor);
            g.fillOval((int)k.position.getX(), (int)k.position.getY(), k.size, k.mass );
            Font font = new Font("Menlo", Font.ITALIC, 15);
            g.setColor(Color.white);
            g.setFont(font);
            String text = Integer.toString(listaKul.indexOf(k));
            g.drawString(text, (int)(k.position.getX()+k.radius), (int)(k.position.getY()+k.radius));
        }

        Font font = new Font("Menlo", Font.ITALIC, 25);
        g.setColor(Color.yellow);
        g.setFont(font);
        g.drawString("Ilosc kul: "+Integer.toString(listaKul.size()),40,40);
        g.drawString("Rozmiar nowej kulki: "+Integer.toString(size), 40, 80);
    }

    private class Listener implements MouseListener, MouseMotionListener, MouseWheelListener, ActionListener
    {

        @Override
        public void mouseClicked(MouseEvent mouseEvent)
        {

        }

        @Override
        public void mousePressed(MouseEvent mouseEvent)
        {
            listaKul.add(new Kula(mouseEvent.getX(),mouseEvent.getY(),size));
            repaint();
        }

        @Override
        public void mouseReleased(MouseEvent mouseEvent)
        {

        }

        @Override
        public void mouseEntered(MouseEvent mouseEvent)
        {
            timer.start();
        }

        @Override
        public void mouseExited(MouseEvent mouseEvent)
        {
            timer.stop();
        }

        @Override
        public void actionPerformed(ActionEvent actionEvent)
        {
            for(Kula k:listaKul)
            {
                k.updateCoordinates();
                k.rozwiazKolizje();
            }
            repaint();
        }

        @Override
        public void mouseWheelMoved(MouseWheelEvent mouseWheelEvent)
        {
            size += - 2 * (mouseWheelEvent.getWheelRotation());
            if (size <= 50)
                size = 50;
        }

        @Override
        public void mouseDragged(MouseEvent mouseEvent)
        {
            listaKul.add(new Kula(mouseEvent.getX(),mouseEvent.getY(),size));
            repaint();

        }

        @Override
        public void mouseMoved(MouseEvent mouseEvent) {

        }
    }

    private class Kula
    {
        public int size, mass;
        public float radius;
        public Vector2d position;
        public Vector2d velocity;
        public Color kolor;
        private final int MAX_SPEED = 15;

        public Kula(int x, int y, int size)
        {
            this.velocity = new Vector2d(0,0);
            this.position = new Vector2d(x,y);
            this.size = size;
            this.mass = size;
            this.radius = size/2.0f;

            kolor = new Color((float) Math.random(),(float) Math.random(),(float) Math.random());

            float tempX, tempY;

            Random gen = new Random();

            tempX = gen.nextFloat()*(MAX_SPEED*2)-MAX_SPEED;
            tempY = gen.nextFloat()*(MAX_SPEED*2)-MAX_SPEED;

            if(tempX == 0 || tempY == 0)
            {
                velocity.setX(MAX_SPEED);
                velocity.setY(MAX_SPEED);
            }
            else
            {
                velocity.setX(tempX);
                velocity.setY(tempY);
            }

            System.out.println("Vx = " + velocity.getX() + ", Vy = " + velocity.getY() + ", V= " + velocity.getLength() + ", mass = " + mass + ", size = "+ size +", radius = " + radius);
        }

        public int getSize() {
            return size;
        }

        public float getMass() {
            return mass;
        }

        public float getRadius() {
            return radius;
        }

        public void updateCoordinates()
        {
            position.setX(position.getX() + velocity.getX());
            position.setY(position.getY() + velocity.getY());
        }


        public void rozwiazKolizje()
        {
            //kolizje ze ścianami
            if (position.getX() <= 0 || position.getX() + size >= getWidth())
                velocity.setX(-velocity.getX()*0.95f) ;
            if (position.getY() <= 0 || position.getY() + size  >= getHeight())
                velocity.setY(-velocity.getY()*0.95f);

            if(position.getX() <= 0)
                position.setX(position.getX()+2);
            if(position.getY() <= 0)
                position.setY(position.getY()+2);

            if(position.getX() + size >= getWidth())
                position.setX(position.getX()-2);
            if(position.getY() + size  >= getHeight())
                position.setY(position.getY()-2);

            //kolizje z kulkami
            double odlegloscX,odlegloscY;
            for(int i = 0; i < listaKul.size(); i++)
            {
                Kula A = listaKul.get(i);
                for(int j = i+1; j < listaKul.size(); j++)
                {
                    Kula B = listaKul.get(j);
                    odlegloscX = (A.position.getX()+A.radius)-(B.position.getX()+B.radius);
                    odlegloscY = (A.position.getY()+A.radius)-(B.position.getY()+B.radius);
                    double odlegloscKwadrat = odlegloscX*odlegloscX + odlegloscY*odlegloscY;
                    if(odlegloscKwadrat <= (A.radius+B.radius)*(A.radius+B.radius))
                    {
                        double xVel = B.velocity.getX() - A.velocity.getX();
                        double yVel = B.velocity.getY() - A.velocity.getY();
                        double iloczynSkalarny = odlegloscX*xVel + odlegloscY*yVel;

                        if(iloczynSkalarny > 0)
                        {
                            double skalaKolizji = iloczynSkalarny/odlegloscKwadrat;
                            double kolizjaX = odlegloscX * skalaKolizji * 0.95;
                            double kolizjaY = odlegloscY * skalaKolizji * 0.95;
                            double sumaMas = A.mass + B.mass;
                            double wagaKolizjiA = 2 * B.mass / sumaMas;
                            double wagaKolizjiB = 2 * A.mass / sumaMas;
                            A.velocity.setX(A.velocity.getX()+((float) (wagaKolizjiA * kolizjaX)));
                            A.velocity.setY(A.velocity.getY()+((float) (wagaKolizjiA * kolizjaY)));
                            B.velocity.setX(B.velocity.getX()-((float) (wagaKolizjiB * kolizjaX)));
                            B.velocity.setY(B.velocity.getY()-((float) (wagaKolizjiB * kolizjaY)));
                        }
                    }
                }
            }
        }
    }
}
