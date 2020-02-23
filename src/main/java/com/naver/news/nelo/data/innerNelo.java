package com.naver.news.nelo.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class innerNelo implements Comparable<innerNelo> {
    private String body;
    private Double count;
    @Override
    public int compareTo(innerNelo o) {
        if (this.count < o.getCount()) {
            return 1;
        } else if (this.count > o.getCount()) {
            return -1;
        }
        return 0;
    }
}