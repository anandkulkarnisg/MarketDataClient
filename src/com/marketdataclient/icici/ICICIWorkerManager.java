package com.marketdataclient.icici;

import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.marketdataclient.configmanager.MarketDataConfigManager;
import com.marketdataclient.icici.ICICIResultParser.exchangeInfo;
import com.marketdataclient.icici.ICICIWorker.tickDestination;
import com.marketdataclient.kdbfeedhandler.ICICIKdbTickPublisher;
import com.marketdataclient.kdbfeedhandler.ICICITickDataQueue;
import com.marketdataclient.kdbfeedhandler.ICICITickEvent;

public class ICICIWorkerManager
{
	private enum executionType
	{
		Threaded, Serial
	}

	final static Logger logger = LogManager.getLogger(ICICIWorkerManager.class);
	private static MarketDataConfigManager config;
	private static String[] stocks;
	private static ICICITickDataQueue tickDataQueue;	

	public static ICICITickDataQueue getTickDataQueue()
	{
		return tickDataQueue;
	}

	public static void setTickDataQueue(ArrayBlockingQueue<ICICITickEvent> dataQueue)
	{
		tickDataQueue.setTickDataQueue(dataQueue);
	}
		
	public static String[] getStocks()
	{
		return stocks;
	}

	public ICICIWorkerManager(MarketDataConfigManager configItem, String[] symbolList)
	{
		config = configItem;
		stocks = symbolList;
		tickDataQueue = new ICICITickDataQueue();
	}

	public void start()
	{

		String executionMode = getConfig().getStringConfig("executionMode", "Threaded");
		if (executionMode.equals(executionType.Threaded.toString()))
			runInThreadedMode(stocks, config);
		else
			runInSerialMode(stocks, config);
	}

	public static MarketDataConfigManager getConfig()
	{
		return config;
	}

	static private void runInThreadedMode(String[] stockItems, MarketDataConfigManager marketDataConfig)
	{
		Boolean tickDisplayMode = marketDataConfig.getBooleanConfig("displayMode", false);
		ExecutorService executor = Executors.newFixedThreadPool(stockItems.length);

		ICICIWorker.setPrintTickResults(tickDisplayMode);
		Boolean NSEStatus = marketDataConfig.getBooleanConfig("NSEStatus", true);
		Boolean BSEStatus = marketDataConfig.getBooleanConfig("BSEStatus", true);
		ICICIWorker.setNSE(NSEStatus);
		ICICIWorker.setBSE(BSEStatus);
		String resultDestination = marketDataConfig.getStringConfig("resultWriterDestination", "KDB");
		ICICIWorker.setDestination(tickDestination.valueOf(resultDestination));

		Long tickSequenceLimit = marketDataConfig.getLongConfig("maxTickLimit", 1000);
		ICICIWorker.setTickSequenceLimit(tickSequenceLimit);

		int cycleSleepDuration = marketDataConfig.getIntegerConfig("cycleSleepDurationMilliSecs", 100);
		ICICIWorker.setCycleSleepDuration(cycleSleepDuration);

		// Now create a arrayBlockingQueue of size number of stock items
		// multiplied by maxTickLimit to make sure we have enough size to cover
		// at all times. More aggressive strategy may be to allocate only around
		// half of the size of above to save on memory.

		double capacityAdjFactor = marketDataConfig.getDoubleConfig("queueCapacityAdjFactor", 0.5);
		int queueSize = (int) ((tickSequenceLimit.intValue() * stockItems.length) * capacityAdjFactor);
		setTickDataQueue(new ArrayBlockingQueue<ICICITickEvent>(queueSize));

		logger.info("Set the below configuration for the icici worker threades. The tick display mode is set to " + tickDisplayMode.toString());
		logger.info("NSE Publishing is set to " + NSEStatus);
		logger.info("BSE Publishing status is set to " + BSEStatus + ".");

		if (!tickDisplayMode)
			ICICIHelperUtils.printCsvHeader();

		for (int i = 0; i < stockItems.length; i++)
		{
			Runnable worker = new ICICIWorker(stockItems[i]);
			executor.execute(worker);
		}

		// Start the KDB processing thread via below.
		int kdbPublisherThreads = marketDataConfig.getIntegerConfig("KdbPublishThreads", 2);
		String kdbServer = marketDataConfig.getStringConfig("kdbServer", "localhost");
		int kdbPort = marketDataConfig.getIntegerConfig("kdbPort", 5000);

		ICICIKdbTickPublisher kdbTickPublisher = new ICICIKdbTickPublisher(kdbPublisherThreads, kdbServer, kdbPort);
		kdbTickPublisher.start();

		while (!ICICIWorker.allThreadsFinished())
		{
			try
			{
				Thread.sleep(1000);
				logger.info("There are " + tickDataQueue.getTickDataQueueSize() + " Items in the queue waiting to be processed");
			} catch (InterruptedException e)
			{

			}
		}

		logger.info("All stock publisher threads have finished.");

		while (tickDataQueue.getTickDataQueueSize() > 0)
		{
			logger.info("There are still " + tickDataQueue.getTickDataQueueSize() + " Items in the queue waiting to be processed");
			try
			{
				Thread.sleep(1000);
			} catch (InterruptedException e)
			{

			}
		}

		// Signal to stop the KDB publishing threads now.
		ICICIKdbTickPublisher.setKeepPublishing(false);
		executor.shutdownNow();
		kdbTickPublisher.getExecutor().shutdownNow();

	}

	static private void runInSerialMode(String[] stockItems, MarketDataConfigManager marketDataConfig)
	{
		boolean tickDisplayMode = Boolean.getBoolean(marketDataConfig.getConfigMap().get("displayMode"));
		int tickCount = 1;
		while (tickCount < 1000)
		{
			for (int i = 0; i < stockItems.length; ++i)
			{
				String stockName = stockItems[i];
				ICICIPrices priceItem = new ICICIPrices(stockName);
				Map<String, Object> streamResultMap = priceItem.streamPrices();
				if (!streamResultMap.isEmpty())
				{
					ICICIResultParser nseResultParser = new ICICIResultParser(streamResultMap, exchangeInfo.NSE);
					if (tickDisplayMode)
						ICICIHelperUtils.printResults(streamResultMap, stockName, tickCount);
					else
						ICICIHelperUtils.csvFormatResultPrinter(nseResultParser, stockName, tickCount);
				}
			}
			logger.info("sequence count at " + tickCount);
			++tickCount;
		}
	}

}
