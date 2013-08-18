
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
 *         &lt;element ref="{http://webservices.amazon.com/AWSECommerceService/2011-08-01}HTTPHeaders" minOccurs="0"/>
 *         &lt;element name="RequestId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element ref="{http://webservices.amazon.com/AWSECommerceService/2011-08-01}Arguments" minOccurs="0"/>
 *         &lt;element ref="{http://webservices.amazon.com/AWSECommerceService/2011-08-01}Errors" minOccurs="0"/>
 *         &lt;element name="RequestProcessingTime" type="{http://www.w3.org/2001/XMLSchema}float" minOccurs="0"/>
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
    "httpHeaders",
    "requestId",
    "arguments",
    "errors",
    "requestProcessingTime"
})
@XmlRootElement(name = "OperationRequest")
public class OperationRequest {

    @XmlElement(name = "HTTPHeaders")
    protected HTTPHeaders httpHeaders;
    @XmlElement(name = "RequestId")
    protected String requestId;
    @XmlElement(name = "Arguments")
    protected Arguments arguments;
    @XmlElement(name = "Errors")
    protected Errors errors;
    @XmlElement(name = "RequestProcessingTime")
    protected Float requestProcessingTime;

    /**
     * Ruft den Wert der httpHeaders-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link HTTPHeaders }
     *     
     */
    public HTTPHeaders getHTTPHeaders() {
        return httpHeaders;
    }

    /**
     * Legt den Wert der httpHeaders-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link HTTPHeaders }
     *     
     */
    public void setHTTPHeaders(HTTPHeaders value) {
        this.httpHeaders = value;
    }

    /**
     * Ruft den Wert der requestId-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRequestId() {
        return requestId;
    }

    /**
     * Legt den Wert der requestId-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRequestId(String value) {
        this.requestId = value;
    }

    /**
     * Ruft den Wert der arguments-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Arguments }
     *     
     */
    public Arguments getArguments() {
        return arguments;
    }

    /**
     * Legt den Wert der arguments-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Arguments }
     *     
     */
    public void setArguments(Arguments value) {
        this.arguments = value;
    }

    /**
     * Ruft den Wert der errors-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Errors }
     *     
     */
    public Errors getErrors() {
        return errors;
    }

    /**
     * Legt den Wert der errors-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Errors }
     *     
     */
    public void setErrors(Errors value) {
        this.errors = value;
    }

    /**
     * Ruft den Wert der requestProcessingTime-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Float }
     *     
     */
    public Float getRequestProcessingTime() {
        return requestProcessingTime;
    }

    /**
     * Legt den Wert der requestProcessingTime-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Float }
     *     
     */
    public void setRequestProcessingTime(Float value) {
        this.requestProcessingTime = value;
    }

}
