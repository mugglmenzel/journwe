package com.journwe.flight.skyscanner.model.reference;

public class Currency {

	private String code;
	private String symbol;
	private String thousandsSeparator;
	private String decimalSeparator;
	private String symbolOnLeft;
	private String spaceBetweenAmountAndSymbol;
	private String roundingCoefficient;
	private String decimalDigits;
	
	public Currency(String code, String symbol, String thousandsSeparator,
			String decimalSeparator, String symbolOnLeft,
			String spaceBetweenAmountAndSymbol, String roundingCoefficient,
			String decimalDigits) {
		super();
		this.code = code;
		this.symbol = symbol;
		this.thousandsSeparator = thousandsSeparator;
		this.decimalSeparator = decimalSeparator;
		this.symbolOnLeft = symbolOnLeft;
		this.spaceBetweenAmountAndSymbol = spaceBetweenAmountAndSymbol;
		this.roundingCoefficient = roundingCoefficient;
		this.decimalDigits = decimalDigits;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	public String getThousandsSeparator() {
		return thousandsSeparator;
	}

	public void setThousandsSeparator(String thousandsSeparator) {
		this.thousandsSeparator = thousandsSeparator;
	}

	public String getDecimalSeparator() {
		return decimalSeparator;
	}

	public void setDecimalSeparator(String decimalSeparator) {
		this.decimalSeparator = decimalSeparator;
	}

	public String getSymbolOnLeft() {
		return symbolOnLeft;
	}

	public void setSymbolOnLeft(String symbolOnLeft) {
		this.symbolOnLeft = symbolOnLeft;
	}

	public String getSpaceBetweenAmountAndSymbol() {
		return spaceBetweenAmountAndSymbol;
	}

	public void setSpaceBetweenAmountAndSymbol(String spaceBetweenAmountAndSymbol) {
		this.spaceBetweenAmountAndSymbol = spaceBetweenAmountAndSymbol;
	}

	public String getRoundingCoefficient() {
		return roundingCoefficient;
	}

	public void setRoundingCoefficient(String roundingCoefficient) {
		this.roundingCoefficient = roundingCoefficient;
	}

	public String getDecimalDigits() {
		return decimalDigits;
	}

	public void setDecimalDigits(String decimalDigits) {
		this.decimalDigits = decimalDigits;
	}

}
