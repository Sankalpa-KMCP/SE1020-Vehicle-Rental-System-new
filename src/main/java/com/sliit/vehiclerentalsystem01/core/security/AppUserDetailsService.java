package com.sliit.vehiclerentalsystem01.core.security;

import com.sliit.vehiclerentalsystem01.user.entity.Customer;
import com.sliit.vehiclerentalsystem01.user.entity.Staff;
import com.sliit.vehiclerentalsystem01.user.entity.User;
import com.sliit.vehiclerentalsystem01.user.repository.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Bridges the file-based user store into Spring Security's UserDetails model.
 * BCrypt-encoding happens on first successful login for legacy SHA-256 passwords.
 *
 * @see SecurityUtils#verifyPassword(String, String)  BCrypt migration on successful auth
 * @see com.sliit.vehiclerentalsystem01.user.service.UserServiceImpl#authenticate(String, String)  where migration runs
 */
@Service
public class AppUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public AppUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> opt = userRepository.findAll().stream()
                .filter(user -> matchesUsername(user, username))
                .findFirst();

        if (opt.isEmpty()) {
            throw new UsernameNotFoundException("User not found: " + username);
        }

        User user = opt.get();
        List<GrantedAuthority> authorities = buildAuthorities(user);

        return new AppUserDetails(
                user.getId(),
                resolveUsername(user),
                user.getPassword() != null ? user.getPassword() : "",
                authorities,
                user.getName() != null ? user.getName() : "",
                user.getProfilePicture() != null ? user.getProfilePicture() : ""
        );
    }

    private boolean matchesUsername(User user, String username) {
        if (user instanceof Customer c) {
            String u = c.getUsername() == null ? "" : c.getUsername().trim();
            String email = c.getEmail() == null ? "" : c.getEmail().trim();
            return u.equalsIgnoreCase(username.trim()) || email.equalsIgnoreCase(username.trim());
        }
        if (user instanceof Staff s) {
            String u = s.getUsername() == null ? "" : s.getUsername().trim();
            return u.equalsIgnoreCase(username.trim());
        }
        return false;
    }

    private String resolveUsername(User user) {
        if (user instanceof Customer c) {
            return c.getUsername() != null ? c.getUsername().trim() : "";
        }
        if (user instanceof Staff s) {
            return s.getUsername() != null ? s.getUsername().trim() : "";
        }
        return "";
    }

    private List<GrantedAuthority> buildAuthorities(User user) {
        List<GrantedAuthority> auths = new ArrayList<>();
        if (user instanceof Customer) {
            auths.add(new SimpleGrantedAuthority("ROLE_CUSTOMER"));
        } else if (user instanceof Staff s) {
            if (s.isAdminRole()) {
                auths.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
            }
            auths.add(new SimpleGrantedAuthority("ROLE_SUPPORT"));
            auths.add(new SimpleGrantedAuthority("ROLE_STAFF"));
        }
        return auths;
    }

    public static final class AppUserDetails implements UserDetails {

        private final String userId;
        private final String username;
        private final String password;
        private final Collection<? extends GrantedAuthority> authorities;
        private final String displayName;
        private final String avatarUrl;

        public AppUserDetails(String userId, String username, String password,
                              Collection<? extends GrantedAuthority> authorities,
                              String displayName, String avatarUrl) {
            this.userId = userId;
            this.username = username;
            this.password = password;
            this.authorities = authorities;
            this.displayName = displayName;
            this.avatarUrl = avatarUrl;
        }

        @Override public String getUsername() { return username; }

        @Override public String getPassword() { return password; }

        @Override public Collection<? extends GrantedAuthority> getAuthorities() { return authorities; }

        @Override public boolean isAccountNonExpired() { return true; }

        @Override public boolean isAccountNonLocked() { return true; }

        @Override public boolean isCredentialsNonExpired() { return true; }

        @Override public boolean isEnabled() { return true; }

        public String getUserId() { return userId; }

        public String getDisplayName() { return displayName; }

        public String getAvatarUrl() { return avatarUrl; }
    }
}
