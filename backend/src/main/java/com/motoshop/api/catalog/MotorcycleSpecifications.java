package com.motoshop.api.catalog;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Predicate;

/**
 * Composable JPA specifications for the catalog. Each method returns
 * a {@link Specification} that contributes one predicate, or no
 * predicate at all if the filter argument is null. The service chains
 * them with {@code Specification.allOf(...)} to build the final query.
 *
 * Using specifications instead of derived query methods keeps the
 * filtering surface small and avoids a combinatorial explosion of
 * {@code findByBrandAndCategoryAnd...} methods.
 */
final class MotorcycleSpecifications {

    private MotorcycleSpecifications() { }

    /** Case-insensitive partial match on brand OR model. */
    static Specification<Motorcycle> search(String q) {
        if (q == null || q.isBlank()) return null;
        String like = "%" + q.toLowerCase() + "%";
        return (root, query, cb) -> {
            Predicate brand = cb.like(cb.lower(root.get("brand")), like);
            Predicate model = cb.like(cb.lower(root.get("model")), like);
            return cb.or(brand, model);
        };
    }

    static Specification<Motorcycle> hasBrand(String brand) {
        if (brand == null || brand.isBlank()) return null;
        return (root, query, cb) -> cb.equal(cb.lower(root.get("brand")), brand.toLowerCase());
    }

    static Specification<Motorcycle> hasCategory(Category category) {
        if (category == null) return null;
        return (root, query, cb) -> cb.equal(root.get("category"), category);
    }

    static Specification<Motorcycle> hasLicense(LicenseType license) {
        if (license == null) return null;
        return (root, query, cb) -> cb.equal(root.get("license"), license);
    }

    static Specification<Motorcycle> priceCentsGte(Long min) {
        if (min == null) return null;
        return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("priceCents"), min);
    }

    static Specification<Motorcycle> priceCentsLte(Long max) {
        if (max == null) return null;
        return (root, query, cb) -> cb.lessThanOrEqualTo(root.get("priceCents"), max);
    }

    /** Only show items with at least one unit available. */
    static Specification<Motorcycle> inStock(Boolean onlyAvailable) {
        if (onlyAvailable == null || !onlyAvailable) return null;
        return (root, query, cb) -> cb.greaterThan(root.get("stock"), 0);
    }

    /**
     * Combines all non-null specs with AND. Filters that come in as
     * null contribute nothing, so an empty filter set returns every row.
     */
    @SafeVarargs
    static Specification<Motorcycle> allOf(Specification<Motorcycle>... specs) {
        List<Specification<Motorcycle>> nonNull = new ArrayList<>();
        for (Specification<Motorcycle> s : specs) {
            if (s != null) nonNull.add(s);
        }
        if (nonNull.isEmpty()) return null;
        Specification<Motorcycle> result = nonNull.get(0);
        for (int i = 1; i < nonNull.size(); i++) {
            result = result.and(nonNull.get(i));
        }
        return result;
    }
}