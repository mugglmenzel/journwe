package com.journwe.productadvertising.webservice.client.enums;

/**
 * Created with IntelliJ IDEA.
 * User: mugglmenzel
 * Date: 18.08.13
 * Time: 15:16
 * To change this template use File | Settings | File Templates.
 */
public enum EMarketplaceDomain {

    DE("www.javari.de", "de"), JP("www.javari.jp", "jp"), UK("www.javari.co.uk", "en_GB"), US("www.amazonsupply.com", "en_US");

    private String marketPlaceDomain;

    private String langCode;

    private EMarketplaceDomain(String marketPlaceDomain, String langCode) {
        this.marketPlaceDomain = marketPlaceDomain;
        this.langCode = langCode;
    }

    public String getMarketPlaceDomain() {
        return marketPlaceDomain;
    }

    public String getLangCode() {
        return langCode;
    }

    public EMarketplaceDomain getByLangCode(String langCode) {
        for(EMarketplaceDomain d : values())
            if(d.getLangCode().equals(langCode)) return d;
        return null;
    }
}
