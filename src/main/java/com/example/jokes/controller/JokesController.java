package com.example.jokes.controller;

import com.example.jokes.dto.Joke;
import com.example.jokes.exception.JokesParallelProcessingFailedException;
import com.example.jokes.exception.JokesCountRangeException;
import com.example.jokes.service.JokesService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class JokesController {

    private final JokesService jokesService;

    @GetMapping("/jokes")
    public List<Joke> getJokes(@RequestParam(name = "count", defaultValue = "5") Integer count) throws JokesParallelProcessingFailedException, JokesCountRangeException {
        if(count < 1 || count > 100) {
            throw new JokesCountRangeException();
        }

        return jokesService.requestJokes(count);
    }

    @ExceptionHandler(JokesParallelProcessingFailedException.class)
    public ResponseEntity<String> handleJokesParallelProcessingFailedException() {
        return ResponseEntity.internalServerError().body("Unable to process jokes");
    }

    @ExceptionHandler(JokesCountRangeException.class)
    public ResponseEntity<String> handleJokesCountRangeException() {
        return ResponseEntity.badRequest().body("За один раз можно получить от 1 до 100 штук.");
    }

}
