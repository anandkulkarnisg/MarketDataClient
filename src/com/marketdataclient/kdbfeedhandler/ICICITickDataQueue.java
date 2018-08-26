package com.marketdataclient.kdbfeedhandler;

import java.util.concurrent.BlockingQueue;

public class ICICITickDataQueue implements TickDataQueue<ICICITickEvent>
{
	private static BlockingQueue<ICICITickEvent> tickDataQueue;

	@Override
	public BlockingQueue<ICICITickEvent> getTickDataQueue()
	{
		return tickDataQueue;
	}

	@Override
	public void setTickDataQueue(BlockingQueue<ICICITickEvent> dataQueue)
	{
		tickDataQueue = dataQueue;
	}

	public int getTickDataQueueSize()
	{
		return (tickDataQueue.size());
	}

	public int getTickDataQueueCapacity()
	{
		return (tickDataQueue.remainingCapacity());
	}

}
