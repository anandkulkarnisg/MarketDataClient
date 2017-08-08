package com.marketdataclient.icici;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.marketdataclient.icici.ICICIResultParser.exchangeInfo;

public class ICICIWorker implements Runnable
{
	final static Logger logger = LogManager.getLogger(ICICIWorker.class);
	
	private String stockName;
	static AtomicInteger atomicInteger = new AtomicInteger(0);
	static boolean printTickResults = true;
	static boolean isNSE = false;
	static boolean isBSE = false;
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
		ICICIWorker.destination = destination;
	}

	public static int getCycleSleepDuration()
	{
		return cycleSleepDuration;
	}

	public static void setCycleSleepDuration(int cycleSleepDuration)
	{
		ICICIWorker.cycleSleepDuration = cycleSleepDuration;
	}

	public static long getTickSequenceLimit()
	{
		return tickSequenceLimit;
	}

	public static void setTickSequenceLimit(long tickSequenceLimit)
	{
		ICICIWorker.tickSequenceLimit = tickSequenceLimit;
	}

	public static boolean isBSE()
	{
		return isBSE;
	}

	public static void setBSE(boolean isBSE)
	{
		ICICIWorker.isBSE = isBSE;
	}

	public static boolean isNSE()
	{
		return isNSE;
	}

	public static void setNSE(boolean isNSE)
	{
		ICICIWorker.isNSE = isNSE;
	}

	public static boolean isPrintTickResults()
	{
		return printTickResults;
	}

	public static void setPrintTickResults(boolean printTickResults)
	{
		ICICIWorker.printTickResults = printTickResults;
	}

	public ICICIWorker(String name)
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
		ICICIWorker.atomicInteger.set(atomicInteger);
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
		ICICIPrices priceItem = new ICICIPrices(stockName);
		long counter = 0;
		while (counter < getTickSequenceLimit())
		{
			Map<String, Object> streamResultMap = priceItem.streamPrices();
			if (!streamResultMap.isEmpty())
			{
				if (isPrintTickResults())
				{
					ICICIHelperUtils.printResults(streamResultMap, stockName, counter);
				} else
				{
					if (isNSE())
					{
						ICICIResultParser nseResultParser = new ICICIResultParser(streamResultMap, exchangeInfo.NSE);
						ICICIHelperUtils.csvFormatResultPrinter(nseResultParser, stockName, counter);
					}

					if (isBSE())
					{
						ICICIResultParser bseResultParser = new ICICIResultParser(streamResultMap, exchangeInfo.BSE);
						ICICIHelperUtils.csvFormatResultPrinter(bseResultParser, stockName, counter);

					}
				}
			} else
			{
				counter = getTickSequenceLimit();
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
