package com.example.jokes.controller;

import com.example.jokes.dto.Joke;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class JokesControllerTest {

    private static final String JOKES_URL = "https://official-joke-api.appspot.com/random_joke";

    @Value(value="${local.server.port}")
    private int port;

    @Autowired
    private TestRestTemplate testRestTemplate;

    @MockBean
    private RestTemplate restTemplate;

    @Test
    public void testJokesSuccess() {
        final int count = 47;
        when(restTemplate.getForEntity(JOKES_URL, Joke.class)).thenReturn(ResponseEntity.ok(new Joke()));

        ResponseEntity<List<Joke>> response = testRestTemplate.exchange("http://localhost:" + port + "/jokes?count=" + count,
                HttpMethod.GET, null, new ParameterizedTypeReference<List<Joke>>() {
                });
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().size()).isEqualTo(count);
        verify(restTemplate, times(count)).getForEntity(JOKES_URL, Joke.class);
    }

    @Test
    public void testJokesRangeException() {
        testJokesRangeException(-1);
        testJokesRangeException(101);
    }

    private void testJokesRangeException(int count) {
        when(restTemplate.getForEntity(JOKES_URL, Joke.class)).thenReturn(ResponseEntity.ok(new Joke()));

        ResponseEntity<String> response = testRestTemplate.exchange("http://localhost:" + port + "/jokes?count=" + count,
                HttpMethod.GET, null, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).isEqualTo("За один раз можно получить от 1 до 100 штук.");
        verify(restTemplate, times(0)).getForEntity(JOKES_URL, Joke.class);
    }

}
