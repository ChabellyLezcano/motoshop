package com.motoshop.api.catalog.repository;

import com.motoshop.api.catalog.model.Category;
import com.motoshop.api.catalog.model.LicenseType;
import com.motoshop.api.catalog.model.Motorcycle;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.jpa.domain.Specification;

// 1. Cambiamos a PUBLIC para que se vea desde fuera de la carpeta
public final class MotorcycleSpecifications {

  private MotorcycleSpecifications() {}

  // 2. Ponemos PUBLIC delante de cada método estático
  public static Specification<Motorcycle> search(String q) {
    if (q == null || q.isBlank()) return null;
    String like = "%" + q.toLowerCase() + "%";
    return (root, query, cb) -> {
      Predicate brand = cb.like(cb.lower(root.get("brand")), like);
      Predicate model = cb.like(cb.lower(root.get("model")), like);
      return cb.or(brand, model);
    };
  }

  public static Specification<Motorcycle> hasBrand(String brand) {
    if (brand == null || brand.isBlank()) return null;
    return (root, query, cb) -> cb.equal(cb.lower(root.get("brand")), brand.toLowerCase());
  }

  public static Specification<Motorcycle> hasCategory(Category category) {
    if (category == null) return null;
    return (root, query, cb) -> cb.equal(root.get("category"), category);
  }

  public static Specification<Motorcycle> hasLicense(LicenseType license) {
    if (license == null) return null;
    return (root, query, cb) -> cb.equal(root.get("license"), license);
  }

  public static Specification<Motorcycle> priceCentsGte(Long min) {
    if (min == null) return null;
    return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("priceCents"), min);
  }

  public static Specification<Motorcycle> priceCentsLte(Long max) {
    if (max == null) return null;
    return (root, query, cb) -> cb.lessThanOrEqualTo(root.get("priceCents"), max);
  }

  public static Specification<Motorcycle> inStock(Boolean onlyAvailable) {
    if (onlyAvailable == null || !onlyAvailable) return null;
    return (root, query, cb) -> cb.greaterThan(root.get("stock"), 0);
  }

  @SafeVarargs
  public static Specification<Motorcycle> allOf(Specification<Motorcycle>... specs) {
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
