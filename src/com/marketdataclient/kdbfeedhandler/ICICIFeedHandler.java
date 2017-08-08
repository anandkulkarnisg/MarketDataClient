package com.marketdataclient.kdbfeedhandler;

import java.io.IOException;
import java.sql.Date;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import kx.c;
import kx.c.KException;

/**
 * Implements {@link FeedListener} so can listen to feeds which it will then
 * parse to a K object and forward to a KDB server.
 */
public class ICICIFeedHandler implements FeedListener<ICICITickEvent>
{

	final static Logger logger = LogManager.getLogger(ICICIFeedHandler.class);
	private static final String kdbTableName = "trade";
	private static final String[] COL_NAMES = new String[] { "tickSequence", "exchangeName", "sym", "highPrice", "lifeTimeHighPrice", "lifeTimeLowPrice", "dayHighPrice",
			"lastTradedPrice", "week52HighPrice", "week52LowPrice", "bestBidPrice", "bestAskPrice", "dayOpenPrice", "dayClosePrice", "prevDayClosePrice", "dayLowPrice",
			"highPriceRange", "lowPriceRange", "absolutePriceChange", "percentPriceChange", "bestBidQuantity", "bestAskQuantity", "dayVolume", "date", "lastTradedTime" };

	private c conn;

	public ICICIFeedHandler(String host, int port) throws KException, IOException
	{
		conn = new c(host, port);
	}

	public boolean publish(String csvTickString)
	{
		List<ICICITickEvent> tickEvents = convertToTickEvent(csvTickString);
		return (pumpTickEventToKDB(tickEvents));

	}

	public boolean publish(ICICITickEvent tickEvent)
	{
		List<ICICITickEvent> tickEvents = new ArrayList<ICICITickEvent>();
		tickEvents.add(tickEvent);
		return (pumpTickEventToKDB(tickEvents));
	}

	private List<ICICITickEvent> convertToTickEvent(String csvFormatRow)
	{
		List<ICICITickEvent> tickEvents = new ArrayList<ICICITickEvent>();
		StringTokenizer tokenizer = new StringTokenizer(csvFormatRow, ",");
		String[] tokens = new String[tokenizer.countTokens()];
		int i = 0;
		while (tokenizer.hasMoreTokens())
		{
			tokens[i] = tokenizer.nextToken();
			++i;
		}

		// Now build the ICICITickEvent.
		ICICITickEvent tickItem = new ICICITickEvent(Integer.parseInt(tokens[0]), tokens[1], tokens[2], Double.parseDouble(tokens[3]), Double.parseDouble(tokens[4]),
				Double.parseDouble(tokens[5]), Double.parseDouble(tokens[6]), Double.parseDouble(tokens[7]), Double.parseDouble(tokens[8]), Double.parseDouble(tokens[9]),
				Double.parseDouble(tokens[10]), Double.parseDouble(tokens[11]), Double.parseDouble(tokens[12]), Double.parseDouble(tokens[13]), Double.parseDouble(tokens[14]),
				Double.parseDouble(tokens[15]), Double.parseDouble(tokens[16]), Double.parseDouble(tokens[17]), Double.parseDouble(tokens[18]), Double.parseDouble(tokens[19]),
				Long.parseLong(tokens[20]), Long.parseLong(tokens[21]), Long.parseLong(tokens[22]), Date.valueOf(tokens[23]), Time.valueOf(tokens[24]));
		tickEvents.add(tickItem);
		return (tickEvents);
	}

	@Override
	public boolean pumpTickEventToKDB(List<ICICITickEvent> iciciTickEvents)
	{

		boolean publishStatus = false;
		int numRecords = iciciTickEvents.size();

		// create the vectors for each column
		int[] seq = new int[numRecords];
		String[] exch = new String[numRecords];
		String[] sym = new String[numRecords];
		double[] hp = new double[numRecords];
		double[] lthp = new double[numRecords];
		double[] ltlp = new double[numRecords];
		double[] dhp = new double[numRecords];
		double[] ltp = new double[numRecords];
		double[] w52hp = new double[numRecords];
		double[] w52lp = new double[numRecords];
		double[] bbp = new double[numRecords];
		double[] bap = new double[numRecords];
		double[] dop = new double[numRecords];
		double[] dcp = new double[numRecords];
		double[] pdcp = new double[numRecords];
		double[] dlp = new double[numRecords];
		double[] hpr = new double[numRecords];
		double[] lpr = new double[numRecords];
		double[] apc = new double[numRecords];
		double[] ppc = new double[numRecords];
		long[] bbq = new long[numRecords];
		long[] baq = new long[numRecords];
		long[] dv = new long[numRecords];
		Date[] dt = new Date[numRecords];
		Time[] ts = new Time[numRecords];

		// loop through filling the columns with data
		for (int i = 0; i < iciciTickEvents.size(); i++)
		{
			ICICITickEvent tickEvent = iciciTickEvents.get(i);

			seq[i] = tickEvent.tickSequence;
			exch[i] = tickEvent.exchangeName;
			sym[i] = tickEvent.sym;
			hp[i] = tickEvent.highPrice;
			lthp[i] = tickEvent.lifeTimeHighPrice;
			ltlp[i] = tickEvent.lifeTimeLowPrice;
			dhp[i] = tickEvent.dayHighPrice;
			ltp[i] = tickEvent.dayLowPrice;
			w52hp[i] = tickEvent.week52HighPrice;
			w52lp[i] = tickEvent.week52LowPrice;
			bbp[i] = tickEvent.bestBidPrice;
			bap[i] = tickEvent.bestAskPrice;
			dop[i] = tickEvent.dayOpenPrice;
			dcp[i] = tickEvent.dayClosePrice;
			pdcp[i] = tickEvent.prevDayClosePrice;
			dlp[i] = tickEvent.dayLowPrice;
			hpr[i] = tickEvent.highPriceRange;
			lpr[i] = tickEvent.lowPriceRange;
			apc[i] = tickEvent.absolutePriceChange;
			ppc[i] = tickEvent.percentPriceChange;
			bbq[i] = tickEvent.bestBidQuantity;
			baq[i] = tickEvent.bestAskQuantity;
			dv[i] = tickEvent.dayVolume;
			dt[i] = tickEvent.date;
			ts[i] = tickEvent.lastTradedTime;
		}

		// create the table itself from the separate columns
		Object[] data = new Object[] { seq, exch, sym, hp, lthp, ltlp, dhp, ltp, w52hp, w52lp, bbp, bap, dop, dcp, pdcp, dlp, hpr, lpr, apc, ppc, bbq, baq, dv, dt, ts };
		c.Flip tableToBeInserted = new c.Flip(new c.Dict(COL_NAMES, data));
		// create the command to insert the table of data into the named table.
		Object[] updStatement = new Object[] { ".u.upd", kdbTableName, tableToBeInserted };
		try
		{
			conn.ks(updStatement); // send asynchronously
			publishStatus = true;
			logger.info("Published " + numRecords + " records to KDB server");
		} catch (IOException e)
		{
			logger.warn("error sending feed to server.");
		}

		return (publishStatus);
	}

}