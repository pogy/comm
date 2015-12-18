/**
 * AddService.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package server;

public interface AddService extends java.rmi.Remote {
    public java.util.HashMap getMaps(java.lang.String json, java.util.HashMap map) throws java.rmi.RemoteException;
    public int add(int a, int b) throws java.rmi.RemoteException;
}
