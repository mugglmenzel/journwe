
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
 *         &lt;element name="BrowseNodeId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="IsCategoryRoot" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="Properties" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element ref="{http://webservices.amazon.com/AWSECommerceService/2011-08-01}Property" maxOccurs="unbounded"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="Children" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element ref="{http://webservices.amazon.com/AWSECommerceService/2011-08-01}BrowseNode" maxOccurs="unbounded"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="Ancestors" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element ref="{http://webservices.amazon.com/AWSECommerceService/2011-08-01}BrowseNode" maxOccurs="unbounded"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element ref="{http://webservices.amazon.com/AWSECommerceService/2011-08-01}TopSellers" minOccurs="0"/>
 *         &lt;element ref="{http://webservices.amazon.com/AWSECommerceService/2011-08-01}NewReleases" minOccurs="0"/>
 *         &lt;element ref="{http://webservices.amazon.com/AWSECommerceService/2011-08-01}TopItemSet" maxOccurs="unbounded" minOccurs="0"/>
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
    "browseNodeId",
    "name",
    "isCategoryRoot",
    "properties",
    "children",
    "ancestors",
    "topSellers",
    "newReleases",
    "topItemSet"
})
@XmlRootElement(name = "BrowseNode")
public class BrowseNode {

    @XmlElement(name = "BrowseNodeId")
    protected String browseNodeId;
    @XmlElement(name = "Name")
    protected String name;
    @XmlElement(name = "IsCategoryRoot")
    protected Boolean isCategoryRoot;
    @XmlElement(name = "Properties")
    protected BrowseNode.Properties properties;
    @XmlElement(name = "Children")
    protected BrowseNode.Children children;
    @XmlElement(name = "Ancestors")
    protected BrowseNode.Ancestors ancestors;
    @XmlElement(name = "TopSellers")
    protected TopSellers topSellers;
    @XmlElement(name = "NewReleases")
    protected NewReleases newReleases;
    @XmlElement(name = "TopItemSet")
    protected List<TopItemSet> topItemSet;

    /**
     * Ruft den Wert der browseNodeId-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBrowseNodeId() {
        return browseNodeId;
    }

    /**
     * Legt den Wert der browseNodeId-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBrowseNodeId(String value) {
        this.browseNodeId = value;
    }

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
     * Ruft den Wert der isCategoryRoot-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isIsCategoryRoot() {
        return isCategoryRoot;
    }

    /**
     * Legt den Wert der isCategoryRoot-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setIsCategoryRoot(Boolean value) {
        this.isCategoryRoot = value;
    }

    /**
     * Ruft den Wert der properties-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link BrowseNode.Properties }
     *     
     */
    public BrowseNode.Properties getProperties() {
        return properties;
    }

    /**
     * Legt den Wert der properties-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link BrowseNode.Properties }
     *     
     */
    public void setProperties(BrowseNode.Properties value) {
        this.properties = value;
    }

    /**
     * Ruft den Wert der children-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link BrowseNode.Children }
     *     
     */
    public BrowseNode.Children getChildren() {
        return children;
    }

    /**
     * Legt den Wert der children-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link BrowseNode.Children }
     *     
     */
    public void setChildren(BrowseNode.Children value) {
        this.children = value;
    }

    /**
     * Ruft den Wert der ancestors-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link BrowseNode.Ancestors }
     *     
     */
    public BrowseNode.Ancestors getAncestors() {
        return ancestors;
    }

    /**
     * Legt den Wert der ancestors-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link BrowseNode.Ancestors }
     *     
     */
    public void setAncestors(BrowseNode.Ancestors value) {
        this.ancestors = value;
    }

    /**
     * Ruft den Wert der topSellers-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link TopSellers }
     *     
     */
    public TopSellers getTopSellers() {
        return topSellers;
    }

    /**
     * Legt den Wert der topSellers-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link TopSellers }
     *     
     */
    public void setTopSellers(TopSellers value) {
        this.topSellers = value;
    }

    /**
     * Ruft den Wert der newReleases-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link NewReleases }
     *     
     */
    public NewReleases getNewReleases() {
        return newReleases;
    }

    /**
     * Legt den Wert der newReleases-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link NewReleases }
     *     
     */
    public void setNewReleases(NewReleases value) {
        this.newReleases = value;
    }

    /**
     * Gets the value of the topItemSet property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the topItemSet property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTopItemSet().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TopItemSet }
     * 
     * 
     */
    public List<TopItemSet> getTopItemSet() {
        if (topItemSet == null) {
            topItemSet = new ArrayList<TopItemSet>();
        }
        return this.topItemSet;
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
     *         &lt;element ref="{http://webservices.amazon.com/AWSECommerceService/2011-08-01}BrowseNode" maxOccurs="unbounded"/>
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
        "browseNode"
    })
    public static class Ancestors {

        @XmlElement(name = "BrowseNode", required = true)
        protected List<BrowseNode> browseNode;

        /**
         * Gets the value of the browseNode property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the browseNode property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getBrowseNode().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link BrowseNode }
         * 
         * 
         */
        public List<BrowseNode> getBrowseNode() {
            if (browseNode == null) {
                browseNode = new ArrayList<BrowseNode>();
            }
            return this.browseNode;
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
     *         &lt;element ref="{http://webservices.amazon.com/AWSECommerceService/2011-08-01}BrowseNode" maxOccurs="unbounded"/>
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
        "browseNode"
    })
    public static class Children {

        @XmlElement(name = "BrowseNode", required = true)
        protected List<BrowseNode> browseNode;

        /**
         * Gets the value of the browseNode property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the browseNode property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getBrowseNode().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link BrowseNode }
         * 
         * 
         */
        public List<BrowseNode> getBrowseNode() {
            if (browseNode == null) {
                browseNode = new ArrayList<BrowseNode>();
            }
            return this.browseNode;
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
     *         &lt;element ref="{http://webservices.amazon.com/AWSECommerceService/2011-08-01}Property" maxOccurs="unbounded"/>
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
        "property"
    })
    public static class Properties {

        @XmlElement(name = "Property", required = true)
        protected List<Property> property;

        /**
         * Gets the value of the property property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the property property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getProperty().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link Property }
         * 
         * 
         */
        public List<Property> getProperty() {
            if (property == null) {
                property = new ArrayList<Property>();
            }
            return this.property;
        }

    }

}
