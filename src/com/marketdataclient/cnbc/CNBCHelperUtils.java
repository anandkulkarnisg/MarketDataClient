package com.marketdataclient.cnbc;

import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.marketdataclient.cnbc.CNBCWorker.tickDestination;
import com.marketdataclient.kdbfeedhandler.CNBCTickEvent;

public class CNBCHelperUtils
{

	final static Logger logger = LogManager.getLogger(CNBCHelperUtils.class);

	public static void printResults(Map<String, Object> streamResultMap, String stockName, long counter)
	{
		System.out.println("---------------------------- " + stockName + ", tick sequence = " + counter + "START ---------------------------------");
		for (Map.Entry<String, Object> entry : streamResultMap.entrySet())
			System.out.println("key=" + entry.getKey() + " --->  value=" + entry.getValue().toString());
		System.out.println("----------------------------" + stockName + ", tick sequence = " + counter + " END ---------------------------------");
		System.out.println("Finished tick count sequence =" + counter);
	}

	public static void csvFormatResultPrinter(CNBCResultParser resultParser, String stockName, long counter)
	{

		CNBCTickEvent tickEvent = null;
		boolean overAllParseStatus = false;

		try
		{
			// Test all double Prices.

			String shortName = resultParser.getShortName();
			double lastPrice = resultParser.getLastPrice();
			double change = resultParser.getChange();
			double changePercent = resultParser.getChangePercent();
			String provider = resultParser.getProvider();
			boolean cacheServed = resultParser.getCacheServed();
			String cacheTime = resultParser.getCacheTime();
			String responseTime = resultParser.getResponseTime();
			boolean realTimeStatus = resultParser.getRealTimeStatus();

			tickEvent = new CNBCTickEvent((int) counter, stockName, shortName, lastPrice, change, changePercent, provider, cacheServed, cacheTime, responseTime, realTimeStatus);
			overAllParseStatus = true;

		} catch (Exception e)
		{
			logger.error(
					"Caught exception while parsing and processing data for symbol = " + stockName + ", The tick sequence was at " + counter + "The exception details are below");
			logger.error(e.getStackTrace().toString());
			e.printStackTrace();
		}

		if (overAllParseStatus)
		{

			if (CNBCWorker.getDestination() == tickDestination.STDOUT)
			{
				System.out.println(tickEvent.toCsvFormart());
				logger.info("Successfully parsed and printed to stdout the data for symbol = " + stockName + ", The tick sequence was at " + counter + ".");
			}

			if (CNBCWorker.getDestination() == tickDestination.KDB)
			{

				try
				{
					CNBCWorkerManager.getTickDataQueue().getTickDataQueue().add(tickEvent);
					logger.info("Successfully parsed and published the data for symbol = " + stockName + ", The tick sequence was at " + counter + ".");
				} catch (Exception e)
				{
					logger.warn("Please verify that TickDataQueue status. It seems pushing data to this queue produced an exception. The current of the queue = "
							+ CNBCWorkerManager.getTickDataQueue().getTickDataQueueSize());
					logger.warn("The capacity of the TickDataQueue is =" + CNBCWorkerManager.getTickDataQueue().getTickDataQueue().remainingCapacity());
				}
			}
		}
	}

	public static void printCsvHeader()
	{
		logger.info("csv header is");
		logger.info("tickSequence,sym,shortName,lastPrice,change,changePercent,provider,cacheServed,cacheTime,responseTime,realTimeStatus");
	}

}
