package com.example.test.payload.request;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class EditRequest {
	
	@NotNull(message = "Please enter id")
    private long id;
	
	private String token;
	
    @Size(min = 3, max = 20)
    private String newusername;
	
 
    @Size(max = 50)
    @Email
    private String email;
  
 
    public String getEmail() {
        return email;
    }
 
    public void setEmail(String email) {
        this.email = email;
    }

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
	

	public String getNewusername() {
		return newusername;
	}

	public void setNewusername(String newusername) {
		this.newusername = newusername;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}
	
	   
}
