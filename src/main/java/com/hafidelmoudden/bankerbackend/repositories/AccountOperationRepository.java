package com.hafidelmoudden.bankerbackend.repositories;

import com.hafidelmoudden.bankerbackend.entities.AccountOperation;
import com.hafidelmoudden.bankerbackend.entities.BankAccount;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;

public interface AccountOperationRepository extends JpaRepository<AccountOperation, Long> {
    List<AccountOperation> findByBankAccountId(String accountId);
    Page<AccountOperation> findByBankAccountId(String accountId, Pageable pageable);
    List<AccountOperation> findByOperationDateBetween(Date startDate, Date endDate);

    Page<AccountOperation> findByBankAccountIdOrderByOperationDateDesc(String accountId, Pageable pageable);
}