package com.taweechai.humansignal.realtimegraph;

import java.util.Random;

public class MockData {

	// x is the day number, 0, 1, 2, 3
	public static Point getDataFromReceiver(int x)
	{
		return new Point(x, generateRandomData());
	}
	
	private static int generateRandomData()
	{
		Random random = new Random();
		return random.nextInt(40);
	}
}
