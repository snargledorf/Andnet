package com.theeste.andnet.System.Threading;

import java.util.*;

public enum ThreadPriority 
{
	Highest(10),
	//AboveNormal(10),
	Normal(5),
	//BelowNormal(1),
	Lowest(1);
	
	private static final Map<Integer, ThreadPriority> m_Lookup = new HashMap<Integer, ThreadPriority>();
	
	static 
	{
		for(ThreadPriority tp : ThreadPriority.values())
		{
			m_Lookup.put(tp.getValue(), tp);
		}
	}
	
	private int m_Value;
	
	private ThreadPriority(int value)
	{
		m_Value = value;
	}
	
	public int getValue()
	{
		return m_Value;
	}
	
	public static ThreadPriority get(int value)
	{
		return m_Lookup.get(value);
	}
}
