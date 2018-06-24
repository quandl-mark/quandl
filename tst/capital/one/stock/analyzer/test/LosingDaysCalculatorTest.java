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

import capital.one.stock.analyzer.LosingDaysCalculator;
import capital.one.stock.data.StockData;
import capital.one.stock.data.StockData.StockDataBuilder;
import capital.one.util.Utils;

public class LosingDaysCalculatorTest {

	private final static String[] TICKERS  = new String[]{"GOOG", "AMZN"};
	
    public Collection<StockData> getData() throws ParseException
    {
		Set<StockData> data = new HashSet<>();
    	for(int t = 0; t < TICKERS.length; ++t)
    	{
			Date date = Utils.FORMAT.parse("2018-01-01");
			Calendar c = Calendar.getInstance();
			c.setTime(date);
			for(int j = 0; j < 4; ++j)
			{
				c.set(Calendar.DAY_OF_MONTH, 1);
				c.add(Calendar.MONTH, 1);
				for(int i = 0; i < 4; ++i)
				{
					c.add(Calendar.DATE, 1);
					data.add(new StockDataBuilder()
							.withClose(1000d)
							.withOpen(950d + t * 100d - 50d * (i%2))
							.withTicker(TICKERS[t])
							.withDate(c.getTime()).build());
				}
			}
    	}
		return data;
    }
	@Test
	public void testLosingDaysCalcuator() throws ParseException {
		// tickers[0]. (GOOG) will have all opens less then close.
		// ticker[1]. (AMZN) will have half of open/close, 1000/1000, and half, 1050/1000
		LosingDaysCalculator calculator = new LosingDaysCalculator();
		calculator.calculate(getData());
		JsonArray jsonArray = calculator.toJson();
		Assert.assertEquals(1, jsonArray.size());
		JsonObject obj = jsonArray.getJsonObject(0);
		String ticker = obj.getString("ticker");
		int days = obj.getJsonNumber("losing_days").intValue();
		Assert.assertEquals("AMZN", ticker);
		Assert.assertEquals(8, days);
	}

}
