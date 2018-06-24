package capital.one.stock.analyzer;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.json.Json;
import javax.json.JsonArray;

import capital.one.stock.data.StockData;

public class LosingDaysCalculator implements Calculator {

	private Map<String, Integer> losingDaysByTicker = new HashMap<>();
	public static final String TYPE = "losing_days";
	
	public Map<String, Integer> getLosingDaysByTicker()
	{
		return losingDaysByTicker;
	}
	
	@Override
	public void calculate(Collection<StockData> stocks) {
		for(StockData stock: stocks) 
		{
			losingDaysByTicker.computeIfAbsent(stock.getTicker(), k -> 0);
			if(stock.getOpen() > stock.getClose())
			{
				losingDaysByTicker.compute(stock.getTicker(), (k,v) -> ++v);
			}
		}
	}

	@Override
	public JsonArray toJson() {
		Map.Entry<String, Integer> max = losingDaysByTicker.entrySet()
				.stream()
				.max((o1, o2) -> o1.getValue().compareTo(o2.getValue()))
				.get();
		return Json.createArrayBuilder()
				.add(
					Json.createObjectBuilder()
					.add("ticker", max.getKey())
					.add("losing_days", max.getValue()).build())
				.build();
	}

	@Override
	public String getType() {
		return TYPE;
	}}
