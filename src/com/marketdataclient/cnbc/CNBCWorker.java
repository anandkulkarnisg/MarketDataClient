package com.marketdataclient.cnbc;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CNBCWorker implements Runnable
{
	final static Logger logger = LogManager.getLogger(CNBCWorker.class);

	private String stockName;
	static AtomicInteger atomicInteger = new AtomicInteger(0);
	static boolean printTickResults = true;
	static long tickSequenceLimit = 10000;
	static int cycleSleepDuration = 0;

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
		setAtomicInteger(atomicInteger.get() + 1);
	}

	public static int getAtomicInteger()
	{
		return atomicInteger.get();
	}

	public static void setAtomicInteger(int atomicInteger)
	{
		CNBCWorker.atomicInteger.set(atomicInteger);
	}

	public static boolean allThreadsFinished()
	{
		if (getAtomicInteger() == 0)
			return (true);
		else
			return (false);
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

		setAtomicInteger(atomicInteger.get() - 1);
	}

}
