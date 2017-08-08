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
		publisherType publisher = publisherType.ICICI;

		if (args.length > 0)
			publisher = publisherType.valueOf(args[0].toUpperCase());

		// Kick Start the application with initial log message.
		logger.info("Starting the MarketDataClient Application with publisherType = " + publisher.toString());

		// Load the properties Configuration file for the marketDataConfig.
		MarketDataConfigManager marketDataConfig = new MarketDataConfigManager(publisher.toString());

		// Load the stock symbols required for the WorkerManager.
		String[] symbolList = marketDataConfig.loadEquitySymbolsFromConfigFile();

		switch (publisher)
		{
		case ICICI:
			ICICIWorkerManager iciciWorkerManager = new ICICIWorkerManager(marketDataConfig, symbolList);
			iciciWorkerManager.start();
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
