package com.sun.techcup;

import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.JavaCameraView;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.highgui.Highgui;
import org.opencv.highgui.VideoCapture;
import org.opencv.imgproc.Imgproc;

import com.sun.techcup.R;

import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.util.Log;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnTouchListener;
import android.annotation.SuppressLint;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.SurfaceView;
import android.widget.Toast;

import com.sun.techcup.UsbSerialDriver;
import com.sun.techcup.UsbSerialPort;
import com.sun.techcup.UsbSerialProber;
import com.sun.techcup.HexDump;
import com.sun.techcup.SerialInputOutputManager;



public class TechcupActivity extends Activity implements CvCameraViewListener2{

	private static final String  TAG              = "OCVSample::Activity";

    private boolean              mIsColorSelected = false;
    private Mat                  mRgba;
    private Scalar               mPlus0;
    private Scalar               mPlus5;
    private Scalar               mGrid1;
    private Scalar               mGrid2;
    private Scalar               mGrid3;
    private Scalar               mGrid4;
    private Scalar               mGrid5;
    private Scalar               mGrid6;
    private Scalar               mGrid7;
    private Scalar               mCross51;
    private Scalar               mCross52;
    private Scalar               mCross53;
    private Scalar               mCross54;
    private Scalar               mCross55;
    private Scalar               mCross56;
    private Scalar               mCross57;
    private Scalar               mCross01;
    private Scalar               mCross02;
    private Scalar               mCross03;
    private Scalar               mCross04;
    private Scalar               mCross05;
    private Scalar               mBlobColorHsv;
    private Detector    		 mDetector;
    private Mat                  mSpectrum;
    private Size                 SPECTRUM_SIZE;
    private Scalar               CONTOUR_COLOR;
    
    int counter;
	Button startstop;
	TextView plus5, plus0;
	int[] Plus5 = {1,1,0,0,0,1,1};
	int[] Plus0 = {1,0,0,0,1};

    private CameraBridgeViewBase mOpenCvCameraView;
    
