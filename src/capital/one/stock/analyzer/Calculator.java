package capital.one.stock.analyzer;

import java.util.Collection;

import javax.json.JsonArray;

import capital.one.stock.data.StockData;

public interface Calculator {

	JsonArray toJson();
	
	String getType();
	
	void calculate(Collection<StockData> stocks);
}
