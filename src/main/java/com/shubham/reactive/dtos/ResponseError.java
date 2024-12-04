package com.shubham.reactive.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.Calendar;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@JsonPropertyOrder(alphabetic = true)
public class ResponseError {
  private int status;

  private String path;

  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  private String errorDescription;

  @Builder.Default private Date timestamp = Calendar.getInstance().getTime();

  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  private String error;
}
