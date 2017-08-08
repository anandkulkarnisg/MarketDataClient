package com.marketdataclient.kdbfeedhandler;

import java.util.concurrent.BlockingQueue;

public class CNBCTickDataQueue implements TickDataQueue<CNBCTickEvent>
{
	private static BlockingQueue<CNBCTickEvent> tickDataQueue;

	@Override
	public BlockingQueue<CNBCTickEvent> getTickDataQueue()
	{
		return tickDataQueue;
	}

	@Override
	public void setTickDataQueue(BlockingQueue<CNBCTickEvent> dataQueue)
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