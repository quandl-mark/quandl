package capital.one.stock.data;

import java.io.IOException;
import java.util.Date;
import java.util.TreeSet;

public interface StockDao {

	TreeSet<StockData> getData(String ticker, Date start, Date end) throws IOException;
	
}
