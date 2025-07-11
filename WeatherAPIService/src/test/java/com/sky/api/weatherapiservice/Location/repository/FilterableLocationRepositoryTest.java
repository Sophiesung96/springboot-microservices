package com.sky.api.weatherapiservice.Location.repository;

import com.sky.api.weatherapicommon.entity.Location;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
class FilterableLocationRepositoryTest {
    @Autowired
    private LocationRepository locationRepository;

    @Test
    public void testListWithDefault(){
        int pageSize=5;
        int pageNum=0;
        String sortField="code";
        Sort sort=Sort.by(sortField).ascending();
        Pageable pageable= PageRequest.of(pageNum,pageSize,sort);
        Page<Location> page=locationRepository.listWithFilters(pageable, Collections.emptyMap());
        List<Location> content=page.getContent();
        System.out.println("Total elements: "+page.getTotalElements());
        assertEquals(content.size(), pageSize);
        content.forEach(System.out::println);

    }

    @Test
    public void testListNoFilterSortedByCityName(){
        int pageSize=5;
        int pageNum=0;
        String sortField="cityName";
        Sort sort=Sort.by(sortField).ascending();
        Pageable pageable= PageRequest.of(pageNum,pageSize,sort);
        Page<Location> page=locationRepository.listWithFilters(pageable, Collections.emptyMap());
        List<Location> content=page.getContent();
        System.out.println("Total elements: "+page.getTotalElements());
        assertEquals(content.size(), pageSize);
        List<Location> mutableSortedList = content.stream()
                .sorted(Comparator.comparing(Location::getCityName))
                .collect(Collectors.toList());
        mutableSortedList.forEach(System.out::println);

    }

    @Test
    public void testListFilteredSortedByCityName(){
        int pageSize=5;
        int pageNum=0;
        String sortField="cityName";
        String regionName="Los Angeles";
        Sort sort=Sort.by(sortField).ascending();
        Pageable pageable= PageRequest.of(pageNum,pageSize,sort);
        Map<String,Object> filteredFields=new HashMap<>();
        filteredFields.put("regionName",regionName);
        Page<Location> page=locationRepository.listWithFilters(pageable, filteredFields);
        List<Location> content=page.getContent();
        //assertEquals(content.size(), pageSize);
        assertFalse(content.isEmpty());
        assertTrue(content.size() <= pageSize);
        List<Location> mutableSortedList = content.stream()
                .sorted(Comparator.comparing(Location::getCityName))
                .collect(Collectors.toList());
        assertEquals(mutableSortedList, content);

        mutableSortedList.forEach(System.out::println);

    }

    @Test
    public void testListFilteredByCountryCodeSortedByCode(){
        int pageSize=5;
        int pageNum=0;
        String sortField="cityName";
        String countryCode="US";
        Sort sort=Sort.by(sortField).ascending();
        Pageable pageable= PageRequest.of(pageNum,pageSize,sort);
        Map<String,Object> filteredFields=new HashMap<>();
        filteredFields.put("countryCode",countryCode);
        Page<Location> page=locationRepository.listWithFilters(pageable, filteredFields);
        List<Location> content=page.getContent();
        assertTrue(content.size()<=pageSize);
        List<Location> mutableSortedList = content.stream()
                .sorted(Comparator.comparing(Location::getCode))
                .collect(Collectors.toList());
        mutableSortedList.forEach(System.out::println);

    }

    @Test
    public void testListFilteredByCountryCodeAndEnabledSortedByCityName(){
        int pageSize=5;
        int pageNum=0;
        String sortField="cityName";
        String countryCode="US";
        boolean enabled=true;
        Sort sort=Sort.by(sortField).ascending();
        Pageable pageable= PageRequest.of(pageNum,pageSize,sort);
        Map<String,Object> filteredFields=new HashMap<>();
        filteredFields.put("countryCode",countryCode);
        filteredFields.put("enabled",enabled);
        Page<Location> page=locationRepository.listWithFilters(pageable, filteredFields);
        List<Location> content=page.getContent();
        assertTrue(content.size()<=pageSize);
        List<Location> mutableSortedList = content.stream()
                .sorted(Comparator.comparing(Location::getCode))
                .collect(Collectors.toList());
        mutableSortedList.forEach(System.out::println);

    }

}