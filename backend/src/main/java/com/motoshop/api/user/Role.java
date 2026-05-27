package com.motoshop.api.user;

/**
 * Roles de la aplicacion. La asignacion del rol es responsabilidad exclusiva del servidor: - El
 * registro publico crea siempre usuarios con rol BUYER (RF-01). - El primer ADMIN se crea por
 * inicializacion segura al arrancar. - La promocion de nuevos ADMIN se hara en el Sprint 2 mediante
 * un endpoint protegido del back-office.
 */
public enum Role {
  BUYER,
  ADMIN
}
