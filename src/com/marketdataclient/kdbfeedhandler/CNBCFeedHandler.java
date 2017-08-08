package com.marketdataclient.kdbfeedhandler;

import java.io.IOException;
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
public class CNBCFeedHandler implements FeedListener<CNBCTickEvent>
{

	final static Logger logger = LogManager.getLogger(CNBCFeedHandler.class);
	private static final String kdbTableName = "trade";	
	private static final String[] COL_NAMES = new String[] { "tickSequence", "sym", "shortName", "lastPrice", "change", "changePercent", "provider", "cacheServed", "cacheTime", "responseTime", "realTimeStatus"};

	private c conn;

	public CNBCFeedHandler(String host, int port) throws KException, IOException
	{
		conn = new c(host, port);
	}

	public boolean publish(String csvTickString)
	{
		List<CNBCTickEvent> tickEvents = convertToTickEvent(csvTickString);
		return (pumpTickEventToKDB(tickEvents));

	}

	public boolean publish(CNBCTickEvent tickEvent)
	{
		List<CNBCTickEvent> tickEvents = new ArrayList<CNBCTickEvent>();
		tickEvents.add(tickEvent);
		return (pumpTickEventToKDB(tickEvents));
	}

	private List<CNBCTickEvent> convertToTickEvent(String csvFormatRow)
	{
		List<CNBCTickEvent> tickEvents = new ArrayList<CNBCTickEvent>();
		StringTokenizer tokenizer = new StringTokenizer(csvFormatRow, ",");
		String[] tokens = new String[tokenizer.countTokens()];
		int i = 0;
		while (tokenizer.hasMoreTokens())
		{
			tokens[i] = tokenizer.nextToken();
			++i;
		}

		// Now build the CNBCTickEvent.
		CNBCTickEvent tickItem = new CNBCTickEvent(Integer.parseInt(tokens[0]), tokens[1], tokens[2], Double.parseDouble(tokens[3]), Double.parseDouble(tokens[3]),
				Double.parseDouble(tokens[4]), tokens[5], Boolean.parseBoolean(tokens[6]), tokens[7], tokens[8], Boolean.parseBoolean(tokens[9]));

		tickEvents.add(tickItem);
		return (tickEvents);
	}

	@Override
	public boolean pumpTickEventToKDB(List<CNBCTickEvent> cnbcTickEvents)
	{

		boolean publishStatus = false;
		int numRecords = cnbcTickEvents.size();

		// create the vectors for each column
		int[] seq = new int[numRecords];
		String[] sym = new String[numRecords];
		String[] sn = new String[numRecords];
		Double[] lp = new Double[numRecords];
		Double[] ch = new Double[numRecords];
		Double[] chp = new Double[numRecords];
		String[] pr = new String[numRecords];
		Boolean[] cs = new Boolean[numRecords];
		String[] ct = new String[numRecords];
		String[] rt = new String[numRecords];
		Boolean[] rts = new Boolean[numRecords];
			
		// loop through filling the columns with data
		for (int i = 0; i < cnbcTickEvents.size(); i++)
		{
			CNBCTickEvent tickEvent = cnbcTickEvents.get(i);
			
			seq[i] = tickEvent.tickSequence;
			sym[i] = tickEvent.sym;
			sn[i] = tickEvent.shortName;
			lp[i] = tickEvent.lastPrice;
			ch[i] = tickEvent.change;
			chp[i] = tickEvent.changePercent;
			pr[i] = tickEvent.provider;
			cs[i] = tickEvent.cacheServed;
			ct[i] = tickEvent.cacheTime;
			rt[i] = tickEvent.responseTime;
			rts[i] = tickEvent.realTimeStatus;
		}

		// create the table itself from the separate columns
		Object[] data = new Object[] { seq, sym, sn, lp, ch, chp, pr, cs, ct, rt, rts };
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