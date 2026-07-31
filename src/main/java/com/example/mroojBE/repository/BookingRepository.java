package com.example.mroojBE.repository;

import com.example.mroojBE.Entity.Booking;
import com.example.mroojBE.Entity.enums.BookingStatus;
import com.example.mroojBE.Entity.enums.Domain;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    Page<Booking> findByFarmerId(Long farmerId, Pageable pageable);

    Page<Booking> findByAssignedConsultantId(Long consultantId, Pageable pageable);

    Page<Booking> findByStatus(BookingStatus status, Pageable pageable);

    Page<Booking> findByDomainAndStatus(Domain domain, BookingStatus status, Pageable pageable);

    long countByAssignedConsultantIdAndStatusIn(Long consultantId, java.util.List<BookingStatus> statuses);
}