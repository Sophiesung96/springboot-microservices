package com.sophie.api.accounts.repository;

import com.sophie.api.accounts.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    @Query("select c from Customer  c where c.mobileNumber=:mobileNumber")
    Optional<Customer> findByMobileNumber(@Param(value = "mobileNumber") String mobileNumber);


}
