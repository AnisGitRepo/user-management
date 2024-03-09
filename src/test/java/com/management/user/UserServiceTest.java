package com.management.user;

import com.management.auth.EmailService;
import com.management.exception.UnauthorizedOperationException;
import com.management.exception.UserNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private EmailService emailService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testCreateUser() {
        UserRequest request = new UserRequest("John", "Doe", "john@example.com");
        String randomPassword = "randomPassword";
        User user = new User(request.getFirstname(), request.getLastname(), request.getEmail());
        user.setPassword(randomPassword);
        user.setTempPassword(true);
        user.setRole(Role.USER);

        when(passwordEncoder.encode(anyString())).thenReturn(randomPassword);
        when(userRepository.save(any(User.class))).thenReturn(user);

        userService.createUser(request);

        verify(userRepository, times(1)).save(user);
        verify(emailService, times(1)).sendEmail(anyString(), anyString(), anyString());
    }

    @Test
    public void testUpdateUser_WithAdminRole() {
        UserDTO userDTO = new UserDTO(1, "John", "Doe", "john@example.com", "01/01/1990", "MALE", "USER");
        User user = User.builder().firstname("John").lastname("Doe").email("john@example.com").build();

        Principal connectedUser = new UsernamePasswordAuthenticationToken(user, null);

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        userService.updateUser(userDTO, connectedUser);

        verify(userRepository, times(1)).findByEmail(userDTO.getEmail());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    public void testUpdateUser_UnauthorizedOperation() {
        UserDTO userDTO = new UserDTO(1, "John", "Doe", "john@example.com", "01/01/1990", "MALE", "USER");
        User user = User.builder().firstname("John").lastname("Doe").email("another@example.com").role(Role.USER).build();

        Principal connectedUser = new UsernamePasswordAuthenticationToken(user, null);

        assertThrows(UnauthorizedOperationException.class, () -> userService.updateUser(userDTO, connectedUser));

    }

    @Test
    public void testDeleteUser() {
        User user = new User("John", "Doe", "john@example.com");
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));

        userService.deleteUser(1);

        verify(userRepository, times(1)).delete(user);
    }

    @Test
    public void testDeleteUser_UserNotFound() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.deleteUser(1));

        verify(userRepository, never()).delete(any(User.class));
    }

    @Test
    public void testFindUser() {
        User user = new User("John", "Doe", "john@example.com");
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));

        UserDTO userDTO = userService.findUser(1);

        assertEquals("John", userDTO.getFirstname());
        assertEquals("Doe", userDTO.getLastname());
        assertEquals("john@example.com", userDTO.getEmail());
    }

    @Test
    public void testFindUser_UserNotFound() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.findUser(1));
    }

    @Test
    public void testFindUsers() {
        // Mocking connected user
        User connectedUser = new User();
        Principal principal = new UsernamePasswordAuthenticationToken(connectedUser, null);

        // Mocking userRepository methods
        List<User> users = new ArrayList<>();
        users.add(new User("John", "Doe", "john@example.com"));
        users.add(new User("Jane", "Doe", "jane@example.com"));
        when(userRepository.findUsersByFirstnameContainingIgnoreCase(anyString())).thenReturn(Optional.of(users));
        when(userRepository.findUsersByLastnameContainingIgnoreCase(anyString())).thenReturn(Optional.of(users));
        when(userRepository.findUsersByIdIn(anyList())).thenReturn(Optional.of(users));
        when(userRepository.findUsersByRole(any())).thenReturn(Optional.of(users));
        when(userRepository.findAll()).thenReturn(users);

        // Testing findUsers method
        List<UserDTO> userDTOs = userService.findUsers("John", null, null, null, principal);

        assertEquals(2, userDTOs.size());
    }
}

