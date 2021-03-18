package com.mutrano.beerstock.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import com.mutrano.beerstock.dto.BeerDTO;
import com.mutrano.beerstock.entities.Beer;
import com.mutrano.beerstock.entities.exceptions.BeerStockExceededException;
import com.mutrano.beerstock.repositories.BeerRepository;
import com.mutrano.beerstock.services.exceptions.BeerAlreadyRegisteredException;
import com.mutrano.beerstock.services.exceptions.ResourceNotFoundException;

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
	public BeerDTO findByName(String name) throws ResourceNotFoundException {
		Optional<Beer> optFoundBeer = beerRepository.findByName(name);
		BeerDTO beerDTO = new BeerDTO(optFoundBeer.orElseThrow( () -> new ResourceNotFoundException(name) ));
		return beerDTO;
	}
	public void verifyIfAlreadyExists(String name) throws BeerAlreadyRegisteredException{
		Optional<Beer> optFoundBeer = beerRepository.findByName(name);
		if(optFoundBeer.isPresent()) {
			throw new BeerAlreadyRegisteredException(name);
		}		
	}
	public List<BeerDTO> findAll(){
		List<Beer> beers= beerRepository.findAll();
		List<BeerDTO> beersDTO = new ArrayList<>();
		beersDTO = beers.stream().map(x-> new BeerDTO(x) ).collect(Collectors.toList());
		return beersDTO;
	}
	
	public void delete(Integer id) throws ResourceNotFoundException {
		try {
			beerRepository.deleteById(id);
		}
		catch(EmptyResultDataAccessException e) {
			throw new ResourceNotFoundException(e.getMessage());
		}	
	}
	public BeerDTO incrementStock(Integer id,Integer increment) throws ResourceNotFoundException, BeerStockExceededException {
		Optional<Beer> optFoundBeer = beerRepository.findById(id);
		Beer foundBeer = optFoundBeer.orElseThrow(() -> new ResourceNotFoundException());
		foundBeer.increment(increment);
		foundBeer = beerRepository.save(foundBeer);
		return new BeerDTO(foundBeer);
	}
	public BeerDTO decrementStock(Integer id, Integer decrement) throws ResourceNotFoundException, BeerStockExceededException {
		Optional<Beer> optFoundBeer = beerRepository.findById(id);
		Beer foundBeer = optFoundBeer.orElseThrow(() -> new ResourceNotFoundException());
		foundBeer.decrement(decrement);
		foundBeer = beerRepository.save(foundBeer);
		return new BeerDTO(foundBeer);
	}
	public Beer fromDTO(BeerDTO dto) {
		Beer obj = new Beer(dto.getId(),dto.getBrand(), dto.getName(), dto.getMax(), dto.getQuantity(), dto.getBeerType());
		return obj;
	}
}

