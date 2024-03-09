package com.management.auth;


import com.management.exception.ChangePasswordException;
import com.management.user.Role;
import com.management.user.User;
import com.management.user.UserRepository;
import com.management.config.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthenticationServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthenticationService authenticationService;

    @Test
    public void testRegister() {
        RegisterRequest request = new RegisterRequest("John", "Doe", "john@example.com", "password", "password");

        when(passwordEncoder.encode(request.getPassword())).thenReturn("encodedPassword");
        when(jwtService.generateToken(any())).thenReturn("jwtToken");

        AuthenticationResponse response = authenticationService.register(request);

        assertNotNull(response.getToken());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    public void testLogin() {
        AuthenticationRequest request = new AuthenticationRequest("john@example.com", "password");
        User user = new User("John", "Doe", "john@example.com", "encodedPassword", Role.USER);

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(user));
        when(jwtService.generateToken(user)).thenReturn("jwtToken");

        AuthenticationResponse response = authenticationService.login(request);

        assertNotNull(response.getToken());
        verify(authenticationManager, times(1)).authenticate(any());
    }

    @Test
    public void testLoginWithTempPassword() {
        AuthenticationRequest request = new AuthenticationRequest("john@example.com", "password");
        User user = new User("John", "Doe", "john@example.com", "encodedPassword", Role.USER);
        user.setTempPassword(true);

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(user));

        assertThrows(ChangePasswordException.class, () -> authenticationService.login(request));
    }

    @Test
    public void testResetPassword() {
        ChangePasswordRequest request = new ChangePasswordRequest("john@example.com", "currentPassword", "newPassword", "newPassword");
        User user = new User("John", "Doe", "john@example.com", "encodedPassword", Role.USER);

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())).thenReturn(true);
        when(passwordEncoder.encode(request.getNewPassword())).thenReturn("encodedNewPassword");

        authenticationService.resetPassword(request);

        assertFalse(user.isTempPassword());
        verify(userRepository, times(1)).save(user);
    }
}

