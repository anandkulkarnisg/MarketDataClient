package com.marketdataclient.yahoo;

import java.net.*;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.io.*;

public class YahooPrices
{
	private String symbol;
	private String fields;
	private int lastHashCode;

	public YahooPrices()
	{
		symbol = "SGDINR=X";
		fields = "nab";
		lastHashCode = 0;
	}

	public YahooPrices(String sym, String flds)
	{
		symbol = sym;
		fields = flds;
	}

	private String fetchYahooPrice()
	{
		String urlString = "http://download.finance.yahoo.com/d/quotes.csv?s=" + symbol + "&f=" + fields;
		String result = new String("Error");
		URL yahooUrl = null;
		try
		{
			yahooUrl = new URL(urlString);
		} catch (MalformedURLException e)
		{
			e.printStackTrace();
			return (result);
		}

		BufferedReader in = null;
		try
		{
			in = new BufferedReader(new InputStreamReader(yahooUrl.openStream()));
		} catch (IOException e)
		{
			e.printStackTrace();
			return (result);
		}

		String bufferString = null;

		try
		{
			while ((bufferString = in.readLine()) != null)
			{
				result = bufferString;
			}
		} catch (IOException e)
		{
			e.printStackTrace();
			return (result);
		}
		try
		{
			in.close();
		} catch (IOException e)
		{
			e.printStackTrace();
		}

		lastHashCode = result.hashCode();
		return (result);
	}

	public void fetchRawYahooStream(String pattern)
	{
		String urlString = "https://sg.finance.yahoo.com/quote/" + symbol;
		URL yahooUrl = null;
		try
		{
			yahooUrl = new URL(urlString);
		} catch (MalformedURLException e)
		{
			e.printStackTrace();
		}

		BufferedReader in = null;
		try
		{
			in = new BufferedReader(new InputStreamReader(yahooUrl.openStream()));
		} catch (IOException e)
		{
			e.printStackTrace();
		}

		String bufferString = null;
		int counter = 0;

		try
		{
			while ((bufferString = in.readLine()) != null)
			{
				if (bufferString.contains(pattern))
				{
					System.out.println(counter + "-->" + bufferString);
					++counter;
				}
			}
		} catch (IOException e)
		{
			e.printStackTrace();
		}
		try
		{
			in.close();
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public String[] getYahooPrices()
	{
		ArrayList<String> resultList = new ArrayList<String>();
		String resultLine = fetchYahooPrice();
		StringTokenizer tokens = new StringTokenizer(resultLine, ",");
		while (tokens.hasMoreTokens())
		{
			resultList.add(tokens.nextToken());
		}

		String[] resultArray = new String[resultList.size()];
		resultArray = resultList.toArray(resultArray);
		return (resultArray);
	}

	public int getHashCode()
	{
		return (lastHashCode);
	}
	
	private void runYahooDemo()
	{
		YahooPrices samplePriceItem = new YahooPrices("SGDINR=X", "nl1abb6a5");
		int prevHashCode = 0, hashCode = 0;
		int count = 1;
		while (count <= 0)
		{
			String[] result = samplePriceItem.getYahooPrices();
			hashCode = samplePriceItem.getHashCode();
			if (hashCode != prevHashCode)
			{
				System.out.println(result[0] + "|" + result[1] + "|" + result[2] + "|" + result[3] + "|" + result[4] + "|" + result[5]);
				System.out.println("HashCode is = " + hashCode);
				prevHashCode = hashCode;
			}
			--count;
		}
	}
	
	
}
