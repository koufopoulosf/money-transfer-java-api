package com.api.repository;

import com.api.model.Transfer;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TransferRepository extends JpaRepository<Transfer, Long> {
    List<Transfer> findByFromAccountNumberOrToAccountNumber(String from, String to);
}