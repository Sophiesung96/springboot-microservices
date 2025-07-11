package com.sky.api.weatherapiservice.Location.repository;

import com.sky.api.weatherapicommon.entity.Location;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class FilterableLocationRepositoryImpl implements  FilterableLocationRepository{

    @Autowired
    EntityManager entityManager;

    @Override
    public Page<Location> listWithFilters(Pageable pageable, Map<String, Object> filterFields) {
        CriteriaBuilder criteriaBuilder=entityManager.getCriteriaBuilder();
        CriteriaQuery<Location> criteriaQuery = criteriaBuilder.createQuery(Location.class);
        Root<Location> root=criteriaQuery.from(Location.class);
        Predicate [] predicates=createPredicates(filterFields, criteriaBuilder, root);
        if(predicates.length > 0) criteriaQuery.where(predicates);
        List<Order> orderList=new ArrayList<Order>();
        pageable.getSort().stream().forEach(order->{
            System.out.println("Order field: "+order.getProperty());
            if(order.isAscending())
            {
                orderList.add(criteriaBuilder.asc(root.get(order.getProperty())));
            }else{
                orderList.add(criteriaBuilder.desc(root.get(order.getProperty())));
            }
        });
        criteriaQuery.orderBy(orderList);
        TypedQuery<Location> typedQuery=entityManager.createQuery(criteriaQuery);
        typedQuery.setFirstResult((int) pageable.getOffset());
        typedQuery.setMaxResults(pageable.getPageSize());
        List<Location> listResult=typedQuery.getResultList();
        Long totalRows=getTotalRows(filterFields);
        return new PageImpl<>(listResult,pageable,totalRows);
    }

    private Predicate [] createPredicates(Map<String, Object> filterFields, CriteriaBuilder criteriaBuilder
            , Root<Location> root) {

        Predicate [] predicate = new Predicate[(filterFields.size()+1)];
        if(!filterFields.isEmpty()) {
            Iterator<String> iterator= filterFields.keySet().iterator();
            int i=0;
            while(iterator.hasNext())
            {
                String filedName=iterator.next();
                Object value= filterFields.get(filedName);
                System.out.println(filedName + "=> "+value);
                predicate[i++]= criteriaBuilder.equal(root.get(filedName),value);
            }

        }
        predicate[predicate.length-1]=criteriaBuilder.equal(root.get("trashed"),false);
        return predicate;
    }

    private Long getTotalRows(Map<String,Object> filterFields) {
        CriteriaBuilder criteriaBuilder=entityManager.getCriteriaBuilder();
        CriteriaQuery<Object> countQuery=criteriaBuilder.createQuery();
        Root<Location> root=countQuery.from(Location.class);
        countQuery.select(criteriaBuilder.count(root));
        Predicate [] predicates=createPredicates(filterFields, criteriaBuilder, root);
        if(predicates.length > 0) countQuery.where(predicates);
        Long totalRows= (Long) entityManager.createQuery(countQuery).getSingleResult();
        return totalRows;
    }
}
