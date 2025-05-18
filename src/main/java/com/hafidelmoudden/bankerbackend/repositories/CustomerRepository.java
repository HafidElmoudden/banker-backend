package com.hafidelmoudden.bankerbackend.repositories;

import com.hafidelmoudden.bankerbackend.entities.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    List<Customer> findByNameContains(String keyword);
    Optional<Customer> findByName(String name);
    long count();

    @Query("select c from Customer c where c.name like :kw")
    List<Customer> searchCustomer(@Param("kw") String keyword);
    
    @Query("select c from Customer c where c.name like :kw or c.email like :kw")
    Page<Customer> searchCustomersPaged(@Param("kw") String keyword, Pageable pageable);
}
