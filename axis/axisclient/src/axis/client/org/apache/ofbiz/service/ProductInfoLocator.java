/**
 * ProductInfoLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.apache.ofbiz.service;

public class ProductInfoLocator extends org.apache.axis.client.Service implements org.apache.ofbiz.service.ProductInfo {

    public ProductInfoLocator() {
    }


    public ProductInfoLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public ProductInfoLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for productInfoPort
    private java.lang.String productInfoPort_address = "http://127.0.0.1:8080/webtools/control/SOAPService";

    public java.lang.String getproductInfoPortAddress() {
        return productInfoPort_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String productInfoPortWSDDServiceName = "productInfoPort";

    public java.lang.String getproductInfoPortWSDDServiceName() {
        return productInfoPortWSDDServiceName;
    }

    public void setproductInfoPortWSDDServiceName(java.lang.String name) {
        productInfoPortWSDDServiceName = name;
    }

    public org.apache.ofbiz.service.ProductInfoPortType getproductInfoPort() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(productInfoPort_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getproductInfoPort(endpoint);
    }

    public org.apache.ofbiz.service.ProductInfoPortType getproductInfoPort(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            org.apache.ofbiz.service.ProductInfoSoapBindingStub _stub = new org.apache.ofbiz.service.ProductInfoSoapBindingStub(portAddress, this);
            _stub.setPortName(getproductInfoPortWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setproductInfoPortEndpointAddress(java.lang.String address) {
        productInfoPort_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (org.apache.ofbiz.service.ProductInfoPortType.class.isAssignableFrom(serviceEndpointInterface)) {
                org.apache.ofbiz.service.ProductInfoSoapBindingStub _stub = new org.apache.ofbiz.service.ProductInfoSoapBindingStub(new java.net.URL(productInfoPort_address), this);
                _stub.setPortName(getproductInfoPortWSDDServiceName());
                return _stub;
            }
        }
        catch (java.lang.Throwable t) {
            throw new javax.xml.rpc.ServiceException(t);
        }
        throw new javax.xml.rpc.ServiceException("There is no stub implementation for the interface:  " + (serviceEndpointInterface == null ? "null" : serviceEndpointInterface.getName()));
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(javax.xml.namespace.QName portName, Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        if (portName == null) {
            return getPort(serviceEndpointInterface);
        }
        java.lang.String inputPortName = portName.getLocalPart();
        if ("productInfoPort".equals(inputPortName)) {
            return getproductInfoPort();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://ofbiz.apache.org/service/", "productInfo");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://ofbiz.apache.org/service/", "productInfoPort"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("productInfoPort".equals(portName)) {
            setproductInfoPortEndpointAddress(address);
        }
        else 
{ // Unknown Port Name
            throw new javax.xml.rpc.ServiceException(" Cannot set Endpoint Address for Unknown Port" + portName);
        }
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(javax.xml.namespace.QName portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        setEndpointAddress(portName.getLocalPart(), address);
    }

}
