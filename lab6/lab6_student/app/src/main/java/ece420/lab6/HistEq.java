package ece420.lab6;

import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import java.io.IOException;
import android.graphics.Canvas;
import android.hardware.Camera.PreviewCallback;
import android.graphics.Rect;
import android.graphics.Bitmap;
import java.lang.Math;
import android.widget.TextView;
import android.graphics.Matrix;
//import java.util.List;
//import android.util.Log;



public class HistEq extends AppCompatActivity implements SurfaceHolder.Callback{

    private Camera camera;
    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;
    private SurfaceView surfaceView2;
    private SurfaceHolder surfaceHolder2;
    boolean previewing = false;
    private int width = 640;
    private int height = 480;
    private TextView textHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hist_eq);
        // Lock down the app orientation
        super.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
        textHelper = (TextView) findViewById(R.id.Helper);
        if(MainActivity.appflag==1) textHelper.setText("HistEq Image");
        else if(MainActivity.appflag==2) textHelper.setText("Sharpened Image");
        else if(MainActivity.appflag==3) textHelper.setText("Edged Image");
        getWindow().setFormat(PixelFormat.UNKNOWN);
        surfaceView = (SurfaceView)findViewById(R.id.ViewOrigin);
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        surfaceView2 = (SurfaceView)findViewById(R.id.ViewHisteq);
        surfaceHolder2 = surfaceView2.getHolder();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
        // TODO Auto-generated method stub
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // TODO Auto-generated method stub
        if(!previewing) {
            camera = Camera.open();
            if (camera != null) {
                try {
                    Camera.Parameters parameters = camera.getParameters();
                    parameters.setPreviewSize(width,height);
//                    List<Camera.Size> sizes = parameters.getSupportedPictureSizes();
//                    for(int i=0;i<sizes.size();i++) {
//                        int height = sizes.get(i).height;
//                        int width = sizes.get(i).width;
//                        Log.d("size: ", Integer.toString(width)+";"+Integer.toString(height));
//                        2592x1944;1920x1080;1440x1080;1280x720;640x480;352x288;320x240;176x144;
//                    }
                    camera.setParameters(parameters);
                    camera.setDisplayOrientation(90);
                    camera.setPreviewDisplay(surfaceHolder);
                    camera.setPreviewCallback(new PreviewCallback() {
                        public void onPreviewFrame(byte[] data, Camera camera)
                        {
                            Canvas canvas = surfaceHolder2.lockCanvas(null);
                            // Where Callback Happens
                            drawSomething(canvas,data);
                            surfaceHolder2.unlockCanvasAndPost(canvas);
                        }
                    });
                    camera.startPreview();
                    previewing = true;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (camera != null && previewing) {
            camera.stopPreview();
            camera.setPreviewCallback(null);
            camera.release();
            camera = null;
            previewing = false;
        }
    }

    // Callback will be directed to this function
    protected void drawSomething(Canvas canvas, byte[] data) {

        Matrix matrix = new Matrix();
        matrix.postRotate(90);

        if(MainActivity.appflag==1){
            // Your function is called here
            byte[] histeqdata = histeq(data,width,height);
            // We convert YUV to RGB For you
            int[] rgbdata = yuv2rgb(histeqdata);
            // Create bitmap and manipulate orientation
            Bitmap bmp = Bitmap.createBitmap(rgbdata,width,height,Bitmap.Config.ARGB_8888);
            bmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);
            // Draw the bitmap
            canvas.drawBitmap(bmp,new Rect(0,0,height,width),new Rect(0,0,canvas.getWidth(),canvas.getHeight()),null);
            return;
        }
        else if (MainActivity.appflag==2){
            // Our Sharpening Kernel
            double[][] kernel = new double[][] {{-1,-1,-1},{-1,9,-1},{-1,-1,-1}};
            // Your function is called here
            int[] sharpdata = conv2(data,width,height,kernel);
            // Convert to Grayscale and Draw
            sharpdata = merge(sharpdata,sharpdata);
            Bitmap bmp = Bitmap.createBitmap(sharpdata,width,height,Bitmap.Config.ARGB_8888);
            bmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);
            canvas.drawBitmap(bmp,new Rect(0,0,height,width),new Rect(0,0,canvas.getWidth(),canvas.getHeight()),null);
            return;
        }
        else if (MainActivity.appflag==3){
            double[][] kernelx = new double[][] {{1,0,-1},{1,0,-1},{1,0,-1}};
            double[][] kernely = new double[][] {{1,1,1},{0,0,0},{-1,-1,-1}};
            // Your function is called here
            int[] xdata = conv2(data,width,height,kernelx);
            int[] ydata = conv2(data,width,height,kernely);
            // Merge, Convert to Grayscale and Draw
            int[] edgedata = merge(xdata,ydata);
            Bitmap bmp = Bitmap.createBitmap(edgedata,width,height,Bitmap.Config.ARGB_8888);
            bmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);
            canvas.drawBitmap(bmp,new Rect(0,0,height,width),new Rect(0,0,canvas.getWidth(),canvas.getHeight()),null);
            return;
        }
    }

    // Convert YUV to RGB
    public int[] yuv2rgb(byte[] data){
        final int frameSize = width * height;
        int[] rgb = new int[frameSize];

        for (int j = 0, yp = 0; j < height; j++) {
            int uvp = frameSize + (j >> 1) * width, u = 0, v = 0;
            for (int i = 0; i < width; i++, yp++) {
                int y = (0xff & ((int) data[yp])) - 16;
                if (y < 0)
                    y = 0;
                if ((i & 1) == 0) {
                    v = (0xff & data[uvp++]) - 128;
                    u = (0xff & data[uvp++]) - 128;
                }

                int y1192 = 1192 * y;
                int r = (y1192 + 1634 * v);
                int g = (y1192 - 833 * v - 400 * u);
                int b = (y1192 + 2066 * u);

                if (r < 0)                  r = 0;
                else if (r > 262143)       r = 262143;
                if (g < 0)                  g = 0;
                else if (g > 262143)       g = 262143;
                if (b < 0)                  b = 0;
                else if (b > 262143)        b = 262143;

                rgb[yp] = 0xff000000 | ((r << 6) & 0xff0000) | ((g >> 2) & 0xff00) | ((b >> 10) & 0xff);
            }
        }
        return rgb;
    }

    // For Converting to Grayscale and Merge
    public int[] merge(int[] xdata,int[] ydata){
        int size = height*width;
        int[] edgedata = new int[size];
        for(int i=0;i<size;i++)
        {
            int p = (int)Math.sqrt((double)((xdata[i]&0xff)*(xdata[i]&0xff) + (ydata[i]&0xff)*(ydata[i]&0xff))/2);
            edgedata[i] = 0xff000000 | p<<16 | p<<8 | p;
        }
        return edgedata;
    }

    // Implement this function
    public byte[] histeq(byte[] data, int width, int height){
        // Your data should be stored inside here
        byte[] histeqdata = new byte[data.length];
        int size = height*width;

        // Perform Histogram Equalization Here
        // Feel Free to modify this part, currently just copying the original Y channel to the output directly
        int[] histogram = new int[256],scaled = new int[256];
        int[] cdf = new int[histogram.length];
        int min;

        for(int i = 0;i < height;i++){
            for(int j = 0;j < width;j++){
                histogram[data[i * width + j] + 128] ++;
            }
        }

        cdf[0] = histogram[0];
        min = size;

        for(int i = 1;i < cdf.length;i++){
            cdf[i] = cdf[i-1] + histogram[i];
            if(cdf[i] < min){
                min = cdf[i];
            }
        }

        for(int i = 0;i < scaled.length;i++){
            int num = (cdf[i] - min) * (255);
            int denom = size - 1;
            scaled[i] = (int)(Math.round(1.0 * num/denom));
        }

        for(int i = 0;i < height;i++){
            for(int j = 0;j < width;j++){
                    histeqdata[i*width + j] = (byte)(scaled[data[i * width+j]+128]-128);
                    //histeqdata[i*width + j] += 128;
            }
        }

        // Don't modify this Part, copying U,V channel data.
        for(int i=size;i<data.length;i++){
            histeqdata[i] = data[i];
        }
        return histeqdata;
    }


    //conversion for data
    // TODO: 2/24/17

    private byte[][] oneDToTwoD(byte[] data,int width,int height){
        byte[][] twoD = new byte[height][width];

        for(int row = 0;row < height;row++) {
            for (int col = 0; col < width; col++) {
                twoD[row][col] = data[row * width + col];
            }
        }

        return twoD;
    }


    // TODO: 2/24/17
    private int[] twoDToOneD(int[][] data){
        int[] oneD = new int[data.length*data[0].length];

        for(int row = 0;row < data.length;row++) {
            for (int col = 0; col < data[0].length; col++) {
                oneD[row * data[0].length + col] = data[row][col];
            }
        }

        return oneD;
    }
    //**************************************************

    //// TODO: 2/24/17
    //reshaping kernel
    private double[] twoDToOneDKernel(double[][] data){
        double[] oneD = new double[data.length*data[0].length];

        for(int row = 0;row < data.length;row++) {
            for (int col = 0; col < data[0].length; col++) {
                oneD[row * data[0].length + col] = data[row][col];
            }
        }

        return oneD;
    }

    //// TODO: 2/24/17
    private double[][] oneDToTwoDKernel(double[] data,int width,int height){
        double[][] twoD = new double[height][width];

        for(int row = 0;row < height;row++) {
            for (int col = 0; col < width; col++) {
                twoD[row][col] = data[row * width + col];
            }
        }

        return twoD;
    }

    //// TODO: 2/24/17
    private double[] flip(double[] input){
        double[] flipped = new double[input.length];

        for(int i = 0;i < flipped.length;i++) {
            flipped[i] = input[flipped.length - 1 - i];
        }

        return flipped;
    }


    // Implement this Function. Notice the Returned type is int[] here
    public int[] conv2(byte[] data, int width, int height, double kernel[][]){
        // 0 is black and 255 is white.
        int size = height*width;
        // Your output data goes here
        int[] convdata = new int[size];
        double[] conv = new double[size];


        // Perform 2-D Convolution Here
        // Feel Free to modify this part, currently just copying the original Y channel to the output directly
        byte[][] image;


        double[] kernel_extend;
        double[][] kernelFlipp;

        image = oneDToTwoD(data,width,height);

        kernel_extend = twoDToOneDKernel(kernel);
        kernel_extend = flip(kernel_extend);
        kernelFlipp = oneDToTwoDKernel(kernel_extend,3,3);

        int h_start = -1,w_start = -1,h_end = 2,w_end = 2;


        for(int row = 0;row < height;row++){
            for(int col = 0;col < width;col++){
                for(int h = h_start;h < h_end;h++) {
                    for (int w = w_start; w < w_end; w++) {
                        if((row+h) >= 0 && (row+h) < height && (col+w) >= 0 && (col+w) < width)
                            conv[row*width+col] += (image[row + h][col + w] * kernelFlipp[1 + h][1 + w]);
                    }
                }

                convdata[row * width + col] = (int)(Math.round(conv[row*width+col]));
            }
        }


        // We are converting to GrayScale so we don't need to copy U,V Channels here

        return convdata;
    }

}
