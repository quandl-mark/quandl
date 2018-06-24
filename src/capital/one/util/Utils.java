package capital.one.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import capital.one.stock.analyzer.AverageOpenCloseCalculator;
import capital.one.stock.analyzer.BusyDaysCalculator;
import capital.one.stock.analyzer.Calculator;
import capital.one.stock.analyzer.LosingDaysCalculator;
import capital.one.stock.analyzer.MaxProfitCalculator;

public class Utils {

    public final static SimpleDateFormat FORMAT =
            new SimpleDateFormat("yyyy-MM-dd");
    
    public final static SimpleDateFormat FORMAT_MONTH =
            new SimpleDateFormat("yyyy-MM");
    
    public final static String TICKER = "ticker";
    public final static String CACLULATOR = "calculator";
    public final static String[] DEFAULT_TICKERS = new String[] {"COF", "GOOGL", "MSFT"};
    
    public final static Date DEFAULT_START_DATE = getDefaultStartDate("2017-01-01");
    public final static Date DEFAULT_END_DATE = getDefaultStartDate("2017-06-30");
    
    public final static String START_DATE = "startdate";
    public final static String END_DATE = "enddate";
    
    public final static String OUTPUT_LOCATION = "output";

	public final static Map<String, Class<? extends Calculator>> CALCULATOR_CLASSES_BY_TYPE = new HashMap<String, Class<? extends Calculator>>()
	{{
		put(AverageOpenCloseCalculator.TYPE, AverageOpenCloseCalculator.class);
		put(BusyDaysCalculator.TYPE, BusyDaysCalculator.class);
		put(LosingDaysCalculator.TYPE, LosingDaysCalculator.class);
		put(MaxProfitCalculator.TYPE, MaxProfitCalculator.class);
	}};
	
	
    private static Date getDefaultStartDate(String date)
    {
    	try {
			return FORMAT.parse(date);
		} catch (ParseException e) {
			// should never happen so throwing runtime.
			throw new RuntimeException(e);
		}
    }
}
