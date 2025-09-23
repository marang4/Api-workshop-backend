package com.senac.Api_workshops.repository;

import com.senac.Api_workshops.model.Workshop;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkshopRepository extends JpaRepository<Workshop,Long> {

}
