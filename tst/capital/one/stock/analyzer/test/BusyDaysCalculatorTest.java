package capital.one.stock.analyzer.test;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.json.JsonArray;
import javax.json.JsonObject;

import org.junit.Assert;
import org.junit.Test;

import capital.one.stock.analyzer.BusyDaysCalculator;
import capital.one.stock.data.StockData;
import capital.one.stock.data.StockData.StockDataBuilder;
import capital.one.util.Utils;

public class BusyDaysCalculatorTest {

	private final static String[] TICKERS  = new String[]{"GOOG", "AMZN"};
	private final static double[] MODIFIER_BY_TICKER = new double[]{0d,1000d};
	private final static double[] MODIFIER_BY_MONTH = new double[]{100d,200d,300d,400d};
	private final static double[] MODIFIER_BY_DATE = new double[]{1d,2d,3d,4d};
	
    public Collection<StockData> getData(int t) throws ParseException
    {
		Set<StockData> data = new HashSet<>();
		Date date = Utils.FORMAT.parse("2017-01-01");
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		for(int m = 0; m < 4; ++m)
		{
			c.set(Calendar.DAY_OF_MONTH, 1);
			c.add(Calendar.MONTH, 1);
			for(int d = 0; d < 4; ++d)
			{
				c.add(Calendar.DATE, 1);
				data.add(new StockDataBuilder()
						.withVolume(MODIFIER_BY_TICKER[t] + MODIFIER_BY_MONTH[m] + MODIFIER_BY_DATE[d])
						.withTicker(TICKERS[t])
						.withDate(c.getTime()).build());
			}
		}
		return data;
    }
    
	@Test
	public void testJson() throws ParseException {
		// GOOG average will be 252.5 with AMZN +1000 
		// GOOG busy days are (4/2 - 4/5), (301-304) and (5/2 -5/5), (401-404)
		// AMZN busy days (5/2 - 5/5), (1401-1404)
		Map<String, Double> expectedVolumeByTicker = new HashMap<String, Double>()
				{{
					put("GOOG", 252.5);
					put("AMZN", 1252.5);
				}};
		Map<String, Double> expectedBusyDaysForAMZN = new HashMap<String, Double>()
		{{
			put("2017-05-02", 1401d);
			put("2017-05-03", 1402d);
			put("2017-05-04", 1403d);
			put("2017-05-05", 1404d);
		}};
		Map<String, Double> expectedBusyDaysForGOOG = new HashMap<String, Double>()
		{{
			put("2017-04-02", 301d);
			put("2017-04-03", 302d);
			put("2017-04-04", 303d);
			put("2017-04-05", 304d);
			put("2017-05-02", 401d);
			put("2017-05-03", 402d);
			put("2017-05-04", 403d);
			put("2017-05-05", 404d);
		}};
		Map<String, Map<String, Double>> expectedBusyDaysByTicker = new HashMap<String, Map<String, Double>>()
				{{
					put("GOOG", expectedBusyDaysForGOOG);
					put("AMZN", expectedBusyDaysForAMZN);
				}};
		
		
		BusyDaysCalculator calculator = new BusyDaysCalculator();
		for(int t = 0; t < TICKERS.length; ++t)
		{
			calculator.calculate(getData(t));
		}
		JsonArray array = calculator.toJson();
		Set<String> foundTickers = new HashSet<>();
		for(int t = 0; t < array.size(); ++t)
		{
			JsonObject tickerObj = array.getJsonObject(t);
			String ticker = tickerObj.getString("ticker");
			foundTickers.add(ticker);
			Assert.assertEquals(expectedVolumeByTicker.get(ticker),
					tickerObj.getJsonNumber("avg_volume").doubleValue(), 1e-13);
			Map<String, Double> expectedBusyDays = expectedBusyDaysByTicker.get(ticker);
			JsonArray busyDaysArray = tickerObj.getJsonArray("busy_days");
			// ensure has correct number of days
			Assert.assertEquals(expectedBusyDays.size(), busyDaysArray.size());
			// ensure all days are present and correct volume
			for(int b = 0; b < busyDaysArray.size(); ++b) 
			{
				JsonObject buyDay = busyDaysArray.getJsonObject(b);
				String date = buyDay.getString("date");
				Double volume = buyDay.getJsonNumber("volume").doubleValue();
				Assert.assertEquals(expectedBusyDays.get(date), volume, 1e-13);
			}
		}
	}

}
