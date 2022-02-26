package com.hardi.Reddit.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SubredditDTO {

    private Long id;
    private String name;
    private String description;
    private Integer numberOfPosts;
}
