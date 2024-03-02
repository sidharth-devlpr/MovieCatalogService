package com.sidharthdevlpr.moviecatalogservice.controller;

import com.sidharthdevlpr.moviecatalogservice.domain.CatalogItem;
import com.sidharthdevlpr.moviecatalogservice.domain.Movie;
import com.sidharthdevlpr.moviecatalogservice.domain.Rating;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class MovieCatalogController {

    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private WebClient.Builder webclientBuilder;
    @GetMapping("/{userId}")
    public List<CatalogItem> getCatalog(@PathVariable String userId){
        /*Using RestTemplate for the connection of 2 microservices*/



       List<Rating> ratings = Arrays.asList(
               new Rating("101",4),
               new Rating("102",3)
       );

       return ratings.stream().map(rating -> {
           Movie movie = restTemplate.getForObject("http://localhost:8083/movies/"+rating.getMovieId(), Movie.class);

           /*Movie movie = webclientBuilder.build()
                   .get()
                   .uri("http://localhost:8083/movies/"+rating.getMovieId())
                   .retrieve()
                   .bodyToMono(Movie.class)
                   .block();
           */

           return new CatalogItem(movie.getName(),"a movie that revolves around the life of a cop",rating.getRating());
       })
               .collect(Collectors.toList());
    }
}
