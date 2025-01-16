package com.sophie.api.accounts.repository;

import com.sophie.api.accounts.entity.Accounts;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountsRepository extends JpaRepository<Accounts, Long> {
    @Query("select a from Accounts  a where a.customerId=:customerId")
    Optional<Accounts> findByCustomerId(@Param(value = "customerId") Long customerId);

    @Query("delete  from Accounts  a where a.customerId=:customerId")
    void deleteByCustomerId(@Param(value = "customerId")Long customerId);

}