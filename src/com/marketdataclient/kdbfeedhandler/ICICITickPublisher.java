package com.marketdataclient.kdbfeedhandler;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.marketdataclient.icici.ICICIWorkerManager;

public class ICICITickPublisher implements Runnable
{
	private ICICIFeedHandler feedHandler;
	private String kdbServer;
	private int kdbPort;

	final static Logger logger = LogManager.getLogger(ICICITickPublisher.class);

	public ICICITickPublisher(String server, int port)
	{
		kdbServer = server;
		kdbPort = port;
		try
		{
			feedHandler = new ICICIFeedHandler(kdbServer, kdbPort);
		} catch (Exception e)
		{
			logger.fatal("Unable to make a connection to the kdb server at host -> " + kdbServer + " and port -> " + kdbPort + ". Exiting the application with failure status 1");
			System.exit(1);
		}
	}

	@Override
	public void run()
	{
		while (ICICIKdbTickPublisher.keepPublishing())
		{
			try
			{
				ICICITickEvent tickEvent = null;
				try
				{
					if (!ICICIWorkerManager.getTickDataQueue().getTickDataQueue().isEmpty())
					{
						tickEvent = ICICIWorkerManager.getTickDataQueue().getTickDataQueue().take();
						boolean publishStatus = feedHandler.publish(tickEvent);
						if (!publishStatus)
						{
							logger.warn("Failed to publish the below tick to the database. Please verify the content of the same. Below is its dump");
							logger.warn(tickEvent.toCsvFormart());
						}
					}

				} catch (InterruptedException e)
				{
					logger.info("Got interrupted while waiting for the tick data in the queue.");
					e.printStackTrace();
				}
			} catch (Exception e)
			{
				logger.error("Exception occured while trying to publish the item from queue.Exception details are below");
				e.printStackTrace();
			}

		}

	}
}
