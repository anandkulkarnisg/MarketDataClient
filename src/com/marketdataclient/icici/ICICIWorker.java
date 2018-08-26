package com.marketdataclient.icici;

import java.util.Map;
import java.util.concurrent.CountDownLatch;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.marketdataclient.icici.ICICIResultParser.exchangeInfo;

public class ICICIWorker implements Runnable
{
	final static Logger logger = LogManager.getLogger(ICICIWorker.class);

	private String stockName;
	static boolean printTickResults = true;
	static boolean isNSE = false;
	static boolean isBSE = false;
	static long tickSequenceLimit = 10000;
	static int cycleSleepDuration = 0;
	CountDownLatch latch;

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
	}

	public ICICIWorker(String name, CountDownLatch inputLatch)
	{
		stockName = name;
		latch = inputLatch;
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
						boolean nseResultStatus = ICICIHelperUtils.csvFormatResultPrinter(nseResultParser, stockName, counter);
						if (!nseResultStatus)
						{
							counter = getTickSequenceLimit();
							logger.fatal("The " + stockName + " Has issues with its page fetched. Parsing failed for NSE. Hence giving up on this symbol completely.");
							logger.fatal("Please investigate in debug mode for details.");
						}
					}

					if (isBSE())
					{
						ICICIResultParser bseResultParser = new ICICIResultParser(streamResultMap, exchangeInfo.BSE);
						boolean bseResultStatus = ICICIHelperUtils.csvFormatResultPrinter(bseResultParser, stockName, counter);
						if (!bseResultStatus)
						{
							counter = getTickSequenceLimit();
							logger.fatal("The " + stockName + " Has issues with its page fetched. Parsing failed for BSE. Hence giving up on this symbol completely.");
							logger.fatal("Please investigate in debug mode for details.");
						}
					}
				}
			} else
			{
				logger.fatal("The " + stockName + " Has problem with its page data or content and we could not derive any price fields from its content.Hence giving up further attempts.");
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

		latch.countDown();
	}

}
