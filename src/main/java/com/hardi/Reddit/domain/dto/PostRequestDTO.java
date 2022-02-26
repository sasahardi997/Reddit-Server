package com.hardi.Reddit.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PostRequestDTO {

    private Long postId;
    private Long subredditId;
    private String postName;
    private String url;
    private String description;
}
