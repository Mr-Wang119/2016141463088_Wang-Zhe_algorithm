import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

public class HuffmanFrame {

    private static File file_in;
    private static File file_out;
    private static int compression=-1;



    public static void main(String[] args){
        JFrame jf = new JFrame("霍夫曼压缩");
        jf.setSize(700,400);
        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel jp = new JPanel();
        //jp.setBorder(new EmptyBorder(5,5,5,5));
        jf.add(jp);

        placeComponents(jp);
        jf.setVisible(true);
    }

    private static void  JProgressBar(boolean compress,Huffman h){
        final JProgressBar progressBar=new JProgressBar();
        JFrame jf_pr = new JFrame("正在进行");
        jf_pr.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        jf_pr.setBounds(100, 100, 250, 100);
        JPanel contentPane=new JPanel();
        contentPane.setBorder(new EmptyBorder(5,5,5,5));
        jf_pr.setContentPane(contentPane);
        contentPane.setLayout(new FlowLayout(FlowLayout.CENTER,5,5));

        progressBar.setStringPainted(true);
        new Thread(){
            public void run(){
                if(compress){
                    h.compress(progressBar);
                    progressBar.setForeground(Color.green);
                    progressBar.setString("压缩完成！");
                }
                else {
                    h.expand(progressBar);
                    progressBar.setForeground(Color.green);
                    progressBar.setString("解压完成！");
                }

            }
        }.start();
        contentPane.add(progressBar);
        jf_pr.setVisible(true);
    }

    private static void placeComponents(JPanel jp) {
        BorderLayout bl = new BorderLayout(5,5);
        jp.setLayout(bl);

        JLabel input = new JLabel("欲处理的文件：");
        JLabel output = new JLabel("生成文件的保存位置：");
        JButton jb_in = new JButton("浏览...");
        JButton jb_out = new JButton("浏览...");
        JTextField jt_in = new JTextField(30);
        JTextField jt_out = new JTextField(30);
        JButton jb = new JButton("开始");

        JPanel jp_radio = new JPanel();
        jp_radio.setBorder(BorderFactory.createTitledBorder("请选择所要进行的操作"));
        jp_radio.setLayout(new GridLayout(1,2));
        JRadioButton jr_in = new JRadioButton("压缩");
        JRadioButton jr_out = new JRadioButton("解压");
        jp_radio.add(jr_in);
        jp_radio.add(jr_out);
        ButtonGroup group = new ButtonGroup();
        group.add(jr_in);
        group.add(jr_out);
        jp.add(jp_radio,BorderLayout.NORTH);


        JPanel jp_file = new JPanel();
        jp_file.setLayout(new GridLayout(2,1));
        JPanel jp_file_in = new JPanel();
        jp_file_in.add(input);
        jp_file_in.add(jt_in);
        jp_file_in .add(jb_in);
        JPanel jp_file_out = new JPanel();
        jp_file_out.add(output);
        jp_file_out.add(jt_out);
        jp_file_out.add(jb_out);
        jp_file.add(jp_file_in);
        jp_file.add(jp_file_out);
        jp.add(jp_file,BorderLayout.CENTER);

        JPanel jp_button = new JPanel();
        jp_button.add(jb);
        jp.add(jp_button,BorderLayout.SOUTH);

        jb_in.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser jf = new JFileChooser();
                jf.setFileSelectionMode(JFileChooser.FILES_ONLY );
                jf.setDialogTitle("选择欲处理的文件");
                jf.showDialog(new JLabel(), "选择");
                file_in = jf.getSelectedFile();
                jt_in.setText(file_in.getAbsolutePath());
            }
        });


        jb_out.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser jf = new JFileChooser();
                jf.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY );
                jf.setDialogTitle("选择欲保存到的文件夹");
                jf.showDialog(new JLabel(), "选择");
                file_out = jf.getSelectedFile();
                jt_out.setText(file_out.getPath());
            }
        });

        jr_in.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                compression=1;
            }
        });

        jr_out.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                compression=0;
            }
        });

        jb.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String address_in = jt_in.getText();
                String address_out = jt_out.getText();
                if(address_in.isEmpty())
                    JOptionPane.showMessageDialog(null, "请选择待处理文件！");
                else if(address_out.isEmpty())
                    JOptionPane.showMessageDialog(null, "请选择保存到的文件夹！");
                else{
                    File file_in = new File(address_in);
                    String[] fileName_p = file_in.getName().split("\\.");
                    String fileName = fileName_p[0]+"out."+fileName_p[1];
                    if(file_in.exists()&&file_out.isDirectory()){
                        File file_out = new File(address_out+"//"+fileName);
                        if(!file_out.exists()){
                            try{
                                file_out.createNewFile();
                            }
                            catch(IOException f){
                                JOptionPane.showMessageDialog(null,
                                        "目标文件创建失败，请检查目录是否为只读！");
                                return;
                            }
                        }
                        Huffman h = new Huffman(file_in,file_out);
                        if(compression==-1)
                            JOptionPane.showMessageDialog(null, "请选择压缩或解压！");
                        else if(compression==1) {

                            JProgressBar(true,h);
                        }
                        else
                            JProgressBar(false,h);
                    }
                    else{
                        JOptionPane.showMessageDialog(null, "读取文件失败，请检查欲处理文件是否存在！");
                    }
                }
            }
        });
    }

}
