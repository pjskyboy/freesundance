public class BuyActions extends fit.Fixture {
	private LineItems lineItems = new LineItems();
	private double currentPrice = 0.0;

	public void price(double currentPrice) {
		this.currentPrice = currentPrice;
	}

	public void buy() {
		lineItems.buyFor(currentPrice);
	}

	public double total() {
		return lineItems.totalIS();
	}
}


