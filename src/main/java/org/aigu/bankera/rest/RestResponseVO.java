package org.aigu.bankera.rest;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.*;

@JsonInclude(Include.NON_NULL)
@ToString
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RestResponseVO {

	private boolean isSuccess;
	private Object response;

	public RestResponseVO(boolean success, String message) {
		isSuccess = success;
		response = message;
	}
}
