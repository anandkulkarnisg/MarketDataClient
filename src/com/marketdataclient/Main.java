package com.marketdataclient;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.marketdataclient.icici.ICICIWorkerManager;
import com.marketdataclient.cnbc.CNBCWorkerManager;
import com.marketdataclient.configmanager.MarketDataConfigManager;

public class Main
{
	final static Logger logger = LogManager.getLogger(Main.class);

	private enum publisherType
	{
		ICICI, CNBC;
	}

	public static void main(String[] args)
	{
		// Set a default publisher Type.
		publisherType publisher = publisherType.CNBC;

		if (args.length > 0)
			publisher = publisherType.valueOf(args[0].toUpperCase());

		// Kick Start the application with initial log message.
		logger.info("Starting the MarketDataClient Application with publisherType = " + publisher.toString());

		// Load the properties Configuration file for the marketDataConfig.
		MarketDataConfigManager marketDataConfig = new MarketDataConfigManager(publisher.toString());

		// Load the stock symbols required for the WorkerManager.
		String[] symbolList = marketDataConfig.loadEquitySymbolsFromConfigFile();
		
		// Anand Kulkarni - 25/08/2018 - The ICICI Market data fetcher is not working as expected. Reason is entire page layout has changed on ICICI side.
		// NSE prices are no more publically available / visible on the page. BSE prices are available though. I am stopping this development work now.
		// CNBC market data fetcher does work still as expected since it uses a more stable quote page query framework.
		
		// Please consider ICICI market feed as unmaintained work further. It kicks out and exits now without running it.

		switch (publisher)
		{
		case ICICI:
			logger.error("The ICICI market feed is no longer maintained and does not work/fetch data as expected. This is due to NSE market data availability and page structure changes on ICICI side.");
			//ICICIWorkerManager iciciWorkerManager = new ICICIWorkerManager(marketDataConfig, symbolList);
			//iciciWorkerManager.start();
			break;

		case CNBC:
			CNBCWorkerManager cnbcWorkerManager = new CNBCWorkerManager(marketDataConfig, symbolList);
			cnbcWorkerManager.start();
			break;

		default:
			logger.info("unknown publisherType = " + publisher.toString() + ". Exiting the application.");

		}

		System.exit(0);
	}
}
