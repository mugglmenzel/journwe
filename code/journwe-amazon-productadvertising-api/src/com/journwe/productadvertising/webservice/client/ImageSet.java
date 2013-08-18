
package com.journwe.productadvertising.webservice.client;

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
 *         &lt;element name="SwatchImage" type="{http://webservices.amazon.com/AWSECommerceService/2011-08-01}Image" minOccurs="0"/>
 *         &lt;element name="SmallImage" type="{http://webservices.amazon.com/AWSECommerceService/2011-08-01}Image" minOccurs="0"/>
 *         &lt;element name="ThumbnailImage" type="{http://webservices.amazon.com/AWSECommerceService/2011-08-01}Image" minOccurs="0"/>
 *         &lt;element name="TinyImage" type="{http://webservices.amazon.com/AWSECommerceService/2011-08-01}Image" minOccurs="0"/>
 *         &lt;element name="MediumImage" type="{http://webservices.amazon.com/AWSECommerceService/2011-08-01}Image" minOccurs="0"/>
 *         &lt;element name="LargeImage" type="{http://webservices.amazon.com/AWSECommerceService/2011-08-01}Image" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="Category" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "swatchImage",
    "smallImage",
    "thumbnailImage",
    "tinyImage",
    "mediumImage",
    "largeImage"
})
@XmlRootElement(name = "ImageSet")
public class ImageSet {

    @XmlElement(name = "SwatchImage")
    protected Image swatchImage;
    @XmlElement(name = "SmallImage")
    protected Image smallImage;
    @XmlElement(name = "ThumbnailImage")
    protected Image thumbnailImage;
    @XmlElement(name = "TinyImage")
    protected Image tinyImage;
    @XmlElement(name = "MediumImage")
    protected Image mediumImage;
    @XmlElement(name = "LargeImage")
    protected Image largeImage;
    @XmlAttribute(name = "Category")
    protected String category;

    /**
     * Ruft den Wert der swatchImage-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Image }
     *     
     */
    public Image getSwatchImage() {
        return swatchImage;
    }

    /**
     * Legt den Wert der swatchImage-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Image }
     *     
     */
    public void setSwatchImage(Image value) {
        this.swatchImage = value;
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
     * Ruft den Wert der thumbnailImage-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Image }
     *     
     */
    public Image getThumbnailImage() {
        return thumbnailImage;
    }

    /**
     * Legt den Wert der thumbnailImage-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Image }
     *     
     */
    public void setThumbnailImage(Image value) {
        this.thumbnailImage = value;
    }

    /**
     * Ruft den Wert der tinyImage-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Image }
     *     
     */
    public Image getTinyImage() {
        return tinyImage;
    }

    /**
     * Legt den Wert der tinyImage-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Image }
     *     
     */
    public void setTinyImage(Image value) {
        this.tinyImage = value;
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
     * Ruft den Wert der category-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCategory() {
        return category;
    }

    /**
     * Legt den Wert der category-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCategory(String value) {
        this.category = value;
    }

}
