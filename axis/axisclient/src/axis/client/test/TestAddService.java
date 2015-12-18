package test;

import org.apache.ofbiz.service.MapEntry;
import org.apache.ofbiz.service.MapKey;
import org.apache.ofbiz.service.MapValue;
import org.apache.ofbiz.service.ProductInfo;
import org.apache.ofbiz.service.ProductInfoLocator;
import org.apache.ofbiz.service.ProductInfoPortType;
import org.apache.ofbiz.service.ProductInfoSoapBindingStub;
import org.apache.ofbiz.service.StdString;
import org.apache.ofbiz.service.holders.MapMapHolder;

/**
 * Web Service 客户端测试
 * @author zengwl
 *
 */
public class TestAddService {

	/**
	 * @param args
	 * 
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try{
/*			AddServiceService service = new AddServiceServiceLocator();
			AddService client = service.getAddService();
			System.out.println("服务器端返回："+client.add(12, 1));
			HashMap<String,Object> map = new HashMap<String,Object>();
			map.put("key","121212");
			System.out.println("服务器端返回："+client.getMaps("1",map).get("content"));	*/		
			//产品信息webservice初始化
			ProductInfo server = new ProductInfoLocator();
			ProductInfoPortType client = server.getproductInfoPort();
			//调用
			client.productInfo(getMapMapHolder());
		}catch(Exception e){
			e.printStackTrace();
		}

	}
	
	public static MapMapHolder getMapMapHolder(){
		//参数key
		MapKey mapKey = new MapKey(new StdString("jsonStr"));
		MapValue mapValue = new MapValue();
		mapValue.setStdString(new StdString("{\"userLoginId\":\"1140225008\",\"userLoginPwd\":\"holpe123\"}"));
		//参数value
		MapEntry map = new MapEntry(mapKey,mapValue);
		MapEntry[] mape = new MapEntry[]{map};
		MapMapHolder mapMap = new MapMapHolder(mape);
		return mapMap;
	}
}
