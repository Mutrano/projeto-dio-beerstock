package com.mutrano.beerstock.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;

public class QuantityDTO {
	@NotNull(message = "Id must not be null")
	private Integer id;
	@NotNull(message = "Quantity must not be null.")
	@PositiveOrZero(message = "Quantity must be positive or zero")
	private Integer quantity;
	public QuantityDTO() {
		
	}
	public QuantityDTO(@NotNull(message = "Id mustg not be null") Integer id,
			@NotNull(message = "Quantity must not be null.") @PositiveOrZero(message = "Quantity must be positive or zero") Integer quantity) {
		this.id = id;
		this.quantity = quantity;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

}
