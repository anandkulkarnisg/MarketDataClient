package com.marketdataclient.kdbfeedhandler;

import java.sql.Date;
import java.sql.Time;

/**
 * Contains data for a single trade.
 */
public class ICICITickEvent
{
	public final int tickSequence;
	public final String exchangeName;
	public final String sym;
	public final double highPrice;
	public final double lifeTimeHighPrice;
	public final double lifeTimeLowPrice;
	public final double dayHighPrice;
	public final double lastTradedPrice;
	public final double week52HighPrice;
	public final double week52LowPrice;
	public final double bestBidPrice;
	public final double bestAskPrice;
	public final double dayOpenPrice;
	public final double dayClosePrice;
	public final double prevDayClosePrice;
	public final double dayLowPrice;
	public final double highPriceRange;
	public final double lowPriceRange;
	public final double absolutePriceChange;
	public final double percentPriceChange;
	public final long bestBidQuantity;
	public final long bestAskQuantity;
	public final long dayVolume;
	public final Date date;
	public final Time lastTradedTime;

	public ICICITickEvent(int tickSequence, String exchangeName, String sym, double highPrice, double lifeTimeHighPrice, double lifeTimeLowPrice, double dayHighPrice,
			double lastTradedPrice, double week52HighPrice, double week52LowPrice, double bestBidPrice, double bestAskPrice, double dayOpenPrice, double dayClosePrice,
			double prevDayClosePrice, double dayLowPrice, double highPriceRange, double lowPriceRange, double absolutePriceChange, double percentPriceChange, long bestBidQuantity,
			long bestAskQuantity, long dayVolume, Date date, Time lastTradedTime)
	{
		this.tickSequence = tickSequence;
		this.exchangeName = exchangeName;
		this.sym = sym;
		this.highPrice = highPrice;
		this.lifeTimeHighPrice = lifeTimeHighPrice;
		this.lifeTimeLowPrice = lifeTimeLowPrice;
		this.dayHighPrice = dayHighPrice;
		this.lastTradedPrice = lastTradedPrice;
		this.week52HighPrice = week52HighPrice;
		this.week52LowPrice = week52LowPrice;
		this.bestBidPrice = bestBidPrice;
		this.bestAskPrice = bestAskPrice;
		this.dayOpenPrice = dayOpenPrice;
		this.dayClosePrice = dayClosePrice;
		this.prevDayClosePrice = prevDayClosePrice;
		this.dayLowPrice = dayLowPrice;
		this.highPriceRange = highPriceRange;
		this.lowPriceRange = lowPriceRange;
		this.absolutePriceChange = absolutePriceChange;
		this.percentPriceChange = percentPriceChange;
		this.bestBidQuantity = bestBidQuantity;
		this.bestAskQuantity = bestAskQuantity;
		this.dayVolume = dayVolume;
		this.date = date;
		this.lastTradedTime = lastTradedTime;
	}

	public String toCsvFormart()
	{
		return (tickSequence + "," + exchangeName + "," + sym + "," + highPrice + "," + lifeTimeHighPrice + "," + lifeTimeLowPrice + "," + dayHighPrice + "," + lastTradedPrice
				+ "," + week52HighPrice + "," + week52LowPrice + "," + bestBidPrice + "," + bestAskPrice + "," + dayOpenPrice + "," + dayClosePrice + "," + prevDayClosePrice + ","
				+ dayLowPrice + "," + highPriceRange + "," + lowPriceRange + "," + absolutePriceChange + "," + percentPriceChange + "," + bestBidQuantity + "," + bestAskQuantity
				+ "," + dayVolume + "," + date + "," + lastTradedTime);
	}

}