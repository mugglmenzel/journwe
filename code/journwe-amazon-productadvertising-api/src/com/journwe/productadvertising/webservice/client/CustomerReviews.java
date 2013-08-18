
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
 *         &lt;element name="IFrameURL" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="HasReviews" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
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
    "iFrameURL",
    "hasReviews"
})
@XmlRootElement(name = "CustomerReviews")
public class CustomerReviews {

    @XmlElement(name = "IFrameURL")
    protected String iFrameURL;
    @XmlElement(name = "HasReviews")
    protected Boolean hasReviews;

    /**
     * Ruft den Wert der iFrameURL-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIFrameURL() {
        return iFrameURL;
    }

    /**
     * Legt den Wert der iFrameURL-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIFrameURL(String value) {
        this.iFrameURL = value;
    }

    /**
     * Ruft den Wert der hasReviews-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isHasReviews() {
        return hasReviews;
    }

    /**
     * Legt den Wert der hasReviews-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setHasReviews(Boolean value) {
        this.hasReviews = value;
    }

}
