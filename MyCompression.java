import java.awt.*;
import java.awt.image.*;
import java.io.*;
import javax.swing.*;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.io.StringReader;
import java.util.Arrays;

public class MyCompression {

    public double[][] _data;
    int[] _label;
    public double[][] _centroids;
    int _nrows, _ndims;
    int _numClusters;
    double[][] val_img_r = new double[288][352];
    double[][] val_img_g = new double[288][352];
    double[][] val_img_b = new double[288][352];


    public void display_color(String param0 , BufferedImage img1) throws  IOException{

        File _file= null;
        _file= new File ("Z:\\CSCI 576\\2018_Fall_HW2_Images\\"+param0);
        byte[] image= new byte[352*288*3];
        FileInputStream ip= new FileInputStream(_file);
        ip.read(image);

        BufferedImage img0= new BufferedImage(352,288,BufferedImage.TYPE_INT_RGB);


        int ind1 = 0;
        for(int y1 = 0; y1 < 288; y1++){

            for(int x1 = 0; x1 < 352; x1++){


                int pix1 = 0xff000000 | ((image[ind1] & 0xff) << 16) | ((image[ind1+(352*288)] & 0xff) << 8) | (image[ind1 +(2*352*288)] & 0xff);

                img0.setRGB(x1,y1,pix1);
                ind1++;
            }
        }

        JFrame frame = new JFrame();
        GridBagLayout gLayout = new GridBagLayout();
        frame.getContentPane().setLayout(gLayout);

        JLabel lbText1 = new JLabel("Original image");
        lbText1.setHorizontalAlignment(SwingConstants.CENTER);
        JLabel lbText2 = new JLabel("Image after compression");
        lbText2.setHorizontalAlignment(SwingConstants.CENTER);
        JLabel lbIm1 = new JLabel(new ImageIcon(img0));
        JLabel lbIm2 = new JLabel(new ImageIcon(img1));

        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.CENTER;
        c.weightx = 0.5;
        c.gridx = 0;
        c.gridy = 0;
        frame.getContentPane().add(lbText1, c);

        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.CENTER;
        c.weightx = 0.5;
        c.gridx = 1;
        c.gridy = 0;
        frame.getContentPane().add(lbText2, c);

        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 1;
        frame.getContentPane().add(lbIm1, c);

        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 1;
        c.gridy = 1;
        frame.getContentPane().add(lbIm2, c);

        frame.pack();
        frame.setVisible(true);

    }


    public void display_gray(String param0, BufferedImage img1) throws IOException{

        File _file= null;
        _file= new File ("Z:\\CSCI 576\\2018_Fall_HW2_Images\\" +param0);
        byte[] image= new byte[352*288*3];
        FileInputStream ip= new FileInputStream(_file);
        ip.read(image);

        BufferedImage img0= new BufferedImage(352,288,BufferedImage.TYPE_BYTE_GRAY);
        WritableRaster raster0 = img0.getRaster();
        raster0.setDataElements(0,0,352,288,image);

        JFrame frame = new JFrame();
        GridBagLayout gLayout = new GridBagLayout();
        frame.getContentPane().setLayout(gLayout);

        JLabel lbText1 = new JLabel("Original image");
        lbText1.setHorizontalAlignment(SwingConstants.CENTER);
        JLabel lbText2 = new JLabel("Image after compression");
        lbText2.setHorizontalAlignment(SwingConstants.CENTER);
        JLabel lbIm1 = new JLabel(new ImageIcon(img0));
        JLabel lbIm2 = new JLabel(new ImageIcon(img1));

        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.CENTER;
        c.weightx = 0.5;
        c.gridx = 0;
        c.gridy = 0;
        frame.getContentPane().add(lbText1, c);

        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.CENTER;
        c.weightx = 0.5;
        c.gridx = 1;
        c.gridy = 0;
        frame.getContentPane().add(lbText2, c);

        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 1;
        frame.getContentPane().add(lbIm1, c);

        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 1;
        c.gridy = 1;
        frame.getContentPane().add(lbIm2, c);

        frame.pack();
        frame.setVisible(true);
    }


