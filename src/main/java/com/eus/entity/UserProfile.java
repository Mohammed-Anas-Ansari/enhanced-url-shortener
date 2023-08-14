package com.eus.entity;

import com.eus.enums.UserType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.TableGenerator;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class UserProfile implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "userProfileSequenceTable")
    @TableGenerator(
            name = "userProfileSequenceTable",
            table = "sequence_table",
            pkColumnName = "SEQUENCE_NAME",
            valueColumnName = "GEN_VALUE",
            pkColumnValue = "USER_PROFILE_SEQ",
            allocationSize = 1)
    private long id;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    @Enumerated
    private UserType userType;
    @CreationTimestamp
    @Column(columnDefinition = "DATETIME(3)")
    private LocalDateTime createdAt;
    @Column(columnDefinition = "DATETIME(3)")
    private LocalDateTime lastLogin;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(userType.name()));
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
//  https://vladmihalcea.com/jpa-entity-identifier-sequence/