package capital.one.stock.integ;

import java.io.IOException;
import java.text.ParseException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import org.junit.Assert;
import org.junit.Test;

import capital.one.stock.data.QuandlDao;
import capital.one.stock.data.StockDao;
import capital.one.stock.data.StockData;
import capital.one.util.Utils;

public class QuandlIntegTest {

	@Test
	public void testGetDataForTickerCorrectDates() throws IOException, ParseException {
		StockDao dao = new QuandlDao();
		TreeSet<StockData> stockdata = dao.getData("AMZN", Utils.FORMAT.parse("2017-01-09"), Utils.FORMAT.parse("2017-01-13"));
		Set<String> datesExcepted = new HashSet<>(
				Arrays.asList("2017-01-09",
								"2017-01-10",
								"2017-01-11",
								"2017-01-12",
								"2017-01-13"));
		Set<String> datesActual = new HashSet<>();
		stockdata.forEach(data -> datesActual.add(Utils.FORMAT.format(data.getDate())));
		Assert.assertArrayEquals(datesExcepted.toArray(), datesActual.toArray());
	}

	@Test
	public void testGetDataForTickerCorrectValues() throws IOException, ParseException {
		StockDao dao = new QuandlDao();
		TreeSet<StockData> stocks = dao.getData("AMZN", Utils.FORMAT.parse("2017-01-09"), Utils.FORMAT.parse("2017-01-13"));
		
		double[] opens = new double[] {798.0, 796.6, 793.66, 800.31, 814.32};
		double[] highs = new double[] {801.7742, 798.0, 799.5, 814.13, 821.65};
		double[] lows = new double[] {791.77, 789.5434, 789.51, 799.5, 811.4};
		double[] closes = new double[] {796.92, 795.9, 799.02, 813.64, 817.14};
		double[] volumes = new double[] {3446109.0, 2558369.0, 2992791.0, 4873922.0, 3791945.0};
		int i = 0;
		for(StockData stock: stocks)
		{
			Assert.assertEquals(opens[i], stock.getOpen(), 1e-13);
			Assert.assertEquals(highs[i], stock.getHigh(), 1e-13);
			Assert.assertEquals(lows[i], stock.getLow(), 1e-13);
			Assert.assertEquals(closes[i], stock.getClose(), 1e-13);
			Assert.assertEquals(volumes[i], stock.getVolume(), 1e-13);
			++i;
		}
	}
	
	@Test(expected = IOException.class)
	public void testBadTicker() throws IOException, ParseException {
		StockDao dao = new QuandlDao();
		dao.getData("NOTAVALIDTICKER", Utils.FORMAT.parse("2017-01-09"), Utils.FORMAT.parse("2017-01-13"));
	}
}