    public void clustering(int numClusters) {
        _numClusters = numClusters;
        _centroids = new double[_numClusters][];

        ArrayList idx = new ArrayList();
        for (int i = 0; i < numClusters; i++) {
            int c;
            do {
                c = (int) (Math.random() * _nrows);
            } while (idx.contains(c)); // avoid duplicates
            idx.add(c);


            _centroids[i] = new double[_ndims];
            for (int j = 0; j < _ndims; j++)
                _centroids[i][j] = _data[c][j];
        }
        System.out.println("selected random centroids");


        double[][] c1 = _centroids;
        double threshold = 0.001;
        int round = 0;
        System.out.println("Centroids");
        while (true) {
            _centroids = c1;
            _label = new int[_nrows];
            for (int i = 0; i < _nrows; i++) {
                _label[i] = closest(_data[i]);
            }
            c1 = updateCentroids();
            round++;
            if(converge(_centroids, c1, threshold))
                break;

        }
        System.out.println("Centroid updated");

    }

    private int closest(double[] v) {
        double mindist = dist(v, _centroids[0]);
        int label = 0;
        for (int i = 1; i < _numClusters; i++) {
            double t = dist(v, _centroids[i]);
            if (mindist > t) {
                mindist = t;
                label = i;
            }
        }
        return label;
    }


    private double[][] updateCentroids() {
        double[][] newc = new double[_numClusters][];
        int[] counts = new int[_numClusters];
        for (int i = 0; i < _numClusters; i++) {
            counts[i] = 0;
            newc[i] = new double[_ndims];
            for (int j = 0; j < _ndims; j++)
                newc[i][j] = 0;
        }


        for (int i = 0; i < _nrows; i++) {
            int cn = _label[i];
            for (int j = 0; j < _ndims; j++) {
                newc[cn][j] += _data[i][j];
            }
            counts[cn]++;
        }

        for (int i = 0; i < _numClusters; i++) {
            for (int j = 0; j < _ndims; j++) {
                newc[i][j] /= counts[i];
            }
        }

        return newc;
    }

    private boolean converge(double[][] c1, double[][] c2, double threshold) {
        // c1 and c2 are two sets of centroids
        double maxv = 0;
        for (int i = 0; i < _numClusters; i++) {
            double d = dist(c1[i], c2[i]);
            if (maxv < d)
                maxv = d;
        }

        if (maxv < threshold)
            return true;
        else
            return false;

    }


    private double dist(double[] v1, double[] v2) {
        double sum = 0;
        for (int i = 0; i < _ndims; i++) {
            double d = v1[i] - v2[i];
            sum += d * d;
        }
        return Math.sqrt(sum);
    }


    public double[][] BytetoInt(String param0) throws IOException{


        File _file= null;
        _file= new File ("Z:\\CSCI 576\\2018_Fall_HW2_Images\\"+param0);
        byte[] image= new byte[352*288*3];
        FileInputStream ip= new FileInputStream(_file);
        ip.read(image);



        int k = 0;
        double[][] val_img = new double[288][352];
        for (int i = 0; i < 288; i++) {
            for (int j = 0; j < 352; j++) {
                val_img[i][j] = (0xff & image[k]);
                k++;
            }
        }
        return val_img;
    }


    public void BytetoInt_color(String param0)throws IOException {


        File _file= null;
        _file= new File ("Z:\\CSCI 576\\2018_Fall_HW2_Images\\"+param0);
        byte[] image= new byte[352*288*3];
        FileInputStream ip= new FileInputStream(_file);
        ip.read(image);





        int k = 0;
        //double[][] val_img_rgb = new double[288][352];

        for (int i = 0; i < 288; i++) {
            for (int j = 0; j < 352; j++) {
                val_img_r[i][j] = (0xff & image[k]);
                k++;
            }
        }
        for (int i = 288; i < (288 * 2); i++) {
            for (int j = 352; j < (352 * 2); j++) {
                //System.out.println(image[k]);
                val_img_g[i - 288][j - 352] = (0xff & image[k]);
                k++;
            }
        }
        for (int i = (288 * 2); i < (288 * 3); i++) {
            for (int j = (352 * 2); j < (352 * 3); j++) {
                val_img_b[i - (288 * 2)][j - (352 * 2)] = (0xff & image[k]);
                k++;
            }
        }

    }







