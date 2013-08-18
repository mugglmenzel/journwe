
package com.journwe.productadvertising.webservice.client;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für ItemLookupRequest complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="ItemLookupRequest">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{http://webservices.amazon.com/AWSECommerceService/2011-08-01}Condition" minOccurs="0"/>
 *         &lt;element name="IdType" minOccurs="0">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;enumeration value="ASIN"/>
 *               &lt;enumeration value="UPC"/>
 *               &lt;enumeration value="SKU"/>
 *               &lt;enumeration value="EAN"/>
 *               &lt;enumeration value="ISBN"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="MerchantId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ItemId" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="ResponseGroup" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="SearchIndex" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="VariationPage" type="{http://webservices.amazon.com/AWSECommerceService/2011-08-01}positiveIntegerOrAll" minOccurs="0"/>
 *         &lt;element name="RelatedItemPage" type="{http://webservices.amazon.com/AWSECommerceService/2011-08-01}positiveIntegerOrAll" minOccurs="0"/>
 *         &lt;element name="RelationshipType" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="IncludeReviewsSummary" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="TruncateReviewsAt" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ItemLookupRequest", propOrder = {
    "condition",
    "idType",
    "merchantId",
    "itemId",
    "responseGroup",
    "searchIndex",
    "variationPage",
    "relatedItemPage",
    "relationshipType",
    "includeReviewsSummary",
    "truncateReviewsAt"
})
public class ItemLookupRequest {

    @XmlElement(name = "Condition")
    protected String condition;
    @XmlElement(name = "IdType")
    protected String idType;
    @XmlElement(name = "MerchantId")
    protected String merchantId;
    @XmlElement(name = "ItemId")
    protected List<String> itemId;
    @XmlElement(name = "ResponseGroup")
    protected List<String> responseGroup;
    @XmlElement(name = "SearchIndex")
    protected String searchIndex;
    @XmlElement(name = "VariationPage")
    protected String variationPage;
    @XmlElement(name = "RelatedItemPage")
    protected String relatedItemPage;
    @XmlElement(name = "RelationshipType")
    protected List<String> relationshipType;
    @XmlElement(name = "IncludeReviewsSummary")
    protected String includeReviewsSummary;
    @XmlElement(name = "TruncateReviewsAt")
    @XmlSchemaType(name = "nonNegativeInteger")
    protected BigInteger truncateReviewsAt;

    /**
     * Ruft den Wert der condition-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCondition() {
        return condition;
    }

    /**
     * Legt den Wert der condition-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCondition(String value) {
        this.condition = value;
    }

    /**
     * Ruft den Wert der idType-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIdType() {
        return idType;
    }

    /**
     * Legt den Wert der idType-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIdType(String value) {
        this.idType = value;
    }

    /**
     * Ruft den Wert der merchantId-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMerchantId() {
        return merchantId;
    }

    /**
     * Legt den Wert der merchantId-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMerchantId(String value) {
        this.merchantId = value;
    }

    /**
     * Gets the value of the itemId property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the itemId property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getItemId().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getItemId() {
        if (itemId == null) {
            itemId = new ArrayList<String>();
        }
        return this.itemId;
    }

    /**
     * Gets the value of the responseGroup property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the responseGroup property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getResponseGroup().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getResponseGroup() {
        if (responseGroup == null) {
            responseGroup = new ArrayList<String>();
        }
        return this.responseGroup;
    }

    /**
     * Ruft den Wert der searchIndex-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSearchIndex() {
        return searchIndex;
    }

    /**
     * Legt den Wert der searchIndex-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSearchIndex(String value) {
        this.searchIndex = value;
    }

    /**
     * Ruft den Wert der variationPage-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVariationPage() {
        return variationPage;
    }

    /**
     * Legt den Wert der variationPage-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVariationPage(String value) {
        this.variationPage = value;
    }

    /**
     * Ruft den Wert der relatedItemPage-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRelatedItemPage() {
        return relatedItemPage;
    }

    /**
     * Legt den Wert der relatedItemPage-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRelatedItemPage(String value) {
        this.relatedItemPage = value;
    }

    /**
     * Gets the value of the relationshipType property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the relationshipType property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getRelationshipType().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getRelationshipType() {
        if (relationshipType == null) {
            relationshipType = new ArrayList<String>();
        }
        return this.relationshipType;
    }

    /**
     * Ruft den Wert der includeReviewsSummary-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIncludeReviewsSummary() {
        return includeReviewsSummary;
    }

    /**
     * Legt den Wert der includeReviewsSummary-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIncludeReviewsSummary(String value) {
        this.includeReviewsSummary = value;
    }

    /**
     * Ruft den Wert der truncateReviewsAt-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getTruncateReviewsAt() {
        return truncateReviewsAt;
    }

    /**
     * Legt den Wert der truncateReviewsAt-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setTruncateReviewsAt(BigInteger value) {
        this.truncateReviewsAt = value;
    }

}
