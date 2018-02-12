package com.client.quickstart.hibernate4.data;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;

import com.client.quickstart.hibernate4.model.ProductInfo;

import java.util.List;
import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.event.Reception;

import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;

@RequestScoped
public class ProductListProducer {
	@Inject
	private EntityManager em;

	private List<ProductInfo> productInfos;

	// @Named provides access the return value via the EL variable name
	// "products" in the UI (e.g.,
	// Facelets or JSP view)
	@Produces
	@Named
	public List<ProductInfo> getProductInfos() {
		return productInfos;
	}

	public void onProductListChanged(@Observes(notifyObserver = Reception.IF_EXISTS) final ProductInfo product) {
		retrieveAllProductsOrderedByName();
	}

	@SuppressWarnings("unchecked")
	@PostConstruct
	public void retrieveAllProductsOrderedByName() {

		// using Hibernate Session and Criteria Query via Hibernate Native API
		Session session = (Session) em.getDelegate();
		Criteria cb = session.createCriteria(ProductInfo.class);
		cb.addOrder(Order.asc("companyName"));
		productInfos = (List<ProductInfo>) cb.list();

	}
}