    public void two_cross_two_pixels(double[][] val_img){

        _data = new double[352 * 288 / 4][4];
        int c=0;
        int d=0;
        int e=0;
        for (e = 0; e < 288; e=e+2) {
            for (d = 0; d < 352; d++) {
                _data[c][0]= val_img[e][d];
                d++;
                _data[c][1]= val_img[e][d];
                e++;
                d--;
                _data[c][2]= val_img[e][d];
                d++;
                _data[c][3]= val_img[e][d];
                c++;
                e--;

            }

            _ndims=4;
            _nrows=352*288/4;





    }}


    public void two_cross_two_pixels_color(){

        _data = new double[352 * 288 / 4][12];
        int c=0;
        int d=0;
        int e=0;

        for (e = 0; e < 288; e=e+2) {
            for (d = 0; d < 352; d++) {

                _data[c][0]= val_img_r[e][d];
                d++;
                _data[c][1]= val_img_r[e][d];
                e++;
                d--;
                _data[c][2]= val_img_r[e][d];
                d++;
                _data[c][3]= val_img_r[e][d];
                e--;
                d--;
                _data[c][4]= val_img_g[e][d];
                d++;
                _data[c][5]= val_img_g[e][d];
                e++;
                d--;
                _data[c][6]= val_img_g[e][d];
                d++;
                _data[c][7]= val_img_g[e][d];
                e--;
                d--;
                _data[c][8]= val_img_b[e][d];
                d++;
                _data[c][9]= val_img_b[e][d];
                e++;
                d--;
                _data[c][10]= val_img_b[e][d];
                d++;
                _data[c][11]= val_img_b[e][d];
                e--;
                c++;

            }
            }

        _ndims=12;
        _nrows=352*288/4;


    }



    public void four_cross_four_pixels(double[][] val_img)

    {
        _data = new double[352 * 288 / 16][16];
        int c=0;
        int d=0;
        int e=0;
        for (e = 0; e < 288; e=e+4) {
            for (d = 0; d < 352; d++) {
                _data[c][0]= val_img[e][d];
                d++;
                _data[c][1]= val_img[e][d];
                d++;
                _data[c][2]= val_img[e][d];
                d++;
                _data[c][3]= val_img[e][d];
                e++;
                d=d-3;
                _data[c][4]= val_img[e][d];
                d++;
                _data[c][5]= val_img[e][d];
                d++;
                _data[c][6]= val_img[e][d];
                d++;
                _data[c][7]= val_img[e][d];
                e++;
                d=d-3;
                _data[c][8]= val_img[e][d];
                d++;
                _data[c][9]= val_img[e][d];
                d++;
                _data[c][10]= val_img[e][d];
                d++;
                _data[c][11]= val_img[e][d];
                e++;
                d=d-3;
                _data[c][12]= val_img[e][d];
                d++;
                _data[c][13]= val_img[e][d];
                d++;
                _data[c][14]= val_img[e][d];
                d++;
                _data[c][15]= val_img[e][d];
                c++;
                e=e-3;

            }

            _ndims=16;
            _nrows=352*288/16;




        }}


