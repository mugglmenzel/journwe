
package com.journwe.productadvertising.webservice.client;

import java.util.ArrayList;
import java.util.List;
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
 *         &lt;element name="MarketplaceDomain" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="AWSAccessKeyId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="AssociateTag" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Validate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="XMLEscaping" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Shared" type="{http://webservices.amazon.com/AWSECommerceService/2011-08-01}CartClearRequest" minOccurs="0"/>
 *         &lt;element name="Request" type="{http://webservices.amazon.com/AWSECommerceService/2011-08-01}CartClearRequest" maxOccurs="unbounded" minOccurs="0"/>
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
    "marketplaceDomain",
    "awsAccessKeyId",
    "associateTag",
    "validate",
    "xmlEscaping",
    "shared",
    "request"
})
@XmlRootElement(name = "CartClear")
public class CartClear {

    @XmlElement(name = "MarketplaceDomain")
    protected String marketplaceDomain;
    @XmlElement(name = "AWSAccessKeyId")
    protected String awsAccessKeyId;
    @XmlElement(name = "AssociateTag")
    protected String associateTag;
    @XmlElement(name = "Validate")
    protected String validate;
    @XmlElement(name = "XMLEscaping")
    protected String xmlEscaping;
    @XmlElement(name = "Shared")
    protected CartClearRequest shared;
    @XmlElement(name = "Request")
    protected List<CartClearRequest> request;

    /**
     * Ruft den Wert der marketplaceDomain-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMarketplaceDomain() {
        return marketplaceDomain;
    }

    /**
     * Legt den Wert der marketplaceDomain-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMarketplaceDomain(String value) {
        this.marketplaceDomain = value;
    }

    /**
     * Ruft den Wert der awsAccessKeyId-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAWSAccessKeyId() {
        return awsAccessKeyId;
    }

    /**
     * Legt den Wert der awsAccessKeyId-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAWSAccessKeyId(String value) {
        this.awsAccessKeyId = value;
    }

    /**
     * Ruft den Wert der associateTag-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAssociateTag() {
        return associateTag;
    }

    /**
     * Legt den Wert der associateTag-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAssociateTag(String value) {
        this.associateTag = value;
    }

    /**
     * Ruft den Wert der validate-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getValidate() {
        return validate;
    }

    /**
     * Legt den Wert der validate-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setValidate(String value) {
        this.validate = value;
    }

    /**
     * Ruft den Wert der xmlEscaping-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getXMLEscaping() {
        return xmlEscaping;
    }

    /**
     * Legt den Wert der xmlEscaping-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setXMLEscaping(String value) {
        this.xmlEscaping = value;
    }

    /**
     * Ruft den Wert der shared-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link CartClearRequest }
     *     
     */
    public CartClearRequest getShared() {
        return shared;
    }

    /**
     * Legt den Wert der shared-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link CartClearRequest }
     *     
     */
    public void setShared(CartClearRequest value) {
        this.shared = value;
    }

    /**
     * Gets the value of the request property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the request property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getRequest().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link CartClearRequest }
     * 
     * 
     */
    public List<CartClearRequest> getRequest() {
        if (request == null) {
            request = new ArrayList<CartClearRequest>();
        }
        return this.request;
    }

}
