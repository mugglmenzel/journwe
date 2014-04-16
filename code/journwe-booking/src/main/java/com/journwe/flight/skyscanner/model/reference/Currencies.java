package com.journwe.flight.skyscanner.model.reference;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

public class Currencies {
	
	private static HashMap<String,Currency> currencyMap = new HashMap<String,Currency>();

	static {
		currencyMap.put("AED", new Currency("AED","د.إ.‏",",",".","true","true","0","2"));
		currencyMap.put("AFN", new Currency("AFN","AFN",",",".","false","false","0","2"));
		currencyMap.put("ARS", new Currency("ARS","$",".",",","true","false","0","2"));
		currencyMap.put("AUD", new Currency("AUD","$",",",".","true","false","0","2"));
		currencyMap.put("BDT", new Currency("BDT","BDT",",",".","true","false","1","0"));
		currencyMap.put("BGN", new Currency("BGN","лв."," ",",","false","true","0","2"));
		currencyMap.put("BOB", new Currency("BOB","Bs.",".",",","true","true","0","2"));
		currencyMap.put("BRL", new Currency("BRL","R$",".",",","true","true","0","2"));
		currencyMap.put("BTN", new Currency("BTN","Nu",",",".","true","false","0","1"));
		currencyMap.put("CAD", new Currency("CAD","$",",",".","true","false","0","2"));
		currencyMap.put("CHF", new Currency("CHF","Fr.","'",".","true","true","0","2"));
		currencyMap.put("CLP", new Currency("CLP","$",".",",","true","true","0","2"));
		currencyMap.put("CNY", new Currency("CNY","元",",",".","false","false","0","2"));
		currencyMap.put("COP", new Currency("COP","$",".",",","true","true","0","2"));
		currencyMap.put("CUC", new Currency("CUC","CUC",",",".","false","true","0","2"));
		currencyMap.put("CZK", new Currency("CZK","Kč"," ",",","false","true","0","2"));
		currencyMap.put("DKK", new Currency("DKK","kr.",".",",","true","true","0","2"));
		currencyMap.put("EGP", new Currency("EGP","ج.م.‏",",",".","true","true","0","2"));
		currencyMap.put("EUR", new Currency("EUR","€",".",",","false","true","0","2"));
		currencyMap.put("FJD", new Currency("FJD","$",",",".","true","false","0","2"));
		currencyMap.put("GBP", new Currency("GBP","£",",",".","true","false","0","2"));
		currencyMap.put("HKD", new Currency("HKD","HK$",",",".","true","false","0","2"));
		currencyMap.put("HRK", new Currency("HRK","kn",".",",","false","true","0","2"));
		currencyMap.put("HUF", new Currency("HUF","Ft"," ",",","false","true","1","2"));
		currencyMap.put("IDR", new Currency("IDR","Rp",".",",","true","true","100","2"));
		currencyMap.put("INR", new Currency("INR","Rs",",",".","true","true","0","2"));
		currencyMap.put("ISK", new Currency("ISK","kr.",".",",","false","true","1","2"));
		currencyMap.put("JOD", new Currency("JOD","د.ا.‏",",",".","true","true","0","2"));
		currencyMap.put("JPY", new Currency("JPY","¥",",",".","true","false","1","2"));
		currencyMap.put("KGS", new Currency("KGS","сом"," ","-","false","true","0","2"));
		currencyMap.put("KHR", new Currency("KHR","KHR",",",".","true","false","50","0"));
		currencyMap.put("KPW", new Currency("KPW","₩",",",".","true","false","0","2"));
		currencyMap.put("KRW", new Currency("KRW","₩",",",".","true","false","10","2"));
		currencyMap.put("KWD", new Currency("KWD","د.ك.‏",",",".","true","true","0","2"));
		currencyMap.put("KZT", new Currency("KZT","Т"," ","-","true","false","1","2"));
		currencyMap.put("LAK", new Currency("LAK","₭",",",".","true","false","1","0"));
		currencyMap.put("LKR", new Currency("LKR","Rp",",",".","true","false","0","0"));
		currencyMap.put("LTL", new Currency("LTL","Lt",".",",","false","true","0","2"));
		currencyMap.put("LVL", new Currency("LVL","Ls"," ",",","true","true","0","2"));
		currencyMap.put("MOP", new Currency("MOP","MOP$",",",".","true","false","0","2"));
		currencyMap.put("MMK", new Currency("MMK","K",",",".","true","false","0","2"));
		currencyMap.put("MNT", new Currency("MNT","₮"," ",",","false","false","10","2"));
		currencyMap.put("MVR", new Currency("MVR","MVR",",",".","true","false","1","1"));
		currencyMap.put("MXN", new Currency("MXN","$",",",".","true","false","0","2"));
		currencyMap.put("MYR", new Currency("MYR","RM",",",".","true","false","0","2"));
		currencyMap.put("NOK", new Currency("NOK","kr"," ",",","true","true","0","2"));
		currencyMap.put("NZD", new Currency("NZD","$",",",".","true","false","0","2"));
		currencyMap.put("OMR", new Currency("OMR","ر.ع.‏",",",".","true","true","0","2"));
		currencyMap.put("PGK", new Currency("PGK","PGK",",",".","true","false","0","2"));
		currencyMap.put("PHP", new Currency("PHP","PhP",",",".","true","false","0","0"));
		currencyMap.put("PKR", new Currency("PKR","Rs",",",".","true","false","1","2"));
		currencyMap.put("PLN", new Currency("PLN","zł"," ",",","false","true","0","2"));
		currencyMap.put("QAR", new Currency("QAR","ر.ق.‏",",",".","true","true","0","2"));
		currencyMap.put("RON", new Currency("RON","lei",".",",","false","true","0","2"));
		currencyMap.put("RUB", new Currency("RUB","р."," ",",","false","false","0","2"));
		currencyMap.put("SAR", new Currency("SAR","ر.س.‏",",",".","true","true","0","2"));
		currencyMap.put("SEK", new Currency("SEK","kr",".",",","false","true","0","2"));
		currencyMap.put("SGD", new Currency("SGD","$",",",".","true","false","0","2"));
		currencyMap.put("THB", new Currency("THB","฿",",",".","true","false","0","2"));
		currencyMap.put("TJS", new Currency("TJS","TJS",",",".","true","false","0","2"));
		currencyMap.put("TMT", new Currency("TMT","m",",",".","true","false","1","0"));
		currencyMap.put("TRY", new Currency("TRY","TL",".",",","false","true","0","2"));
		currencyMap.put("TWD", new Currency("TWD","NT$",",",".","true","false","1","2"));
		currencyMap.put("USD", new Currency("USD","$",",",".","true","false","0","2"));
		currencyMap.put("UAH", new Currency("UAH","грн."," ",",","false","false","0","2"));
		currencyMap.put("VEF", new Currency("VEF","Bs. F.",".",",","true","true","0","2"));
		currencyMap.put("VND", new Currency("VND","₫",".",",","false","true","100","1"));
		currencyMap.put("VUV", new Currency("VUV","VT",",",".","false","false","1","0"));
		currencyMap.put("XAF", new Currency("XAF","F"," ",",","false","false","0","2"));
		currencyMap.put("XOF", new Currency("XOF","F"," ",",","false","false","0","2"));
		currencyMap.put("XPF", new Currency("XPF","F"," ",",","false","false","0","2"));
		currencyMap.put("WST", new Currency("WST","WS$",",",".","true","false","0","2"));
		currencyMap.put("ZAR", new Currency("ZAR","R",",",".","true","false","0","2"));
	}

	public static Set<Entry<String, Currency>> entrySet() {
		return currencyMap.entrySet();
	}

	public static Currency get(String key) {
		return currencyMap.get(key);
	}

	public static Set<String> keySet() {
		return currencyMap.keySet();
	}

}