        public void four_cross_four_pixels_color(){

            _data = new double[352 * 288 / 16][48];
            int c=0;
            int d=0;
            int e=0;
            for (e = 0; e < 288; e=e+4) {
                for (d = 0; d < 352; d++) {

                    _data[c][0]= val_img_r[e][d];
                    d++;
                    _data[c][1]= val_img_r[e][d];
                    d++;
                    _data[c][2]= val_img_r[e][d];
                    d++;
                    _data[c][3]= val_img_r[e][d];
                    e++;
                    d=d-3;
                    _data[c][4]= val_img_r[e][d];
                    d++;
                    _data[c][5]= val_img_r[e][d];
                    d++;
                    _data[c][6]= val_img_r[e][d];
                    d++;
                    _data[c][7]= val_img_r[e][d];
                    e++;
                    d=d-3;
                    _data[c][8]= val_img_r[e][d];
                    d++;
                    _data[c][9]= val_img_r[e][d];
                    d++;
                    _data[c][10]= val_img_r[e][d];
                    d++;
                    _data[c][11]= val_img_r[e][d];
                    e++;
                    d=d-3;
                    _data[c][12]= val_img_r[e][d];
                    d++;
                    _data[c][13]= val_img_r[e][d];
                    d++;
                    _data[c][14]= val_img_r[e][d];
                    d++;
                    _data[c][15]= val_img_r[e][d];
                    e=e-3;
                    d=d-3;
                    _data[c][16]= val_img_g[e][d];
                    d++;
                    _data[c][17]= val_img_g[e][d];
                    d++;
                    _data[c][18]= val_img_g[e][d];
                    d++;
                    _data[c][19]= val_img_g[e][d];
                    e++;
                    d=d-3;
                    _data[c][20]= val_img_g[e][d];
                    d++;
                    _data[c][21]= val_img_g[e][d];
                    d++;
                    _data[c][22]= val_img_g[e][d];
                    d++;
                    _data[c][23]= val_img_g[e][d];
                    e++;
                    d=d-3;
                    _data[c][24]= val_img_g[e][d];
                    d++;
                    _data[c][25]= val_img_g[e][d];
                    d++;
                    _data[c][26]= val_img_g[e][d];
                    d++;
                    _data[c][27]= val_img_g[e][d];
                    e++;
                    d=d-3;
                    _data[c][28]= val_img_g[e][d];
                    d++;
                    _data[c][29]= val_img_g[e][d];
                    d++;
                    _data[c][30]= val_img_g[e][d];
                    d++;
                    _data[c][31]= val_img_g[e][d];
                    e=e-3;
                    d=d-3;
                    _data[c][32]= val_img_b[e][d];
                    d++;
                    _data[c][33]= val_img_b[e][d];
                    d++;
                    _data[c][34]= val_img_b[e][d];
                    d++;
                    _data[c][35]= val_img_b[e][d];
                    e++;
                    d=d-3;
                    _data[c][36]= val_img_b[e][d];
                    d++;
                    _data[c][37]= val_img_b[e][d];
                    d++;
                    _data[c][38]= val_img_b[e][d];
                    d++;
                    _data[c][39]= val_img_b[e][d];
                    e++;
                    d=d-3;
                    _data[c][40]= val_img_b[e][d];
                    d++;
                    _data[c][41]= val_img_b[e][d];
                    d++;
                    _data[c][42]= val_img_b[e][d];
                    d++;
                    _data[c][43]= val_img_b[e][d];
                    e++;
                    d=d-3;
                    _data[c][44]= val_img_b[e][d];
                    d++;
                    _data[c][45]= val_img_b[e][d];
                    d++;
                    _data[c][46]= val_img_b[e][d];
                    d++;
                    _data[c][47]= val_img_b[e][d];
                    c++;
                    e=e-3;
                }


                }

                _ndims=48;
                _nrows=352*288/16;
    }





    public void two_pixels(double[][] val_img) {
        int c = 0;
        int d = 0;
        int e = 0;
        _data = new double[352 * 288 / 2][2];


        for (e = 0; e < 288; e++) {
            for (d = 0; d < 352; d++) {
                _data[c][0] = val_img[e][d];
                d++;
                _data[c][1] = val_img[e][d];
                c++;
            }

            _ndims=2;
            _nrows=352*288/2;


        }

    }


    public void two_pixels_color(){
        int c = 0;
        int d = 0;
        int e = 0;
        _data = new double[352 * 288 / 2][6];

        for (e = 0; e < 288; e++) {
            for (d = 0; d < 352; d++) {
                _data[c][0] = val_img_r[e][d];
                d++;
                _data[c][1] = val_img_r[e][d];
                d--;
                _data[c][2] = val_img_g[e][d];
                d++;
                _data[c][3] = val_img_g[e][d];
                d--;
                _data[c][4] = val_img_b[e][d];
                d++;
                _data[c][5] = val_img_b[e][d];
                c++;
            }

            _ndims=6;
            _nrows= 352*288/2;






    }}



