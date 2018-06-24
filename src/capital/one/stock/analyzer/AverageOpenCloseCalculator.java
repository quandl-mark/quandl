package capital.one.stock.analyzer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import capital.one.stock.data.StockData;
import capital.one.util.Utils;

public class AverageOpenCloseCalculator implements Calculator {

	private Map<String, Map<String, Double>> openAverageByMonthByTicker = new HashMap<>();
	private Map<String, Map<String, Double>> closeAverageByMonthByTicker = new HashMap<>();
	private Map<String, Map<String, Integer>> countByMonthByTicker = new HashMap<>();

	public static final String TYPE = "average_open_close";
	
	private JsonObject getJsonForMonth(String ticker, String month)
	{
		JsonObjectBuilder jsonObjectBuilder = Json.createObjectBuilder();
		jsonObjectBuilder.add("month", month);
		jsonObjectBuilder.add("average_open", openAverageByMonthByTicker.get(ticker).get(month));
		jsonObjectBuilder.add("average_close", closeAverageByMonthByTicker.get(ticker).get(month));
		return jsonObjectBuilder.build();
	}
	
	@Override
	public JsonArray toJson()
	{
		JsonArrayBuilder jsonArrayReturnBuilder = Json.createArrayBuilder();
		for (String ticker: countByMonthByTicker.keySet())
		{
			JsonObjectBuilder jsonObjectBuilder = Json.createObjectBuilder();
			JsonArrayBuilder jsonAvgArrayBuilder = Json.createArrayBuilder();
			List<String> months = new ArrayList<>(countByMonthByTicker.get(ticker).keySet());
			Collections.sort(months);
			months.stream().forEach(month -> jsonAvgArrayBuilder.add(getJsonForMonth(ticker, month)));
			jsonObjectBuilder.add("ticker", ticker).add("averages", jsonAvgArrayBuilder.build());
			jsonArrayReturnBuilder.add(jsonObjectBuilder.build());
		}
		return jsonArrayReturnBuilder.build();
	}
	
	@Override 
	public String getType()
	{
		return TYPE;
	}
	
	private void computeNewAverage(final Map<String, Double> averageByMonth, final Map<String, Integer> countByMonth, final String month, final double newValue)
	{
		double oldAvg = averageByMonth.get(month);
		int count = countByMonth.get(month);
		double newAvg = (oldAvg * count + newValue) / (count + 1);
		averageByMonth.put(month, newAvg);
	}
	
	private void add(final StockData stock) {
		final String month = Utils.FORMAT_MONTH.format(stock.getDate());
		Map<String, Double> openAverageByMonth = openAverageByMonthByTicker.computeIfAbsent(stock.getTicker(), t -> new HashMap<>());
		Map<String, Double> closeAverageByMonth = closeAverageByMonthByTicker.computeIfAbsent(stock.getTicker(), t -> new HashMap<>());
		Map<String, Integer> countByMonth = countByMonthByTicker.computeIfAbsent(stock.getTicker(), t -> new HashMap<>());
		openAverageByMonth.computeIfAbsent(month, m -> 0d);
		closeAverageByMonth.computeIfAbsent(month, m -> 0d);
		countByMonth.computeIfAbsent(month, m -> 0);
		computeNewAverage(openAverageByMonth, countByMonth, month, stock.getOpen());
		computeNewAverage(closeAverageByMonth, countByMonth, month, stock.getClose());
		countByMonth.compute(month, (k,v) -> ++v);
	}
	
	@Override
	public void calculate(Collection<StockData> stocks) {
		for(StockData stock: stocks) {
			add(stock);
		}
	}

}
