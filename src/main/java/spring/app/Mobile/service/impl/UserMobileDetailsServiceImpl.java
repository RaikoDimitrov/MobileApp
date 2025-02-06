package spring.app.Mobile.service.impl;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import spring.app.Mobile.model.entity.UserEntity;
import spring.app.Mobile.model.entity.UserRoleEntity;
import spring.app.Mobile.model.enums.UserRoleEnum;
import spring.app.Mobile.model.user.UserMobileDetails;
import spring.app.Mobile.repository.UserRepository;
import spring.app.Mobile.security.CurrentUser;

import java.io.IOException;

@Service
public class UserMobileDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;
    private final CurrentUser currentUser;

    public UserMobileDetailsServiceImpl(UserRepository userRepository, CurrentUser currentUser) {
        this.userRepository = userRepository;
        this.currentUser = currentUser;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsernameIgnoreCase(username)
                .map(UserMobileDetailsServiceImpl::map)
                .orElseThrow(() -> new UsernameNotFoundException("User with " + username + " not found!"));
    }

    private static UserDetails map(UserEntity userEntity) {
        return new UserMobileDetails(userEntity.getUuid(),
                userEntity.getUsername(),
                userEntity.getEmail(),
                userEntity.getPassword(),
                userEntity.getRoles().stream().map(UserRoleEntity::getRole).map(UserMobileDetailsServiceImpl::map).toList(),
                userEntity.getFirstName(),
                userEntity.getLastName());
    }

    private static GrantedAuthority map(UserRoleEnum roleEnum) {
        return new SimpleGrantedAuthority("ROLE_" + roleEnum);
    }

    public void handlePostLogin(Authentication authentication) throws IOException {
        // Set currentUser based on the authenticated user
        UserMobileDetails principal = (UserMobileDetails) authentication.getPrincipal();
        currentUser.setAuthenticated(principal.getFullName());
    }
}