    public void two_recon_color(String param0) throws IOException{

        int len= _label.length;
        System.out.println("Length of label array is "+ len);
        JFrame frame;
        int l=0;
        byte[] fin_image = new byte[352 * 288 * 3];
        for(int p=0; p<(352*288/2);p++)
        {

            fin_image[l]= (byte) _centroids[_label[p]][0];
            l++;
            fin_image[l]= (byte) _centroids[_label[p]][1];
            l--;
            l=l+(352*288);
            fin_image[l]= (byte) _centroids[_label[p]][2];
            l++;
            fin_image[l]= (byte) _centroids[_label[p]][3];
            l--;
            l=l+(352*288);
            fin_image[l]= (byte) _centroids[_label[p]][4];
            l++;
            fin_image[l]= (byte) _centroids[_label[p]][5];
            l--;
            l=l-(2*352*288);
            l=l+2;


        }
        BufferedImage img1= new BufferedImage(352,288,BufferedImage.TYPE_INT_RGB);


        int ind = 0;
        for(int y = 0; y < 288; y++){

            for(int x = 0; x < 352; x++){

                // byte a = (byte) 255;
                /*byte r = (byte) 255;
                byte g = (byte) 255;
                byte b = (byte) 255;*/

                int pix = 0xff000000 | ((fin_image[ind] & 0xff) << 16) | ((fin_image[ind+(352*288)] & 0xff) << 8) | (fin_image[ind +(2*352*288)] & 0xff);
                //int pix = ((a << 24) + (r << 16) + (g << 8) + b);
                img1.setRGB(x,y,pix);
                ind++;
            }
        }


        /*WritableRaster raster = img1.getRaster();
        raster.setDataElements(0,0,352,288,image);*/
        frame = new JFrame();
        display_color(param0,img1);


    }






    public void two_recon(String param0)throws IOException {
        int len= _label.length;
        System.out.println("Length of label array is "+ len);
        JFrame frame;


    byte[] fin_image = new byte[352 * 288];
        int p=0;
        for(int l = 0; l<(352*288);l++)

    {
        fin_image[l] =(byte) _centroids[_label[p]][0];
        l++;

        fin_image[l] =(byte) _centroids[_label[p]][1];
        p++;
    }
        BufferedImage img1= new BufferedImage(352,288,BufferedImage.TYPE_BYTE_GRAY);
        WritableRaster raster = img1.getRaster();
        raster.setDataElements(0,0,352,288,fin_image);
        display_gray(param0,img1);

    }




    public void two_cross_two_recon(String param0) throws IOException {
        int len= _label.length;
        System.out.println("Length of label array is "+ len);
        JFrame frame;


        byte[] fin_image = new byte[352 * 288];
        int l=0;
        for(int p = 0; p<(352*288/4);p++) {


            fin_image[l] = (byte) _centroids[_label[p]][0];
            l++;
            fin_image[l] = (byte) _centroids[_label[p]][1];
            l--;
            l = l + 352;
            fin_image[l] = (byte) _centroids[_label[p]][2];
            l++;
            fin_image[l] = (byte) _centroids[_label[p]][3];
            l = l - 353;
            l=l+2;
            if ((l % 352 == 0)) {
                l = l + 352;
            }
        }
        BufferedImage img1= new BufferedImage(352,288,BufferedImage.TYPE_BYTE_GRAY);
        WritableRaster raster = img1.getRaster();
        raster.setDataElements(0,0,352,288,fin_image);
        display_gray(param0,img1);


    }


