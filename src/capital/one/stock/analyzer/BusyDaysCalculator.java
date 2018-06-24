package capital.one.stock.analyzer;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import capital.one.stock.data.StockData;
import capital.one.util.Utils;

public class BusyDaysCalculator implements Calculator {
	
	private Map<String, Double> avgVolumeByTicker = new HashMap<>();
	private Map<String, List<StockData>> busyDaysByTicker = new HashMap<>();
	private final double busyDayThreshod;
	public static final String TYPE = "busy_days";
	
	
	public Map<String, Double> getAvgVolumeByTicker()
	{
		return avgVolumeByTicker;
	}
	public Map<String, List<StockData>> getBusyDays()
	{
		return busyDaysByTicker;
	}
	
	public BusyDaysCalculator()
	{
		this(.1);
	}
	public BusyDaysCalculator(double busyDayThreshod)
	{
		this.busyDayThreshod = 1d + busyDayThreshod;
	}
	
	
	@Override
	public void calculate(Collection<StockData> stocks) {
		String ticker = stocks.iterator().next().getTicker();
		double avgVolume = stocks.stream().mapToDouble(d -> d.getVolume()).average().getAsDouble();
		avgVolumeByTicker.put(ticker, avgVolume);
		busyDaysByTicker.put(ticker, stocks.stream()
				.filter(stock -> stock.getVolume() > avgVolume * busyDayThreshod)
				.collect(Collectors.toList()));
	}
	private JsonObject getJsonForData(StockData stock) {
		JsonObjectBuilder builder = Json.createObjectBuilder();
		builder.add("date", Utils.FORMAT.format(stock.getDate()));
		builder.add("volume", stock.getVolume());
		return builder.build();
	}
	private JsonObject getJsonForTicker(String ticker) {
		JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
		busyDaysByTicker.get(ticker).forEach(data -> arrayBuilder.add(getJsonForData(data)));
		return Json.createObjectBuilder()
				   .add("ticker", ticker)
				   .add("avg_volume", this.avgVolumeByTicker.get(ticker))
				   .add("busy_days", arrayBuilder.build()).build();
	}
	
	@Override
	public JsonArray toJson() {
		JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
		busyDaysByTicker.keySet().forEach(ticker -> arrayBuilder.add(getJsonForTicker(ticker)));
		return arrayBuilder.build();
	}
	
	@Override
	public String getType() {
		return TYPE;
	}
	

}
