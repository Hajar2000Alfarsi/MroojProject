package com.example.mroojBE.DTOs.RequestDTO;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ConsultantUpdateRequestDTO {
    @Size(max = 100)
    private String firstName;
    @Size(max = 100)
    private String lastName;
    @Size(max = 20)
    private String phone;
    private String specialtyDomain;
    @Size(max = 255)
    private String specialtyTags;
    @Min(0)
    private Integer experienceYears;
    private Double latitude;
    private Double longitude;
    private Boolean available;
}
