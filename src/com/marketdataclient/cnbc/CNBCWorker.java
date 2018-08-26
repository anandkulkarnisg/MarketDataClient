package com.marketdataclient.cnbc;

import java.util.Map;
import java.util.concurrent.CountDownLatch;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CNBCWorker implements Runnable
{
	final static Logger logger = LogManager.getLogger(CNBCWorker.class);

	private String stockName;
	static boolean printTickResults = true;
	static long tickSequenceLimit = 10000;
	static int cycleSleepDuration = 0;
	private CountDownLatch latch;

	public enum tickDestination
	{
		STDOUT, KDB
	}

	private static tickDestination destination;

	public static tickDestination getDestination()
	{
		return destination;
	}

	public static void setDestination(tickDestination destination)
	{
		CNBCWorker.destination = destination;
	}

	public static int getCycleSleepDuration()
	{
		return cycleSleepDuration;
	}

	public static void setCycleSleepDuration(int cycleSleepDuration)
	{
		CNBCWorker.cycleSleepDuration = cycleSleepDuration;
	}

	public static long getTickSequenceLimit()
	{
		return tickSequenceLimit;
	}

	public static void setTickSequenceLimit(long tickSequenceLimit)
	{
		CNBCWorker.tickSequenceLimit = tickSequenceLimit;
	}

	public static boolean isPrintTickResults()
	{
		return printTickResults;
	}

	public static void setPrintTickResults(boolean printTickResults)
	{
		CNBCWorker.printTickResults = printTickResults;
	}

	public CNBCWorker(String name)
	{
		stockName = name;
	}
	
	public CNBCWorker(String name, CountDownLatch inputLatch)
	{
		stockName = name;
		latch = inputLatch;
	}

	@Override
	public void run()
	{
		CNBCPrices priceItem = new CNBCPrices(stockName);
		long counter = 0;
		while (counter < getTickSequenceLimit())
		{
			Map<String, Object> streamResultMap = priceItem.streamPrices();
			if (!streamResultMap.isEmpty())
			{
				if (isPrintTickResults())
				{
					CNBCHelperUtils.printResults(streamResultMap, stockName, counter);

				} else
				{
					CNBCResultParser cnbcResultParser = new CNBCResultParser(streamResultMap);
					CNBCHelperUtils.csvFormatResultPrinter(cnbcResultParser, stockName, counter);
				}
			} else
			{
//				counter = getTickSequenceLimit(); // We seem to be getting bad xml from CNBC hence commenting this.
			}

			try
			{
				Thread.sleep(cycleSleepDuration);
			} catch (InterruptedException e)
			{
			}
			++counter;
		}

		latch.countDown();
	}

}
