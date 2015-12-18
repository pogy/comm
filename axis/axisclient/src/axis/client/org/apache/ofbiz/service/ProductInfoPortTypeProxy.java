package org.apache.ofbiz.service;

public class ProductInfoPortTypeProxy implements org.apache.ofbiz.service.ProductInfoPortType {
  private String _endpoint = null;
  private org.apache.ofbiz.service.ProductInfoPortType productInfoPortType = null;
  
  public ProductInfoPortTypeProxy() {
    _initProductInfoPortTypeProxy();
  }
  
  public ProductInfoPortTypeProxy(String endpoint) {
    _endpoint = endpoint;
    _initProductInfoPortTypeProxy();
  }
  
  private void _initProductInfoPortTypeProxy() {
    try {
      productInfoPortType = (new org.apache.ofbiz.service.ProductInfoLocator()).getproductInfoPort();
      if (productInfoPortType != null) {
        if (_endpoint != null)
          ((javax.xml.rpc.Stub)productInfoPortType)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
        else
          _endpoint = (String)((javax.xml.rpc.Stub)productInfoPortType)._getProperty("javax.xml.rpc.service.endpoint.address");
      }
      
    }
    catch (javax.xml.rpc.ServiceException serviceException) {}
  }
  
  public String getEndpoint() {
    return _endpoint;
  }
  
  public void setEndpoint(String endpoint) {
    _endpoint = endpoint;
    if (productInfoPortType != null)
      ((javax.xml.rpc.Stub)productInfoPortType)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
    
  }
  
  public org.apache.ofbiz.service.ProductInfoPortType getProductInfoPortType() {
    if (productInfoPortType == null)
      _initProductInfoPortTypeProxy();
    return productInfoPortType;
  }
  
  public void productInfo(org.apache.ofbiz.service.holders.MapMapHolder mapMap) throws java.rmi.RemoteException{
    if (productInfoPortType == null)
      _initProductInfoPortTypeProxy();
    productInfoPortType.productInfo(mapMap);
  }
  
  
}