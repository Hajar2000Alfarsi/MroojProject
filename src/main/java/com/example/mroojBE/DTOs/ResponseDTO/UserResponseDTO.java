package com.example.mroojBE.DTOs.ResponseDTO;

import com.example.mroojBE.Entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserResponseDTO {
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private String phone;
    private String role;
    private Boolean enabled;
    private String preferredLanguage;

    public static UserResponseDTO fromEntity(User user) {
        if (user == null) {
            return null;
        }

        UserResponseDTO dto = new UserResponseDTO();

        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setPhone(user.getPhone());
        dto.setRole(user.getRole().name());
        dto.setEnabled(user.getEnabled());
        dto.setPreferredLanguage(user.getPreferredLanguage());

        return dto;
    }

    public static List<UserResponseDTO> fromEntity(List<User> users) {
        List<UserResponseDTO> dtos = new ArrayList<>();
        if (users != null) {
            for (User user : users) {
                dtos.add(fromEntity(user));
            }
        }
        return dtos;
    }
}
