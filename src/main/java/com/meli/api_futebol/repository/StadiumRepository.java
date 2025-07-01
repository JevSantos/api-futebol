package com.meli.api_futebol.repository;

import com.meli.api_futebol.model.Stadium;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StadiumRepository extends JpaRepository<Stadium, Long> {
    Page<Stadium> findAll(Pageable pageable);
}
