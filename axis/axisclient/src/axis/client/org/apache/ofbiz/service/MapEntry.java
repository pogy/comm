/**
 * MapEntry.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.apache.ofbiz.service;

public class MapEntry  implements java.io.Serializable {
    private org.apache.ofbiz.service.MapKey mapKey;

    private org.apache.ofbiz.service.MapValue mapValue;

    public MapEntry() {
    }

    public MapEntry(
           org.apache.ofbiz.service.MapKey mapKey,
           org.apache.ofbiz.service.MapValue mapValue) {
           this.mapKey = mapKey;
           this.mapValue = mapValue;
    }


    /**
     * Gets the mapKey value for this MapEntry.
     * 
     * @return mapKey
     */
    public org.apache.ofbiz.service.MapKey getMapKey() {
        return mapKey;
    }


    /**
     * Sets the mapKey value for this MapEntry.
     * 
     * @param mapKey
     */
    public void setMapKey(org.apache.ofbiz.service.MapKey mapKey) {
        this.mapKey = mapKey;
    }


    /**
     * Gets the mapValue value for this MapEntry.
     * 
     * @return mapValue
     */
    public org.apache.ofbiz.service.MapValue getMapValue() {
        return mapValue;
    }


    /**
     * Sets the mapValue value for this MapEntry.
     * 
     * @param mapValue
     */
    public void setMapValue(org.apache.ofbiz.service.MapValue mapValue) {
        this.mapValue = mapValue;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof MapEntry)) return false;
        MapEntry other = (MapEntry) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.mapKey==null && other.getMapKey()==null) || 
             (this.mapKey!=null &&
              this.mapKey.equals(other.getMapKey()))) &&
            ((this.mapValue==null && other.getMapValue()==null) || 
             (this.mapValue!=null &&
              this.mapValue.equals(other.getMapValue())));
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
        if (getMapKey() != null) {
            _hashCode += getMapKey().hashCode();
        }
        if (getMapValue() != null) {
            _hashCode += getMapValue().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(MapEntry.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://ofbiz.apache.org/service/", "map-Entry"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("mapKey");
        elemField.setXmlName(new javax.xml.namespace.QName("http://ofbiz.apache.org/service/", "map-Key"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://ofbiz.apache.org/service/", "map-Key"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("mapValue");
        elemField.setXmlName(new javax.xml.namespace.QName("http://ofbiz.apache.org/service/", "map-Value"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://ofbiz.apache.org/service/", "map-Value"));
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
