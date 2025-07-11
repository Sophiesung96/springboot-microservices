//package com.sky.api.weatherapiservice.security.jwt;
//
//import jakarta.validation.Valid;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.authentication.BadCredentialsException;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//@RestController
//@RequestMapping("/api/oauth")
//public class AuthController {
//
//    @Autowired
//    AuthenticationManager authenticationManager;
//    @Autowired
//    TokenService tokenService;
//    @Autowired
//    private RefreshTokenRepository refreshTokenRepository;
//
//    @PostMapping("/token")
//    public ResponseEntity<?>getAccessToken(@Valid @RequestBody AuthRequest authRequest){
//        String username=authRequest.getUsername();
//        String password=authRequest.getPassword();
//        AuthResponse response=null;
//        try{
//            Authentication authentication=authenticationManager
//                    .authenticate(new UsernamePasswordAuthenticationToken(username,password));
//            CustomerUserDetail userDetail= (CustomerUserDetail) authentication.getPrincipal();
//            response=tokenService.generateToken(userDetail.getUser());
//        }catch(BadCredentialsException ex){
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
//        }
//        return ResponseEntity.ok(response);
//    }
//
//    @PostMapping("/token/refresh")
//    public ResponseEntity<?> refreshToken(@Valid @RequestBody RefreshTokenRequest refreshRequest)
//    {
//        AuthResponse response=tokenService.refreshToken(refreshRequest);
//        return new ResponseEntity<>(response,HttpStatus.OK);
//    }
//
//
//
//}
