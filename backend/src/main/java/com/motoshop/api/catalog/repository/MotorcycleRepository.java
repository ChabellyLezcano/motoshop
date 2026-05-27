package com.motoshop.api.catalog.repository;

import com.motoshop.api.catalog.model.Motorcycle;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * Repository for the catalog. Combines: - JpaRepository: CRUD + pagination. -
 * JpaSpecificationExecutor: dynamic filtering, used by the service to assemble optional
 * brand/category/license/price filters.
 */
public interface MotorcycleRepository
    extends JpaRepository<Motorcycle, Long>, JpaSpecificationExecutor<Motorcycle> {

  Page<Motorcycle> findAll(Specification<Motorcycle> spec, Pageable pageable);
}
