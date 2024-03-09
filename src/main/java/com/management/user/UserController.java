package com.management.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
@Tag(name = "User Management")
public class UserController {

    private final UserService userService;

    @Operation(
            description = "Find an existing user",
            summary = "This is a summary for find user endpoint",
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200"
                    ),
                    @ApiResponse(
                            description = "Unauthorized / Invalid Token",
                            responseCode = "403"
                    )
            }

    )
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public UserDTO findUser(@PathVariable Integer id) {
        return userService.findUser(id);
    }

    @Operation(
            description = "Create a new user",
            summary = "This is a summary for create a new user endpoint",
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200"
                    ),
                    @ApiResponse(
                            description = "Unauthorized / Invalid Token",
                            responseCode = "403"
                    )
            }

    )
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity createUser(@RequestBody UserRequest request) {
        userService.createUser(request);
        return ResponseEntity.ok().build();
    }

    @Operation(
            description = "Update an existing user",
            summary = "This is a summary for update user endpoint",
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200"
                    ),
                    @ApiResponse(
                            description = "Unauthorized / Invalid Token",
                            responseCode = "403"
                    )
            }

    )
    @PutMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity updateUser(@RequestBody UserDTO userDTO, Principal connectedUser) {
        userService.updateUser(userDTO, connectedUser);
        return ResponseEntity.ok().build();
    }

    @Operation(
            description = "Delete an existing user",
            summary = "This is a summary for delete user endpoint",
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200"
                    ),
                    @ApiResponse(
                            description = "Unauthorized / Invalid Token",
                            responseCode = "403"
                    )
            }

    )
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity delete(@PathVariable Integer id) {
        userService.deleteUser(id);
        return ResponseEntity.ok().build();
    }

    @Operation(
            description = "Find existing users",
            summary = "This is a summary for find users endpoint",
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200"
                    ),
                    @ApiResponse(
                            description = "Unauthorized / Invalid Token",
                            responseCode = "403"
                    )
            }

    )
    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public List<UserDTO> findUsers(@RequestParam(value = "firstname", required = false) String firstname,
                                                   @RequestParam(value = "lastname", required = false) String lastname,
                                                   @RequestParam(value = "ids", required = false) List<Integer> userIds,
                                                   @RequestParam(value = "role", required = false) String role,
                                                   Principal connectedUser) {
        return userService.findUsers(firstname, lastname, userIds, role, connectedUser);
    }


}
