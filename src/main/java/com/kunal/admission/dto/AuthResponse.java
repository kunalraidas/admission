package com.kunal.admission.dto;

// what the server sends  back after a successful login or registration
public class AuthResponse {
    private String token;
    private String role;
    private String name;
    private String email;
    private String message;

    // Used for successful login
    public AuthResponse(String token, String role, String name, String email) {
        this.token   = token;
        this.role    = role;
        this.name    = name;
        this.email   = email;
        this.message = "Success";
    }

    // Used for registration success (no token yet — user must login separately)
    public AuthResponse(String message) {
        this.message = message;
    }

    // Getters
    public String getToken()    { return token; }
    public String getRole()     { return role; }
    public String getName()     { return name; }
    public String getEmail()    { return email; }
    public String getMessage()  { return message; }
}
