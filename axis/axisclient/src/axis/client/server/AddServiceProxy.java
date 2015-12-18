package server;

public class AddServiceProxy implements server.AddService {
  private String _endpoint = null;
  private server.AddService addService = null;
  
  public AddServiceProxy() {
    _initAddServiceProxy();
  }
  
  public AddServiceProxy(String endpoint) {
    _endpoint = endpoint;
    _initAddServiceProxy();
  }
  
  private void _initAddServiceProxy() {
    try {
      addService = (new server.AddServiceServiceLocator()).getAddService();
      if (addService != null) {
        if (_endpoint != null)
          ((javax.xml.rpc.Stub)addService)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
        else
          _endpoint = (String)((javax.xml.rpc.Stub)addService)._getProperty("javax.xml.rpc.service.endpoint.address");
      }
      
    }
    catch (javax.xml.rpc.ServiceException serviceException) {}
  }
  
  public String getEndpoint() {
    return _endpoint;
  }
  
  public void setEndpoint(String endpoint) {
    _endpoint = endpoint;
    if (addService != null)
      ((javax.xml.rpc.Stub)addService)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
    
  }
  
  public server.AddService getAddService() {
    if (addService == null)
      _initAddServiceProxy();
    return addService;
  }
  
  public java.util.HashMap getMaps(java.lang.String json, java.util.HashMap map) throws java.rmi.RemoteException{
    if (addService == null)
      _initAddServiceProxy();
    return addService.getMaps(json, map);
  }
  
  public int add(int a, int b) throws java.rmi.RemoteException{
    if (addService == null)
      _initAddServiceProxy();
    return addService.add(a, b);
  }
  
  
}