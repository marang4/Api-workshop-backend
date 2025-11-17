package com.senac.Api_workshops.domain.repository;

import com.senac.Api_workshops.domain.entity.Workshop;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkshopRepository extends JpaRepository<Workshop,Long> {

}
