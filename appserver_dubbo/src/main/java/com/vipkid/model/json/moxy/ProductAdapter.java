package com.vipkid.model.json.moxy;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import com.vipkid.model.Product;

public class ProductAdapter extends XmlAdapter<Product, Product> {

	@Override
	public Product unmarshal(Product product) throws Exception {
		return product;
	}

	@Override
	public Product marshal(Product product) throws Exception {
		if(product == null) {
			return null;
		}else {
			Product simplifiedProduct = new Product();
			simplifiedProduct.setId(product.getId());
			simplifiedProduct.setCourse(product.getCourse());
			simplifiedProduct.setName(product.getName());
			return simplifiedProduct;
		}	
	}

}
