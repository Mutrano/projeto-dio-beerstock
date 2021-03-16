package com.mutrano.beerstock.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mutrano.beerstock.dto.BeerDTO;
import com.mutrano.beerstock.entities.Beer;
import com.mutrano.beerstock.repositories.BeerRepository;
import com.mutrano.beerstock.services.exceptions.BeerAlreadyRegisteredException;

@Service
public class BeerService {
	
	@Autowired
	BeerRepository beerRepository;
	
	public BeerDTO insertBeer(BeerDTO dto) throws BeerAlreadyRegisteredException {
		verifyIfAlreadyExists(dto.getName());
		Beer insertedBeer = beerRepository.save(fromDTO(dto));
		BeerDTO beerDTO = new BeerDTO(insertedBeer);
		return beerDTO;
	}
	public BeerDTO findByName(String name) {
		Optional<Beer> optFoundBeer = beerRepository.findByName(name);
		BeerDTO beerDTO = new BeerDTO(optFoundBeer.orElseThrow());
		return beerDTO;
	}
	public void verifyIfAlreadyExists(String name) throws BeerAlreadyRegisteredException{
		Optional<Beer> optFoundBeer = beerRepository.findByName(name);
		if(optFoundBeer.isPresent()) {
			throw new BeerAlreadyRegisteredException(name);
		}

	}
	public Beer fromDTO(BeerDTO dto) {
		Beer obj = new Beer(dto.getId(),dto.getBrand(), dto.getName(), dto.getMax(), dto.getQuantity(), dto.getBeerType());
		return obj;
	}
}

