package com.sidharthdevlpr.moviecatalogservice.controller;

import com.sidharthdevlpr.moviecatalogservice.domain.CatalogItem;
import com.sidharthdevlpr.moviecatalogservice.domain.Movie;
import com.sidharthdevlpr.moviecatalogservice.domain.UserRating;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
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
//tells the springboot that here we handle the http request and response
public class MovieCatalogController {

    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private WebClient.Builder webclientBuilder;

//    Returns a list of movies that a particular user has seen
    @GetMapping("/{userId}")
    @CircuitBreaker(name = "getCatalogCircuitBreaker", fallbackMethod = "getFallBackCatalogItem")
    public List<CatalogItem> getCatalog(@PathVariable String userId){
        /*Using RestTemplate for the connection of 2 microservices*/


        /*returns an object of UserRating from the rating-data-service
        * and also uses the instance from the eureka server to connect */
        UserRating ratingList = restTemplate.getForObject("http://Ratings-data-service/ratingsdata/"+userId, UserRating.class);

        // the user-rating obj is used to stream and put inside a map using lambda expresssion
       return ratingList.getUserRating().stream().map(rating -> {
           Movie movie = restTemplate.getForObject("http://Movie-info-service/movies/"+rating.getMovieId(), Movie.class);

           return new CatalogItem(movie.getName(),"a movie that revolves around the life of a cop",rating.getRating());
       })
               .collect(Collectors.toList());
    }
    // can be used while using the web client

    /*Movie movie = webclientBuilder.build()
                   .get()
                   .uri("http://localhost:8083/movies/"+rating.getMovieId())
                   .retrieve()
                   .bodyToMono(Movie.class)
                   .block();
           */
    public List<CatalogItem> getFallBackCatalogItem(@PathVariable String userId, Throwable throwable){
        return Arrays.asList(new CatalogItem("","",0));
    }
}
