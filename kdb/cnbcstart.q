/ *  Starting functions for the tickerplant go here.
/ *  trade:([]time:`time$();sym:`symbol$();price:`float$();size:`int$();stop:`boolean$();cond:`char$();ex:`char$())
/ * .u.upd:insert This allows using the same commands as a KDB+ tickerplant.

/	public final int tickSequence;
/	public final String sym;
/	public final String shortName;
/	public final Double lastPrice;
/	public final Double change;
/	public final Double changePercent;
/	public final String provider;
/	public final boolean cacheServed;
/	public final String cacheTime;
/	public final String responseTime;
/	public final boolean realTimeStatus;

trade:([] tickSequence:`int$(); sym:`symbol$(); shortName:`symbol$(); lastPrice:`float$(); change:`float$(); changePercent:`float$(); provider:`symbol$(); cacheServed:`boolean$(); cacheTime:`symbol$(); responseTime:`symbol$(); realTimeStatus:`boolean$());
.u.upd:insert; 



