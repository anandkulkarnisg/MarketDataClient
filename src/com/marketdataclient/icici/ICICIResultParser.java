package com.marketdataclient.icici;

import java.util.Map;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class ICICIResultParser
{
	private Map<String, Object> resultMap = null;
	private exchangeInfo exchange;

	public exchangeInfo getExchange()
	{
		return exchange;
	}

	public void setExchange(exchangeInfo exchange)
	{
		this.exchange = exchange;
	}

	public enum exchangeInfo
	{
		NSE, BSE
	}

	public ICICIResultParser(Map<String, Object> results, exchangeInfo exch)
	{
		resultMap = results;
		exchange = exch;
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

	private long getLongPriceByKey(String key, long defaultBadPrice)
	{
		Object value;
		if (resultMap != null)
			value = resultMap.get(key);
		else
			return (defaultBadPrice);
		long result;
		try
		{
			result = Long.parseLong((value.toString().replace(",", "")));
		} catch (NumberFormatException e)
		{
			// -ve price is always unlikely where as a stock price can
			// theoratically reach zero [ although not likely on an exchange ].
			result = defaultBadPrice;
		}
		return (result);
	}

	private LocalDate getDatePriceByKey(String key, LocalDate defaultDate)
	{
		Object value;
		if (resultMap != null)
			value = resultMap.get(key);
		else
			return (defaultDate);
		LocalDate result;
		try
		{
			DateTimeFormatter dtf = DateTimeFormat.forPattern("dd-MMM-yyyy");
			try
			{
				result = dtf.parseLocalDate(value.toString());
			} catch (IllegalArgumentException e)
			{
				result = defaultDate;
			}
		} catch (NumberFormatException e)
		{
			// -ve price is always unlikely where as a stock price can
			// theoratically reach zero [ although not likely on an exchange ].
			result = defaultDate;
		}
		return (result);
	}

	public double getHighPriceRange()
	{
		return (getDoublePriceByKey("HIGH PRICE RANGE" + " " + exchange.toString().toUpperCase(), -1.0));
	}

	public double getLifeTimeHigh()
	{
		return (getDoublePriceByKey("LIFE TIME HIGH" + " " + exchange.toString().toUpperCase(), -1.0));
	}

	public double getLifeTimeLow()
	{
		return (getDoublePriceByKey("LIFE TIME LOW" + " " + exchange.toString().toUpperCase(), -1.0));
	}

	public double getDayHigh()
	{
		return (getDoublePriceByKey("DAY HIGH" + " " + exchange.toString().toUpperCase(), -1.0));
	}

	public double getLastTradePrice()
	{
		return (getDoublePriceByKey("LAST TRADE PRICE" + " " + exchange.toString().toUpperCase(), -1.0));
	}

	public double get52WeekHighPrice()
	{
		return (getDoublePriceByKey("52 WEEK HIGH" + " " + exchange.toString().toUpperCase(), -1.0));
	}

	public double get52WeekLowPrice()
	{
		return (getDoublePriceByKey("52 WEEK LOW" + " " + exchange.toString().toUpperCase(), -1.0));
	}

	public double getBestBidPrice()
	{
		return (getDoublePriceByKey("BEST BID PRICE" + " " + exchange.toString().toUpperCase(), -1.0));
	}

	public double getBestOfferPrice()
	{
		return (getDoublePriceByKey("BEST OFFER PRICE" + " " + exchange.toString().toUpperCase(), -1.0));
	}

	public double getDayOpenPrice()
	{
		return (getDoublePriceByKey("DAY OPEN" + " " + exchange.toString().toUpperCase(), -1.0));
	}

	public double getDayClosePrice()
	{
		return (getDoublePriceByKey("DAY CLOSE" + " " + exchange.toString().toUpperCase(), -1.0));
	}

	public double getPreviousDayClosePrice()
	{
		return (getDoublePriceByKey("PREVIOUS DAY CLOSE" + " " + exchange.toString().toUpperCase(), -1.0));
	}

	public double getDayLowPrice()
	{
		return (getDoublePriceByKey("DAY LOW" + " " + exchange.toString().toUpperCase(), -1.0));
	}

	public double getHighPriceRangePrice()
	{
		return (getDoublePriceByKey("HIGH PRICE RANGE" + " " + exchange.toString().toUpperCase(), -1.0));
	}

	public double getLowPriceRangePrice()
	{
		return (getDoublePriceByKey("LOW PRICE RANGE" + " " + exchange.toString().toUpperCase(), -1.0));
	}

	public double getAbsoluteChangePrice()
	{
		return (getDoublePriceByKey("CHANGE" + " " + exchange.toString().toUpperCase(), -1.0));
	}

	public double getPercentChange()
	{
		return (getDoublePriceByKey("% CHANGE" + " " + exchange.toString().toUpperCase(), -1.0));
	}

	public long getBestBidQuantity()
	{
		return (getLongPriceByKey("BEST BID QTY" + " " + exchange.toString().toUpperCase(), -1));
	}

	public long getBestOfferQuantity()
	{
		return (getLongPriceByKey("BEST OFFER QTY" + " " + exchange.toString().toUpperCase(), -1));
	}

	public long getDayVolume()
	{
		return (getLongPriceByKey("DAY VOLUME" + " " + exchange.toString().toUpperCase(), -1));
	}

	public LocalDate getValueDate()
	{
		return (getDatePriceByKey("DATE" + " " + exchange.toString().toUpperCase(), LocalDate.now()));
	}

	public String getLastTradedTime()
	{
		return ((resultMap.get("LAST TRADED TIME" + " " + exchange.toString().toUpperCase())).toString());
	}
}
