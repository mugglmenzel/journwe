
package com.journwe.productadvertising.webservice.client;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für Image complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="Image">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="URL" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Height" type="{http://webservices.amazon.com/AWSECommerceService/2011-08-01}DecimalWithUnits"/>
 *         &lt;element name="Width" type="{http://webservices.amazon.com/AWSECommerceService/2011-08-01}DecimalWithUnits"/>
 *         &lt;element name="IsVerified" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Image", propOrder = {
    "url",
    "height",
    "width",
    "isVerified"
})
public class Image {

    @XmlElement(name = "URL", required = true)
    protected String url;
    @XmlElement(name = "Height", required = true)
    protected DecimalWithUnits height;
    @XmlElement(name = "Width", required = true)
    protected DecimalWithUnits width;
    @XmlElement(name = "IsVerified")
    protected String isVerified;

    /**
     * Ruft den Wert der url-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getURL() {
        return url;
    }

    /**
     * Legt den Wert der url-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setURL(String value) {
        this.url = value;
    }

    /**
     * Ruft den Wert der height-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link DecimalWithUnits }
     *     
     */
    public DecimalWithUnits getHeight() {
        return height;
    }

    /**
     * Legt den Wert der height-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link DecimalWithUnits }
     *     
     */
    public void setHeight(DecimalWithUnits value) {
        this.height = value;
    }

    /**
     * Ruft den Wert der width-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link DecimalWithUnits }
     *     
     */
    public DecimalWithUnits getWidth() {
        return width;
    }

    /**
     * Legt den Wert der width-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link DecimalWithUnits }
     *     
     */
    public void setWidth(DecimalWithUnits value) {
        this.width = value;
    }

    /**
     * Ruft den Wert der isVerified-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIsVerified() {
        return isVerified;
    }

    /**
     * Legt den Wert der isVerified-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIsVerified(String value) {
        this.isVerified = value;
    }

}
