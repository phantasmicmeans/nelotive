package com.naver.news.nelo.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Nelo {
    @JsonProperty("logLevel") private String logLevel;
    @JsonProperty("host") private String host;
    @JsonProperty("projectName") private String projectName;
    @JsonProperty("projectVersion") private String projectVersion;
    @JsonProperty("agentVersion") private String agentVersion;
    @JsonProperty("body") private String body;
}
