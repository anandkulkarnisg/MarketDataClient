/ *  Starting functions for the tickerplant go here.
/ *  trade:([]time:`time$();sym:`symbol$();price:`float$();size:`int$();stop:`boolean$();cond:`char$();ex:`char$())
/ * .u.upd:insert This allows using the same commands as a KDB+ tickerplant.
/	public final int tickSequence;
/	public final String exchangeName;
/	public final String sym;
/	public final double highPrice;
/	public final double lifeTimeHighPrice;
/	public final double lifeTimeLowPrice;
/	public final double dayHighPrice;
/	public final double lastTradedPrice;
/	public final double week52HighPrice;
/	public final double week52LowPrice;
/	public final double bestBidPrice;
/	public final double bestAskPrice;
/	public final double dayOpenPrice;
/	public final double dayClosePrice;
/	public final double prevDayClosePrice;
/	public final double dayLowPrice;
/	public final double highPriceRange;
/	public final double lowPriceRange;
/	public final double absolutePriceChange;
/	public final double percentPriceChange;
/	public final long bestBidQuantity;
/	public final long bestAskQuantity;
/	public final long dayVolume;
/	public final Date date;
/	public final Time lastTradedTime;

trade:([] tickSequence:`int$(); exchangeName:`symbol$(); sym:`symbol$();highPrice:`float$(); lifeTimeHighPrice:`float$(); lifeTimeLowPrice:`float$(); dayHighPrice:`float$(); lastTradedPrice:`float$(); week52HighPrice:`float$(); week52LowPrice:`float$(); bestBidPrice:`float$(); bestAskPrice:`float$(); dayOpenPrice:`float$(); dayClosePrice:`float$(); prevDayClosePrice:`float$(); dayLowPrice:`float$(); highPriceRange:`float$(); lowPriceRange:`float$(); absolutePriceChange:`float$(); percentPriceChange:`float$(); bestBidQuantity:`long$(); bestAskQuantity:`long$(); dayVolume:`long$(); date:`date$(); lastTradedTime:`time$());
.u.upd:insert;

