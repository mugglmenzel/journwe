
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
 *         &lt;element name="ASIN" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="ParentASIN" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element ref="{http://webservices.amazon.com/AWSECommerceService/2011-08-01}Errors" minOccurs="0"/>
 *         &lt;element name="DetailPageURL" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element ref="{http://webservices.amazon.com/AWSECommerceService/2011-08-01}ItemLinks" minOccurs="0"/>
 *         &lt;element name="SalesRank" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="SmallImage" type="{http://webservices.amazon.com/AWSECommerceService/2011-08-01}Image" minOccurs="0"/>
 *         &lt;element name="MediumImage" type="{http://webservices.amazon.com/AWSECommerceService/2011-08-01}Image" minOccurs="0"/>
 *         &lt;element name="LargeImage" type="{http://webservices.amazon.com/AWSECommerceService/2011-08-01}Image" minOccurs="0"/>
 *         &lt;element name="ImageSets" maxOccurs="unbounded" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element ref="{http://webservices.amazon.com/AWSECommerceService/2011-08-01}ImageSet" maxOccurs="unbounded" minOccurs="0"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element ref="{http://webservices.amazon.com/AWSECommerceService/2011-08-01}ItemAttributes" minOccurs="0"/>
 *         &lt;element name="VariationAttributes" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element ref="{http://webservices.amazon.com/AWSECommerceService/2011-08-01}VariationAttribute" maxOccurs="unbounded" minOccurs="0"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element ref="{http://webservices.amazon.com/AWSECommerceService/2011-08-01}RelatedItems" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://webservices.amazon.com/AWSECommerceService/2011-08-01}Collections" minOccurs="0"/>
 *         &lt;element name="Subjects" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="Subject" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element ref="{http://webservices.amazon.com/AWSECommerceService/2011-08-01}OfferSummary" minOccurs="0"/>
 *         &lt;element ref="{http://webservices.amazon.com/AWSECommerceService/2011-08-01}Offers" minOccurs="0"/>
 *         &lt;element ref="{http://webservices.amazon.com/AWSECommerceService/2011-08-01}VariationSummary" minOccurs="0"/>
 *         &lt;element ref="{http://webservices.amazon.com/AWSECommerceService/2011-08-01}Variations" minOccurs="0"/>
 *         &lt;element ref="{http://webservices.amazon.com/AWSECommerceService/2011-08-01}CustomerReviews" minOccurs="0"/>
 *         &lt;element ref="{http://webservices.amazon.com/AWSECommerceService/2011-08-01}EditorialReviews" minOccurs="0"/>
 *         &lt;element ref="{http://webservices.amazon.com/AWSECommerceService/2011-08-01}SimilarProducts" minOccurs="0"/>
 *         &lt;element ref="{http://webservices.amazon.com/AWSECommerceService/2011-08-01}Accessories" minOccurs="0"/>
 *         &lt;element ref="{http://webservices.amazon.com/AWSECommerceService/2011-08-01}Tracks" minOccurs="0"/>
 *         &lt;element ref="{http://webservices.amazon.com/AWSECommerceService/2011-08-01}BrowseNodes" minOccurs="0"/>
 *         &lt;element name="AlternateVersions" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="AlternateVersion" maxOccurs="unbounded" minOccurs="0">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="ASIN" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                             &lt;element name="Title" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                             &lt;element name="Binding" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                           &lt;/sequence>
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
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
    "asin",
    "parentASIN",
    "errors",
    "detailPageURL",
    "itemLinks",
    "salesRank",
    "smallImage",
    "mediumImage",
    "largeImage",
    "imageSets",
    "itemAttributes",
    "variationAttributes",
    "relatedItems",
    "collections",
    "subjects",
    "offerSummary",
    "offers",
    "variationSummary",
    "variations",
    "customerReviews",
    "editorialReviews",
    "similarProducts",
    "accessories",
    "tracks",
    "browseNodes",
    "alternateVersions"
})
@XmlRootElement(name = "Item")
public class Item {

