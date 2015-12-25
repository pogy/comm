package com.vipkid.rest;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.vipkid.model.Course.Type;
import com.vipkid.model.Product;
import com.vipkid.model.Unit;
import com.vipkid.rest.vo.query.CourseView;
import com.vipkid.rest.vo.query.ProductView;
import com.vipkid.rest.vo.query.UnitView;
import com.vipkid.service.ProductService;
import com.vipkid.service.pojo.Count;

@RestController
@RequestMapping("/api/service/private/products")
public class ProductController {
	private Logger logger = LoggerFactory.getLogger(ProductController.class.getSimpleName());

	@Resource
	private ProductService productService;

	@RequestMapping(value = "/find",method = RequestMethod.GET)
	public Product find(@RequestParam("id") long id) {
		logger.info("find product for id = {}", id);
		return productService.find(id);
	}

	@RequestMapping(value = "/list",method = RequestMethod.GET)
	public List<Product> list(@RequestParam(value = "search", required = false) String search, @RequestParam(value = "mode", required = false) String mode, @RequestParam(value = "start", required = false) Integer start, @RequestParam(value = "length", required = false) Integer length) {
		logger.info("list product with params: search = {}, mode = {}, start = {}, length = {}.", search, mode, start, length);
		if (null == start) {
            start = 0;
        }
        if (null == length) {
            length = 0;
        }
        return productService.list(search, mode, start, length);
	}
	
	@RequestMapping(value = "/findPurchasedProductsByStudentId",method = RequestMethod.GET)
	public List<Product> findPurchasedProductsByStudentId(@RequestParam("studentId") long studentId) {
		return null;
	}
	
	@RequestMapping(value = "/findCouldPurchaseProductsByStudentId",method = RequestMethod.GET)
	public List<Product> findCouldPurchaseProductsByStudentId(@RequestParam("studentId") long studentId) {
		return null;
	}
	
	@RequestMapping(value = "/findByCourseType",method = RequestMethod.GET)
	public List<Product> findByCourseType(@RequestParam("type") Type type) {
		logger.info("find by coursetype, type = {}.", type);
		return productService.findByCourseType(type);
	}

	@RequestMapping(value = "/count",method = RequestMethod.GET)
	public Count count(@RequestParam(value = "search", required = false) String search, @RequestParam(value = "mode", required = false) String mode) {
		logger.info("count product with params: search = {}, mode = {}.", search, mode);
		return productService.count(search, mode);
	}

	@RequestMapping(method = RequestMethod.POST)
	public Product create(@RequestBody Product product) {
		logger.info("create product: {}", product);
		return productService.create(product);
	}

	@RequestMapping(method = RequestMethod.PUT)
	public Product update(@RequestBody Product product) {
		logger.info("update product: {}", product);
		return productService.update(product);
	}
	
	@RequestMapping(value = "/offSale",method = RequestMethod.GET)
	public Product offSale(@RequestParam("id") long id) {
		logger.info("offsale, id = {}.", id);
		return productService.doOffSale(id);
		
	}
	
	@RequestMapping(value = "/onSale",method = RequestMethod.GET)
	public Product onSale(@RequestParam("id") long id) {
		logger.info("onsale, id = {}.", id);
		return productService.doOnSale(id);
	}
	
	@RequestMapping(value = "/filter",method = RequestMethod.GET)
	public List<ProductView> filter(@RequestParam(value = "search", required = false) String search, @RequestParam(value = "mode", required = false) String mode, @RequestParam(value = "start", required = false) Integer start, @RequestParam(value = "length", required = false) Integer length) {
		logger.info("list product with params: search = {}, mode = {}, start = {}, length = {}.", search, mode, start, length);
		if (null == start) {
            start = 0;
        }
        if (null == length) {
            length = 0;
        }
        List<Product> productList = productService.list(search, mode, start, length);
        
        return getProductQueryResultView(productList);
	}
	
	@RequestMapping(value = "/filterforSelect",method = RequestMethod.GET)
	public List<ProductView> filterforSelect(@RequestParam(value = "search", required = false) String search, @RequestParam(value = "mode", required = false) String mode, @RequestParam(value = "start", required = false) Integer start, @RequestParam(value = "length", required = false) Integer length) {
		logger.info("list product with params: search = {}, mode = {}, start = {}, length = {}.", search, mode, start, length);
		if (null == start) {
            start = 0;
        }
        if (null == length) {
            length = 0;
        }
        List<Product> productList = productService.list(search, mode, start, length);
        
        return getProductSelectResultView(productList);
	}
	
	private List<ProductView> getProductQueryResultView(List<Product> productList) {
		
		List<ProductView> productViewList = new ArrayList<ProductView>();
		if (productList != null && productList.size() > 0) {
			for (Product product : productList) {
				ProductView productView = new ProductView();
				
				//course
				CourseView courseView = null;
				if (product.getCourse() != null) {
					courseView = new CourseView();
					courseView.setId(product.getCourse().getId());
					courseView.setName(product.getCourse().getName());
					courseView.setMode(product.getCourse().getMode());
				}
				
				//units
				List<UnitView> unitViews = new ArrayList<UnitView>();
				List<Unit> units = product.getUnits();
				if (units != null) {
					for (Unit unit : units) {
						UnitView unitView = new UnitView();
						unitView.setId(unit.getId());
						unitView.setName(unit.getName());
                        unitView.setSequence(unit.getSequence());
						unitViews.add(unitView);
					}
				}
				productView.setId(product.getId());
				productView.setName(product.getName());
				productView.setStatus(product.getStatus());
				productView.setType(product.getType());
				productView.setBaseSalary(product.getBaseSalary());
				productView.setClassHourPrice(product.getClassHourPrice());
				productView.setCreateDateTime(product.getCreateDateTime());
				productView.setDescription(product.getDescription());
				productView.setCourse(courseView);
				productView.setUnits(unitViews);
				productViewList.add(productView);
			}
		}
		return productViewList;
	}
	
	
	private List<ProductView> getProductSelectResultView(List<Product> productList) {
		List<ProductView> productViewList = new ArrayList<ProductView>();
		if (productList != null && productList.size() > 0) {
			for (Product product : productList) {
				ProductView productView = new ProductView();

				productView.setId(product.getId());
				productView.setName(product.getName());
				productViewList.add(productView);
			}
		}
		return productViewList;
	}
}
