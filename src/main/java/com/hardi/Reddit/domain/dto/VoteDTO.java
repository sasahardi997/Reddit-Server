package com.hardi.Reddit.domain.dto;

import com.hardi.Reddit.domain.VoteType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VoteDTO {

    private VoteType voteType;
    private Long postId;
}
