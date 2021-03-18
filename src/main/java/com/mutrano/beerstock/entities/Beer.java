package com.mutrano.beerstock.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.mutrano.beerstock.entities.exceptions.BeerStockExceededException;
import com.mutrano.beerstock.enums.BeerType;

@Entity
public class Beer {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@Column(nullable=true)
	private String name;
	
	@Column(nullable = true)
	private String brand;

	@Column(nullable = true)
	private Integer max;

	@Column(nullable = true)
	private Integer quantity;

	@Enumerated
	@Column(nullable = true)
	private BeerType beerType;
	public Beer() {
		
	}
	public Beer(Integer id,String brand,String name, Integer max, Integer quantity, BeerType beerType) {
		this.id=id;
		this.brand = brand;
		this.name=name;
		this.max = max;
		this.quantity = quantity;
		this.beerType = beerType;
	}
	
	public void increment(Integer increment) throws BeerStockExceededException {
		if(increment+quantity<=max) {
			quantity += increment;
		}
		else {
			throw new BeerStockExceededException(name);
		}

	}
	public void decrement(Integer decrement) throws BeerStockExceededException  {
		if(quantity-decrement>=0) {
			quantity-=decrement;
		}
		else {
			throw new BeerStockExceededException(name);
		}
		
	}
	public String getBrand() {
		return brand;
	}

	public void setBrand(String brand) {
		this.brand = brand;
	}
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getMax() {
		return max;
	}

	public void setMax(Integer max) {
		this.max = max;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public BeerType getBeerType() {
		return beerType;
	}

	public void setBeerType(BeerType beerType) {
		this.beerType = beerType;
	}

	public Integer getId() {
		return id;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Beer other = (Beer) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

}
