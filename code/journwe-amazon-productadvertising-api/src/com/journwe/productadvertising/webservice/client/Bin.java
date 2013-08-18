
package com.journwe.productadvertising.webservice.client;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
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
 *         &lt;element name="BinName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="BinItemCount" type="{http://www.w3.org/2001/XMLSchema}positiveInteger"/>
 *         &lt;element name="BinParameter" maxOccurs="unbounded" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="Name" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="Value" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
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
    "binName",
    "binItemCount",
    "binParameter"
})
@XmlRootElement(name = "Bin")
public class Bin {

    @XmlElement(name = "BinName", required = true)
    protected String binName;
    @XmlElement(name = "BinItemCount", required = true)
    @XmlSchemaType(name = "positiveInteger")
    protected BigInteger binItemCount;
    @XmlElement(name = "BinParameter")
    protected List<Bin.BinParameter> binParameter;

    /**
     * Ruft den Wert der binName-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBinName() {
        return binName;
    }

    /**
     * Legt den Wert der binName-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBinName(String value) {
        this.binName = value;
    }

    /**
     * Ruft den Wert der binItemCount-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getBinItemCount() {
        return binItemCount;
    }

    /**
     * Legt den Wert der binItemCount-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setBinItemCount(BigInteger value) {
        this.binItemCount = value;
    }

    /**
     * Gets the value of the binParameter property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the binParameter property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getBinParameter().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Bin.BinParameter }
     * 
     * 
     */
    public List<Bin.BinParameter> getBinParameter() {
        if (binParameter == null) {
            binParameter = new ArrayList<Bin.BinParameter>();
        }
        return this.binParameter;
    }


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
     *         &lt;element name="Name" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="Value" type="{http://www.w3.org/2001/XMLSchema}string"/>
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
        "name",
        "value"
    })
    public static class BinParameter {

        @XmlElement(name = "Name", required = true)
        protected String name;
        @XmlElement(name = "Value", required = true)
        protected String value;

        /**
         * Ruft den Wert der name-Eigenschaft ab.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getName() {
            return name;
        }

        /**
         * Legt den Wert der name-Eigenschaft fest.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setName(String value) {
            this.name = value;
        }

        /**
         * Ruft den Wert der value-Eigenschaft ab.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getValue() {
            return value;
        }

        /**
         * Legt den Wert der value-Eigenschaft fest.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setValue(String value) {
            this.value = value;
        }

    }

}
