package capital.one.stock.analyzer;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.logging.Logger;

import javax.json.Json;
import javax.json.JsonObjectBuilder;

import capital.one.stock.data.StockDao;
import capital.one.stock.data.StockData;
import capital.one.stock.exceptions.BadRequestException;
import capital.one.util.Utils;

public class CalculatorRunner {

	private final StockDao stockDao;
	
	private final Logger logger;
	
	public CalculatorRunner(StockDao stockDao)
	{
		this.stockDao = stockDao;
		this.logger = Logger.getLogger(CalculatorRunner.class.getName());
	}
	
	public void run(final Map<String, String> params) throws BadRequestException
	{
		String[] tickers = Utils.DEFAULT_TICKERS;
		if(params.containsKey(Utils.TICKER))
		{
			tickers = params.get(Utils.TICKER).toUpperCase().split(",");
		}
		logger.info("Using tickers: " + Arrays.toString(tickers));
		String[] calculatorNames = new String[] {AverageOpenCloseCalculator.TYPE};
		if(params.containsKey(Utils.CACLULATOR))
		{
			calculatorNames = params.get(Utils.CACLULATOR).split(",");
			List<String> invalid = new LinkedList<>();
			Arrays.asList(calculatorNames).forEach(c ->
			{
				if(!Utils.CALCULATOR_CLASSES_BY_TYPE.containsKey(c))
				{
					invalid.add(c);
				}
			});
			if(!invalid.isEmpty())
			{
				String msg = String.format("Invalid calcualtors: %s.  Supported Calculators are %s",
						String.join(",", invalid),
						String.join(",", Utils.CALCULATOR_CLASSES_BY_TYPE.keySet()));
				logger.severe(msg);
				throw new BadRequestException(msg);
			}
		}
		logger.info("Using calculators: " + Arrays.toString(calculatorNames));
		Date startDate = Utils.DEFAULT_START_DATE;
		Date endDate = Utils.DEFAULT_END_DATE;
		if(params.containsKey(Utils.START_DATE))
		{
			try {
				startDate = Utils.FORMAT.parse(params.get(Utils.START_DATE));
			} catch (ParseException e) {
				String msg = String.format("Unable to parse %s: %s.  Input in format yyyy-MM-dd",
						Utils.START_DATE,
						params.get(startDate));
				logger.severe(msg);
				throw new BadRequestException(msg);
			}
		}
		if(params.containsKey(Utils.END_DATE))
		{
			try {
				endDate = Utils.FORMAT.parse(params.get(Utils.END_DATE));
			} catch (ParseException e) {
				String msg = String.format("Unable to parse %s: %s.  Input in format yyyy-MM-dd",
						Utils.END_DATE,
						params.get(startDate));
				logger.severe(msg);
				throw new BadRequestException(msg);
			}
		}
		if(endDate.before(startDate))
		{
			String msg = String.format("%s: %s must be before %s: %s",
					Utils.START_DATE,
					Utils.FORMAT.format(startDate),
					Utils.END_DATE,
					Utils.FORMAT.format(endDate));
			logger.severe(msg);
			throw new BadRequestException(msg);
		}
		logger.info(
				String.format("Using %s: %s, %s: %s",
						Utils.START_DATE,
						Utils.FORMAT.format(startDate),
						Utils.END_DATE,
						Utils.FORMAT.format(endDate)));
		String outputLocation = Utils.OUTPUT_LOCATION;
		if(params.containsKey(Utils.OUTPUT_LOCATION) && !params.get(Utils.OUTPUT_LOCATION).isEmpty())
		{
			outputLocation = params.get(Utils.OUTPUT_LOCATION);
		}
		logger.info("Using output: " + outputLocation);
		Map<String,Collection<StockData>> stocksByTicker = new HashMap<>();
		for(String ticker: tickers)
		{
			try {
				TreeSet<StockData> stocks = stockDao.getData(ticker, startDate, endDate);
				if(stocks.isEmpty())
				{
					logger.severe("Unable to get data for " + ticker);
				}
				else
				{
					logger.info(String.format("Data recieved for ticker: %s from %s to %s",
							ticker,
							Utils.FORMAT.format(stocks.first().getDate()),
							Utils.FORMAT.format(stocks.last().getDate())));
					stocksByTicker.put(ticker, stockDao.getData(ticker, startDate, endDate));
				}
			} catch (IOException e) {
				logger.severe("Unable to get data for " + ticker);
			}
		}
		if(stocksByTicker.isEmpty())
		{
			logger.severe("Unable to get any data");
			throw new BadRequestException("Unable to get any data");
		}
		logger.info("Running analysis for tickers: " + String.join(",", stocksByTicker.keySet()));

		JsonObjectBuilder builder = Json.createObjectBuilder();
		for(String calculatorName: calculatorNames)
		{
			try {
				final Calculator calculator = Utils.CALCULATOR_CLASSES_BY_TYPE.get(calculatorName).newInstance();
				stocksByTicker.values().forEach(stocks -> calculator.calculate(stocks));
				builder.add(calculator.getType(), calculator.toJson());
			} catch (InstantiationException | IllegalAccessException e) {
				// will never get here based on call pattern  Throwing runtime.
				throw new RuntimeException(e);
			}
		}
		try {
			Files.write(Paths.get(outputLocation), Collections.singleton(builder.build().toString()), StandardCharsets.UTF_8);
		} catch (IOException e) {
			logger.severe("Unable to write to file: " + outputLocation);
			throw new BadRequestException(e);
		}
		logger.info("Output record to file: " + outputLocation);
	}
	
}
