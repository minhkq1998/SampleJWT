package com.example.test.payload.request;

import javax.validation.constraints.NotNull;

public class DeleteRequest {
	
	@NotNull(message = "Please enter id")
    private long id;
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
}
