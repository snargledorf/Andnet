package com.theeste.andnet.System;

public class Random
{
	private java.util.Random m_InternalRandom;

	public Random()
	{
		this(0);
	}
	
	public Random(int seed)
	{
		m_InternalRandom = new java.util.Random(seed);
	}
	
	public int Next()
	{
		return m_InternalRandom.nextInt();
	}
	
	public int Next(int maxValue)
	{
		return m_InternalRandom.nextInt(maxValue);
	}
	
	public int Next(int minValue, int maxValue)
	{
		int result;
		
		do 
		{
			result = m_InternalRandom.nextInt(maxValue);
		} 
		while (result <= minValue);
		
		return result;
	}
	
	public void NextBytes(byte[] buffer)
	{
		m_InternalRandom.nextBytes(buffer);
	}
	
	public double NextDouble()
	{
		return m_InternalRandom.nextDouble();
	}
}
