package com.marketdataclient.kdbfeedhandler;

import java.util.concurrent.BlockingQueue;

public interface TickDataQueue<T>
{
	public BlockingQueue<T> getTickDataQueue();
	public void setTickDataQueue(BlockingQueue<T> dataQueue);
}
