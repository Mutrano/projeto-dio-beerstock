package com.mutrano.beerstock.dto;

import java.io.Serializable;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;

import com.mutrano.beerstock.entities.Beer;
import com.mutrano.beerstock.enums.BeerType;

public class BeerDTO implements Serializable{
	
	private static final long serialVersionUID = 1L;

	private Integer id;
	@NotEmpty(message="")
	private String name;
	
	@NotEmpty(message="")
	private String brand;
	
	@NotNull(message="")
	@PositiveOrZero(message="Max quantity must be postive or zero")
	private Integer max;

	@NotNull(message="Quantity must not be null.")
	@PositiveOrZero(message="Quantity must be positive or zero")
	private Integer quantity;

	@NotNull(message = "")
	private BeerType beerType;
	
	public BeerDTO() {
		
	}
	public BeerDTO(Beer obj) {
		this.id=obj.getId();
		this.name=obj.getName();
		this.brand=obj.getBrand();
		this.max=obj.getMax();
		this.quantity=obj.getQuantity();
		this.beerType=obj.getBeerType();
	}

	public BeerDTO(Integer id, @NotEmpty(message = "") String name,
			@NotEmpty(message = "") String brand, @NotEmpty(message = "") Integer max,
			@NotNull(message = "") Integer quantity, @NotNull(message = "") BeerType beerType) {
		this.id = id;
		this.name = name;
		this.brand = brand;
		this.max = max;
		this.quantity = quantity;
		this.beerType = beerType;
	}


	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getBrand() {
		return brand;
	}

	public void setBrand(String brand) {
		this.brand = brand;
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
		BeerDTO other = (BeerDTO) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	
	
}
