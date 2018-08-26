package com.marketdataclient.cnbc;

import java.util.Map;

public class CNBCResultParser
{
	private Map<String, Object> resultMap = null;

	public CNBCResultParser(Map<String, Object> results)
	{
		resultMap = results;
	}

	private double getDoublePriceByKey(String key, double defaultBadPrice)
	{
		Object value;
		if (resultMap != null)
			value = resultMap.get(key);
		else
			return (defaultBadPrice);
		double result;
		try
		{
			result = Double.parseDouble(value.toString().replace(",", ""));
		} catch (NumberFormatException e)
		{
			// -ve price is always unlikely where as a stock price can
			// theoratically reach zero [ although not likely on an exchange ].
			result = defaultBadPrice;
		}
		return (result);

	}

	public String getShortName()
	{
		return (resultMap.get("shortName").toString());
	}

	public Double getLastPrice()
	{
		return (getDoublePriceByKey("lastPrice", -1.0));
	}

	public Double getChange()
	{
		return (getDoublePriceByKey("change", -1.0));
	}

	public Double getChangePercent()
	{
		return (getDoublePriceByKey("changePercent", -1.0));
	}

	public String getProvider()
	{
		return (resultMap.get("provider").toString());
	}

	public boolean getCacheServed()
	{
		return (Boolean.parseBoolean(resultMap.get("cacheServed").toString()));
	}
	
	public String getCacheTime()
	{
		return(resultMap.get("cacheTime").toString());
	}

	public String getResponseTime()
	{
		return (resultMap.get("responseTime").toString());
	}

	public boolean getRealTimeStatus()
	{
		return (Boolean.parseBoolean(resultMap.get("realTimeStatus").toString()));
	}

}
