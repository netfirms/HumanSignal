package com.taweechai.humansignal;

import com.badlogic.gdx.audio.analysis.FFT;
import com.jjoe64.graphview.BarGraphView;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.LineGraphView;
import com.jjoe64.graphview.GraphView.GraphViewData;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

public class FFTAnalysisActivity extends Activity {
	String rangeForm;
	String rangeTo;
	String[] signalDataArray;
	TextView fromSignal, toSignal, signalData;
	
	private GraphView graphView;
	private GraphViewSeries rawSignalSeries;
	private GraphViewSeries fftSignalMagnitudeSeries;
	private GraphViewSeries fftSignalPhaseSeries;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fftanalysis);
		
		fromSignal = (TextView) findViewById(R.id.fromSignal);
		toSignal = (TextView) findViewById(R.id.toSignal);
		signalData = (TextView) findViewById(R.id.signalDataTxt);
		
		Intent intent = getIntent();
        rangeForm = intent.getStringExtra(HumanSignalActivity.RANGEFROM);
        rangeTo = intent.getStringExtra(HumanSignalActivity.RANGETO);
        signalDataArray = intent.getStringArrayExtra(HumanSignalActivity.SIGNALDATA);
        fromSignal.setText(rangeForm);
        toSignal.setText(rangeTo);
        System.out.println(signalDataArray.length);
        for (int i = 0; i < signalDataArray.length; i++){
        	System.out.println(signalDataArray[i]);
        }
        GraphViewData[] gDataArr = new GraphViewData[signalDataArray.length];
        for (int i = 0; i < signalDataArray.length ;i++){
        	gDataArr[i] = new GraphViewData( i, Double.parseDouble(signalDataArray[i]));
        }
        /*FFT operation section*/
        float[] array = new float[signalDataArray.length];
        for(int i = 0; i< array.length; i++){
        	array[i] = (float)Double.parseDouble(signalDataArray[i]);
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
        for (int i = 0; i < array.length ;i++){
        	
        }
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
          
        /*Graph plotting section*/
        rawSignalSeries = new GraphViewSeries(gDataArr);
		graphView = new LineGraphView(
				this
				, "ECG Captured Signal Measure"
		);
		((LineGraphView) graphView).setDrawBackground(true);
		graphView.addSeries(rawSignalSeries); // data
		
		graphView.setViewPort(1, 1024);
		graphView.setHorizontalScrollBarEnabled(true);
		graphView.setScrollable(true);
		graphView.setScalable(true);
		LinearLayout layout = (LinearLayout) findViewById(R.id.graph3);
		layout.addView(graphView);
		
		fftSignalMagnitudeSeries = new GraphViewSeries(gDataFftMagnitudeArr);
		fftSignalPhaseSeries = new GraphViewSeries(gDataFftPhaseArr);
		graphView = new BarGraphView(
				this
				, "ECG Signal FFT Converted"
		);
		//((BarGraphView) graphView).setDrawBackground(true);
		graphView.addSeries(fftSignalMagnitudeSeries); // data
		//graphView.addSeries(fftSignalPhaseSeries);
		
		graphView.setViewPort(1, 50);
		graphView.setHorizontalScrollBarEnabled(true);
		graphView.setScrollable(true);
		layout = (LinearLayout) findViewById(R.id.fftgraph1);
		layout.addView(graphView);
        
        //signalData.setText(signalDataArray.toString());
		
	}
	
	@Override
	protected void onPause() {
		
		super.onPause();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
	}

}
