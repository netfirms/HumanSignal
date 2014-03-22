package com.taweechai.humansignal.realtimegraph;

import java.util.List;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.TimeSeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.content.Context;
import android.graphics.Color;

public class LineGraph {

	private GraphicalView view;
	
	private TimeSeries ekgDataset = new TimeSeries("Realtime EKG Signal Plotting"); 
	private TimeSeries ecgDataset = new TimeSeries("Realtime ECG Signal Plotting"); 
	private XYMultipleSeriesDataset mDataset = new XYMultipleSeriesDataset();
	
	private XYSeriesRenderer renderer = new XYSeriesRenderer(); // This will be used to customize line 1
	private XYSeriesRenderer renderer2 = new XYSeriesRenderer(); // This will be used to customize line 2
	private XYMultipleSeriesRenderer mRenderer = new XYMultipleSeriesRenderer(); // Holds a collection of XYSeriesRenderer and customizes the graph
	
	public LineGraph()
	{
		// Add single dataset to multiple dataset
		//mDataset.addSeries(ekgDataset);
		// Add second dataset to multiple dataset
		mDataset.addSeries(ecgDataset);
		
		// Customization time for line 1!
		/*renderer.setColor(Color.GREEN);
		renderer.setPointStyle(PointStyle.X);
		renderer.setFillPoints(true);*/
		
		// Customization time for line 2!
		renderer2.setColor(Color.YELLOW);
		renderer2.setPointStyle(PointStyle.X);
		renderer2.setFillPoints(true);
		
		// Enable Zoom
		mRenderer.setZoomButtonsVisible(false);
		mRenderer.setXTitle("Sample n");
		mRenderer.setYTitle("Amplitude ");
		mRenderer.setShowGrid(true);
		
		// Add single renderer to multiple renderer
		//mRenderer.addSeriesRenderer(renderer);	
		mRenderer.addSeriesRenderer(renderer2);	
	}
	
	public GraphicalView getView(Context context) 
	{
		view =  ChartFactory.getLineChartView(context, mDataset, mRenderer);
		return view;
	}
	
	public void addEkgNewPoints(Point p)
	{
		ekgDataset.add(p.getX(), p.getY());
	}
	
	public void addEcgNewPoints(List<Point> p)
	{
		for (int i = 0; i< p.size(); i++){
			ecgDataset.add(p.get(i).getX(), p.get(i).getY());
		}
		
	}
	
}
