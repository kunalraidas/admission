package com.kunal.admission.service;

import com.kunal.admission.datavalues.AdmissionStatus;
import com.kunal.admission.datavalues.Role;
import com.kunal.admission.dto.AuthResponse;
import com.kunal.admission.dto.LoginRequest;
import com.kunal.admission.dto.RegisterRequest;
import com.kunal.admission.model.User;
import com.kunal.admission.model.Visitor;
import com.kunal.admission.repository.UserRepository;
import com.kunal.admission.repository.VisitorRepository;
import com.kunal.admission.util.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final VisitorRepository visitorRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(UserRepository userRepository,
                       VisitorRepository visitorRepository,
                       PasswordEncoder passwordEncoder,
                       JwtUtil jwtUtil){
        this.userRepository = userRepository;
        this.visitorRepository = visitorRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    //-- REGISTER --
    public AuthResponse register(RegisterRequest request){

        // check if email already registered
        if(userRepository.existsByEmail(request.getEmail())){
            throw new RuntimeException("Email is already registered. Please login instead.");
        }

        // Hash the password
        String hashedPassword = passwordEncoder.encode(request.getPassword());

        // Create and save user account
        User user = new User(
                request.getName(),
                request.getEmail(),
                hashedPassword,
                Role.APPLICANT
        );
        userRepository.save(user);

        Visitor visitor = new Visitor();
        visitor.setName(request.getName());
        visitor.setEmail(request.getEmail());
        visitor.setPhone(request.getPhone());
        visitor.setCourseInterested(request.getCourseInterested());
        visitor.setSubmissionDate(LocalDate.now());
        visitor.setStatus(AdmissionStatus.NEW);
        visitorRepository.save(visitor);

        // Registration success
        return new AuthResponse("Registration successful! Please login to continue.");
    }

    // Login
    public AuthResponse login(LoginRequest request){
        User user =  userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("No account found with this Email. Please register first."));

        if(!passwordEncoder.matches(request.getPassword(), user.getPassword())){
            throw new RuntimeException("Incorrect password, Please try Again.");
        }

        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());

        return new AuthResponse(token, user.getRole().name(), user.getName(), user.getEmail());
    }
}
