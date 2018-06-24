package capital.one.stock.analyzer;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;

import capital.one.stock.data.StockData;
import capital.one.util.Utils;

public class MaxProfitCalculator implements Calculator {

	private Map<String, StockData> maxProfitByTicker = new HashMap<>();
	public static final String TYPE = "max_profit";
	
	public Map<String, StockData> getMaxProfit()
	{
		return maxProfitByTicker;
	}
	
	@Override
	public void calculate(Collection<StockData> stocks) {
		for(StockData stock: stocks) {
			maxProfitByTicker.computeIfAbsent(stock.getTicker(), ticker -> stock);
			double currProfit = stock.getHigh() - stock.getLow();
			StockData currMax = maxProfitByTicker.get(stock.getTicker());
			if(currProfit > currMax.getHigh() - currMax.getLow())
			{
				maxProfitByTicker.put(stock.getTicker(), stock);
			}
		}
	}

	public JsonObject createJsonForTicker(StockData stock) {
		return Json.createObjectBuilder()
				.add("ticker", stock.getTicker())
				.add("maxprofit", stock.getHigh() - stock.getLow())
				.add("date", Utils.FORMAT.format(stock.getDate())).build();
	}
	
	@Override
	public JsonArray toJson() {
		JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
		maxProfitByTicker.values().forEach(stock -> arrayBuilder.add(createJsonForTicker(stock)));
		return arrayBuilder.build();
	}
	@Override
	public String getType() {
		return TYPE;
	}

}
