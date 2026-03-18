package com.kunal.admission.controller;

import com.kunal.admission.dto.AuthResponse;
import com.kunal.admission.dto.LoginRequest;
import com.kunal.admission.dto.RegisterRequest;
import com.kunal.admission.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService){
        this.authService = authService;
    }

    /**
     * POST /auth/register
     * Public — no token needed.
     *
     * Request body:
     * {
     *   "name": "Ravi Kumar",
     *   "email": "ravi@example.com",
     *   "password": "mypassword123",
     *   "phone": "9876543210",
     *   "courseInterested": "MCA"
     * }
     *
     * Response: { "message": "Registration successful! Please login to continue." }
     */

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request){
        try{
            AuthResponse response =  authService.register(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }catch (RuntimeException e){
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new AuthResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new AuthResponse("Something went wrong. Please try again."));
        }
    }



    /**
     * POST /auth/login
     * Public — no token needed.
     *
     * Request body:
     * {
     *   "email": "ravi@example.com",
     *   "password": "mypassword123"
     * }
     *
     * Response:
     * {
     *   "token": "eyJhbGciOiJIUzI1NiJ9...",
     *   "role": "APPLICANT",
     *   "name": "Ravi Kumar",
     *   "email": "ravi@example.com",
     *   "message": "Success"
     * }
     *
     * The client must send this token in every future request as:
     * Authorization: Bearer <token>
     */

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request){
        try {
            AuthResponse response = authService.login(request);
            return ResponseEntity.ok(response);
        }catch (RuntimeException e){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new AuthResponse(e.getMessage()));
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new AuthResponse("Something went wrong. Please try again."));
        }
    }
}
