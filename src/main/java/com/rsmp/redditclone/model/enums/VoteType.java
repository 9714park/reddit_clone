package com.rsmp.redditclone.model.enums;

import java.util.Arrays;

public enum VoteType {
    UPVOTE(1),
    DOWNVOTE(-1);

    private int direction;

    VoteType(int direction) {
        this.direction = direction;
    }

    public static VoteType lookup(Integer direction) throws Exception {
        return Arrays.stream(VoteType.values())
                .filter(value -> value.getDirection().equals(direction))
                .findAny()
                .orElseThrow(() -> new Exception("Vote not found"));
    }

    public Integer getDirection() {
        return direction;
    }
}
