
import javax.swing.*;
import java.io.*;
import java.util.NoSuchElementException;


public class Huffman {
    private static int R = 256;
    private  File file_in;
    private  File file_out;
    private BufferedInputStream in;
    private static BufferedOutputStream out;

    private static int buffer_in;
    private static int buffer_out;
    private static int n_in;
    private static int n_out;
    private static final int EOF = -1;    // end of file

    private static class Node implements Comparable<Node>{
        private char ch;
        private int freq;
        private final Node left,right;

        Node(char ch, int freq,Node left,Node right){
            this.ch=ch;
            this.freq=freq;
            this.left=left;
            this.right=right;
        }

        public boolean isLeaf(){
            return left==null&&right==null;
        }

        @Override
        public int compareTo(Node that) {
            return this.freq-that.freq;
        }
    }

    public Huffman(File file_in,File file_out){
        this.file_in=file_in;
        this.file_out=file_out;
        try{
            in = new BufferedInputStream(new FileInputStream(file_in));
            out = new BufferedOutputStream(new FileOutputStream(file_out));
            fillBuffer();
        }
        catch(Exception e){
            JOptionPane.showMessageDialog(null, "读取文件时出错！");
        }
    }

    private void fillBuffer() {
        try {
            buffer_in = in.read();
            n_in = 8;
        }
        catch (IOException e) {
            System.out.println("EOF");
            buffer_in = EOF;
            n_in = EOF;
        }
    }

    private boolean isEmpty() {
        return buffer_in == EOF;
    }


    private char readChar() {
        if (isEmpty()) throw new NoSuchElementException("Reading from empty input stream");

        if (n_in == 8) {
            int x = buffer_in;
            fillBuffer();
            return (char) (x & 0xff);
        }
        int x = buffer_in;
        x <<= (8 - n_in);
        int oldN = n_in;
        fillBuffer();
        //if(isEmpty()) throw new NoSuchElementException("Reading from empty input stream");
        n_in = oldN;
        x |= (buffer_in >>> n_in);
        return (char) (x & 0xff);

    }

    private int readInt() {
        int x = 0;
        for (int i = 0; i < 4; i++) {
            char c = readChar();
            x <<= 8;
            x |= c;
        }
        return x;
    }

    private boolean readBoolean() {
        //if (isEmpty()) throw new NoSuchElementException("Reading from empty input stream");
        n_in--;
        boolean bit = ((buffer_in >> n_in) & 1) == 1;
        if (n_in == 0) fillBuffer();
        return bit;
    }

    private void clearBuffer() {
        if (n_out == 0) return;
        if (n_out > 0) buffer_out <<= (8 - n_out);
        try {
            out.write(buffer_out);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        n_out = 0;
        buffer_out = 0;
    }


    private void writeBit(boolean bit) {
        buffer_out <<= 1;
        if (bit) buffer_out |= 1;

        n_out++;
        if (n_out == 8) clearBuffer();
    }

    private void writeByte(int x) {
        assert x >= 0 && x < 256;

        if (n_out == 0) {
            try {
                out.write(x);
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }

        for (int i = 0; i < 8; i++) {
            boolean bit = ((x >>> (8 - i - 1)) & 1) == 1;
            writeBit(bit);
        }
    }


//    private void write(char x) {
//        if (x < 0 || x >= 256) throw new IllegalArgumentException("Illegal 8-bit char = " + x);
//        writeByte(x);
//    }

    private void flush() {
        clearBuffer();
        try {
            out.flush();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void close_out() {
        flush();
        try {
            out.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void write(int x, int r) {
        if (r == 32) {
            write(x);
            return;
        }
        if (r < 1 || r > 32)        throw new IllegalArgumentException("Illegal value for r = " + r);
        if (x < 0 || x >= (1 << r)) throw new IllegalArgumentException("Illegal " + r + "-bit char = " + x);
        for (int i = 0; i < r; i++) {
            boolean bit = ((x >>> (r - i - 1)) & 1) == 1;
            writeBit(bit);
        }
    }

    private String readString() {
        if (isEmpty()) throw new NoSuchElementException("Reading from empty input stream");

        StringBuilder sb = new StringBuilder();
        while (!isEmpty()) {
            char c = readChar();
            sb.append(c);
        }
        return sb.toString();
    }


    private void write(boolean x) {
        writeBit(x);
    }

    private void write(int x) {
        writeByte((x >>> 24) & 0xff);
        writeByte((x >>> 16) & 0xff);
        writeByte((x >>>  8) & 0xff);
        writeByte((x >>>  0) & 0xff);
    }


    public void expand(JProgressBar progressBar){
        Node root = readTrie();
        int N = readInt();
        for(int i=0;i<N;i++){
            progressBar.setValue(i*100/N+1);
            Node x = root;
            while(!x.isLeaf()){
                if(readBoolean())
                    x=x.right;
                else
                    x=x.left;
            }
            write(x.ch,8);
        }
        close_out();
        //JOptionPane.showMessageDialog(null, "解压完成！");
    }

//    private String[] buildCode(Node root){
//        String[] st = new String[R];
//        buildCode(st,root,"");
//        return st;
//    }

    private static void buildCode(String[] st, Node x, String s){
        if(x.isLeaf()){
            st[x.ch] = s;
            return;
        }
        buildCode(st,x.left,s+'0');
        buildCode(st,x.right,s+'1');
    }

    private Node buildTrie(int[] freq){
        MinPQ<Node> pq = new MinPQ<Node>();
        for(char c=0;c<R;c++)
            if(freq[c]>0)
                pq.insert(new Node(c,freq[c],null,null));
        if (pq.size() == 1) {
            if (freq['\0'] == 0) pq.insert(new Node('\0', 0, null, null));
            else                 pq.insert(new Node('\1', 0, null, null));
        }
        while(pq.size()>1){
            Node x=pq.delMin();
            Node y = pq.delMin();
            Node parent = new Node('\0',x.freq+y.freq,x,y);
            pq.insert(parent);
        }
        return pq.delMin();
    }

    private void writeTrie(Node x){
        if(x.isLeaf()){
            write(true);
            write(x.ch,8);
            return;
        }
        write(false);
        writeTrie(x.left);
        writeTrie(x.right);
    }

    private Node readTrie(){
        if(readBoolean())
            return new Node(readChar(),-1,null,null);
        return new Node('\0',-1,readTrie(),readTrie());
    }

    public void compress(JProgressBar progressBar){
        String s=readString();
        char[] input = s.toCharArray();
        int[] freq = new int[R];
        for(int i=0;i<input.length;i++) {
            freq[input[i]]++;
            progressBar.setValue(i*50/input.length);
        }
        Node root = buildTrie(freq);
        String[] st = new String[R];
        buildCode(st,root,"");
        writeTrie(root);
        write(input.length);
        for(int i=0;i<input.length;i++){
            String code = st[input[i]];
            progressBar.setValue(51+i*50/input.length);
            for(int j=0;j<code.length();j++)
                if(code.charAt(j)=='1')
                    write(true);
            else write(false);
        }
        close_out();
        //JOptionPane.showMessageDialog(null, "压缩完成！");
    }

}
