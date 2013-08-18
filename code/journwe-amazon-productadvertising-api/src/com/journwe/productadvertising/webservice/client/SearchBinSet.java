
package com.journwe.productadvertising.webservice.client;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
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
 *         &lt;element ref="{http://webservices.amazon.com/AWSECommerceService/2011-08-01}Bin" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="NarrowBy" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "bin"
})
@XmlRootElement(name = "SearchBinSet")
public class SearchBinSet {

    @XmlElement(name = "Bin")
    protected List<Bin> bin;
    @XmlAttribute(name = "NarrowBy", required = true)
    protected String narrowBy;

    /**
     * Gets the value of the bin property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the bin property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getBin().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Bin }
     * 
     * 
     */
    public List<Bin> getBin() {
        if (bin == null) {
            bin = new ArrayList<Bin>();
        }
        return this.bin;
    }

    /**
     * Ruft den Wert der narrowBy-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNarrowBy() {
        return narrowBy;
    }

    /**
     * Legt den Wert der narrowBy-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNarrowBy(String value) {
        this.narrowBy = value;
    }

}
