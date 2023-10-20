package com.example.jokes.service;

import com.example.jokes.dto.Joke;
import com.example.jokes.exception.JokesParallelProcessingFailedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Service
@Slf4j
public class JokesService {

    private static final String JOKES_URL = "https://official-joke-api.appspot.com/random_joke";

    @Autowired
    private Executor jokesTaskExecutor;

    @Autowired
    private RestTemplate restTemplate;

    public List<Joke> requestJokes(int count) throws JokesParallelProcessingFailedException {

        List<CompletableFuture<Joke>> jokesFutureList = new ArrayList<>();
        for (int i = 0; i < count; i++) {
             jokesFutureList.add(CompletableFuture.supplyAsync(this::requestJoke, jokesTaskExecutor));
        }

        List<Joke> jokes = new ArrayList<>();
        for (CompletableFuture<Joke> future : jokesFutureList) {
            try {
                jokes.add(future.get());
            } catch (Exception e) {
                log.error("Unable to retrieve jokes in parallel", e);
                throw new JokesParallelProcessingFailedException();
            }
        }

        return jokes;
    }

    private Joke requestJoke() {
        ResponseEntity<Joke> response = restTemplate.getForEntity(JOKES_URL, Joke.class);
        if(response.getStatusCode() != HttpStatus.OK) {
           log.error("Get joke request failed with status: " + response.getStatusCode());
        }
        return response.getBody();
    }

}
