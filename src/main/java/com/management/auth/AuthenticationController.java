package com.management.auth;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication")
public class AuthenticationController {

  private final AuthenticationService service;

  @Operation(
          description = "Register a new user",
          summary = "This is a summary for register endpoint",
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
  @PostMapping("/register")
  public ResponseEntity<AuthenticationResponse> register(
      @RequestBody RegisterRequest request
  ) {
    return ResponseEntity.ok(service.register(request));
  }
  @Operation(
          description = "Login",
          summary = "This is a summary for login endpoint",
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
  @PostMapping("/login")
  public ResponseEntity<AuthenticationResponse> login(
      @RequestBody AuthenticationRequest request
  ) {
    return ResponseEntity.ok(service.login(request));
  }

  @Operation(
          description = "Reset password",
          summary = "This is a summary for reset password endpoint",
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
  @PatchMapping("/reset-password")
  public ResponseEntity<?>
  Password(
          @RequestBody ChangePasswordRequest request
  ) {
    service.resetPassword(request);
    return ResponseEntity.ok().build();
  }



}
