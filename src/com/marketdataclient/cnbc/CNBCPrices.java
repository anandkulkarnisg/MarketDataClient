package com.marketdataclient.cnbc;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class CNBCPrices
{
	final static Logger logger = LogManager.getLogger(CNBCPrices.class);
	private static final String CNBC_QUOTEBASE_URL = "http://quote.cnbc.com/quote-html-webservice/quote.htm";
	private String symbol;

	public CNBCPrices()
	{
		symbol = "@SI.1";
	}

	public CNBCPrices(String sym)
	{
		symbol = sym;
	}

	public String getSymbol()
	{
		return symbol;
	}

	public Map<String, Object> streamPrices()
	{
		Map<String, Object> result = new ConcurrentHashMap<String, Object>();
		URL obj = null;
		try
		{
			obj = new URL(CNBC_QUOTEBASE_URL);

		} catch (MalformedURLException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		HttpURLConnection con = null;
		try
		{
			con = (HttpURLConnection) obj.openConnection();
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// CNBC Header requests go here.
		try
		{
			con.setRequestMethod("POST");
		} catch (ProtocolException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		con.setRequestProperty("Host", "quote.cnbc.com");
		con.setRequestProperty("User-Agent", "Mozilla/5.0 (X11; Fedora; Linux x86_64; rv:54.0) Gecko/20100101 Firefox/54.0");
		con.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
		con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
		con.setRequestProperty("Accept-Encoding", "gzip, deflate");
		con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		con.setRequestProperty("Content-Length", "155");
		con.setRequestProperty("Referer", "http://quote.cnbc.com/quote-html-webservice/quoteform.htm");
		con.setRequestProperty("Cookie",
				"ga=GA1.2.1932008668.1464309307; news_default_view_100746255=regular; __gads=ID=2b3331b918226251:T=1464309307:S=ALNI_MZfDiZtSJmT6IP7jgEqpyNQP0L_1Q; s_fid=1A066B7F31F57BDE-0906E315BAC5ADDC; s_getNewRepeat90=1500604834555-Repeat; s_lv=1500604834558; __tbc=%7Bjzx%7DqtWUkoJ3NpvC9-OeQ3UXT81V-UH54B-yB5GERJ9BKK_comN1-apYYkCD_PIMi6MrjiooncIhy8NM0HF_Weg5lqBA_ewSPBVxlw4ivmxfN_8aU7G34BKQeM9SRb9V6AFDMGcml2gzw_PqmUXtgsytUcvBXoIYFcNah-WJ3sD_FPenCHBY47u-eqnmjdFuPuSxnP4bzSn0EzeAVBruRaC7BjRV4to0bRgc1aaMeQurHchMUvWpBQTDFXzQU3yUlt8teI2d4IHPwaKAvtEaiDKZqHn-f_vMwGMtGBbl4hTt_VdslWE-92K0xRco4fwcUtDc4oCAOzFc79knamizf0iwq2-poxg1jhc3x1J6XIxEkr--0UKcc73NGHxJ-puOlN972CTJzQuwtFzUi5iIVzeL6U8fh3HzV5woutPaY4KOMkmmOYloM9m_kDXTRxmhRKlh6cl1Ffk7_uh2eGqOt43lRE59UTwN23P-7nP2VPX_AqGwxwfXbJC0hqyYzPoYm8kb11ThsyJIVWj3HkSWN8feJ19Uf9vGzyUA4mZRxSVB3GpD5P6HQ_HS3kPmy_siUZJhkMpw5hG7C0BYsCcLcD0yAnS0Hk57nzH1M4mJAfV_v4ndPmFvyp7UhBIP7piUPtFD7l8cQuDGUXeyPqMprjwLjFoottjHHnDBkiXphMuT2qg5-FaxJxEeyovKFIrD5GJs; vid=e275761c8de57f2b11f8e6a1260a2f04; _nv=1; __utma=35821554.1932008668.1464309307.1496711416.1498098099.3; _dy_soct=76408.100964.1500604833*157911.224069.1500604833; _dy_ses_load_seq=1162%3A1500604833921; _dy_c_exps=; _dy_c_att_exps=; s_getNewRepeat30=1500604834555-Repeat; aam_uuid=07496774167840221350199933014986498019; _dycst=dk.l.f.ws.frv5.ltos.ah.clk.; _dyus_8765304=1798%7C264%7C21%7C1%7C0%7C0.0.1464309308446.1500604836629.36295528.0%7C201%7C29%7C6%7C1161%7C159%7C0%7C0%7C0%7C0%7C0%7C0%7C159%7C0%7C0%7C0%7C0%7C0%7C159%7C0%7C7%7C1%7C5%7C17; NEW_DYSession=%7B%22session_id%22%3A1500605351372.163%2C%22lastActivity%22%3A1500604836674%7D; _dy_geo=SG.AS.SG_00.SG_00_Singapore; _dy_toffset=-2; CNBC_MarketOverviewPref={\"CNBCMarket\":{\"100746255\":{\"index\":4}}}; xbc=%7Bjzx%7DHCtgRKUMhBXfP_1sqEhUYetbfy6vg9Nuazyiom7Ohu4Kq9CryhDH9Raz5xn0anJ47VjdISOH6NWLOaw_qrjWFYSuxAMAlvbEab8zJlw9WrjYm9b5ic0djTlC2B6HOPCgkuyTA8XpxOMwgDGXrtZ6l61nsxLuNTA5gmWHraELXXf9rqvNiUi0xbqMOP-Xu1pcDgHeMRTa7zhlJ6hPoVtWu2k25uTKBg0Ds0DyHT6XGISHYi7yaokz46e5j3ZXg1Vm2U7c3378vagvZQzx2vVQ3Jp73ra8soNOhoKFi6lg8hVp9ggvXO6--Ur1yIXF134di1TR-WQDiRTqRIIpW4TAnxfKu5LVv3Y7R6_gcGc-oI3bO6yeMSFgX48ot-iAR7nBIGsqttq2c9GVgD794XDKTAI31xxVFQvFLTZFoNxPmXyqdic9F8OF2GyfcFiJeRi0; gig_hasGmid=ver2; region=WORLD; __utmz=35821554.1498098099.3.2.utmcsr=watchlist.cnbc.com|utmccn=(referral)|utmcmd=referral|utmcct=/; _dyid=-8624393138921302470; s_vmonthnum=1501516800946%26vn%3D17; _gid=GA1.2.725236254.1500604834; __qca=P0-1184973944-1500604834646; __pvi=%7B%22id%22%3A%22v-2017-07-21-10-40-37-423-nm7pPZ3Zy3LryRZV-4b1cd051509536a700d3d985df681fa3%22%2C%22domain%22%3A%22.cnbc.com%22%2C%22time%22%3A1500604837890%7D");
		con.setRequestProperty("Connection", "keep-alive");
		con.setRequestProperty("Upgrade-Insecure-Requests", "1");
		con.setRequestProperty("Pragma", "no-cache");
		con.setRequestProperty("Cache-Control", "no-cache");

		// The post parameters go here.
		// String urlParameters =
		// "sn=C02G8416DRJM&cn=&locale=&caller=&num=12345"; // This servers as a
		// guideline.
		String urlParameters = "";

		Map<String, String> postParametersMap = new HashMap<String, String>();
		postParametersMap.put("symbols", symbol);
		postParametersMap.put("symbolType", "symbol");
		postParametersMap.put("requestMethod", "fast");
		postParametersMap.put("exthrs", "1");
		postParametersMap.put("extmode", "");
		postParametersMap.put("fund", "1");
		postParametersMap.put("events", "1");
		postParametersMap.put("entitlement", "0");
		postParametersMap.put("skipcache", "");
		postParametersMap.put("extendedMask", "2");
		postParametersMap.put("partnerId", "2");
		postParametersMap.put("output", "xml");
		postParametersMap.put("noform", "1");

		for (Map.Entry<String, String> mapItem : postParametersMap.entrySet())
		{
			urlParameters += mapItem.getKey() + "=" + mapItem.getValue();
			urlParameters += "&";
		}

		urlParameters = urlParameters.substring(0, urlParameters.length() - 1);
		con.setDoOutput(true);
		DataOutputStream wr = null;
		try
		{
			wr = new DataOutputStream(con.getOutputStream());
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try
		{
			wr.writeBytes(urlParameters);
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try
		{
			wr.flush();
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try
		{
			wr.close();
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		int responseCode = -1;
		try
		{
			responseCode = con.getResponseCode();
			if (responseCode != 200)
				logger.warn("Got a bad response code " + responseCode);

			// System.out.println("Response Content : " +
			// con.getResponseMessage());
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// System.out.println("\nSending 'POST' request to URL : " + url);
		// System.out.println("Post parameters : " + urlParameters);
		// System.out.println("Response Code : " + responseCode);

		BufferedReader in = null;
		try
		{
			// in = new BufferedReader(new InputStreamReader(new
			// GZIPInputStream(con.getInputStream()))); // This is for the
			// detailed xml not fastQuote!
			in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String inputLine = null;
		StringBuffer response = new StringBuffer();

		try
		{
			while ((inputLine = in.readLine()) != null)
			{
				response.append(inputLine);
			}
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try
		{
			in.close();
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try
		{
			// File fXmlFile = new
			// File("/home/anand/repogit/java/eclipse/MarketDataClients/sample2.xml");
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			InputSource is = new InputSource(new StringReader(response.toString()));
			// Document doc = dBuilder.parse(fXmlFile);
			Document doc = null;
			try
			{
				doc = dBuilder.parse(is);
			} catch (Exception e)
			{
				System.out.println("The stock symbol is :" + getSymbol());
				System.out.println("The bad html is :" + response.toString());
			}

			// optional, but recommended
			// read this -
			// http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
			if (doc != null)
			{
				doc.getDocumentElement().normalize();
				NodeList nList = doc.getElementsByTagName("FastQuote");
				for (int temp = 0; temp < nList.getLength(); temp++)
				{
					Node nNode = nList.item(temp);
					if (nNode.getNodeType() == Node.ELEMENT_NODE)
					{
						Element eElement = (Element) nNode;
						result.put("symbol", eElement.getElementsByTagName("symbol").item(0).getTextContent());
						result.put("shortName", eElement.getElementsByTagName("shortName").item(0).getTextContent());
						result.put("lastPrice", eElement.getElementsByTagName("last").item(0).getTextContent());
						result.put("change", eElement.getElementsByTagName("change").item(0).getTextContent());
						result.put("provider", eElement.getElementsByTagName("provider").item(0).getTextContent());
						result.put("changePercent", eElement.getElementsByTagName("change_pct").item(0).getTextContent());
						result.put("cacheServed", eElement.getElementsByTagName("cacheServed").item(0).getTextContent());
						result.put("cacheTime", eElement.getElementsByTagName("cachedTime").item(0).getTextContent());
						result.put("responseTime", eElement.getElementsByTagName("responseTime").item(0).getTextContent());
						result.put("realTimeStatus", eElement.getElementsByTagName("realTime").item(0).getTextContent());
					}
				}
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}

		return (result);
	}

}
