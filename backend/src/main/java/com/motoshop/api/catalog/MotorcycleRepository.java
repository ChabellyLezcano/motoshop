package com.motoshop.api.catalog;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * Repository for the catalog. Combines:
 *   - JpaRepository: CRUD + pagination.
 *   - JpaSpecificationExecutor: dynamic filtering, used by the service
 *     to assemble optional brand/category/license/price filters.
 */
public interface MotorcycleRepository
        extends JpaRepository<Motorcycle, Long>, JpaSpecificationExecutor<Motorcycle> {
}