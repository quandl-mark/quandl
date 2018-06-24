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

import capital.one.stock.analyzer.MaxProfitCalculator;
import capital.one.stock.data.StockData;
import capital.one.stock.data.StockData.StockDataBuilder;
import capital.one.util.Utils;

public class MaxProfitCalculatorTest {

	private final static String[] TICKERS  = new String[]{"GOOG", "AMZN"};
	
    public Collection<StockData> getData() throws ParseException
    {
		Set<StockData> data = new HashSet<>();
    	for(int t = 0; t < TICKERS.length; ++t)
    	{
			Date date = Utils.FORMAT.parse("2017-01-01");
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
							.withHigh(1000d + 10 * (i) + 100*j + t * 1d)
							.withLow(1000d - 10 * (i) - 100*j - t * 1d)
							.withTicker(TICKERS[t])
							.withDate(c.getTime()).build());
				}
			}
    	}
		return data;
    }
    
    @Test
	public void testMaxProofit() throws ParseException {
		// 5/5 will have low of 670 and high of 1330, which will be max profit day for GOOG and
    	// 669 and 1331 AMZN
    	
    	MaxProfitCalculator calculator = new MaxProfitCalculator();
    	calculator.calculate(getData());
    	JsonArray jsonArray = calculator.toJson();
    	Assert.assertEquals(TICKERS.length, jsonArray.size());
    	Set<String> foundTickers = new HashSet<>();
    	for(int t = 0; t < TICKERS.length; ++t)
    	{
    		JsonObject obj = jsonArray.getJsonObject(t);
    		String ticker = obj.getString("ticker");
    		double maxProfit = obj.getJsonNumber("maxprofit").doubleValue();
    		String date = obj.getString("date");
    		foundTickers.add(ticker);
    		Assert.assertEquals("2017-05-05", date);
    		double mod = ticker.equals("GOOG") ? 0d : 1d;
    		Assert.assertEquals("2017-05-05", date);
    		Assert.assertEquals(1330d - 670d + mod*2, maxProfit, 1e-13);
    	}
    	
    }
}
