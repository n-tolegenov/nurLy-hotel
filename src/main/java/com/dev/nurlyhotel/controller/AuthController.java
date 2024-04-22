package com.dev.nurlyhotel.controller;

import com.dev.nurlyhotel.dto.JwtResponse;
import com.dev.nurlyhotel.exception.UserAlreadyExistsException;
import com.dev.nurlyhotel.model.User;
import com.dev.nurlyhotel.request.LoginRequest;
import com.dev.nurlyhotel.security.jwt.JwtUtils;
import com.dev.nurlyhotel.security.user.HotelUserDetails;
import com.dev.nurlyhotel.service.IUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final IUserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    @PostMapping("/register-user")
    public ResponseEntity<?> registerUser(@RequestBody User user){
        try{
            userService.registerUser(user);
            return ResponseEntity.ok("Registration successful!");
        }catch (UserAlreadyExistsException e){
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    /*
        UsernamePasswordAuthenticationToken - это специальный класс аутентификации в Spring Security,
        который используется для представления информации об аутентификации пользователя на основе его имени пользователя (логина) и пароля.

        UsernamePasswordAuthenticationToken обычно создается при успешной аутентификации пользователя,
        после чего он используется для представления аутентифицированного пользователя в системе.
        Этот токен затем передается в AuthenticationManager Spring Security
        для проверки подлинности и установки соответствующего статуса аутентификации.
     */
    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest request){
        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtTokenForUser(authentication);
        HotelUserDetails userDetails = (HotelUserDetails) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
        return ResponseEntity.ok(new JwtResponse(
                userDetails.getId(),
                userDetails.getEmail(),
                jwt,
                roles
        ));
    }
}
