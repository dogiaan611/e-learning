package com.example.e_learning.service;

import com.example.e_learning.dto.request.LoginRequest;
import com.example.e_learning.dto.request.RegisterRequest;
import com.example.e_learning.dto.response.JwtResponse;
import com.example.e_learning.model.Role;
import com.example.e_learning.model.User;
import com.example.e_learning.model.enums.UserRole;
import com.example.e_learning.repository.RoleRepository;
import com.example.e_learning.repository.UserRepository;
import com.example.e_learning.security.JwtUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder encoder;

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private AuthService authService;

    private User testUser;
    private Role testRole;

    @BeforeEach
    void setUp() {
        testRole = new Role(1L, UserRole.ROLE_STUDENT);
        testUser = User.builder()
                .id(1L)
                .email("test@example.com")
                .password("encoded_password")
                .fullName("Test User")
                .roles(Collections.singleton(testRole))
                .build();
    }

    @Test
    void registerUser_Success() {
        // Arrange
        RegisterRequest request = new RegisterRequest();
        request.setEmail("newuser@example.com");
        request.setPassword("password");
        request.setFullName("New User");

        when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(encoder.encode(request.getPassword())).thenReturn("encoded_password");
        when(roleRepository.findByName(UserRole.ROLE_STUDENT)).thenReturn(Optional.of(testRole));

        // Act
        authService.registerUser(request);

        // Assert
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void registerUser_EmailAlreadyExists_ThrowsException() {
        // Arrange
        RegisterRequest request = new RegisterRequest();
        request.setEmail("test@example.com");
        
        when(userRepository.existsByEmail(request.getEmail())).thenReturn(true);

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () -> authService.registerUser(request));
        assertEquals("Error: Email is already in use!", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void authenticateUser_Success() {
        // Arrange
        LoginRequest request = new LoginRequest();
        request.setEmail("test@example.com");
        request.setPassword("password");

        Authentication authentication = mock(Authentication.class);
        UserDetails userDetails = mock(UserDetails.class);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(jwtUtils.generateJwtToken(authentication)).thenReturn("mock_jwt_token");
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn("test@example.com");
        
        // Mocking getAuthorities() requires a bit of setup if we want to fully test roles, 
        // but for simplicity we return an empty list or mock it.
        when(userDetails.getAuthorities()).thenReturn(Collections.emptyList());
        
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

        // Act
        JwtResponse response = authService.authenticateUser(request);

        // Assert
        assertNotNull(response);
        assertEquals("mock_jwt_token", response.getToken());
        assertEquals("test@example.com", response.getEmail());
    }
}
