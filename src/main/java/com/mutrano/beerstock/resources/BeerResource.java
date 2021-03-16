package com.mutrano.beerstock.resources;

import java.net.URI;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.mutrano.beerstock.dto.BeerDTO;
import com.mutrano.beerstock.services.BeerService;
import com.mutrano.beerstock.services.exceptions.BeerAlreadyRegisteredException;
import com.mutrano.beerstock.services.exceptions.ResourceNotFoundException;


@RestController
@RequestMapping(value = "/Beers")
public class BeerResource {

	
	@Autowired
	BeerService beerService;
	
	@PostMapping()
	ResponseEntity<Void> insertBeer(@RequestBody @Valid BeerDTO dto) throws BeerAlreadyRegisteredException{
		BeerDTO insertedBeer =beerService.insertBeer(dto);
		URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(insertedBeer.getId()).toUri();
		return ResponseEntity.created(uri).build();
	}
	
	@GetMapping("/{name}")
	ResponseEntity<BeerDTO> findByName(@PathVariable String name) throws ResourceNotFoundException{
		BeerDTO foundBeer = beerService.findByName(name);
		return ResponseEntity.ok().body(foundBeer);
	}
}
