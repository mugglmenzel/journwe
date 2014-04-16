package com.journwe.flight.skyscanner.model.reference;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

public class Locales {

	private static HashMap<String,Locale> localesMap = new HashMap<String,Locale>();
	
	static {
		localesMap.put("cs-CZ", new Locale("cs-CZ","Čeština"));
		localesMap.put("da-DK", new Locale("da-DK","Dansk"));
		localesMap.put("de-DE", new Locale("de-DE","Deutsch"));
		localesMap.put("en-GB", new Locale("en-GB","English"));
		localesMap.put("es-ES", new Locale("es-ES","Español"));
		localesMap.put("fi-FI", new Locale("fi-FI","Suomi"));
		localesMap.put("fr-FR", new Locale("fr-FR","Français"));
		localesMap.put("el-GR", new Locale("el-GR","Ελληνικά"));
		localesMap.put("hu-HU", new Locale("hu-HU","Magyar"));
		localesMap.put("id-ID", new Locale("id-ID","Bahasa Indonesia"));
		localesMap.put("it-IT", new Locale("it-IT","Italiano"));
		localesMap.put("ja-JP", new Locale("ja-JP","日本語"));
		localesMap.put("ko-KR", new Locale("ko-KR","한국어"));
		localesMap.put("ms-MY", new Locale("ms-MY","Bahasa Malaysia"));
		localesMap.put("nl-NL", new Locale("nl-NL","Nederlands"));
		localesMap.put("nb-NO", new Locale("nb-NO","Norsk"));
		localesMap.put("pl-PL", new Locale("pl-PL","Polski"));
		localesMap.put("pt-PT", new Locale("pt-PT","Português"));
		localesMap.put("pt-BR", new Locale("pt-BR","Português"));
		localesMap.put("ro-RO", new Locale("ro-RO","Română"));
		localesMap.put("ru-RU", new Locale("ru-RU","Русский"));
		localesMap.put("sv-SE", new Locale("sv-SE","Svenska"));
		localesMap.put("th-TH", new Locale("th-TH","ภาษาไทย"));
		localesMap.put("tl-PH", new Locale("tl-PH","Filipino"));
		localesMap.put("tr-TR", new Locale("tr-TR","Türkçe"));
		localesMap.put("uk-UA", new Locale("uk-UA","Українська"));
		localesMap.put("vi-VN", new Locale("vi-VN","Tiếng Việt"));
		localesMap.put("zh-CN", new Locale("zh-CN","中文"));
		localesMap.put("zh-TW", new Locale("zh-TW","繁體中文"));
	}

	public Set<Entry<String, Locale>> entrySet() {
		return localesMap.entrySet();
	}

	public Locale get(String key) {
		return localesMap.get(key);
	}

	public Set<String> keySet() {
		return localesMap.keySet();
	}
	
}
