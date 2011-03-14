package com.freesundance.http;

public class ThreeAllowanceItem {

	private String title = null;
	private String amount = null;

	public ThreeAllowanceItem(String title, String amount) {
		setTitle(title);
		setAmount(amount);
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();

		sb.append("title [").append(title).append("] ");
		sb.append("amount [").append(amount).append("]");

		return sb.toString();
	}
}
