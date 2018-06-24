package capital.one.stock.driver;

import java.util.Arrays;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;

import capital.one.stock.analyzer.AverageOpenCloseCalculator;
import capital.one.stock.analyzer.CalculatorRunner;
import capital.one.stock.data.QuandlDao;
import capital.one.stock.exceptions.BadRequestException;
import capital.one.util.Utils;

public class Driver {

	public static void main(String[] args)
	{
		CalculatorRunner runner = new CalculatorRunner(new QuandlDao());
		Scanner scanner = new Scanner(System.in);
		while(true)
		{
			System.out.println("Awaiting Input: ");
			String[] lines = scanner.nextLine().split("\\s+");
			Map<String, String> parsed = Arrays.asList(lines)
					.stream()
					.map(x -> x.toLowerCase())
					.map(s -> s.split("="))
					.collect(Collectors.toMap(s -> s[0], 
							s -> (s.length < 2) ? 
									"" : s[1]));

			if(parsed.containsKey("exit"))
			{
				System.out.println("Exiting");
				break;
			}
			if(parsed.containsKey("help"))
			{
				System.out.println("Usage: ");
				System.out.println(String.format("ticker=[TICKERS (Default: %s)]",
						Arrays.toString(Utils.DEFAULT_TICKERS)));
				System.out.println(String.format("%s=[DATE (Default: %s)]",
						Utils.START_DATE,
						Utils.FORMAT.format(Utils.DEFAULT_START_DATE)));
				System.out.println(String.format("%s=[DATE (Default: %s)]",
						Utils.END_DATE,
						Utils.FORMAT.format(Utils.DEFAULT_END_DATE)));
				System.out.println(String.format("%s=[%s (Default: %s)]",
						Utils.CACLULATOR,
						Utils.CALCULATOR_CLASSES_BY_TYPE.keySet(),
						AverageOpenCloseCalculator.TYPE));
				System.out.println(String.format("%s=[FILE (Default: %s)]",
						Utils.OUTPUT_LOCATION,
						Utils.OUTPUT_LOCATION));
				
				continue;
			}
			try {
				runner.run(parsed);
			} catch (BadRequestException e) {
				System.out.println("Bad Request: " + e.getMessage());
				e.printStackTrace();
			}
		}
		scanner.close();
		System.exit(0);
	}
}
