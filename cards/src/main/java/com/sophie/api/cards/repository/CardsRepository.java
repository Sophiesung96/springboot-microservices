package com.sophie.api.cards.repository;

import com.sophie.api.cards.entity.Cards;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CardsRepository extends JpaRepository<Cards, Long> {

    @Query("select c from Cards  c where c.mobileNumber=:mobileNumber")
    Optional<Cards> findByMobileNumber(@Param(value = "mobileNumber") String mobileNumber);

    @Query("select c from Cards  c where c.cardNumber=:cardNumber")
    Optional<Cards> findByCardNumber(@Param(value = "cardNumber")String cardNumber);

}
