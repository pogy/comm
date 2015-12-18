/**
 * MapKey.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.apache.ofbiz.service;

public class MapKey  implements java.io.Serializable {
    private org.apache.ofbiz.service.StdString stdString;

    public MapKey() {
    }

    public MapKey(
           org.apache.ofbiz.service.StdString stdString) {
           this.stdString = stdString;
    }


    /**
     * Gets the stdString value for this MapKey.
     * 
     * @return stdString
     */
    public org.apache.ofbiz.service.StdString getStdString() {
        return stdString;
    }


    /**
     * Sets the stdString value for this MapKey.
     * 
     * @param stdString
     */
    public void setStdString(org.apache.ofbiz.service.StdString stdString) {
        this.stdString = stdString;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof MapKey)) return false;
        MapKey other = (MapKey) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.stdString==null && other.getStdString()==null) || 
             (this.stdString!=null &&
              this.stdString.equals(other.getStdString())));
        __equalsCalc = null;
        return _equals;
    }

    private boolean __hashCodeCalc = false;
    public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = 1;
        if (getStdString() != null) {
            _hashCode += getStdString().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(MapKey.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://ofbiz.apache.org/service/", "map-Key"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("stdString");
        elemField.setXmlName(new javax.xml.namespace.QName("http://ofbiz.apache.org/service/", "std-String"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://ofbiz.apache.org/service/", ">std-String"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
    }

    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

    /**
     * Get Custom Serializer
     */
    public static org.apache.axis.encoding.Serializer getSerializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanSerializer(
            _javaType, _xmlType, typeDesc);
    }

    /**
     * Get Custom Deserializer
     */
    public static org.apache.axis.encoding.Deserializer getDeserializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanDeserializer(
            _javaType, _xmlType, typeDesc);
    }

}