    public void two_cross_two_color_recon(String param0) throws IOException{

        int len= _label.length;
        System.out.println("Length of label array is "+ len);
        JFrame frame;


        byte[] fin_image = new byte[352 * 288 *3];
        int l=0;
        for(int p = 0; p<(352*288/4);p++) {
            fin_image[l] = (byte) _centroids[_label[p]][0];
            l++;
            fin_image[l] = (byte) _centroids[_label[p]][1];
            l--;
            l=l+352;
            fin_image[l] = (byte) _centroids[_label[p]][2];
            l++;
            fin_image[l] = (byte) _centroids[_label[p]][3];
            l = l - 353;
            if((l+2)%352==0){
                l=l+2+352;
            }
            else{l=l+2;}
        }
        int k=352*288;
        for(int p = 0; p<(352*288/4);p++) {
            fin_image[k] = (byte) _centroids[_label[p]][4];
            k++;
            fin_image[k] = (byte) _centroids[_label[p]][5];
            k--;
            k=k+352;
            fin_image[k] = (byte) _centroids[_label[p]][6];
            k++;
            fin_image[k] = (byte) _centroids[_label[p]][7];
            k = k - 353;
            if((k+2)%(352)==0){
                k=k+2+352;
            }
            else{k=k+2;}
            }
        int m=2*352*288;

        for(int p = 0; p<(352*288/4);p++) {
            fin_image[m] = (byte) _centroids[_label[p]][8];
            m++;
            fin_image[m] = (byte) _centroids[_label[p]][9];
            m--;
            m=m+352;
            fin_image[m] = (byte) _centroids[_label[p]][10];
            m++;
            fin_image[m] = (byte) _centroids[_label[p]][11];
            m = m - 353;
            if((m+2)%(352)==0){
                m=m+2+352;
            }
            else{m=m+2;}
        }








        BufferedImage img1= new BufferedImage(352,288,BufferedImage.TYPE_INT_RGB);


        int ind = 0;
        for(int y = 0; y < 288; y++){

            for(int x = 0; x < 352; x++){

                // byte a = (byte) 255;
                /*byte r = (byte) 255;
                byte g = (byte) 255;
                byte b = (byte) 255;*/

                int pix = 0xff000000 | ((fin_image[ind] & 0xff) << 16) | ((fin_image[ind+(352*288)] & 0xff) << 8) | (fin_image[ind +(2*352*288)] & 0xff);
                //int pix = ((a << 24) + (r << 16) + (g << 8) + b);
                img1.setRGB(x,y,pix);
                ind++;
            }
        }


        display_color(param0,img1);



    }





