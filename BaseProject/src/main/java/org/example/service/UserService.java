package org.example.service;

import org.example.model.dto.request.RefreshTokenRequest;
import org.example.model.dto.request.UserLoginDTO;
import org.example.model.dto.request.UserRegisterDTO;
import org.example.model.dto.response.JwtResponse;
import org.example.model.entity.User;
import org.example.repository.UserRepository;
import org.example.security.jwt.JwtProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleService roleService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtProvider jwtProvider;

    public ResponseEntity<?> register(UserRegisterDTO userRegisterDTO) {
        User user = User
                .builder()
                .username(userRegisterDTO.getUsername())
                .password(passwordEncoder.encode(userRegisterDTO.getPassword()))
                .email(userRegisterDTO.getEmail())
                .phone(userRegisterDTO.getPhone())
                .createdAt(LocalDate.now())
                .role(roleService.findRoleByName("USER"))
                .build();
        return new ResponseEntity<>(userRepository.save(user), HttpStatus.CREATED);
    }

    public ResponseEntity<?> login(UserLoginDTO userLoginDTO) {
        User user = userRepository.findByUsername(userLoginDTO.getUsername()).orElse(null);
        if(user == null || !passwordEncoder.matches(userLoginDTO.getPassword(), user.getPassword())){
            return new ResponseEntity<>("Username or password is incorrect", HttpStatus.UNAUTHORIZED);
        }
        JwtResponse jwtResponse = JwtResponse
                .builder()
                .username(user.getUsername())
                .accessToken(jwtProvider.generateAccessToken(user))
                .refreshToken(jwtProvider.generateRefreshToken(user))
                .build();
        return new ResponseEntity<>(jwtResponse, HttpStatus.OK);
    }

    public ResponseEntity<?> refreshToken(RefreshTokenRequest request) {
        try {
            String token = request.getRefreshToken();
            if(!jwtProvider.validateJwtToken(token)) {
                return new ResponseEntity<>("Invalid refresh token", HttpStatus.UNAUTHORIZED);
            }
            String username = jwtProvider.getUsernameFromToken(token);
            User user = userRepository.findByUsername(username).orElse(null);
            if(user == null) {
                return new ResponseEntity<>("User not found", HttpStatus.UNAUTHORIZED);
            }
            String newAccessToken = jwtProvider.generateAccessToken(user);
            JwtResponse jwtResponse = JwtResponse
                    .builder()
                    .username(user.getUsername())
                    .accessToken(newAccessToken)
                    .refreshToken(token)
                    .build();
            return new ResponseEntity<>(jwtResponse, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
