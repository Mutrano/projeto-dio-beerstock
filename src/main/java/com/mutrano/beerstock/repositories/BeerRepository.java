package com.mutrano.beerstock.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mutrano.beerstock.entities.Beer;

@Repository
public interface BeerRepository extends JpaRepository<Beer, Integer>{

	Optional<Beer> findByName(String name);
}
