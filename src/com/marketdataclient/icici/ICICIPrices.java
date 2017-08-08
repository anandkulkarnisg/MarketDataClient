package com.marketdataclient.icici;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.marketdataclient.icici.ICICIResultParser.exchangeInfo;

public class ICICIPrices extends LivePrices
{
	final static Logger logger = LogManager.getLogger(ICICIPrices.class);
	private static final String ICICI_QUOTEBASE_URL = "http://getquote.icicidirect.com/trading_stock_quote.aspx?Symbol=";
	private String symbol;
	String urlString = ICICI_QUOTEBASE_URL;
	private int lastHashCode;
	private static Map<String, Pair<Integer, Integer>> initialQuoteMap;
	private Map<String, Pair<Integer, Integer>> equityQuoteMap;
	private Map<Integer, String> requiredLines;
	private int parserLineLowerBound;
	private int parserLineUpperBound;
	private boolean mapStatus;
	private static final Integer syncKey = Integer.MAX_VALUE;

	public ICICIPrices()
	{
		symbol = "INFTEC";
		lastHashCode = 0;
		urlString += symbol;
		parserLineLowerBound = Integer.MAX_VALUE;
		parserLineUpperBound = Integer.MIN_VALUE;

		if (initialQuoteMap == null)
			loadInitialQuoteMap();

		if (equityQuoteMap == null)
			loadEquityFieldMapFromConfig();
	}

	public ICICIPrices(String sym)
	{
		symbol = sym;
		lastHashCode = 0;
		urlString += symbol;
		parserLineLowerBound = Integer.MAX_VALUE;
		parserLineUpperBound = Integer.MIN_VALUE;

		if (initialQuoteMap == null)
			loadInitialQuoteMap();

		if (equityQuoteMap == null)
			loadEquityFieldMapFromConfig();
	}

	public void fetchRawICICIStream(String pattern)
	{
		printRawLiveStream(urlString);
	}

	public int getHashCode()
	{
		return (lastHashCode);
	}

	private void loadEquityFieldMapFromConfig()
	{
		equityQuoteMap = new ConcurrentHashMap<String, Pair<Integer, Integer>>();
		for (Map.Entry<String, Pair<Integer, Integer>> mapItem : initialQuoteMap.entrySet())
			equityQuoteMap.put(mapItem.getKey(), mapItem.getValue());

		// We need to fetch a one time page and parse to get the actual
		// field
		// offset for each item of the map.
		// Ex:- LAST TRADE PRICE happens to be at 161 then the NSE price is
		// at
		// +3 = 164 and BSE price is at +6 = 167

		getRawLiveStream(urlString);
		String[] liveStreamData = getRawStreamAsArray();
		int counter = 0;

		for (int i = 0; i < liveStreamData.length; ++i)
		{
			String bufferString = liveStreamData[i];
			{
				if (bufferString != null && !bufferString.isEmpty())
				{
					Pair<Boolean, String> resultPair = isAValidField(bufferString, equityQuoteMap.keySet());
					if (resultPair.getLeft())
					{
						Pair<Integer, Integer> integerPair = equityQuoteMap.get(resultPair.getRight());
						Integer nseItemOffSet = counter + integerPair.getLeft();
						Integer bseItemOffSet = counter + integerPair.getRight();
						Integer maxVal = (nseItemOffSet > bseItemOffSet) ? nseItemOffSet : bseItemOffSet;
						Integer minVal = (nseItemOffSet < bseItemOffSet) ? nseItemOffSet : bseItemOffSet;
						if (minVal < parserLineLowerBound)
							parserLineLowerBound = minVal;
						if (maxVal > parserLineUpperBound)
							parserLineUpperBound = maxVal;
						equityQuoteMap.put(resultPair.getRight(), Pair.of(nseItemOffSet, bseItemOffSet));
					}
					++counter;
				}
			}
		}

		requiredLines = getLineNumFromEquityQuoteMap();
		if (equityQuoteMap.isEmpty())
			mapStatus = false;
		else
			mapStatus = true;
	}

	private Map<Integer, String> getLineNumFromEquityQuoteMap()
	{
		Map<Integer, String> resultList = new ConcurrentHashMap<Integer, String>();
		for (Map.Entry<String, Pair<Integer, Integer>> mapItem : equityQuoteMap.entrySet())
		{
			String key = mapItem.getKey();
			if (key.contains("<b>*</b> "))
				key = key.replace("<b>*</b> ", "");
			resultList.put(mapItem.getValue().getLeft(), key + " " + exchangeInfo.NSE.toString());
			resultList.put(mapItem.getValue().getRight(), key + " " + exchangeInfo.BSE.toString());
		}

		return (resultList);
	}

	private Pair<Boolean, String> isAValidField(String itemString, Set<String> keySet)
	{

		for (String item : keySet)
		{
			if (itemString.trim().equals(item))
				return (Pair.of(true, item));
		}
		return (Pair.of(false, ""));

	}

	public Map<String, Object> streamPrices()
	{
		loadEquityFieldMapFromConfig();
		String[] liveStreamData = getRawStreamAsArray();
		Map<String, Object> result = new ConcurrentHashMap<String, Object>();
		String bufferString = null;

		for (int i = parserLineLowerBound; i <= parserLineUpperBound; ++i)
		{
			bufferString = liveStreamData[i];
			{
				if (bufferString != null && !bufferString.isEmpty())
				{
					if (requiredLines.keySet().contains(i))
						result.put(requiredLines.get(i), bufferString.trim());
				}
			}
		}

		return (result);
	}

	public Map<Integer, String> getParserMap()
	{
		return requiredLines;
	}

	public boolean getMapStatus()
	{
		return mapStatus;
	}

	public void loadInitialQuoteMap()
	{
		synchronized (syncKey)
		{
			initialQuoteMap = new ConcurrentHashMap<String, Pair<Integer, Integer>>();
			String filePath = System.getProperty("user.dir") + "/resources/icici.eqconfig.cfg";
			BufferedReader b = null;
			File f = new File(filePath);
			try
			{
				b = new BufferedReader(new FileReader(f));
			} catch (FileNotFoundException e)
			{
				logger.info("Failed to get the config file. please verify the path " + filePath + ". Exiting with failure code = 1.");
				System.exit(1);
			}
			String readLine = "";
			try
			{
				while ((readLine = b.readLine()) != null)
				{
					StringTokenizer tokens = new StringTokenizer(readLine, "|");
					ArrayList<String> resultList = new ArrayList<String>();
					while (tokens.hasMoreTokens())
						resultList.add(tokens.nextToken());

					String fieldName = resultList.get(0);
					if (!fieldName.equals("COLUMN"))
					{
						Integer val1 = Integer.parseInt(resultList.get(1));
						Integer val2 = Integer.parseInt(resultList.get(2));
						Pair<Integer, Integer> pair = Pair.of(val1, val2);
						initialQuoteMap.put(fieldName, pair);
					}
				}
			} catch (IOException e)
			{
				logger.error("Error loading the config file " + filePath + ".Without this file live stream can not be parsed or processed. Exiting with failure code = 1.");
				System.exit(1);
			}

			try
			{
				b.close();
			} catch (IOException e)
			{
				logger.warn("Error while closing the stream for the config file " + filePath + ". Please verify what happened here.");
				logger.warn(e.getStackTrace().toString());
			}
		}
	}
}
