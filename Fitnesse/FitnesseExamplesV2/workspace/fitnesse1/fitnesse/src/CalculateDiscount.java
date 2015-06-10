public class CalculateDiscount extends fit.ColumnFixture {
	
	public double amount;
	private Discount sut = new Discount();

	public double discount() {
		return sut.getDiscount(amount);
	}
}

