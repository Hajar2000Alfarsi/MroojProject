package com.example.mroojBE.Service;

import com.example.mroojBE.Entity.Consultant;
import com.example.mroojBE.Entity.Farmer;
import com.example.mroojBE.Entity.User;
import com.example.mroojBE.Security.SecurityUtils;
import com.example.mroojBE.exceptions.ResourceNotFoundException;
import com.example.mroojBE.repository.ConsultantRepository;
import com.example.mroojBE.repository.FarmerRepository;
import com.example.mroojBE.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthenticatedUserService {

    private final UserRepository userRepository;
    private final FarmerRepository farmerRepository;
    private final ConsultantRepository consultantRepository;

    public User currentUser() {
        return userRepository.findByEmail(SecurityUtils.currentEmail())
                .orElseThrow(() -> new ResourceNotFoundException("Authenticated user was not found"));
    }

    public Farmer currentFarmer() {
        User user = currentUser();
        return farmerRepository.findByUserId(user.getId())
                .orElseThrow(() -> new AccessDeniedException("The authenticated user does not have a farmer profile"));
    }

    public Consultant currentConsultant() {
        User user = currentUser();
        return consultantRepository.findByUserId(user.getId())
                .orElseThrow(() -> new AccessDeniedException("The authenticated user does not have a consultant profile"));
    }
}
