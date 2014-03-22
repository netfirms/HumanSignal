package com.taweechai.humansignal.realtimegraph;

public class Point {
	
	private int x;
	private float y;
	
	public Point( int x, float audioBuf)
	{
		this.x = x;
		this.y = audioBuf;
	}
	
	public int getX() {
		return x;
	}

	public float getY() {
		return y;
	}
	
}
