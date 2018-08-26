package com.marketdataclient.configmanager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.configuration.tree.ConfigurationNode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MarketDataConfigManager
{
	private XMLConfiguration config;
	private static final String defaultConfigPath = System.getProperty("user.dir") + "/resources/marketDataClientConfig.xml";
	private static Map<String, String> configMap;
	private static String configPrefix;
	private static String configFilePath;

	public static String getConfigFilePath()
	{
		return configFilePath;
	}

	public static String getConfigPrefix()
	{
		return configPrefix;
	}

	final static Logger logger = LogManager.getLogger(MarketDataConfigManager.class);

	public Map<String, String> getConfigMap()
	{
		return configMap;
	}

	private void loadConfigAsMap()
	{
		configMap = new ConcurrentHashMap<String, String>();
		ConfigurationNode node = config.getRootNode();
		for (ConfigurationNode c : node.getChildren("entry"))
		{
			String key = (String) c.getAttribute(0).getValue();
			String value = (String) c.getValue();
			configMap.put(key, value);
		}
		logger.info("Successfully Loaded " + configMap.size() + " items into the properties config file");
	}

	public MarketDataConfigManager(String configType)
	{

		configPrefix = configType.toLowerCase();
		try
		{
			String filePath = "";
			if (!configType.isEmpty())
			{
				String patternFrom = "marketDataClientConfig.xml";
				String patternTo = configPrefix + "." + patternFrom;
				filePath = defaultConfigPath.replaceAll(patternFrom, patternTo);
			} else
				filePath = defaultConfigPath;

			config = new XMLConfiguration(filePath);
			config.setValidating(true);
			loadConfigAsMap();
		} catch (ConfigurationException e)
		{
			logger.error("Error Loading XMLConfiguration for the properties file = " + defaultConfigPath);
			logger.error("The stack Trace is dumped below" + e.getStackTrace().toString());
		}
	}

	public int getIntegerConfig(String key, int defaultValue)
	{
		String result = getConfigMap().get(key);
		int returnResult;
		if (result == null)
		{
			return (defaultValue);
		} else
		{
			try
			{
				returnResult = Integer.parseInt(result);
			} catch (NumberFormatException e)
			{
				return (defaultValue);
			}
		}
		return (returnResult);
	}

	public double getDoubleConfig(String key, double defaultValue)
	{
		String result = getConfigMap().get(key);
		double returnResult;
		if (result == null)
		{
			return (defaultValue);
		} else
		{
			try
			{
				returnResult = Double.parseDouble(result);
			} catch (NumberFormatException e)
			{
				return (defaultValue);
			}
		}
		return (returnResult);
	}

	public boolean getBooleanConfig(String key, boolean defaultValue)
	{
		String result = getConfigMap().get(key);
		boolean returnResult;
		if (result == null)
		{
			return (defaultValue);
		} else
		{
			returnResult = Boolean.parseBoolean(result);
		}
		return (returnResult);
	}

	public long getLongConfig(String key, long defaultValue)
	{
		String result = getConfigMap().get(key);
		long returnResult;
		if (result == null)
		{
			return (defaultValue);
		} else
		{
			try
			{
				returnResult = Long.parseLong(result);
			} catch (NumberFormatException e)
			{
				return (defaultValue);
			}
		}
		return (returnResult);
	}

	public String getStringConfig(String key, String defaultValue)
	{
		String result = getConfigMap().get(key);
		if (result == null)
		{
			return (defaultValue);
		} else
		{
			return (result);
		}
	}

	// public static class StringBar extends Bar<String> {
	// public String get() {
	// return "";
	// }
	// }

	public String[] loadEquitySymbolsFromConfigFile()
	{

		BufferedReader b = null;
		ArrayList<String> stockSymbols = new ArrayList<String>();
		String filePath = System.getProperty("user.dir") + "/resources/" + configPrefix + ".eqsymbols.cfg";
		File f = new File(filePath);
		try
		{
			b = new BufferedReader(new FileReader(f));
			logger.info("Successfully Loaded the config file at path = " + filePath);

		} catch (FileNotFoundException e)
		{
			logger.error("Failed to get the config file. please verify the path = " + filePath);
			logger.error("Exiting the Application");
			System.exit(1);
		}
		String readLine = "";
		try
		{
			while ((readLine = b.readLine()) != null)
			{
				if (!readLine.trim().isEmpty())
					stockSymbols.add(readLine);
			}
		} catch (IOException e)
		{
			logger.warn("Error while reading the equity symbols config file. returning empty array");
		}

		try
		{
			b.close();
		} catch (IOException e)
		{
			logger.error("Error closing the file stream for the equity symbols config file. Please verify");
			logger.warn("The stack trace is as follows " + e.getStackTrace().toString());
		}

		if (stockSymbols.size() == 0)
		{
			logger.error("Can not load or find any stock symbols from config file. Exiting the application with failure status");
			System.exit(1);
		} else
			logger.info("Successfully loaded " + stockSymbols.size() + " symbols from the config file.");

		String[] stocksArray = new String[stockSymbols.size()];
		stocksArray = stockSymbols.toArray(stocksArray);
		return (stocksArray);
	}

}
