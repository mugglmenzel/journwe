
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
 * <p>Java-Klasse für ItemSearchRequest complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="ItemSearchRequest">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Actor" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Artist" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Availability" minOccurs="0">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;enumeration value="Available"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element ref="{http://webservices.amazon.com/AWSECommerceService/2011-08-01}AudienceRating" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="Author" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Brand" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="BrowseNode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Composer" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element ref="{http://webservices.amazon.com/AWSECommerceService/2011-08-01}Condition" minOccurs="0"/>
 *         &lt;element name="Conductor" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Director" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ItemPage" type="{http://www.w3.org/2001/XMLSchema}positiveInteger" minOccurs="0"/>
 *         &lt;element name="Keywords" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Manufacturer" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="MaximumPrice" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger" minOccurs="0"/>
 *         &lt;element name="MerchantId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="MinimumPrice" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger" minOccurs="0"/>
 *         &lt;element name="MinPercentageOff" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger" minOccurs="0"/>
 *         &lt;element name="MusicLabel" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Orchestra" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Power" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Publisher" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="RelatedItemPage" type="{http://webservices.amazon.com/AWSECommerceService/2011-08-01}positiveIntegerOrAll" minOccurs="0"/>
 *         &lt;element name="RelationshipType" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="ResponseGroup" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="SearchIndex" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Sort" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Title" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ReleaseDate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
@XmlType(name = "ItemSearchRequest", propOrder = {
    "actor",
    "artist",
    "availability",
    "audienceRating",
    "author",
    "brand",
    "browseNode",
    "composer",
    "condition",
    "conductor",
    "director",
    "itemPage",
    "keywords",
    "manufacturer",
    "maximumPrice",
    "merchantId",
    "minimumPrice",
    "minPercentageOff",
    "musicLabel",
    "orchestra",
    "power",
    "publisher",
    "relatedItemPage",
    "relationshipType",
    "responseGroup",
    "searchIndex",
    "sort",
    "title",
    "releaseDate",
    "includeReviewsSummary",
    "truncateReviewsAt"
})
public class ItemSearchRequest {

    @XmlElement(name = "Actor")
    protected String actor;
    @XmlElement(name = "Artist")
    protected String artist;
    @XmlElement(name = "Availability")
    protected String availability;
    @XmlElement(name = "AudienceRating")
    protected List<String> audienceRating;
    @XmlElement(name = "Author")
    protected String author;
    @XmlElement(name = "Brand")
    protected String brand;
    @XmlElement(name = "BrowseNode")
    protected String browseNode;
    @XmlElement(name = "Composer")
    protected String composer;
    @XmlElement(name = "Condition")
    protected String condition;
    @XmlElement(name = "Conductor")
    protected String conductor;
    @XmlElement(name = "Director")
    protected String director;
    @XmlElement(name = "ItemPage")
    @XmlSchemaType(name = "positiveInteger")
    protected BigInteger itemPage;
    @XmlElement(name = "Keywords")
    protected String keywords;
    @XmlElement(name = "Manufacturer")
    protected String manufacturer;
    @XmlElement(name = "MaximumPrice")
    @XmlSchemaType(name = "nonNegativeInteger")
    protected BigInteger maximumPrice;
    @XmlElement(name = "MerchantId")
    protected String merchantId;
    @XmlElement(name = "MinimumPrice")
    @XmlSchemaType(name = "nonNegativeInteger")
    protected BigInteger minimumPrice;
    @XmlElement(name = "MinPercentageOff")
    @XmlSchemaType(name = "nonNegativeInteger")
    protected BigInteger minPercentageOff;
    @XmlElement(name = "MusicLabel")
    protected String musicLabel;
    @XmlElement(name = "Orchestra")
    protected String orchestra;
    @XmlElement(name = "Power")
    protected String power;
    @XmlElement(name = "Publisher")
    protected String publisher;
    @XmlElement(name = "RelatedItemPage")
    protected String relatedItemPage;
    @XmlElement(name = "RelationshipType")
    protected List<String> relationshipType;
    @XmlElement(name = "ResponseGroup")
    protected List<String> responseGroup;
    @XmlElement(name = "SearchIndex")
    protected String searchIndex;
    @XmlElement(name = "Sort")
    protected String sort;
    @XmlElement(name = "Title")
    protected String title;
    @XmlElement(name = "ReleaseDate")
    protected String releaseDate;
    @XmlElement(name = "IncludeReviewsSummary")
    protected String includeReviewsSummary;
    @XmlElement(name = "TruncateReviewsAt")
    @XmlSchemaType(name = "nonNegativeInteger")
    protected BigInteger truncateReviewsAt;

    /**
     * Ruft den Wert der actor-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getActor() {
        return actor;
    }

    /**
     * Legt den Wert der actor-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setActor(String value) {
        this.actor = value;
    }

    /**
     * Ruft den Wert der artist-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getArtist() {
        return artist;
    }

    /**
     * Legt den Wert der artist-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setArtist(String value) {
        this.artist = value;
    }

    /**
     * Ruft den Wert der availability-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAvailability() {
        return availability;
    }

    /**
     * Legt den Wert der availability-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAvailability(String value) {
        this.availability = value;
    }

    /**
     * Gets the value of the audienceRating property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the audienceRating property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAudienceRating().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getAudienceRating() {
        if (audienceRating == null) {
            audienceRating = new ArrayList<String>();
        }
        return this.audienceRating;
    }

    /**
     * Ruft den Wert der author-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAuthor() {
        return author;
    }

    /**
     * Legt den Wert der author-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAuthor(String value) {
        this.author = value;
    }

    /**
     * Ruft den Wert der brand-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBrand() {
        return brand;
    }

    /**
     * Legt den Wert der brand-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBrand(String value) {
        this.brand = value;
    }

    /**
     * Ruft den Wert der browseNode-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBrowseNode() {
        return browseNode;
    }

    /**
     * Legt den Wert der browseNode-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBrowseNode(String value) {
        this.browseNode = value;
    }

    /**
     * Ruft den Wert der composer-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getComposer() {
        return composer;
    }

    /**
     * Legt den Wert der composer-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setComposer(String value) {
        this.composer = value;
    }

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
     * Ruft den Wert der conductor-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getConductor() {
        return conductor;
    }

    /**
     * Legt den Wert der conductor-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setConductor(String value) {
        this.conductor = value;
    }

    /**
     * Ruft den Wert der director-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDirector() {
        return director;
    }

    /**
     * Legt den Wert der director-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDirector(String value) {
        this.director = value;
    }

    /**
     * Ruft den Wert der itemPage-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getItemPage() {
        return itemPage;
    }

    /**
     * Legt den Wert der itemPage-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setItemPage(BigInteger value) {
        this.itemPage = value;
    }

    /**
     * Ruft den Wert der keywords-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getKeywords() {
        return keywords;
    }

    /**
     * Legt den Wert der keywords-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setKeywords(String value) {
        this.keywords = value;
    }

    /**
     * Ruft den Wert der manufacturer-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getManufacturer() {
        return manufacturer;
    }

    /**
     * Legt den Wert der manufacturer-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setManufacturer(String value) {
        this.manufacturer = value;
    }

    /**
     * Ruft den Wert der maximumPrice-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getMaximumPrice() {
        return maximumPrice;
    }

    /**
     * Legt den Wert der maximumPrice-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setMaximumPrice(BigInteger value) {
        this.maximumPrice = value;
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
     * Ruft den Wert der minimumPrice-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getMinimumPrice() {
        return minimumPrice;
    }

    /**
     * Legt den Wert der minimumPrice-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setMinimumPrice(BigInteger value) {
        this.minimumPrice = value;
    }

    /**
     * Ruft den Wert der minPercentageOff-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getMinPercentageOff() {
        return minPercentageOff;
    }

    /**
     * Legt den Wert der minPercentageOff-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setMinPercentageOff(BigInteger value) {
        this.minPercentageOff = value;
    }

    /**
     * Ruft den Wert der musicLabel-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMusicLabel() {
        return musicLabel;
    }

    /**
     * Legt den Wert der musicLabel-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMusicLabel(String value) {
        this.musicLabel = value;
    }

    /**
     * Ruft den Wert der orchestra-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOrchestra() {
        return orchestra;
    }

    /**
     * Legt den Wert der orchestra-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOrchestra(String value) {
        this.orchestra = value;
    }

    /**
     * Ruft den Wert der power-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPower() {
        return power;
    }

    /**
     * Legt den Wert der power-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPower(String value) {
        this.power = value;
    }

    /**
     * Ruft den Wert der publisher-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPublisher() {
        return publisher;
    }

    /**
     * Legt den Wert der publisher-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPublisher(String value) {
        this.publisher = value;
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
     * Ruft den Wert der sort-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSort() {
        return sort;
    }

    /**
     * Legt den Wert der sort-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSort(String value) {
        this.sort = value;
    }

    /**
     * Ruft den Wert der title-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTitle() {
        return title;
    }

    /**
     * Legt den Wert der title-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTitle(String value) {
        this.title = value;
    }

    /**
     * Ruft den Wert der releaseDate-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getReleaseDate() {
        return releaseDate;
    }

    /**
     * Legt den Wert der releaseDate-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setReleaseDate(String value) {
        this.releaseDate = value;
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
