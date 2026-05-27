package com.motoshop.api.catalog.model;

public enum EngineType {
    SINGLE,           // Monocilindrico
    PARALLEL_TWIN,    // Bicilindrico en paralelo
    V_TWIN,           // Bicilindrico en V (incluye L-twin Ducati)
    BOXER,            // Bicilindrico boxer (BMW)
    INLINE_TRIPLE,    // Tres cilindros en linea
    INLINE_FOUR,      // Cuatro cilindros en linea
    V_FOUR            // Cuatro cilindros en V
}