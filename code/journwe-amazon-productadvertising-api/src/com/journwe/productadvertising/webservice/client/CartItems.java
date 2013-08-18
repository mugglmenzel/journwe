
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
 *         &lt;element name="SubTotal" type="{http://webservices.amazon.com/AWSECommerceService/2011-08-01}Price" minOccurs="0"/>
 *         &lt;element name="CartItem" type="{http://webservices.amazon.com/AWSECommerceService/2011-08-01}CartItem" maxOccurs="unbounded"/>
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
    "subTotal",
    "cartItem"
})
@XmlRootElement(name = "CartItems")
public class CartItems {

    @XmlElement(name = "SubTotal")
    protected Price subTotal;
    @XmlElement(name = "CartItem", required = true)
    protected List<CartItem> cartItem;

    /**
     * Ruft den Wert der subTotal-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Price }
     *     
     */
    public Price getSubTotal() {
        return subTotal;
    }

    /**
     * Legt den Wert der subTotal-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Price }
     *     
     */
    public void setSubTotal(Price value) {
        this.subTotal = value;
    }

    /**
     * Gets the value of the cartItem property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the cartItem property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCartItem().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link CartItem }
     * 
     * 
     */
    public List<CartItem> getCartItem() {
        if (cartItem == null) {
            cartItem = new ArrayList<CartItem>();
        }
        return this.cartItem;
    }

}
