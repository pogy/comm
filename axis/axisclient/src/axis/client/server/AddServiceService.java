/**
 * AddServiceService.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package server;

public interface AddServiceService extends javax.xml.rpc.Service {
    public java.lang.String getAddServiceAddress();

    public server.AddService getAddService() throws javax.xml.rpc.ServiceException;

    public server.AddService getAddService(java.net.URL portAddress) throws javax.xml.rpc.ServiceException;
}
