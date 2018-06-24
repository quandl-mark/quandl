package capital.one.stock.analyzer.test;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.json.JsonArray;
import javax.json.JsonObject;

import org.junit.Assert;
import org.junit.Test;

import capital.one.stock.analyzer.AverageOpenCloseCalculator;
import capital.one.stock.data.StockData;
import capital.one.stock.data.StockData.StockDataBuilder;
import capital.one.util.Utils;

public class AverageOpenCloseCalculatorTest {

	private final static String[] TICKERS  = new String[]{"GOOG", "AMZN"};
	private final static double[] MODIFIER_BY_DATE = new double[]{1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20};
	private final static double[] MODIFIER_BY_MONTH = new double[]{0,10,20,30};
	private final static double[] MODIFIER_BY_TICKER = new double[]{0, 100};
	
    public static Collection<StockData> getData() throws ParseException
    {
		Set<StockData> data = new HashSet<>();
    	for(int t = 0; t < TICKERS.length; ++t)
    	{
			Date date = Utils.FORMAT.parse("2017-01-01");
			Calendar c = Calendar.getInstance();
			c.setTime(date);
			for(int m = 0; m < 4; ++m)
			{
				c.set(Calendar.DAY_OF_MONTH, 1);
				c.add(Calendar.MONTH, 1);
				for(int d = 0; d < 20; ++d)
				{
					c.add(Calendar.DATE, 1);
					data.add(new StockDataBuilder()
							.withClose(100d + MODIFIER_BY_DATE[d] + MODIFIER_BY_MONTH[m] + MODIFIER_BY_TICKER[t])
							.withOpen(MODIFIER_BY_DATE[d] + MODIFIER_BY_MONTH[m] + MODIFIER_BY_TICKER[t])
							.withTicker(TICKERS[t])
							.withDate(c.getTime()).build());
				}
			}
    	}
		return data;
    }
    
	@Test
	public void testJson() throws ParseException {
		
		// average open for GOOG will be:
		// days = avg(1,...,20) = 21/2 = 10.5 on first month (feb) and +10 for each month threforeafter.
		// close will be +100 above open
		// average for AMZN will be +100 over GOOG
		
		AverageOpenCloseCalculator calculator = new AverageOpenCloseCalculator();
		calculator.calculate(getData());
		JsonArray array = calculator.toJson();
		// ensure correct number of tickers were recorded (2, GOOG and AMZN)
		Assert.assertEquals(TICKERS.length, array.size());
		Set<String> foundTickers = new HashSet<>();
		for (int i = 0; i < array.size(); ++i)
		{
			JsonObject tickerObj = array.getJsonObject(i);
			String ticker = tickerObj.getString("ticker");
			foundTickers.add(ticker);
			JsonArray averagesArray = tickerObj.getJsonArray("averages");
			Set<Integer> foundMonths = new HashSet<>();
			// ensure averages are calculated correctly
			for(int j = 0; j < averagesArray.size(); ++j)
			{
				JsonObject averageObj = averagesArray.getJsonObject(j);
				// feb (2) is first month inputed
				int month = Integer.parseInt(averageObj.getString("month").split("-")[1]) - 2;
				foundMonths.add(month);
				double tickerMod = ticker.equals("GOOG") ? 0d : 100d;
				double expectedOpen = tickerMod + month*10d + 10.5;
				double actualOpen = averageObj.getJsonNumber("average_open").doubleValue();
				double expectedClose = tickerMod + month*10d + 110.5;
				double actualClose = averageObj.getJsonNumber("average_close").doubleValue();
				Assert.assertEquals(expectedOpen, actualOpen, 1e-13);
				Assert.assertEquals(expectedClose, actualClose, 1e-13);
			}
			// ensure all months were present
			for(int m = 0; m < 4; ++m)
			{
				Assert.assertTrue(foundMonths.contains(m));
			}
			// ensure no more other months beside feb, march, april, may were found
			Assert.assertEquals(4, foundMonths.size());
		}
		for(String ticker: TICKERS)
		{
			// assure all ticker have data
			Assert.assertTrue(foundTickers.contains(ticker));
		}
		
	}
	
}
