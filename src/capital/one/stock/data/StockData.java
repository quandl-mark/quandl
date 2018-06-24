package capital.one.stock.data;

import java.util.Date;

public class StockData {

	private String ticker;
	private Date date;
	private Double open;
	private Double high;
	private Double low;
	private Double close;
	private Double volume;
	
	public static class StockDataBuilder
	{
		private String ticker;
		private Date date;
		private Double open;
		private Double high;
		private Double low;
		private Double close;
		private Double volume;
		public StockDataBuilder withTicker(final String ticker) {
			this.ticker = ticker;
			return this;
		}
		public StockDataBuilder withDate(final Date date) {
			this.date = date;
			return this;
		}
		public StockDataBuilder withOpen(final Double open) {
			this.open = open;
			return this;
		}
		public StockDataBuilder withClose(final Double close) {
			this.close = close;
			return this;
		}
		public StockDataBuilder withHigh(final Double high) {
			this.high = high;
			return this;
		}
		public StockDataBuilder withLow(final Double low) {
			this.low = low;
			return this;
		}
		public StockDataBuilder withVolume(final Double volume) {
			this.volume = volume;
			return this;
		}
		
		
		public StockData build()
		{
			StockData ret = new StockData();
			ret.close = close;
			ret.date = date;
			ret.high = high;
			ret.low = low;
			ret.open = open;
			ret.ticker = ticker;
			ret.volume = volume;
 			return ret;
		}
	}
	
	public Date getDate()
	{
		return this.date;
	}

	public Double getOpen() {
		return open;
	}

	public Double getHigh() {
		return high;
	}

	public Double getLow() {
		return low;
	}

	public Double getClose() {
		return close;
	}

	public Double getVolume() {
		return volume;
	}

	public String getTicker() {
		return ticker;
	}
}
