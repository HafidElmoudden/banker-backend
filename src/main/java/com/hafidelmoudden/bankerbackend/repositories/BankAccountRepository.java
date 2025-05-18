package com.hafidelmoudden.bankerbackend.repositories;

import com.hafidelmoudden.bankerbackend.entities.BankAccount;
import com.hafidelmoudden.bankerbackend.entities.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BankAccountRepository extends JpaRepository<BankAccount, String> {
    long count();
    List<BankAccount> findByCustomerId(Long customerId);
    
    @Query("SELECT b FROM BankAccount b WHERE b.id LIKE :keyword OR b.customer.name LIKE :keyword")
    Page<BankAccount> searchAccounts(@Param("keyword") String keyword, Pageable pageable);
}