    @XmlElement(name = "ASIN", required = true)
    protected String asin;
    @XmlElement(name = "ParentASIN")
    protected String parentASIN;
    @XmlElement(name = "Errors")
    protected Errors errors;
    @XmlElement(name = "DetailPageURL")
    protected String detailPageURL;
    @XmlElement(name = "ItemLinks")
    protected ItemLinks itemLinks;
    @XmlElement(name = "SalesRank")
    protected String salesRank;
    @XmlElement(name = "SmallImage")
    protected Image smallImage;
    @XmlElement(name = "MediumImage")
    protected Image mediumImage;
    @XmlElement(name = "LargeImage")
    protected Image largeImage;
    @XmlElement(name = "ImageSets")
    protected List<Item.ImageSets> imageSets;
    @XmlElement(name = "ItemAttributes")
    protected ItemAttributes itemAttributes;
    @XmlElement(name = "VariationAttributes")
    protected Item.VariationAttributes variationAttributes;
    @XmlElement(name = "RelatedItems")
    protected List<RelatedItems> relatedItems;
    @XmlElement(name = "Collections")
    protected Collections collections;
    @XmlElement(name = "Subjects")
    protected Item.Subjects subjects;
    @XmlElement(name = "OfferSummary")
    protected OfferSummary offerSummary;
    @XmlElement(name = "Offers")
    protected Offers offers;
    @XmlElement(name = "VariationSummary")
    protected VariationSummary variationSummary;
    @XmlElement(name = "Variations")
    protected Variations variations;
    @XmlElement(name = "CustomerReviews")
    protected CustomerReviews customerReviews;
    @XmlElement(name = "EditorialReviews")
    protected EditorialReviews editorialReviews;
    @XmlElement(name = "SimilarProducts")
    protected SimilarProducts similarProducts;
    @XmlElement(name = "Accessories")
    protected Accessories accessories;
    @XmlElement(name = "Tracks")
    protected Tracks tracks;
    @XmlElement(name = "BrowseNodes")
    protected BrowseNodes browseNodes;
    @XmlElement(name = "AlternateVersions")
    protected Item.AlternateVersions alternateVersions;

    /**
     * Ruft den Wert der asin-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getASIN() {
        return asin;
    }

    /**
     * Legt den Wert der asin-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setASIN(String value) {
        this.asin = value;
    }

    /**
     * Ruft den Wert der parentASIN-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getParentASIN() {
        return parentASIN;
    }

    /**
     * Legt den Wert der parentASIN-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setParentASIN(String value) {
        this.parentASIN = value;
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
     * Ruft den Wert der detailPageURL-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDetailPageURL() {
        return detailPageURL;
    }

    /**
     * Legt den Wert der detailPageURL-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDetailPageURL(String value) {
        this.detailPageURL = value;
    }

    /**
     * Ruft den Wert der itemLinks-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link ItemLinks }
     *     
     */
    public ItemLinks getItemLinks() {
        return itemLinks;
    }

    /**
     * Legt den Wert der itemLinks-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link ItemLinks }
     *     
     */
    public void setItemLinks(ItemLinks value) {
        this.itemLinks = value;
    }

    /**
     * Ruft den Wert der salesRank-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSalesRank() {
        return salesRank;
    }

    /**
     * Legt den Wert der salesRank-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSalesRank(String value) {
        this.salesRank = value;
    }

    /**
     * Ruft den Wert der smallImage-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Image }
     *     
     */
    public Image getSmallImage() {
        return smallImage;
    }

    /**
     * Legt den Wert der smallImage-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Image }
     *     
     */
    public void setSmallImage(Image value) {
        this.smallImage = value;
    }

    /**
     * Ruft den Wert der mediumImage-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Image }
     *     
     */
    public Image getMediumImage() {
        return mediumImage;
    }

    /**
     * Legt den Wert der mediumImage-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Image }
     *     
     */
    public void setMediumImage(Image value) {
        this.mediumImage = value;
    }

    /**
     * Ruft den Wert der largeImage-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Image }
     *     
     */
    public Image getLargeImage() {
        return largeImage;
    }

    /**
     * Legt den Wert der largeImage-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Image }
     *     
     */
    public void setLargeImage(Image value) {
        this.largeImage = value;
    }

    /**
     * Gets the value of the imageSets property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the imageSets property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getImageSets().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Item.ImageSets }
     * 
     * 
     */
    public List<Item.ImageSets> getImageSets() {
        if (imageSets == null) {
            imageSets = new ArrayList<Item.ImageSets>();
        }
        return this.imageSets;
    }

    /**
     * Ruft den Wert der itemAttributes-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link ItemAttributes }
     *     
     */
    public ItemAttributes getItemAttributes() {
        return itemAttributes;
    }

