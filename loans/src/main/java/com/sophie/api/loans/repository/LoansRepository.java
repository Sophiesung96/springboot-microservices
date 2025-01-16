package com.sophie.api.loans.repository;

import com.sophie.api.loans.entity.Loans;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LoansRepository extends JpaRepository<Loans, Long> {

    @Query("select l from Loans  l where l.mobileNumber=:mobileNumber")
    Optional<Loans> findByMobileNumber(String mobileNumber);

    @Query("select l from Loans  l where l.loanNumber=:loanNumber")
    Optional<Loans> findByLoanNumber(String loanNumber);

}
