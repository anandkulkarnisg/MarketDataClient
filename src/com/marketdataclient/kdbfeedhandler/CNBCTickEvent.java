package com.marketdataclient.kdbfeedhandler;

/**
 * Contains data for a single trade.
 */
public class CNBCTickEvent
{
	public final int tickSequence;
	public final String sym;
	public final String shortName;
	public final Double lastPrice;
	public final Double change;
	public final Double changePercent;
	public final String provider;
	public final boolean cacheServed;
	public final String cacheTime;
	public final String responseTime;
	public final boolean realTimeStatus;

	public CNBCTickEvent(int tickSequence, String sym, String shortName, Double lastPrice, Double change, Double changePercent, String provider, boolean cacheServed,
			String cacheTime, String responseTime, boolean realTimeStatus)
	{
		super();
		this.tickSequence = tickSequence;
		this.sym = sym;
		this.shortName = shortName;
		this.lastPrice = lastPrice;
		this.change = change;
		this.changePercent = changePercent;
		this.provider = provider;
		this.cacheServed = cacheServed;
		this.cacheTime = cacheTime;
		this.responseTime = responseTime;
		this.realTimeStatus = realTimeStatus;
	}

	public String toCsvFormart()
	{
		return (tickSequence + "," + sym + "," + shortName + "," + lastPrice + "," + change + "," + changePercent + "," + provider + "," + cacheServed + "," + cacheTime + ","
				+ responseTime + "," + realTimeStatus);
	}

}