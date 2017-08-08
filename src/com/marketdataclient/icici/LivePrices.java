package com.marketdataclient.icici;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LivePrices
{
	
	final static Logger logger = LogManager.getLogger(LivePrices.class);	
	private String[] urlTextArray;

	public LivePrices()
	{
		urlTextArray = new String[1000];
	}

	protected void getRawLiveStream(String urlString)
	{
		URL yahooUrl = null;
		try
		{
			yahooUrl = new URL(urlString);
		} catch (MalformedURLException e)
		{
			logger.error("Encountered MalFormedURLException encountered.The stack trace is below.");
			logger.error(e.getStackTrace().toString());
		}

		BufferedReader in = null;
		try
		{
			in = new BufferedReader(new InputStreamReader(yahooUrl.openStream()));
		} catch (IOException e)
		{
			logger.error("Encountered IOException While processing via BufferedReaded in getRawLiveStream Function.The stack trace is below.");
			logger.error(e.getStackTrace().toString());
		}

		String bufferString = null;
		int counter = 0;

		try
		{
			while ((bufferString = in.readLine()) != null)
			{
				if (!bufferString.isEmpty())
				{
					urlTextArray[counter] = bufferString;
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

	public String[] getRawStreamAsArray()
	{
		return urlTextArray;
	}

	public void printRawLiveStream(String urlString)
	{
		if (urlTextArray != null)
			getRawLiveStream(urlString);
		for (int i = 0; i < urlTextArray.length; ++i)
			System.out.println(i + " --> " + urlTextArray[i]);
	}
}
