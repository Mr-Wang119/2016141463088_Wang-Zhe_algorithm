import edu.princeton.cs.algs4.In;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class SPFrame extends JFrame{

    private static EdgeWeightedDigraph dg;
    private static int points[] = {-1,-1};
    private JPanel jp_map;
    private double line_n,row_n,line_dis,row_dis;
    private int[][] coords;
    private Graphics g;
    private double dist = 0;
    private int counter = 0;

    public static void main(String[] args){
        JFrame jf = new JFrame("最短路径");
        jf.setSize(1400,800);
        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel jp = new JPanel();
        BorderLayout bl = new BorderLayout(5,5);
        jp.setLayout(bl);

        jf.add(jp);

        SPFrame spf = new SPFrame();
        spf.placeComponents(jp,jf);
//        jf.setVisible(true);
    }


    private void readDigraph(String choice,JPanel jp,JTextArea ta){
        switch(choice){
            case "小城市": dg = new EdgeWeightedDigraph(new In("smallEWG.txt"));break;
            case "大城市": dg = new EdgeWeightedDigraph(new In("mediumEWG.txt"));break;
            default: return;
        }
        int v = dg.V();
        line_n = Math.sqrt(dg.V()/15.0)*5;
        row_n = Math.sqrt(dg.V()/15.0)*3;
        line_dis = 1000/line_n;
        row_dis = 600/row_n;
        coords = new int[dg.V()][2];

        if(jp_map!=null) {
            jp.remove(jp_map);
            jp.updateUI();
        }
        jp_map = new JPanel();
//        {
//            public void print(Graphics g){
//                for(int i = 0;i<line_n;i++){
//                    for(int j = 0; j<row_n;j++){
//                        g.fillOval(400+j*(int)line_dis,200+i*(int)row_dis,5,5);
//
//                    }
//                }
//            }
//        };
//        jp_map.updateUI();
        jp_map.setSize(new Dimension(1400,800));
        jp.add(jp_map,BorderLayout.CENTER);
        g = jp.getGraphics();
        //jp.updateUI();
        paint(g);

        jp_map.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                paint(g);
                if(e.getButton()==e.BUTTON1) {
                    if(points[0]==-1){
                        double x=e.getX()-coords[0][0]+45;
                        double y=e.getY()-coords[0][1]+45;
                        int x_point = (int)(x/line_dis);
                        int y_point = (int)(y/row_dis);
                        points[0]=((int)line_n+1)*y_point+x_point;
                        g.setColor(new Color(0,175,255));
                        g.fillOval(coords[points[0]][0]-8,coords[points[0]][1]-8,15,15);
                    }
                    else if(points[1]==-1){
                        int x=e.getX()-coords[0][0]+45;
                        int y=e.getY()-coords[0][1]+45;
                        int x_point = (int)(x/line_dis);
                        int y_point = (int)(y/row_dis);
                        points[1]=((int)line_n+1)*y_point+x_point;
                        g.setColor(new Color(0,175,255));

                        DijstraSP sp = new DijstraSP(dg,points[0]);
                        if(sp.hasPathTo(points[1])) {
                            ta.setText("存在路径：");
                            paint(g);
                            g.setColor(new Color(0,175,255));
                            g.fillOval(coords[points[0]][0]-8,coords[points[0]][1]-8,15,15);
                            g.fillOval(coords[points[1]][0]-8,coords[points[1]][1]-8,15,15);
                            for (DirectedEdge b : sp.pathTo(points[1])) {
                                ta.append(b.toString()+"   ");
                                //paint(g);
                                //Graphics2D g = (Graphics2D)jp_map.getGraphics();
                                counter++;
                                dist+=b.weight();
                                g.setColor(new Color(0, 0, 0));
                                g.drawLine(coords[b.from()][0], coords[b.from()][1], coords[b.to()][0], coords[b.to()][1]);
                                g.setColor(new Color(192,222,225));
                                g.fillOval(coords[b.from()][0]-8,coords[b.from()][1]-8,15,15);
                            }
                            ta.append("。\r\n路径长度为"+dist+"，共经过"+(counter-1)+"个结点。");
                            g.setColor(new Color(0,175,255));
                            g.fillOval(coords[points[0]][0]-8,coords[points[0]][1]-8,15,15);
                            g.fillOval(coords[points[1]][0]-8,coords[points[1]][1]-8,15,15);
                            points[0]=-1;
                            points[1]=-1;
                            dist=0;
                            counter=0;
                        }
                        else {
                            ta.setText("无路。");
                            points[0] = -1;
                            points[1] = -1;
                            counter=0;
                        }

                    }
                }

            }

            @Override
            public void mousePressed(MouseEvent e) {
            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });

    }


    private void placeComponents(JPanel jp,JFrame jf){

        JLabel jl = new JLabel("城市：");
        JPanel jp_map = new JPanel();
        jp_map.setLayout(new FlowLayout(FlowLayout.CENTER,5,5));
        jp_map.add(jl);
        JComboBox cb = new JComboBox();
        cb.addItem("请选择");
        cb.addItem("小城市");
        cb.addItem("大城市");
        jp_map.add(cb);
        jp.add(jp_map,BorderLayout.EAST);
        jf.setVisible(true);


        JPanel jp_way = new JPanel();
        JLabel jl_way = new JLabel("路径：");
        jp_way.add(jl_way);
        JTextArea ta = new JTextArea(2,100);
        ta.setEditable(false);
        ta.setText("若未显示地图，请点击空白处");
        jp_way.add(ta);
        jp.add(jp_way,BorderLayout.SOUTH);

        cb.addItemListener(new ItemListener(){
            public void itemStateChanged(ItemEvent event){

                switch(event.getStateChange()){
                    case ItemEvent.SELECTED:readDigraph(event.getItem().toString(),jp,ta);break;
                }
            }
        });



    }



    public void paint(Graphics g){

        super.paint(g);
        g.setColor(new Color(64,255,173));

        int temp=0;
        for(int i = 0;i<(int)row_n+1;i++){
            for(int j = 0; j<(int)line_n+1;j++){
                g.fillOval(200+j*(int)line_dis,150+i*(int)row_dis,15,15);
                coords[temp][0]=208+j*(int)line_dis;
                coords[temp][1]=158+i*(int)row_dis;
                temp++;
                if(temp==dg.V())
                    break;
            }
            if(temp==dg.V())
                break;
        }
        g.setColor(new Color(255,255,255));
        for(int i=0;i<dg.V();i++){
            for(DirectedEdge b:dg.adj(i)){
                g.drawLine(coords[i][0],coords[i][1],coords[b.to()][0],coords[b.to()][1]);
            }
        }

    }


}
