package com.motoshop.api.catalog;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.motoshop.api.catalog.dto.CreateMotorcycleRequest;
import com.motoshop.api.catalog.dto.MotorcycleResponse;
import com.motoshop.api.catalog.dto.UpdateMotorcycleRequest;

@Service
public class MotorcycleService {

    private final MotorcycleRepository repository;

    public MotorcycleService(MotorcycleRepository repository) {
        this.repository = repository;
    }

    /**
     * Paginated, filtered, public listing of the catalog.
     * Every filter is optional; combining them narrows the result set.
     */
    @Transactional(readOnly = true)
    public Page<MotorcycleResponse> list(MotorcycleFilter filter, Pageable pageable) {
        Specification<Motorcycle> spec = MotorcycleSpecifications.allOf(
                MotorcycleSpecifications.search(filter.q()),
                MotorcycleSpecifications.hasBrand(filter.brand()),
                MotorcycleSpecifications.hasCategory(filter.category()),
                MotorcycleSpecifications.hasLicense(filter.license()),
                MotorcycleSpecifications.priceCentsGte(filter.minPriceCents()),
                MotorcycleSpecifications.priceCentsLte(filter.maxPriceCents()),
                MotorcycleSpecifications.inStock(filter.inStock())
        );
        return repository.findAll(spec, pageable).map(MotorcycleMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public MotorcycleResponse findById(Long id) {
        return repository.findById(id)
                .map(MotorcycleMapper::toResponse)
                .orElseThrow(() -> new MotorcycleNotFoundException(id));
    }

    @Transactional
    public MotorcycleResponse create(CreateMotorcycleRequest req) {
        Motorcycle entity = MotorcycleMapper.fromCreate(req);
        return MotorcycleMapper.toResponse(repository.save(entity));
    }

    @Transactional
    public MotorcycleResponse update(Long id, UpdateMotorcycleRequest req) {
        Motorcycle entity = repository.findById(id)
                .orElseThrow(() -> new MotorcycleNotFoundException(id));
        MotorcycleMapper.applyUpdate(entity, req);
        // No save() needed: entity is managed within the transaction,
        // changes flush automatically. Kept implicit on purpose.
        return MotorcycleMapper.toResponse(entity);
    }

    @Transactional
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new MotorcycleNotFoundException(id);
        }
        repository.deleteById(id);
    }

    /**
     * Filter bundle for the listing endpoint. A record so the controller
     * can build it inline from query params without a dedicated DTO.
     */
    public record MotorcycleFilter(
            String q,
            String brand,
            Category category,
            LicenseType license,
            Long minPriceCents,
            Long maxPriceCents,
            Boolean inStock
    ) { }
}