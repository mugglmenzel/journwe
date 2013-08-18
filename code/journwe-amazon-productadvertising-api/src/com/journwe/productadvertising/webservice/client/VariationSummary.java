
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
 *         &lt;element name="LowestPrice" type="{http://webservices.amazon.com/AWSECommerceService/2011-08-01}Price" minOccurs="0"/>
 *         &lt;element name="HighestPrice" type="{http://webservices.amazon.com/AWSECommerceService/2011-08-01}Price" minOccurs="0"/>
 *         &lt;element name="LowestSalePrice" type="{http://webservices.amazon.com/AWSECommerceService/2011-08-01}Price" minOccurs="0"/>
 *         &lt;element name="HighestSalePrice" type="{http://webservices.amazon.com/AWSECommerceService/2011-08-01}Price" minOccurs="0"/>
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
    "lowestPrice",
    "highestPrice",
    "lowestSalePrice",
    "highestSalePrice"
})
@XmlRootElement(name = "VariationSummary")
public class VariationSummary {

    @XmlElement(name = "LowestPrice")
    protected Price lowestPrice;
    @XmlElement(name = "HighestPrice")
    protected Price highestPrice;
    @XmlElement(name = "LowestSalePrice")
    protected Price lowestSalePrice;
    @XmlElement(name = "HighestSalePrice")
    protected Price highestSalePrice;

    /**
     * Ruft den Wert der lowestPrice-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Price }
     *     
     */
    public Price getLowestPrice() {
        return lowestPrice;
    }

    /**
     * Legt den Wert der lowestPrice-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Price }
     *     
     */
    public void setLowestPrice(Price value) {
        this.lowestPrice = value;
    }

    /**
     * Ruft den Wert der highestPrice-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Price }
     *     
     */
    public Price getHighestPrice() {
        return highestPrice;
    }

    /**
     * Legt den Wert der highestPrice-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Price }
     *     
     */
    public void setHighestPrice(Price value) {
        this.highestPrice = value;
    }

    /**
     * Ruft den Wert der lowestSalePrice-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Price }
     *     
     */
    public Price getLowestSalePrice() {
        return lowestSalePrice;
    }

    /**
     * Legt den Wert der lowestSalePrice-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Price }
     *     
     */
    public void setLowestSalePrice(Price value) {
        this.lowestSalePrice = value;
    }

    /**
     * Ruft den Wert der highestSalePrice-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Price }
     *     
     */
    public Price getHighestSalePrice() {
        return highestSalePrice;
    }

    /**
     * Legt den Wert der highestSalePrice-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Price }
     *     
     */
    public void setHighestSalePrice(Price value) {
        this.highestSalePrice = value;
    }

}
