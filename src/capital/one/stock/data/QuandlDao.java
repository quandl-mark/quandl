package capital.one.stock.data;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;
import java.util.logging.Logger;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;

import capital.one.stock.data.StockData.StockDataBuilder;
import capital.one.util.Utils;


public class QuandlDao implements StockDao {

	private final static String API_KEY = "s-GMZ_xkw6CrkGYUWs1p";
	private final static String API_CALL_FORMAT = 
			"https://www.quandl.com/api/v3/datasets/WIKI/%s/data.json?start_date=%s&end_date=%s&api_key=%s";
	private static final String DATE_COLUMN = "Date";
	private static final String OPEN_COLUMN = "Open";
	private static final String HIGH_COLUMN = "High";
	private static final String LOW_COLUMN = "Low";
	private static final String CLOSE_COLUMN = "Close";
	private static final String VOLUME_COLUMN = "Volume";
	
	
    private final Logger logger = Logger.getLogger(QuandlDao.class.getName());
    
	private static StockData createStockData(
			String ticker,
			Map<String, Integer> columsByName,
			JsonArray row) throws IOException
	{
		StockDataBuilder builder = new StockDataBuilder().withTicker(ticker)
						.withOpen(row.getJsonNumber(columsByName.get(OPEN_COLUMN)).doubleValue())
						.withClose(row.getJsonNumber(columsByName.get(CLOSE_COLUMN)).doubleValue())
						.withHigh(row.getJsonNumber(columsByName.get(HIGH_COLUMN)).doubleValue())
						.withLow(row.getJsonNumber(columsByName.get(LOW_COLUMN)).doubleValue())
						.withVolume(row.getJsonNumber(columsByName.get(VOLUME_COLUMN)).doubleValue());
		try {
			builder.withDate(Utils.FORMAT.parse(row.getString(columsByName.get(DATE_COLUMN))));
		} catch (ParseException e) {
			// unable to parse date object from Quandl. Throwing IO here.
			throw new IOException(e);
		}
		return builder.build();
	}
		
	@Override
	public TreeSet<StockData> getData(String ticker, Date start, Date end) throws IOException {
		String urlStr = String.format(API_CALL_FORMAT, ticker, Utils.FORMAT.format(start), Utils.FORMAT.format(end), API_KEY);
		URL url = new URL(urlStr);
		InputStream is = null;
		try
		{
			logger.info("Fetch data for url: " + urlStr);
			is = url.openStream();
			JsonReader reader = Json.createReader(is);
			JsonObject fullObj = reader.readObject().getJsonObject("dataset_data");
			JsonArray rows = fullObj.getJsonArray("data");
			JsonArray colums = fullObj.getJsonArray("column_names");
			Map<String, Integer> columsByName = new HashMap<>();
			for(int i = 0; i < colums.size(); ++i) 
			{
				String name = colums.getString(i);
				columsByName.put(name, i);
			}
			TreeSet<StockData> ret = new TreeSet<StockData>((o1, o2) -> o1.getDate().compareTo(o2.getDate()));
			for(int i =  0; i < rows.size(); ++i)
			{
				JsonArray row = rows.getJsonArray(i);
				StockData stock = createStockData(ticker, columsByName, row);
				ret.add(stock);
			}
			return ret;
		}
		finally
		{
			if(is != null)
			{
				is.close();
			}
		}
	}

}
