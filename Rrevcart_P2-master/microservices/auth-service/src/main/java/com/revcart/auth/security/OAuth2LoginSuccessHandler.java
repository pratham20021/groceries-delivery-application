package com.revcart.auth.security;

import com.revcart.auth.entity.User;
import com.revcart.auth.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.util.Collections;
import java.util.UUID;

@Component
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    
    private final UserRepository userRepository;
    private final JwtUtils jwtUtils;
    private final PasswordEncoder passwordEncoder;
    
    public OAuth2LoginSuccessHandler(UserRepository userRepository, JwtUtils jwtUtils, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.jwtUtils = jwtUtils;
        this.passwordEncoder = passwordEncoder;
    }
    
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");
        
        User user = userRepository.findByEmail(email).orElseGet(() -> {
            User newUser = new User();
            newUser.setEmail(email);
            newUser.setName(name);
            newUser.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));
            newUser.setEmailVerified(true);
            newUser.setRole(User.Role.CUSTOMER);
            return userRepository.save(newUser);
        });
        
        UserPrincipal userPrincipal = new UserPrincipal(
            user.getId(), 
            user.getName(), 
            user.getEmail(), 
            user.getPassword(), 
            Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()))
        );
        Authentication auth = new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(userPrincipal, null, userPrincipal.getAuthorities());
        String token = jwtUtils.generateJwtToken(auth);
        
        String frontendUrl = System.getenv().getOrDefault("FRONTEND_URL", "http://localhost:4200");
        response.sendRedirect(frontendUrl + "/oauth2/redirect?token=" + token + "&id=" + user.getId() + "&name=" + user.getName() + "&email=" + user.getEmail() + "&role=" + user.getRole());
    }
}