    private BaseLoaderCallback  mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i(TAG, "OpenCV loaded successfully");
                    mOpenCvCameraView.enableView();
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };

    public TechcupActivity() {
        Log.i(TAG, "Instantiated new " + this.getClass());
    }

   
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		Log.i(TAG, "called onCreate");
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.techcup);
        mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.cameraview);
        mOpenCvCameraView.setCvCameraViewListener(this);
        
		counter = 0;
		startstop = (Button)findViewById(R.id.startstop);
		plus5 = (TextView)findViewById(R.id.plus5);
		plus0 = (TextView)findViewById(R.id.plus0);
		
		startstop.setOnClickListener(new View.OnClickListener() 
		{
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// inverse the counter
				switch(counter)
				{
					case 0:	counter = 1;
							plus5.setText("Front: "+Plus5[0]+" "+Plus5[1]+" "+Plus5[2]+" "+Plus5[3]+" "+Plus5[4]+" "+Plus5[5]+" "+Plus5[6]);
							plus0.setText("  Now: "+Plus0[0]+" "+Plus0[1]+" "+Plus0[2]+" "+Plus0[3]+" "+Plus0[4]);
							break;
					case 1: counter = 0;
							break;
				}	
			}
		});
		
	}
	
	@Override
    public void onPause()
    {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_3, this, mLoaderCallback);
    }

    public void onDestroy() {
        super.onDestroy();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    public void onCameraViewStarted(int width, int height) {
        mRgba = new Mat(height, width, CvType.CV_8UC4);
        mDetector = new Detector();
        mSpectrum = new Mat();
        mPlus0 = new Scalar(255);
        mPlus5 = new Scalar(255);
        mGrid1 = new Scalar(255);
        mGrid2 = new Scalar(255);
        mGrid3 = new Scalar(255);
        mGrid4 = new Scalar(255);
        mGrid5 = new Scalar(255);
        mGrid6 = new Scalar(255);
        mGrid7 = new Scalar(255);
        mCross51 = new Scalar(255);
        mCross52 = new Scalar(255);
        mCross53 = new Scalar(255);
        mCross54 = new Scalar(255);
        mCross55 = new Scalar(255);
        mCross56 = new Scalar(255);
        mCross57 = new Scalar(255);
        mCross01 = new Scalar(255);
        mCross02 = new Scalar(255);
        mCross03 = new Scalar(255);
        mCross04 = new Scalar(255);
        mCross05 = new Scalar(255);
        mBlobColorHsv = new Scalar(255);
        SPECTRUM_SIZE = new Size(200, 64);
        CONTOUR_COLOR = new Scalar(255,0,0,255);
    }

    public void onCameraViewStopped() {
        mRgba.release();
    }

    public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
        mRgba = inputFrame.rgba();

        if (counter==1) {
            //mDetector.process(mRgba);
            //List<MatOfPoint> contours = mDetector.getContours();
            //Scalar temp = mDetector.getRGB();
            //Log.e(TAG, "Contours count: " + contours.size());
            //Imgproc.drawContours(mRgba, contours, -1, CONTOUR_COLOR);
            
        	// For Plus0-1
            int cols = mRgba.cols();
            int rows = mRgba.rows();

            int x = 472;
            int y = 380;
            int up = 90;
            int down = 80;

            //Log.i(TAG, "Touch image coordinates: (" + x + ", " + y + ")");


            Rect touchedRect = new Rect();

            touchedRect.x = (x>4) ? x-4 : 0;
            touchedRect.y = (y>4) ? y-4 : 0;

            touchedRect.width = (x+4 < cols) ? x + 4 - touchedRect.x : cols - touchedRect.x;
            touchedRect.height = (y+4 < rows) ? y + 4 - touchedRect.y : rows - touchedRect.y;

            Mat touchedRegionRgba = mRgba.submat(touchedRect);

            Mat touchedRegionHsv = new Mat();
            Imgproc.cvtColor(touchedRegionRgba, touchedRegionHsv, Imgproc.COLOR_RGB2HSV_FULL);

            // Calculate average color of touched region
            mBlobColorHsv = Core.sumElems(touchedRegionHsv);
            int pointCount = touchedRect.width*touchedRect.height;
            for (int i = 0; i < mBlobColorHsv.val.length; i++)
                mBlobColorHsv.val[i] /= pointCount;
            
            mCross01 = converScalarHsv2Rgba(mBlobColorHsv);
            
            if(mCross01.val[0]>=up && mCross01.val[1]>=up && mCross01.val[2]>=up)
            {
            	mCross01.val[0]=255;
            	mCross01.val[1]=255;
            	mCross01.val[2]=255;
            	Plus0[0]=1;
            }
            else if(mCross01.val[0]<=down && mCross01.val[1]<=down && mCross01.val[2]<=down)
            {
            	mCross01.val[0]=0;
            	mCross01.val[1]=0;
            	mCross01.val[2]=0;
            	Plus0[0]=0;
            }
            touchedRegionRgba.release();
            touchedRegionHsv.release();
            
            
         // For Plus0-2

            x = 472;
            y = 320;

            //Log.i(TAG, "Touch image coordinates: (" + x + ", " + y + ")");


            touchedRect = new Rect();

            touchedRect.x = (x>4) ? x-4 : 0;
            touchedRect.y = (y>4) ? y-4 : 0;

            touchedRect.width = (x+4 < cols) ? x + 4 - touchedRect.x : cols - touchedRect.x;
            touchedRect.height = (y+4 < rows) ? y + 4 - touchedRect.y : rows - touchedRect.y;

            touchedRegionRgba = mRgba.submat(touchedRect);

            touchedRegionHsv = new Mat();
            Imgproc.cvtColor(touchedRegionRgba, touchedRegionHsv, Imgproc.COLOR_RGB2HSV_FULL);

            // Calculate average color of touched region
            mBlobColorHsv = Core.sumElems(touchedRegionHsv);
            pointCount = touchedRect.width*touchedRect.height;
            for (int i = 0; i < mBlobColorHsv.val.length; i++)
                mBlobColorHsv.val[i] /= pointCount;
            
            mCross02 = converScalarHsv2Rgba(mBlobColorHsv);
            
            if(mCross02.val[0]>=up && mCross02.val[1]>=up && mCross02.val[2]>=up)
            {
            	mCross02.val[0]=255;
            	mCross02.val[1]=255;
            	mCross02.val[2]=255;
            	Plus0[1]=1;
            }
            else if(mCross02.val[0]<=down && mCross02.val[1]<=down && mCross02.val[2]<=down)
            {
            	mCross02.val[0]=0;
            	mCross02.val[1]=0;
            	mCross02.val[2]=0;
            	Plus0[1]=0;
            }
            touchedRegionRgba.release();
            touchedRegionHsv.release();
        	
        	
        	
        	// For Plus0-3

            x = 472;
            y = 240;

            Log.i(TAG, "Touch image coordinates: (" + x + ", " + y + ")");


            touchedRect = new Rect();

            touchedRect.x = (x>4) ? x-4 : 0;
            touchedRect.y = (y>4) ? y-4 : 0;

            touchedRect.width = (x+4 < cols) ? x + 4 - touchedRect.x : cols - touchedRect.x;
            touchedRect.height = (y+4 < rows) ? y + 4 - touchedRect.y : rows - touchedRect.y;

            touchedRegionRgba = mRgba.submat(touchedRect);

            touchedRegionHsv = new Mat();
            Imgproc.cvtColor(touchedRegionRgba, touchedRegionHsv, Imgproc.COLOR_RGB2HSV_FULL);

            // Calculate average color of touched region
            mBlobColorHsv = Core.sumElems(touchedRegionHsv);
            pointCount = touchedRect.width*touchedRect.height;
            for (int i = 0; i < mBlobColorHsv.val.length; i++)
                mBlobColorHsv.val[i] /= pointCount;
            
            mCross03 = converScalarHsv2Rgba(mBlobColorHsv);
            
            if(mCross03.val[0]>=up && mCross03.val[1]>=up && mCross03.val[2]>=up)
            {
            	mCross03.val[0]=255;
            	mCross03.val[1]=255;
            	mCross03.val[2]=255;
            	Plus0[2]=1;
            }
            else if(mCross03.val[0]<=down && mCross03.val[1]<=down && mCross03.val[2]<=down)
            {
            	mCross03.val[0]=0;
            	mCross03.val[1]=0;
            	mCross03.val[2]=0;
            	Plus0[2]=0;
            }
            touchedRegionRgba.release();
            touchedRegionHsv.release();
            
            
            
         // For Plus0-4

            x = 472;
            y = 160;

            Log.i(TAG, "Touch image coordinates: (" + x + ", " + y + ")");


            touchedRect = new Rect();

            touchedRect.x = (x>4) ? x-4 : 0;
            touchedRect.y = (y>4) ? y-4 : 0;

            touchedRect.width = (x+4 < cols) ? x + 4 - touchedRect.x : cols - touchedRect.x;
            touchedRect.height = (y+4 < rows) ? y + 4 - touchedRect.y : rows - touchedRect.y;

            touchedRegionRgba = mRgba.submat(touchedRect);

            touchedRegionHsv = new Mat();
            Imgproc.cvtColor(touchedRegionRgba, touchedRegionHsv, Imgproc.COLOR_RGB2HSV_FULL);

            // Calculate average color of touched region
            mBlobColorHsv = Core.sumElems(touchedRegionHsv);
            pointCount = touchedRect.width*touchedRect.height;
            for (int i = 0; i < mBlobColorHsv.val.length; i++)
                mBlobColorHsv.val[i] /= pointCount;
            
            mCross04 = converScalarHsv2Rgba(mBlobColorHsv);
                       
            if(mCross04.val[0]>=up && mCross04.val[1]>=up && mCross04.val[2]>=up)
            {
            	mCross04.val[0]=255;
            	mCross04.val[1]=255;
            	mCross04.val[2]=255;
            	Plus0[3]=1;
            }
            else if(mCross04.val[0]<=down && mCross04.val[1]<=down && mCross04.val[2]<=down)
            {
            	mCross04.val[0]=0;
            	mCross04.val[1]=0;
            	mCross04.val[2]=0;
            	Plus0[3]=0;
            }
            touchedRegionRgba.release();
            touchedRegionHsv.release();
            
            
         // For Plus0-5

            x = 472;
            y = 100;

            Log.i(TAG, "Touch image coordinates: (" + x + ", " + y + ")");


            touchedRect = new Rect();

            touchedRect.x = (x>4) ? x-4 : 0;
            touchedRect.y = (y>4) ? y-4 : 0;

            touchedRect.width = (x+4 < cols) ? x + 4 - touchedRect.x : cols - touchedRect.x;
            touchedRect.height = (y+4 < rows) ? y + 4 - touchedRect.y : rows - touchedRect.y;

            touchedRegionRgba = mRgba.submat(touchedRect);

            touchedRegionHsv = new Mat();
            Imgproc.cvtColor(touchedRegionRgba, touchedRegionHsv, Imgproc.COLOR_RGB2HSV_FULL);

            // Calculate average color of touched region
            mBlobColorHsv = Core.sumElems(touchedRegionHsv);
            pointCount = touchedRect.width*touchedRect.height;
            for (int i = 0; i < mBlobColorHsv.val.length; i++)
                mBlobColorHsv.val[i] /= pointCount;
            
            mCross05 = converScalarHsv2Rgba(mBlobColorHsv);
            
            if(mCross05.val[0]>=up && mCross05.val[1]>=up && mCross05.val[2]>=up)
            {
            	mCross05.val[0]=255;
            	mCross05.val[1]=255;
            	mCross05.val[2]=255;
            	Plus0[4]=1;
            }
            else if(mCross05.val[0]<=down && mCross05.val[1]<=down && mCross05.val[2]<=down)
            {
            	mCross05.val[0]=0;
            	mCross05.val[1]=0;
            	mCross05.val[2]=0;
            	Plus0[4]=0;
            }
            touchedRegionRgba.release();
            touchedRegionHsv.release();
            
            
            // For Plus5-1

            x = 42;
            y = 450;

            Log.i(TAG, "Touch image coordinates: (" + x + ", " + y + ")");


            touchedRect = new Rect();

            touchedRect.x = (x>4) ? x-4 : 0;
            touchedRect.y = (y>4) ? y-4 : 0;

            touchedRect.width = (x+4 < cols) ? x + 4 - touchedRect.x : cols - touchedRect.x;
            touchedRect.height = (y+4 < rows) ? y + 4 - touchedRect.y : rows - touchedRect.y;

            touchedRegionRgba = mRgba.submat(touchedRect);

            touchedRegionHsv = new Mat();
            Imgproc.cvtColor(touchedRegionRgba, touchedRegionHsv, Imgproc.COLOR_RGB2HSV_FULL);

            // Calculate average color of touched region
            mBlobColorHsv = Core.sumElems(touchedRegionHsv);
            pointCount = touchedRect.width*touchedRect.height;
            for (int i = 0; i < mBlobColorHsv.val.length; i++)
                mBlobColorHsv.val[i] /= pointCount;
            
            mCross51 = converScalarHsv2Rgba(mBlobColorHsv);
            
            if(mCross51.val[0]>=up && mCross51.val[1]>=up && mCross51.val[2]>=up)
            {
            	mCross51.val[0]=255;
            	mCross51.val[1]=255;
            	mCross51.val[2]=255;
            	Plus5[0]=1;
            }
            else if(mCross51.val[0]<=down && mCross51.val[1]<=down && mCross51.val[2]<=down)
            {
            	mCross51.val[0]=0;
            	mCross51.val[1]=0;
            	mCross51.val[2]=0;
            	Plus5[0]=0;
            }
            touchedRegionRgba.release();
            touchedRegionHsv.release();
            
            
         // For Plus5-2

            x = 42;
            y = 380;

            Log.i(TAG, "Touch image coordinates: (" + x + ", " + y + ")");


            touchedRect = new Rect();

            touchedRect.x = (x>4) ? x-4 : 0;
            touchedRect.y = (y>4) ? y-4 : 0;

            touchedRect.width = (x+4 < cols) ? x + 4 - touchedRect.x : cols - touchedRect.x;
            touchedRect.height = (y+4 < rows) ? y + 4 - touchedRect.y : rows - touchedRect.y;

            touchedRegionRgba = mRgba.submat(touchedRect);

            touchedRegionHsv = new Mat();
            Imgproc.cvtColor(touchedRegionRgba, touchedRegionHsv, Imgproc.COLOR_RGB2HSV_FULL);

            // Calculate average color of touched region
            mBlobColorHsv = Core.sumElems(touchedRegionHsv);
            pointCount = touchedRect.width*touchedRect.height;
            for (int i = 0; i < mBlobColorHsv.val.length; i++)
                mBlobColorHsv.val[i] /= pointCount;
            
            mCross52 = converScalarHsv2Rgba(mBlobColorHsv);
            
            if(mCross52.val[0]>=up && mCross52.val[1]>=up && mCross52.val[2]>=up)
            {
            	mCross52.val[0]=255;
            	mCross52.val[1]=255;
            	mCross52.val[2]=255;
            	Plus5[1]=1;
            }
            else if(mCross52.val[0]<=down && mCross52.val[1]<=down && mCross52.val[2]<=down)
            {
            	mCross52.val[0]=0;
            	mCross52.val[1]=0;
            	mCross52.val[2]=0;
            	Plus5[1]=0;
            }
            touchedRegionRgba.release();
            touchedRegionHsv.release();
            
            
         // For Plus5-3

            x = 42;
            y = 320;

            Log.i(TAG, "Touch image coordinates: (" + x + ", " + y + ")");


            touchedRect = new Rect();

            touchedRect.x = (x>4) ? x-4 : 0;
            touchedRect.y = (y>4) ? y-4 : 0;

            touchedRect.width = (x+4 < cols) ? x + 4 - touchedRect.x : cols - touchedRect.x;
            touchedRect.height = (y+4 < rows) ? y + 4 - touchedRect.y : rows - touchedRect.y;

            touchedRegionRgba = mRgba.submat(touchedRect);

            touchedRegionHsv = new Mat();
            Imgproc.cvtColor(touchedRegionRgba, touchedRegionHsv, Imgproc.COLOR_RGB2HSV_FULL);

            // Calculate average color of touched region
            mBlobColorHsv = Core.sumElems(touchedRegionHsv);
            pointCount = touchedRect.width*touchedRect.height;
            for (int i = 0; i < mBlobColorHsv.val.length; i++)
                mBlobColorHsv.val[i] /= pointCount;
            
            mCross53 = converScalarHsv2Rgba(mBlobColorHsv);
            
            if(mCross53.val[0]>=up && mCross53.val[1]>=up && mCross53.val[2]>=up)
            {
            	mCross53.val[0]=255;
            	mCross53.val[1]=255;
            	mCross53.val[2]=255;
            	Plus5[2]=1;
            }
            else if(mCross53.val[0]<=down && mCross53.val[1]<=down && mCross53.val[2]<=down)
            {
            	mCross53.val[0]=0;
            	mCross53.val[1]=0;
            	mCross53.val[2]=0;
            	Plus5[2]=0;
            }
            touchedRegionRgba.release();
            touchedRegionHsv.release();
            
            
            // For Plus5-4

            x = 42;
            y = 240;

            Log.i(TAG, "Touch image coordinates: (" + x + ", " + y + ")");


            touchedRect = new Rect();

            touchedRect.x = (x>4) ? x-4 : 0;
            touchedRect.y = (y>4) ? y-4 : 0;

            touchedRect.width = (x+4 < cols) ? x + 4 - touchedRect.x : cols - touchedRect.x;
            touchedRect.height = (y+4 < rows) ? y + 4 - touchedRect.y : rows - touchedRect.y;

            touchedRegionRgba = mRgba.submat(touchedRect);

            touchedRegionHsv = new Mat();
            Imgproc.cvtColor(touchedRegionRgba, touchedRegionHsv, Imgproc.COLOR_RGB2HSV_FULL);

            // Calculate average color of touched region
            mBlobColorHsv = Core.sumElems(touchedRegionHsv);
            pointCount = touchedRect.width*touchedRect.height;
            for (int i = 0; i < mBlobColorHsv.val.length; i++)
                mBlobColorHsv.val[i] /= pointCount;
            
            mCross54 = converScalarHsv2Rgba(mBlobColorHsv);
            
            if(mCross54.val[0]>=up && mCross54.val[1]>=up && mCross54.val[2]>=up)
            {
            	mCross54.val[0]=255;
            	mCross54.val[1]=255;
            	mCross54.val[2]=255;
            	Plus5[3]=1;
            }
            else if(mCross54.val[0]<=down && mCross54.val[1]<=down && mCross54.val[2]<=down)
            {
            	mCross54.val[0]=0;
            	mCross54.val[1]=0;
            	mCross54.val[2]=0;
            	Plus5[3]=0;
            }
            touchedRegionRgba.release();
            touchedRegionHsv.release();
            
            
         // For Plus5-5

            x = 42;
            y = 160;

            Log.i(TAG, "Touch image coordinates: (" + x + ", " + y + ")");


            touchedRect = new Rect();

            touchedRect.x = (x>4) ? x-4 : 0;
            touchedRect.y = (y>4) ? y-4 : 0;

            touchedRect.width = (x+4 < cols) ? x + 4 - touchedRect.x : cols - touchedRect.x;
            touchedRect.height = (y+4 < rows) ? y + 4 - touchedRect.y : rows - touchedRect.y;

            touchedRegionRgba = mRgba.submat(touchedRect);

            touchedRegionHsv = new Mat();
            Imgproc.cvtColor(touchedRegionRgba, touchedRegionHsv, Imgproc.COLOR_RGB2HSV_FULL);

            // Calculate average color of touched region
            mBlobColorHsv = Core.sumElems(touchedRegionHsv);
            pointCount = touchedRect.width*touchedRect.height;
            for (int i = 0; i < mBlobColorHsv.val.length; i++)
                mBlobColorHsv.val[i] /= pointCount;
            
            mCross55 = converScalarHsv2Rgba(mBlobColorHsv);
            
            if(mCross55.val[0]>=up && mCross55.val[1]>=up && mCross55.val[2]>=up)
            {
            	mCross55.val[0]=255;
            	mCross55.val[1]=255;
            	mCross55.val[2]=255;
            	Plus5[4]=1;
            }
            else if(mCross55.val[0]<=down && mCross55.val[1]<=down && mCross55.val[2]<=down)
            {
            	mCross55.val[0]=0;
            	mCross55.val[1]=0;
            	mCross55.val[2]=0;
            	Plus5[4]=0;
            }
            touchedRegionRgba.release();
            touchedRegionHsv.release();
            
            
         // For Plus5-6

            x = 42;
            y = 100;

            Log.i(TAG, "Touch image coordinates: (" + x + ", " + y + ")");


            touchedRect = new Rect();

            touchedRect.x = (x>4) ? x-4 : 0;
            touchedRect.y = (y>4) ? y-4 : 0;

            touchedRect.width = (x+4 < cols) ? x + 4 - touchedRect.x : cols - touchedRect.x;
            touchedRect.height = (y+4 < rows) ? y + 4 - touchedRect.y : rows - touchedRect.y;

            touchedRegionRgba = mRgba.submat(touchedRect);

            touchedRegionHsv = new Mat();
            Imgproc.cvtColor(touchedRegionRgba, touchedRegionHsv, Imgproc.COLOR_RGB2HSV_FULL);

            // Calculate average color of touched region
            mBlobColorHsv = Core.sumElems(touchedRegionHsv);
            pointCount = touchedRect.width*touchedRect.height;
            for (int i = 0; i < mBlobColorHsv.val.length; i++)
                mBlobColorHsv.val[i] /= pointCount;
            
            mCross56 = converScalarHsv2Rgba(mBlobColorHsv);
            
            if(mCross56.val[0]>=up && mCross56.val[1]>=up && mCross56.val[2]>=up)
            {
            	mCross56.val[0]=255;
            	mCross56.val[1]=255;
            	mCross56.val[2]=255;
            	Plus5[5]=1;
            }
            else if(mCross56.val[0]<=down && mCross56.val[1]<=down && mCross56.val[2]<=down)
            {
            	mCross56.val[0]=0;
            	mCross56.val[1]=0;
            	mCross56.val[2]=0;
            	Plus5[5]=0;
            }
            touchedRegionRgba.release();
            touchedRegionHsv.release();
            
            
            // For Plus5-7

            x = 42;
            y = 30;

            Log.i(TAG, "Touch image coordinates: (" + x + ", " + y + ")");


            touchedRect = new Rect();

            touchedRect.x = (x>4) ? x-4 : 0;
            touchedRect.y = (y>4) ? y-4 : 0;

            touchedRect.width = (x+4 < cols) ? x + 4 - touchedRect.x : cols - touchedRect.x;
            touchedRect.height = (y+4 < rows) ? y + 4 - touchedRect.y : rows - touchedRect.y;

            touchedRegionRgba = mRgba.submat(touchedRect);

            touchedRegionHsv = new Mat();
            Imgproc.cvtColor(touchedRegionRgba, touchedRegionHsv, Imgproc.COLOR_RGB2HSV_FULL);

            // Calculate average color of touched region
            mBlobColorHsv = Core.sumElems(touchedRegionHsv);
            pointCount = touchedRect.width*touchedRect.height;
            for (int i = 0; i < mBlobColorHsv.val.length; i++)
                mBlobColorHsv.val[i] /= pointCount;
            
            mCross57 = converScalarHsv2Rgba(mBlobColorHsv);
            
            if(mCross57.val[0]>=up && mCross57.val[1]>=up && mCross57.val[2]>=up)
            {
            	mCross57.val[0]=255;
            	mCross57.val[1]=255;
            	mCross57.val[2]=255;
            	Plus5[6]=1;
            }
            else if(mCross57.val[0]<=down && mCross57.val[1]<=down && mCross57.val[2]<=down)
            {
            	mCross57.val[0]=0;
            	mCross57.val[1]=0;
            	mCross57.val[2]=0;
            	Plus5[6]=0;
            }
            touchedRegionRgba.release();
            touchedRegionHsv.release();
            
            
            
            
            mPlus0 = converScalarHsv2Rgba(mBlobColorHsv);
            mPlus0.val[0]=255;
            mPlus0.val[1]=0;
            mPlus0.val[2]=0;
            
            mPlus5 = mPlus0;
            mGrid1 = mPlus0;
            mGrid2 = mPlus0;
            mGrid3 = mPlus0;
            mGrid4 = mPlus0;
            mGrid5 = mPlus0;
            mGrid6 = mPlus0;
            mGrid7 = mPlus0;   
            
            //mBlobColorRgb = temp;
                        
            Mat colorLabel1 = mRgba.submat(1, 480, 470, 475);
            colorLabel1.setTo(mPlus0);
            
            Mat colorLabel2 = mRgba.submat(1, 480, 40, 45);
            colorLabel2.setTo(mPlus5);
            
            Mat colorLabel3 = mRgba.submat(98, 102, 1, 520);
            colorLabel3.setTo(mGrid1);
            
            Mat colorLabel4 = mRgba.submat(158, 162, 1, 520);
            colorLabel4.setTo(mGrid2);
            
            Mat colorLabel5 = mRgba.submat(238, 242, 1, 520);
            colorLabel5.setTo(mGrid3);
            
            Mat colorLabel6 = mRgba.submat(318, 322, 1, 520);
            colorLabel6.setTo(mGrid4);
            
            Mat colorLabel7 = mRgba.submat(378, 382, 1, 520);
            colorLabel7.setTo(mGrid5);
            
            Mat colorLabel8 = mRgba.submat(28, 32, 1, 80);
            colorLabel8.setTo(mGrid6);
            
            Mat colorLabel9 = mRgba.submat(448, 452, 1, 80);
            colorLabel9.setTo(mGrid7);
            
            
            Mat colorLabel03 = mRgba.submat(228, 252, 460, 484);
            colorLabel03.setTo(mCross03);
            
            Mat colorLabel01 = mRgba.submat(368, 392, 460, 484);
            colorLabel01.setTo(mCross01);
            
            Mat colorLabel02 = mRgba.submat(308, 332, 460, 484);
            colorLabel02.setTo(mCross02);
            
            Mat colorLabel04 = mRgba.submat(148, 172, 460, 484);
            colorLabel04.setTo(mCross04);

            Mat colorLabel05 = mRgba.submat(88, 112, 460, 484);
            colorLabel05.setTo(mCross05);
            
            Mat colorLabel51 = mRgba.submat(438, 462, 30, 54);
            colorLabel51.setTo(mCross51);
            
            Mat colorLabel52 = mRgba.submat(368, 392, 30, 54);
            colorLabel52.setTo(mCross52);
            
            Mat colorLabel53 = mRgba.submat(308, 332, 30, 54);
            colorLabel53.setTo(mCross53);
            
            Mat colorLabel54 = mRgba.submat(228, 252, 30, 54);
            colorLabel54.setTo(mCross54);
            
            Mat colorLabel55 = mRgba.submat(148, 172, 30, 54);
            colorLabel55.setTo(mCross55);
            
            Mat colorLabel56 = mRgba.submat(88, 112, 30, 54);
            colorLabel56.setTo(mCross56);
            
            Mat colorLabel57 = mRgba.submat(18, 42, 30, 54);
            colorLabel57.setTo(mCross57);
            
            //Mat spectrumLabel = mRgba.submat(4, 4 + mSpectrum.rows(), 70, 70 + mSpectrum.cols());
            //mSpectrum.copyTo(spectrumLabel);
            
        }
        

                        
        return mRgba;
    }

    private Object Size(int i, int j) {
		// TODO Auto-generated method stub
		return null;
	}


	private Scalar converScalarHsv2Rgba(Scalar hsvColor) {
        Mat pointMatRgba = new Mat();
        Mat pointMatHsv = new Mat(1, 1, CvType.CV_8UC3, hsvColor);
        Imgproc.cvtColor(pointMatHsv, pointMatRgba, Imgproc.COLOR_HSV2RGB_FULL, 4);

        return new Scalar(pointMatRgba.get(0, 0));
    }	
}
