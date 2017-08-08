package com.marketdataclient.kdbfeedhandler;

import java.util.List;

/**
 * Allows listening to incoming trade data.
 */
public interface FeedListener<T> {
	
	/**
	 * Event received when a number of trades have occurred.
	 */
	public boolean pumpTickEventToKDB(List<T> tickEvents);
}