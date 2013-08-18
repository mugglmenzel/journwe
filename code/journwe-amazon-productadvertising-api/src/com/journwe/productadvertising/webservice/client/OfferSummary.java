
package com.journwe.productadvertising.webservice.client;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für anonymous complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="LowestNewPrice" type="{http://webservices.amazon.com/AWSECommerceService/2011-08-01}Price" minOccurs="0"/>
 *         &lt;element name="LowestUsedPrice" type="{http://webservices.amazon.com/AWSECommerceService/2011-08-01}Price" minOccurs="0"/>
 *         &lt;element name="LowestCollectiblePrice" type="{http://webservices.amazon.com/AWSECommerceService/2011-08-01}Price" minOccurs="0"/>
 *         &lt;element name="LowestRefurbishedPrice" type="{http://webservices.amazon.com/AWSECommerceService/2011-08-01}Price" minOccurs="0"/>
 *         &lt;element name="TotalNew" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="TotalUsed" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="TotalCollectible" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="TotalRefurbished" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "lowestNewPrice",
    "lowestUsedPrice",
    "lowestCollectiblePrice",
    "lowestRefurbishedPrice",
    "totalNew",
    "totalUsed",
    "totalCollectible",
    "totalRefurbished"
})
@XmlRootElement(name = "OfferSummary")
public class OfferSummary {

    @XmlElement(name = "LowestNewPrice")
    protected Price lowestNewPrice;
    @XmlElement(name = "LowestUsedPrice")
    protected Price lowestUsedPrice;
    @XmlElement(name = "LowestCollectiblePrice")
    protected Price lowestCollectiblePrice;
    @XmlElement(name = "LowestRefurbishedPrice")
    protected Price lowestRefurbishedPrice;
    @XmlElement(name = "TotalNew")
    protected String totalNew;
    @XmlElement(name = "TotalUsed")
    protected String totalUsed;
    @XmlElement(name = "TotalCollectible")
    protected String totalCollectible;
    @XmlElement(name = "TotalRefurbished")
    protected String totalRefurbished;

    /**
     * Ruft den Wert der lowestNewPrice-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Price }
     *     
     */
    public Price getLowestNewPrice() {
        return lowestNewPrice;
    }

    /**
     * Legt den Wert der lowestNewPrice-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Price }
     *     
     */
    public void setLowestNewPrice(Price value) {
        this.lowestNewPrice = value;
    }

    /**
     * Ruft den Wert der lowestUsedPrice-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Price }
     *     
     */
    public Price getLowestUsedPrice() {
        return lowestUsedPrice;
    }

    /**
     * Legt den Wert der lowestUsedPrice-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Price }
     *     
     */
    public void setLowestUsedPrice(Price value) {
        this.lowestUsedPrice = value;
    }

    /**
     * Ruft den Wert der lowestCollectiblePrice-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Price }
     *     
     */
    public Price getLowestCollectiblePrice() {
        return lowestCollectiblePrice;
    }

    /**
     * Legt den Wert der lowestCollectiblePrice-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Price }
     *     
     */
    public void setLowestCollectiblePrice(Price value) {
        this.lowestCollectiblePrice = value;
    }

    /**
     * Ruft den Wert der lowestRefurbishedPrice-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Price }
     *     
     */
    public Price getLowestRefurbishedPrice() {
        return lowestRefurbishedPrice;
    }

    /**
     * Legt den Wert der lowestRefurbishedPrice-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Price }
     *     
     */
    public void setLowestRefurbishedPrice(Price value) {
        this.lowestRefurbishedPrice = value;
    }

    /**
     * Ruft den Wert der totalNew-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTotalNew() {
        return totalNew;
    }

    /**
     * Legt den Wert der totalNew-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTotalNew(String value) {
        this.totalNew = value;
    }

    /**
     * Ruft den Wert der totalUsed-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTotalUsed() {
        return totalUsed;
    }

    /**
     * Legt den Wert der totalUsed-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTotalUsed(String value) {
        this.totalUsed = value;
    }

    /**
     * Ruft den Wert der totalCollectible-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTotalCollectible() {
        return totalCollectible;
    }

    /**
     * Legt den Wert der totalCollectible-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTotalCollectible(String value) {
        this.totalCollectible = value;
    }

    /**
     * Ruft den Wert der totalRefurbished-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTotalRefurbished() {
        return totalRefurbished;
    }

    /**
     * Legt den Wert der totalRefurbished-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTotalRefurbished(String value) {
        this.totalRefurbished = value;
    }

}
