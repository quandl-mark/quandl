package capital.one.stock.analyzer.test;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import capital.one.stock.analyzer.CalculatorRunner;
import capital.one.stock.exceptions.BadRequestException;

public class CalculatorRunnerTest {

	@Test(expected = BadRequestException.class)
	public void throwsBadRequestIfInvalidCalculatorInput() throws BadRequestException
	{
		Map<String, String> badInput = Collections.singletonMap("calculator", "NOTAVALIDCULATOR");
		CalculatorRunner runner = new CalculatorRunner(null);
		runner.run(badInput);
	}

	@Test(expected = BadRequestException.class)
	public void throwsBadRequestIfBadStartDateInput() throws BadRequestException
	{
		Map<String, String> badInput = Collections.singletonMap("startdate", "NOTAVALIDDATE");
		CalculatorRunner runner = new CalculatorRunner(null);
		runner.run(badInput);
	}
	@Test(expected = BadRequestException.class)
	public void throwsBadRequestIfBadEndDateInput() throws BadRequestException
	{
		Map<String, String> badInput = Collections.singletonMap("enddate", "NOTAVALIDDATE");
		CalculatorRunner runner = new CalculatorRunner(null);
		runner.run(badInput);
	}
	@Test(expected = BadRequestException.class)
	public void throwsBadRequestIfEndDateBeforeStartDateInput() throws BadRequestException
	{
		Map<String, String> badInput = new HashMap<>();
		badInput.put("enddate", "2017-01-01");
		badInput.put("startdate", "2017-06-01");
		CalculatorRunner runner = new CalculatorRunner(null);
		runner.run(badInput);
	}
}
