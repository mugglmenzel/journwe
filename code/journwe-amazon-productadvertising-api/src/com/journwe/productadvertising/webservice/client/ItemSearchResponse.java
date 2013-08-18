
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
 *         &lt;element ref="{http://webservices.amazon.com/AWSECommerceService/2011-08-01}OperationRequest" minOccurs="0"/>
 *         &lt;element ref="{http://webservices.amazon.com/AWSECommerceService/2011-08-01}Items" maxOccurs="unbounded" minOccurs="0"/>
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
    "operationRequest",
    "items"
})
@XmlRootElement(name = "ItemSearchResponse")
public class ItemSearchResponse {

    @XmlElement(name = "OperationRequest")
    protected OperationRequest operationRequest;
    @XmlElement(name = "Items")
    protected List<Items> items;

    /**
     * Ruft den Wert der operationRequest-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link OperationRequest }
     *     
     */
    public OperationRequest getOperationRequest() {
        return operationRequest;
    }

    /**
     * Legt den Wert der operationRequest-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link OperationRequest }
     *     
     */
    public void setOperationRequest(OperationRequest value) {
        this.operationRequest = value;
    }

    /**
     * Gets the value of the items property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the items property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getItems().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Items }
     * 
     * 
     */
    public List<Items> getItems() {
        if (items == null) {
            items = new ArrayList<Items>();
        }
        return this.items;
    }

}
