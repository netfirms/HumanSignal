package com.taweechai.humansignal.realtimegraph;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.TimeSeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.content.Context;
import android.graphics.Color;

public class HearthrateLineGraph {

	private GraphicalView view;
	
	private TimeSeries dataset2 = new TimeSeries("Realtime ECG Signal Plotting"); 
	private XYMultipleSeriesDataset mDataset2 = new XYMultipleSeriesDataset();
	
	private XYSeriesRenderer renderer2 = new XYSeriesRenderer(); // This will be used to customize line 1
	private XYMultipleSeriesRenderer mRenderer2 = new XYMultipleSeriesRenderer(); // Holds a collection of XYSeriesRenderer and customizes the graph
	
	public HearthrateLineGraph()
	{
		// Add single dataset to multiple dataset
		mDataset2.addSeries(dataset2);
		
		// Customization time for line 1!
		renderer2.setColor(Color.GREEN);
		renderer2.setPointStyle(PointStyle.X);
		renderer2.setFillPoints(true);
		
		// Enable Zoom
		mRenderer2.setZoomButtonsVisible(false);
		mRenderer2.setXTitle("Sample n");
		mRenderer2.setYTitle("Amplitude ");
		mRenderer2.setShowGrid(true);
		
		// Add single renderer to multiple renderer
		mRenderer2.addSeriesRenderer(renderer2);	
	}
	
	public GraphicalView getView(Context context) 
	{
		view =  ChartFactory.getLineChartView(context, mDataset2, mRenderer2);
		return view;
	}
	
	public void addNewPoints(Point p)
	{
		dataset2.add(p.getX(), p.getY());
	}
	
}