    public void four_cross_four_color_recon(String param0) throws IOException{


        int len= _label.length;
        System.out.println("Length of label array is "+ len);
        JFrame frame;


        byte[] fin_image = new byte[352 * 288 *3];
        int l=0;
        for(int p = 0; p<(352*288/16);p++) {
            fin_image[l] = (byte) _centroids[_label[p]][0];
            l++;
            fin_image[l] = (byte) _centroids[_label[p]][1];
            l++;
            fin_image[l] = (byte) _centroids[_label[p]][2];
            l++;
            fin_image[l] = (byte) _centroids[_label[p]][3];
            l=l-3;
            l=l+352;
            fin_image[l] = (byte) _centroids[_label[p]][4];
            l++;
            fin_image[l] = (byte) _centroids[_label[p]][5];
            l++;
            fin_image[l] = (byte) _centroids[_label[p]][6];
            l++;
            fin_image[l] = (byte) _centroids[_label[p]][7];
            l=l-3;
            l = l +352;
            fin_image[l] = (byte) _centroids[_label[p]][8];
            l++;
            fin_image[l] = (byte) _centroids[_label[p]][9];
            l++;
            fin_image[l] = (byte) _centroids[_label[p]][10];
            l++;
            fin_image[l] = (byte) _centroids[_label[p]][11];
            l=l-3;
            l=l+352;
            fin_image[l] = (byte) _centroids[_label[p]][12];
            l++;
            fin_image[l] = (byte) _centroids[_label[p]][13];
            l++;
            fin_image[l] = (byte) _centroids[_label[p]][14];
            l++;
            fin_image[l] = (byte) _centroids[_label[p]][15];
            l=l-1059;
            if((l+4)%352==0){
                l=l+4+1056;
            }
            else{l=l+4;}
        }
        int k=352*288;
        for(int p = 0; p<(352*288/16);p++) {
            fin_image[k] = (byte) _centroids[_label[p]][16];
            k++;
            fin_image[k] = (byte) _centroids[_label[p]][17];
            k++;
            fin_image[k] = (byte) _centroids[_label[p]][18];
            k++;
            fin_image[k] = (byte) _centroids[_label[p]][19];
            k=k-3;
            k=k+352;
            fin_image[k] = (byte) _centroids[_label[p]][20];
            k++;
            fin_image[k] = (byte) _centroids[_label[p]][21];
            k++;
            fin_image[k] = (byte) _centroids[_label[p]][22];
            k++;
            fin_image[k] = (byte) _centroids[_label[p]][23];
            k=k-3;
            k = k +352;
            fin_image[k] = (byte) _centroids[_label[p]][24];
            k++;
            fin_image[k] = (byte) _centroids[_label[p]][25];
            k++;
            fin_image[k] = (byte) _centroids[_label[p]][26];
            k++;
            fin_image[k] = (byte) _centroids[_label[p]][27];
            k=k-3;
            k=k+352;
            fin_image[k] = (byte) _centroids[_label[p]][28];
            k++;
            fin_image[k] = (byte) _centroids[_label[p]][29];
            k++;
            fin_image[k] = (byte) _centroids[_label[p]][30];
            k++;
            fin_image[k] = (byte) _centroids[_label[p]][31];
            k=k-1059;
            if((k+4)%352==0){
                k=k+4+1056;
            }
            else{k=k+4;}
        }
        int m=2*352*288;

        for(int p = 0; p<(352*288/16);p++) {
            fin_image[m] = (byte) _centroids[_label[p]][32];
            m++;
            fin_image[m] = (byte) _centroids[_label[p]][33];
            m++;
            fin_image[m] = (byte) _centroids[_label[p]][34];
            m++;
            fin_image[m] = (byte) _centroids[_label[p]][35];
            m=m-3;
            m=m+352;
            fin_image[m] = (byte) _centroids[_label[p]][36];
            m++;
            fin_image[m] = (byte) _centroids[_label[p]][37];
            m++;
            fin_image[m] = (byte) _centroids[_label[p]][38];
            m++;
            fin_image[m] = (byte) _centroids[_label[p]][39];
            m=m-3;
            m = m +352;
            fin_image[m] = (byte) _centroids[_label[p]][40];
            m++;
            fin_image[m] = (byte) _centroids[_label[p]][41];
            m++;
            fin_image[m] = (byte) _centroids[_label[p]][42];
            m++;
            fin_image[m] = (byte) _centroids[_label[p]][43];
            m=m-3;
            m=m+352;
            fin_image[m] = (byte) _centroids[_label[p]][44];
            m++;
            fin_image[m] = (byte) _centroids[_label[p]][45];
            m++;
            fin_image[m] = (byte) _centroids[_label[p]][46];
            m++;
            fin_image[m] = (byte) _centroids[_label[p]][47];
            m=m-1059;
            if((m+4)%352==0){
                m=m+4+1056;
            }
            else{m=m+4;}
        }


        BufferedImage img1= new BufferedImage(352,288,BufferedImage.TYPE_INT_RGB);


        int ind = 0;
        for(int y = 0; y < 288; y++){

            for(int x = 0; x < 352; x++){

                // byte a = (byte) 255;
                /*byte r = (byte) 255;
                byte g = (byte) 255;
                byte b = (byte) 255;*/

                int pix = 0xff000000 | ((fin_image[ind] & 0xff) << 16) | ((fin_image[ind+(352*288)] & 0xff) << 8) | (fin_image[ind +(2*352*288)] & 0xff);
                //int pix = ((a << 24) + (r << 16) + (g << 8) + b);
                img1.setRGB(x,y,pix);
                ind++;
            }
        }

        display_color(param0,img1);

    }





