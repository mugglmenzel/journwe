
package com.journwe.productadvertising.webservice.client;

import java.math.BigInteger;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
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
 *         &lt;element name="Points" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger" minOccurs="0"/>
 *         &lt;element name="TypicalRedemptionValue" type="{http://webservices.amazon.com/AWSECommerceService/2011-08-01}Price" minOccurs="0"/>
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
    "points",
    "typicalRedemptionValue"
})
@XmlRootElement(name = "LoyaltyPoints")
public class LoyaltyPoints {

    @XmlElement(name = "Points")
    @XmlSchemaType(name = "nonNegativeInteger")
    protected BigInteger points;
    @XmlElement(name = "TypicalRedemptionValue")
    protected Price typicalRedemptionValue;

    /**
     * Ruft den Wert der points-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getPoints() {
        return points;
    }

    /**
     * Legt den Wert der points-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setPoints(BigInteger value) {
        this.points = value;
    }

    /**
     * Ruft den Wert der typicalRedemptionValue-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Price }
     *     
     */
    public Price getTypicalRedemptionValue() {
        return typicalRedemptionValue;
    }

    /**
     * Legt den Wert der typicalRedemptionValue-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Price }
     *     
     */
    public void setTypicalRedemptionValue(Price value) {
        this.typicalRedemptionValue = value;
    }

}