    /**
     * Legt den Wert der itemAttributes-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link ItemAttributes }
     *     
     */
    public void setItemAttributes(ItemAttributes value) {
        this.itemAttributes = value;
    }

    /**
     * Ruft den Wert der variationAttributes-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Item.VariationAttributes }
     *     
     */
    public Item.VariationAttributes getVariationAttributes() {
        return variationAttributes;
    }

    /**
     * Legt den Wert der variationAttributes-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Item.VariationAttributes }
     *     
     */
    public void setVariationAttributes(Item.VariationAttributes value) {
        this.variationAttributes = value;
    }

    /**
     * Gets the value of the relatedItems property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the relatedItems property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getRelatedItems().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link RelatedItems }
     * 
     * 
     */
    public List<RelatedItems> getRelatedItems() {
        if (relatedItems == null) {
            relatedItems = new ArrayList<RelatedItems>();
        }
        return this.relatedItems;
    }

    /**
     * Ruft den Wert der collections-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Collections }
     *     
     */
    public Collections getCollections() {
        return collections;
    }

    /**
     * Legt den Wert der collections-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Collections }
     *     
     */
    public void setCollections(Collections value) {
        this.collections = value;
    }

    /**
     * Ruft den Wert der subjects-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Item.Subjects }
     *     
     */
    public Item.Subjects getSubjects() {
        return subjects;
    }

    /**
     * Legt den Wert der subjects-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Item.Subjects }
     *     
     */
    public void setSubjects(Item.Subjects value) {
        this.subjects = value;
    }

    /**
     * Ruft den Wert der offerSummary-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link OfferSummary }
     *     
     */
    public OfferSummary getOfferSummary() {
        return offerSummary;
    }

    /**
     * Legt den Wert der offerSummary-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link OfferSummary }
     *     
     */
    public void setOfferSummary(OfferSummary value) {
        this.offerSummary = value;
    }

    /**
     * Ruft den Wert der offers-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Offers }
     *     
     */
    public Offers getOffers() {
        return offers;
    }

    /**
     * Legt den Wert der offers-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Offers }
     *     
     */
    public void setOffers(Offers value) {
        this.offers = value;
    }

    /**
     * Ruft den Wert der variationSummary-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link VariationSummary }
     *     
     */
    public VariationSummary getVariationSummary() {
        return variationSummary;
    }

    /**
     * Legt den Wert der variationSummary-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link VariationSummary }
     *     
     */
    public void setVariationSummary(VariationSummary value) {
        this.variationSummary = value;
    }

    /**
     * Ruft den Wert der variations-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Variations }
     *     
     */
    public Variations getVariations() {
        return variations;
    }

    /**
     * Legt den Wert der variations-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Variations }
     *     
     */
    public void setVariations(Variations value) {
        this.variations = value;
    }

    /**
     * Ruft den Wert der customerReviews-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link CustomerReviews }
     *     
     */
    public CustomerReviews getCustomerReviews() {
        return customerReviews;
    }

    /**
     * Legt den Wert der customerReviews-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link CustomerReviews }
     *     
     */
    public void setCustomerReviews(CustomerReviews value) {
        this.customerReviews = value;
    }

    /**
     * Ruft den Wert der editorialReviews-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link EditorialReviews }
     *     
     */
    public EditorialReviews getEditorialReviews() {
        return editorialReviews;
    }

    /**
     * Legt den Wert der editorialReviews-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link EditorialReviews }
     *     
     */
    public void setEditorialReviews(EditorialReviews value) {
        this.editorialReviews = value;
    }

    /**
     * Ruft den Wert der similarProducts-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link SimilarProducts }
     *     
     */
    public SimilarProducts getSimilarProducts() {
        return similarProducts;
    }

    /**
     * Legt den Wert der similarProducts-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link SimilarProducts }
     *     
     */
    public void setSimilarProducts(SimilarProducts value) {
        this.similarProducts = value;
    }

    /**
     * Ruft den Wert der accessories-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Accessories }
     *     
     */
    public Accessories getAccessories() {
        return accessories;
    }

    /**
     * Legt den Wert der accessories-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Accessories }
     *     
     */
    public void setAccessories(Accessories value) {
        this.accessories = value;
    }

    /**
     * Ruft den Wert der tracks-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Tracks }
     *     
     */
    public Tracks getTracks() {
        return tracks;
    }

    /**
     * Legt den Wert der tracks-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Tracks }
     *     
     */
    public void setTracks(Tracks value) {
        this.tracks = value;
    }

