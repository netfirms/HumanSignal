package com.taweechai.humansignal;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.achartengine.GraphicalView;

import com.badlogic.gdx.audio.analysis.FFT;
import com.jjoe64.graphview.BarGraphView;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphView.GraphViewData;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.LineGraphView;
import com.taweechai.humansignal.realtimegraph.HearthrateLineGraph;
import com.taweechai.humansignal.realtimegraph.LineGraph;
import com.taweechai.humansignal.realtimegraph.MockData;
import com.taweechai.humansignal.realtimegraph.Point;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;

public class HumanSignalActivity extends Activity {
	final static int PULSE_WIDTH = 450;
	final static double PULSE_COUNT_THEADSHOLD = 1.5;
	final static double FFT_WINDOWS_LEN = 1024;
	private int pulseCountAcc = 0;
	private int heartRate = 0;
	private double tempPulseLockVal = 0d;
	private static Thread thread;
	static float [] signalBuf;
	static String[] signalDataArr;
	
	static String inputPath;
	
	
	static String RANGEFROM = "RANGEFROM";
	static String RANGETO = "RANGETO";
	static String SIGNALDATA = "SIGNALDATA";
	/* data source */
    InputStream inFile = null;
    String[] index = null;
    
    TextView heartrateLabel;
    Button plottingControl;
    EditText edittext1;
    EditText signalRangeFrom, signalRangeTo;
    private final Handler mHandler = new Handler();
	private Runnable mTimer1;
	private Runnable mTimer2;
	private Runnable mTimer3;
	private GraphView graphView;
	private GraphViewSeries exampleSeries1;
	private GraphViewSeries realtimeEcgSignalSeries;
	private double graph2LastXValue = 0d;
	private double graph2LastYValue = 0d;
	private boolean isPlot = false;
	private boolean isPulseChecklock = false;
	int n = 0;
	
	/*------For FFT Transform------*/
	private GraphViewSeries fftSignalMagnitudeSeries;
	private GraphViewSeries fftSignalPhaseSeries;
	
	private double getRandom() {
		double high = 3;
		double low = 0.5;
		return Math.random() * (high - low) + low;
	}
	
	public GraphViewData[] realtimeFftCalculation(double lastXValue, String[] signalDataArray, double windowsLens){
		/*FFT operation section*/
		double initialWindowsPosition = 0;
		initialWindowsPosition = lastXValue - windowsLens;
		System.out.println(lastXValue + " " + windowsLens + " " + initialWindowsPosition);
        float[] array = new float[(int)windowsLens];
        for(int i = 0; i< array.length; i++){
        	array[i] = (float)Double.parseDouble(signalDataArray[(int)initialWindowsPosition + i]);
        }
        float[] array_hat,res=new float[array.length/2];
    	float[] fft_cpx,tmpr,tmpi;
    	float[] mod_spec =new float[array.length/2];
    	float[] real_mod = new float[array.length];
    	float[] imag_mod = new float[array.length];
    	double[] real = new double[array.length];
    	double[] imag= new double[array.length];
    	double[] mag = new double[array.length];
    	double[] phase = new double[array.length];
    	int n;
    	float tmp_val;
    	String strings;
    	FFT fft = new FFT(array.length, 1);
    	
    	fft.forward(array);
        fft_cpx=fft.getSpectrum();
        tmpi = fft.getImaginaryPart();
        tmpr = fft.getRealPart();
        
        GraphViewData[] gDataFftMagnitudeArr = new GraphViewData[array.length];
        GraphViewData[] gDataFftPhaseArr = new GraphViewData[array.length];
        
        for(int i=0;i<array.length;i++)
        {
     	   real[i] = (double) tmpr[i];
     	   imag[i] = (double) tmpi[i];
           mag[i] = Math.sqrt((real[i]*real[i]) + (imag[i]*imag[i]));
     	   phase[i]=Math.atan2(imag[i],real[i]);
     	   
     	   gDataFftMagnitudeArr[i] = new GraphViewData( i, mag[i]);
     	   gDataFftPhaseArr[i] = new GraphViewData( i, phase[i]);
     	   /****Reconstruction****/
     	  // real_mod[i] = (float) (mag[i] * Math.cos(phase[i]));
     	  // imag_mod[i] = (float) (mag[i] * Math.sin(phase[i]));
  
        }
        return gDataFftMagnitudeArr;
	}
	
	public void signalAnalysis(View view){
		int signalFrom = Integer.parseInt(signalRangeFrom.getText().toString());
		int signalTo = Integer.parseInt(signalRangeTo.getText().toString());
		String[] signalArray = new String[signalTo - signalFrom];
		Intent intent = new Intent(HumanSignalActivity.this, FFTAnalysisActivity.class);
        int k = 0;
        for(int i = signalFrom; i < signalTo; i++){
        	signalArray[k] = signalDataArr[i];
        	k++;
        }
        intent.putExtra(RANGEFROM, signalRangeFrom.getText().toString());
        intent.putExtra(RANGETO, signalRangeTo.getText().toString());
        intent.putExtra(SIGNALDATA, signalArray);
        startActivity(intent);
	}
	
