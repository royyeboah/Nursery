package org.roy.nursery.controller;

import lombok.Data;
import org.roy.nursery.model.Role;
import org.roy.nursery.model.User;
import org.roy.nursery.model.ParentProfile;
import org.roy.nursery.model.StaffProfile;
import org.roy.nursery.repository.ParentProfileRepository;
import org.roy.nursery.repository.StaffProfileRepository;
import org.roy.nursery.security.JwtUtil;
import org.roy.nursery.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final ParentProfileRepository parentProfileRepository;
    private final StaffProfileRepository staffProfileRepository;

    public AuthController(UserService userService, JwtUtil jwtUtil, AuthenticationManager authenticationManager,
                         ParentProfileRepository parentProfileRepository, StaffProfileRepository staffProfileRepository) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
        this.parentProfileRepository = parentProfileRepository;
        this.staffProfileRepository = staffProfileRepository;
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody SignupRequest request) {
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(request.getPassword());
        user.setRole(request.getRole());
        userService.save(user);

        if (request.getRole() == Role.PARENT) {
            ParentProfile profile = new ParentProfile();
            profile.setUser(user);
            profile.setFullName(request.getFullName());
            parentProfileRepository.save(profile);
        } else if (request.getRole() == Role.STAFF) {
            StaffProfile profile = new StaffProfile();
            profile.setUser(user);
            profile.setFullName(request.getFullName());
            staffProfileRepository.save(profile);
        }
        return ResponseEntity.ok("User registered successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );
            User user = userService.findByUsername(request.getUsername());
            String token = jwtUtil.generateToken(user.getUsername(), user.getRole());

            Object profile = null;
            if (user.getRole() == Role.PARENT && user.getParentProfile() != null) {
                profile = user.getParentProfile();
            } else if (user.getRole() == Role.STAFF && user.getStaffProfile() != null) {
                profile = user.getStaffProfile();
            }

            return ResponseEntity.ok(new LoginResponse(token, user.getRole(), profile));
        } catch (AuthenticationException e) {
            return ResponseEntity.status(401).body("Invalid username or password");
        }
    }

    @Data
    public static class SignupRequest {
        private String username;
        private String password;
        private Role role;
        private String fullName; // for profile
    }

    @Data
    public static class LoginRequest {
        private String username;
        private String password;
    }

    @Data
    public static class LoginResponse {
        private final String token;
        private final Role role;
        private final Object profile;
    }
}