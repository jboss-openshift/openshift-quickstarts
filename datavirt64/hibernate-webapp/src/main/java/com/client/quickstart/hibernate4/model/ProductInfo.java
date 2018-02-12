/*
 * JBoss, Home of Professional Open Source
 * Copyright 2012, JBoss Inc., and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package com.client.quickstart.hibernate4.model;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/*The Model uses JPA Entity as well as Hibernate Validators
 * 
 */

@Entity
//@XmlRootElement
@Table(name = "ProductInfo", uniqueConstraints = @UniqueConstraint(columnNames = "id"))
public class ProductInfo implements java.io.Serializable {
	/** Default value included to remove warning. Remove or modify at will. **/
	private static final long serialVersionUID = 1L;

	@NotNull
	@Id
	private int id;

	@NotNull
	@Size(min = 1, max = 10)
	@Pattern(regexp = "[A-Za-z]*", message = "must contain only letters")
	private String symbol;

	@NotNull
	@Size(min = 1, max = 100)
	@Pattern(regexp = "[A-Za-z0-9 ]*", message = "must contain only letters, numbers and spaces")
	private String companyName;

	@Column(name="price", insertable=false, updatable=false)
	private BigDecimal price;

	public ProductInfo() {
	}

	public ProductInfo(int id) {
		this.id = id;
	}

	public ProductInfo(int id, String symbol, String companyName) {
		this.id = id;
		this.symbol = symbol;
		this.companyName = companyName;
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getSymbol() {
		return this.symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	public String getCompanyName() {
		return this.companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public BigDecimal getPrice() {
		return this.price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public String toString() {
		return "Product: (id) " + getId() + " (symbol) " + getSymbol()
				+ " (companyName) " + getCompanyName() + " (price) "
				+ getPrice();
	}

}
