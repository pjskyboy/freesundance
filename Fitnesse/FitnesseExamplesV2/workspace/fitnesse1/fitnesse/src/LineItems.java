public class LineItems {
	private double total = 0;

	public void buyFor(double currentPrice) {
		total = total + currentPrice;
	}

	public double totalIS() {
		return total;
	}
}


