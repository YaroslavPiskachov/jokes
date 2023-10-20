package com.example.jokes.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Joke {
    private Integer id;
    private String type;
    private String setup;
    private String punchline;
}
