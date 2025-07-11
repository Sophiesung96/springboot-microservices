package com.sky.api.weatherapiservice.Location;

import com.sky.api.weatherapicommon.entity.Location;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
@DataJpaTest
@AutoConfigureTestDatabase(replace= AutoConfigureTestDatabase.Replace.NONE)
class LocationCriteriaQueryTests {
    @Autowired
   private EntityManager entityManager;

    @Test
    public void testCriteriaQuery(){
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Location> cq = cb.createQuery(Location.class);
        Root<Location> root = cq.from(Location.class);
        //Add where clause
        Predicate predicate=cb.equal(root.get("countryCode"),"US");
        cq.where(predicate);
        // Add order by clause
        cq.orderBy(cb.asc(root.get("cityName")));

        TypedQuery<Location> typedQuery= entityManager.createQuery(cq);
        // Add pagination
        typedQuery.setFirstResult(0);
        typedQuery.setMaxResults(3);
        List<Location> locationList=typedQuery.getResultList();
        assertTrue(!locationList.isEmpty());
        locationList.forEach(System.out::println);

    }

    @Test
    public void testJPQL(){
        String jpql = "FROM Location WHERE countryCode = 'US' ORDER BY cityName";
        TypedQuery<Location> locationTypedQeury=entityManager.createQuery(jpql,Location.class);
        List<Location> locationList=locationTypedQeury.getResultList();
        assertTrue(!locationList.isEmpty());
    }
}