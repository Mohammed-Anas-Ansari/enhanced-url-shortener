package com.eus.service;

import com.eus.entity.UserProfile;
import com.eus.repository.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserProfileService {

    private final UserProfileRepository userProfileRepository;

    public void saveSample() {
        UserProfile userProfile = UserProfile.builder()
                .firstName("abcd")
                .lastName("xyz")
                .email("dummy@gmail.com")
                .password("pass@123")
                .build();
        userProfileRepository.save(userProfile);
    }
}
