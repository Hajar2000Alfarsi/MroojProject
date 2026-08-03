package com.example.mroojBE.DTOs;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingSummaryResponse {

    private Long id;

    // سيظهر في عمود Problem Type
    private String problemType;

    // اسم المستشار، وإذا لم يتم تعيين مستشار بعد سيظهر "Not Assigned"
    private String consultant;

    // حالة الحجز (PENDING, RESOLVED, ...)
    private String status;

}