    /**
     * Ruft den Wert der browseNodes-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link BrowseNodes }
     *     
     */
    public BrowseNodes getBrowseNodes() {
        return browseNodes;
    }

    /**
     * Legt den Wert der browseNodes-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link BrowseNodes }
     *     
     */
    public void setBrowseNodes(BrowseNodes value) {
        this.browseNodes = value;
    }

    /**
     * Ruft den Wert der alternateVersions-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Item.AlternateVersions }
     *     
     */
    public Item.AlternateVersions getAlternateVersions() {
        return alternateVersions;
    }

    /**
     * Legt den Wert der alternateVersions-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Item.AlternateVersions }
     *     
     */
    public void setAlternateVersions(Item.AlternateVersions value) {
        this.alternateVersions = value;
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
     *         &lt;element name="AlternateVersion" maxOccurs="unbounded" minOccurs="0">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;sequence>
     *                   &lt;element name="ASIN" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *                   &lt;element name="Title" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *                   &lt;element name="Binding" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
        "alternateVersion"
    })
    public static class AlternateVersions {

        @XmlElement(name = "AlternateVersion")
        protected List<Item.AlternateVersions.AlternateVersion> alternateVersion;

        /**
         * Gets the value of the alternateVersion property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the alternateVersion property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getAlternateVersion().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link Item.AlternateVersions.AlternateVersion }
         * 
         * 
         */
        public List<Item.AlternateVersions.AlternateVersion> getAlternateVersion() {
            if (alternateVersion == null) {
                alternateVersion = new ArrayList<Item.AlternateVersions.AlternateVersion>();
            }
            return this.alternateVersion;
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
         *         &lt;element name="ASIN" type="{http://www.w3.org/2001/XMLSchema}string"/>
         *         &lt;element name="Title" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
         *         &lt;element name="Binding" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
            "asin",
            "title",
            "binding"
        })
        public static class AlternateVersion {

            @XmlElement(name = "ASIN", required = true)
            protected String asin;
            @XmlElement(name = "Title")
            protected String title;
            @XmlElement(name = "Binding")
            protected String binding;

            /**
             * Ruft den Wert der asin-Eigenschaft ab.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getASIN() {
                return asin;
            }

            /**
             * Legt den Wert der asin-Eigenschaft fest.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setASIN(String value) {
                this.asin = value;
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
             * Ruft den Wert der binding-Eigenschaft ab.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getBinding() {
                return binding;
            }

            /**
             * Legt den Wert der binding-Eigenschaft fest.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setBinding(String value) {
                this.binding = value;
            }

        }

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
     *         &lt;element ref="{http://webservices.amazon.com/AWSECommerceService/2011-08-01}ImageSet" maxOccurs="unbounded" minOccurs="0"/>
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
        "imageSet"
    })
    public static class ImageSets {

        @XmlElement(name = "ImageSet")
        protected List<ImageSet> imageSet;

        /**
         * Gets the value of the imageSet property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the imageSet property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getImageSet().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link ImageSet }
         * 
         * 
         */
        public List<ImageSet> getImageSet() {
            if (imageSet == null) {
                imageSet = new ArrayList<ImageSet>();
            }
            return this.imageSet;
        }

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
     *         &lt;element name="Subject" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
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
        "subject"
    })
    public static class Subjects {

        @XmlElement(name = "Subject")
        protected List<String> subject;

        /**
         * Gets the value of the subject property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the subject property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getSubject().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link String }
         * 
         * 
         */
        public List<String> getSubject() {
            if (subject == null) {
                subject = new ArrayList<String>();
            }
            return this.subject;
        }

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
     *         &lt;element ref="{http://webservices.amazon.com/AWSECommerceService/2011-08-01}VariationAttribute" maxOccurs="unbounded" minOccurs="0"/>
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
        "variationAttribute"
    })
    public static class VariationAttributes {

        @XmlElement(name = "VariationAttribute")
        protected List<VariationAttribute> variationAttribute;

        /**
         * Gets the value of the variationAttribute property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the variationAttribute property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getVariationAttribute().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link VariationAttribute }
         * 
         * 
         */
        public List<VariationAttribute> getVariationAttribute() {
            if (variationAttribute == null) {
                variationAttribute = new ArrayList<VariationAttribute>();
            }
            return this.variationAttribute;
        }

    }

}