    public void four_cross_four_recon(String param0) throws IOException{
        int len= _label.length;
        System.out.println("Length of label array is "+ len);



        byte[] fin_image = new byte[352 * 288];
        int l=0;
        for(int p = 0; p<(352*288/16);p++) {


            fin_image[l] = (byte) _centroids[_label[p]][0];
            l++;
            fin_image[l] = (byte) _centroids[_label[p]][1];
            l++;
            fin_image[l] = (byte) _centroids[_label[p]][2];
            l++;
            fin_image[l] = (byte) _centroids[_label[p]][3];
            l=l-3;
            l = l + 352;
            fin_image[l] = (byte) _centroids[_label[p]][4];
            l++;
            fin_image[l] = (byte) _centroids[_label[p]][5];
            l++;
            fin_image[l] = (byte) _centroids[_label[p]][6];
            l++;
            fin_image[l] = (byte) _centroids[_label[p]][7];
            l=l-3;
            l=l+352;
            fin_image[l] = (byte) _centroids[_label[p]][8];
            l++;
            fin_image[l] = (byte) _centroids[_label[p]][9];
            l++;
            fin_image[l] = (byte) _centroids[_label[p]][10];
            l++;
            fin_image[l] = (byte) _centroids[_label[p]][11];
            l=l-3;
            l=l+352;
            fin_image[l] = (byte) _centroids[_label[p]][12];
            l++;
            fin_image[l] = (byte) _centroids[_label[p]][13];
            l++;
            fin_image[l] = (byte) _centroids[_label[p]][14];
            l++;
            fin_image[l] = (byte) _centroids[_label[p]][15];
            l = l - 1059;
            l=l+4;
            if ((l % 352 == 0)) {
                l = l + 1056;
            }
        }


        BufferedImage img1= new BufferedImage(352,288,BufferedImage.TYPE_BYTE_GRAY);
        WritableRaster raster = img1.getRaster();
        raster.setDataElements(0,0,352,288,fin_image);
        display_gray(param0,img1);
    }





    public static void main(String args[])throws IOException{

        double [][] val_img= new double[288][352];
        String param0;
        String param1;
        String param2;

        param0 = args[0];
        param1 = args[1];
        param2 = args[2];

        int N=Integer.parseInt(param1);
        double mode=Double.parseDouble(param2);
        String type= param0.substring(param0.length() - 3);

        if(type.equals("rgb") ){

            if(mode==1){

                MyCompression obj= new MyCompression();
                obj.BytetoInt_color(param0);
                System.out.println("Byte to Int is done");
                obj.two_pixels_color();
                System.out.println("Two pixels is done");
                obj.clustering(N);
                System.out.println("Clustering is done");
                obj.two_recon_color(param0);
            }


            if(mode==2){

                MyCompression obj= new MyCompression();
                obj.BytetoInt_color(param0);
                System.out.println("Byte to Int is done");
                obj.two_cross_two_pixels_color();
                System.out.println("Two cross two pixels is done");
                obj.clustering(N);
                System.out.println("Clustering is done");
                obj.two_cross_two_color_recon(param0);

            }

            if(mode==3){
                MyCompression obj= new MyCompression();
                obj.BytetoInt_color(param0);
                System.out.println("Byte to Int is done");
                obj.four_cross_four_pixels_color();
                System.out.println("Two cross two pixels is done");
                obj.clustering(N);
                System.out.println("Clustering is done");
                obj.four_cross_four_color_recon(param0);

            }
        }
        if(type.equals("raw") ){

            if(mode==1){

                MyCompression obj= new MyCompression();
                val_img= obj.BytetoInt(param0);
                System.out.println("Byte to Int is done");
                obj.two_pixels(val_img);
                System.out.println("Two pixels is done");
                obj.clustering(N);
                System.out.println("Clustering is done");
                obj.two_recon(param0);
            }



            if(mode==2){

                MyCompression obj= new MyCompression();
                val_img=obj.BytetoInt(param0);
                System.out.println("Byte to Int is done");
                obj.two_cross_two_pixels(val_img);
                System.out.println("Two pixels is done");
                obj.clustering(N);
                System.out.println("Clustering is done");
                obj.two_cross_two_recon(param0);

            }


            if(mode==3){

                MyCompression obj= new MyCompression();
                val_img=obj.BytetoInt(param0);
                System.out.println("Byte to Int is done");
                obj.four_cross_four_pixels(val_img);
                System.out.println("Two pixels is done");
                obj.clustering(N);
                System.out.println("Clustering is done");
                obj.four_cross_four_recon(param0);

            }

        }

    }}