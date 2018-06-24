package capital.one.stock.integ;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;

import org.junit.Assert;
import org.junit.Test;

import capital.one.stock.analyzer.AverageOpenCloseCalculator;
import capital.one.stock.analyzer.CalculatorRunner;
import capital.one.stock.data.QuandlDao;
import capital.one.stock.exceptions.BadRequestException;
import capital.one.util.Utils;

public class CalculatorIntegTest {

	@Test
	public void happyPathTest() throws BadRequestException, IOException
	{
		InputStream is = null;
		try
		{
			CalculatorRunner runner = new CalculatorRunner(new QuandlDao());
			runner.run(new HashMap<>());
			is = new FileInputStream("output");
			JsonObject obj = Json.createReader(is).readObject();
			JsonArray array = obj.getJsonArray(AverageOpenCloseCalculator.TYPE);
			Set<String> foundTickers = new HashSet<>();
			Assert.assertEquals(Utils.DEFAULT_TICKERS.length, array.size());
			for(int t = 0; t < array.size(); ++t)
			{
				foundTickers.add(array.getJsonObject(t).getString(Utils.TICKER));
			}
			for(String ticker: Utils.DEFAULT_TICKERS)
			{
				Assert.assertTrue(foundTickers.contains(ticker));
			}
		}
		finally
		{
			if (is != null)
			{
				is.close();
			}
			Files.delete(Paths.get("output"));
		}
		
	}
}
