package com.vipkid.service;

import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.vipkid.model.Audit.Category;
import com.vipkid.model.Audit.Level;
import com.vipkid.model.Course.Type;
import com.vipkid.model.Product;
import com.vipkid.model.Product.Status;
import com.vipkid.model.Unit;
import com.vipkid.repository.LessonRepository;
import com.vipkid.repository.ProductRepository;
import com.vipkid.security.SecurityService;
import com.vipkid.service.exception.UserNotExistServiceException;
import com.vipkid.service.pojo.Count;

@Service
public class ProductService {
	private Logger logger = LoggerFactory.getLogger(ProductService.class.getSimpleName());

	@Resource
	private ProductRepository productRepository;
	
	@Resource
	private LessonRepository lessonRepository;
	
	@Resource
	private SecurityService securityService;

	public Product find(long id) {
		logger.info("find product for id = {}", id);
		return productRepository.find(id);
	}

	public List<Product> list(String search, String mode, Integer start, Integer length) {
		logger.info("list product with params: search = {}, mode = {}, start = {}, length = {}.", search, mode, start, length);
		List<Product> products = productRepository.list(search, mode, start, length);
		for(Product product : products){
			if(!product.getUnits().isEmpty()){
				for(Unit unit : product.getUnits()){
					long lessonCount = lessonRepository.findLessonCountByUnitId(unit.getId());
					unit.setLessonCount(lessonCount);
				}
			}
		}
		return products;
	}
	
	public List<Product> findPurchasedProductsByStudentId(long studentId) {
		return null;
	}
	
	public List<Product> findCouldPurchaseProductsByStudentId(long studentId) {
		return null;
	}
	
	public List<Product> findByCourseType(Type type) {
		return productRepository.findByCourseType(type);
	}

	public Count count(String search, String mode) {
		logger.info("count product with params: search = {}, mode = {}.", search, mode);
		return new Count(productRepository.count(search, mode));
	}

	public Product create(Product product) {
		logger.info("create product: {}", product);
		
		productRepository.create(product);
		
		securityService.logAudit(Level.INFO, Category.PRODUCT_CREATE, "Create product: " + product.getName());
		
		return product;
	}

	public Product update(Product product) {
		logger.info("update product: {}", product);
		
		productRepository.update(product);
		
		securityService.logAudit(Level.INFO, Category.PRODUCT_UPDATE, "Update product: " + product.getName());
		
		return product;
	}
	
	public Product doOffSale(long id) {
		Product product = productRepository.find(id);
		if(product == null) {
			throw new UserNotExistServiceException("Product[id: {}] is not exist.", id);
		}else {
			product.setStatus(Status.OFF_SALE);
			productRepository.update(product);
			
			securityService.logAudit(Level.INFO, Category.PRODUCT_OFF_SALE, "Off sale product: " + product.getName());
		}
		
		return product;
	}
	
	public Product doOnSale(long id) {
		Product product = productRepository.find(id);
		if(product == null) {
			throw new UserNotExistServiceException("Product[id: {}] is not exist.", id);
		}else {
			product.setStatus(Status.ON_SALE);
			productRepository.update(product);
			
			securityService.logAudit(Level.INFO, Category.PRODUCT_ON_SALE, "On sale product: " + product.getName());
		}
		
		return product;
	}
}