	public void plottingControl(View view){
		if(isPlot == true){
			isPlot = false;
			plottingControl.setText("Start measure");
		}
		else if(isPlot == false){
			isPlot = true;
			plottingControl.setText("Stop measure");
		}
		else{
			isPlot = false;
			plottingControl.setText("Start measure");
		}
	}
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        inputPath = "ecg.txt";
        InputStream input;
        AssetManager assetManager = getAssets();
        plottingControl = (Button) findViewById(R.id.measureBtn);
        heartrateLabel = (TextView) findViewById(R.id.hearthrateTv);
        signalRangeFrom = (EditText) findViewById(R.id.signalRangeFromTxt);
        signalRangeTo = (EditText) findViewById(R.id.signalRangeToTxt);
		try{
			input = assetManager.open(inputPath);
	          int size = input.available();
	          byte[] buffer = new byte[size];
	          input.read(buffer);
	          input.close();
	          // byte buffer into a string
	          String text = new String(buffer);
	          signalDataArr = text.split("\n");

		}catch(Exception e){
			Log.d("SpecGram2","Exception= "+e);
		}
		Log.d("audioBuf size ","Exception= "+signalDataArr.length);
		// init example series data
		GraphViewData[] initFftSignalMagnitudeSeriesData = new GraphViewData[(int)FFT_WINDOWS_LEN];
		for(int i = 0; i < (int)FFT_WINDOWS_LEN; i++){
			
			initFftSignalMagnitudeSeriesData[i] = new GraphViewData( i, 0);
			
		}
		fftSignalMagnitudeSeries = new GraphViewSeries(initFftSignalMagnitudeSeriesData);

		graphView = new BarGraphView(
				this // context
				, "Realtime Frequency Domain" // heading
		);

		graphView.addSeries(fftSignalMagnitudeSeries); // data
		graphView.setViewPort(1, 40);
		
		LinearLayout layout = (LinearLayout) findViewById(R.id.realtimefftlayout);
		layout.addView(graphView);

		// ----------
		realtimeEcgSignalSeries = new GraphViewSeries(new GraphViewData[] {
				new GraphViewData(0, 0.0d)
		});
		
		graphView = new LineGraphView(
				this
				, "Real-Time ECG Signal Measure"
		);
		graphView.addSeries(realtimeEcgSignalSeries); // data
		graphView.setViewPort(1, 1500);
		graphView.setHorizontalScrollBarEnabled(true);
		graphView.setScrollable(true);
		
		layout = (LinearLayout) findViewById(R.id.graph2);
		layout.addView(graphView);
		runOnUiThread(new Runnable() {
			public void run() {
				heartrateLabel.setText(String.valueOf(heartRate));
				
			}
			 });
    }
	
	
    @Override
	protected void onPause() {
		mHandler.removeCallbacks(mTimer1);
		mHandler.removeCallbacks(mTimer2);
		mHandler.removeCallbacks(mTimer3);
		super.onPause();
	}
    
	@Override
	protected void onResume() {
		super.onResume();
		mTimer1 = new Runnable() {
			//@Override
			public void run() {
				if(isPlot == true){
					if(graph2LastXValue >= FFT_WINDOWS_LEN){
						GraphViewData[] tmpGVD = new GraphViewData[(int)FFT_WINDOWS_LEN];
						tmpGVD = realtimeFftCalculation(graph2LastXValue, signalDataArr, FFT_WINDOWS_LEN);
						fftSignalMagnitudeSeries.resetData(tmpGVD);
					}
					else{
						
					}
					
				}
				else{
					
				}
				mHandler.postDelayed(this, 500);
			}
		};
		mHandler.postDelayed(mTimer1, 500);
		
		mTimer2 = new Runnable() {
			
			//@Override
			public void run() {
				if(isPlot == true){
					if((n < signalDataArr.length) && (n + PULSE_WIDTH < signalDataArr.length)){
						for (int k = 0; k < PULSE_WIDTH; k = k + 10){
							graph2LastXValue = n+k;
							graph2LastYValue = Double.parseDouble(signalDataArr[n+k]);
							realtimeEcgSignalSeries.appendData(new GraphViewData(graph2LastXValue, graph2LastYValue), true);
							
							if((graph2LastYValue >=PULSE_COUNT_THEADSHOLD) && (isPulseChecklock == false)){
								tempPulseLockVal = graph2LastYValue;
								isPulseChecklock = true;
								
							}
							else if((graph2LastYValue <= tempPulseLockVal) && (isPulseChecklock == true)){
								pulseCountAcc++;
								isPulseChecklock = false;
							}
							else{
								
							}
						}
						n = n + PULSE_WIDTH;
						
					}
					else{
						n = 0;
					}
				}
				else{
					
				}		
				mHandler.postDelayed(this, 500);
			}
		};
		mHandler.postDelayed(mTimer2, 500);
		
		mTimer3 = new Runnable() {
			//@Override
			public void run() {
				if(isPlot == true){
					heartRate = pulseCountAcc * 6;
					heartrateLabel.setText(String.valueOf(heartRate));
					pulseCountAcc = 0;
				}
				else{
					
				}
				mHandler.postDelayed(this, 10000);
			}
		};
		mHandler.postDelayed(mTimer3, 10000);
	}
}