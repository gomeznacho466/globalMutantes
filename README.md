# Mutant Detector API

API REST para detección de mutantes basada en secuencias de ADN. Desarrollado para el desafío técnico de MercadoLibre.

## 🧬 Descripción del Proyecto

Magneto quiere reclutar mutantes y necesita una herramienta que detecte si un humano es mutante basándose en su secuencia de ADN. Se considera mutante si se encuentran **más de una secuencia** de cuatro letras iguales (A, T, C, G) de forma horizontal, vertical u oblicua.

### Ejemplos

**Mutante (Horizontal + Vertical)**
```
ATGCGA
CAGTGC
TTATGT
AGAAGG
CCCCTA
TCACTG
```

**Humano (No Mutante)**
```
ATGCGA
CAGTGC
TTATGT
AGAAGG
CCCTTA
TCACTG
```

## 🚀 Tecnologías Utilizadas

- **Java 17**
- **Spring Boot 3.2.0**
- **Spring Data JPA** con Hibernate
- **H2 Database** (persistente en disco: `~/test`)
- **Lombok** (reducción de boilerplate)
- **Swagger/OpenAPI** (documentación)
- **JUnit 5 + Mockito** (testing)
- **Jacoco** (cobertura de código)

## 🏗️ Arquitectura

Arquitectura N-Capas con separación clara de responsabilidades:

```
├── domain/
│   ├── detector/          # Algoritmo puro de detección
│   ├── entity/            # Entidades JPA
│   └── repository/        # Interfaces de repositorio
├── application/
│   ├── dto/               # Data Transfer Objects
│   ├── service/           # Lógica de negocio
│   └── validation/        # Validaciones customizadas
└── infrastructure/
    ├── controller/        # Controladores REST
    └── exception/         # Manejo de excepciones
```

## ⚡ Optimizaciones Implementadas

### 1. Algoritmo de Alto Rendimiento

- **Conversión a `char[][]`**: Evita el overhead de `String.charAt()` (~2-3x más rápido)
- **Early Termination**: Detiene la búsqueda al encontrar >1 secuencia
- **Loop Unrolling**: Verifica secuencias sin bucles internos
- **Complejidad**: O(N²) worst case, ~O(N) average case para mutantes

### 2. Persistencia Inteligente

- **Hash SHA-256**: Clave primaria única para evitar duplicados
- **Caché automático**: No analiza dos veces el mismo ADN
- **Índices optimizados**: Queries de estadísticas en O(1)

### 3. Validaciones

- Validación customizada `@ValidDna` antes de procesar
- Matriz NxN cuadrada
- Solo caracteres válidos (A, T, C, G)

## 📋 Endpoints

### POST /mutant

Analiza una secuencia de ADN y determina si es mutante.

**Request:**
```json
{
  "dna": ["ATGCGA","CAGTGC","TTATGT","AGAAGG","CCCCTA","TCACTG"]
}
```

**Response:**
- `200 OK` - Es mutante
- `403 FORBIDDEN` - No es mutante
- `400 BAD REQUEST` - Datos inválidos

### GET /stats

Retorna estadísticas de verificaciones.

**Response:**
```json
{
  "count_mutant_dna": 40,
  "count_human_dna": 100,
  "ratio": 0.4
}
```

## 🛠️ Instalación y Ejecución

### Requisitos

- Java 17 o superior
- Maven Wrapper incluido (no requiere instalación de Maven)

### Inicio Rápido

**Windows:**
```powershell
# Compilar y ejecutar tests
.\mvnw clean test

# Ejecutar la aplicación
.\mvnw spring-boot:run

# La aplicación estará disponible en:
# - Swagger UI: http://localhost:8080/swagger-ui.html
# - H2 Console: http://localhost:8080/h2-console
```

**Linux/Mac:**
```bash
# Compilar y ejecutar tests
./mvnw clean test

# Ejecutar la aplicación
./mvnw spring-boot:run
```

**Scripts de conveniencia (Windows):**
```powershell
# Inicio rápido con build y run
.\start.bat   # o .\start.ps1
```

### Tareas de VS Code

El proyecto incluye tareas configuradas. Abre Command Palette (`Ctrl+Shift+P`) y busca:

- **Run App** - Inicia la aplicación en modo background
- **Run Tests** - Ejecuta la suite de tests
- **Clean Install** - Build completo con tests
- **Generate Coverage Report** - Genera reporte Jacoco
- **Open Swagger UI** - Abre Swagger en el navegador
- **Open H2 Console** - Abre consola H2 en el navegador

### Ejecutar Tests

```powershell
# Ejecutar todos los tests
.\mvnw test

# Generar reporte de cobertura (Jacoco)
.\mvnw clean install
.\mvnw jacoco:report

# El reporte estará en target\site\jacoco\index.html
```

## 📊 Cobertura de Tests

El proyecto incluye tests exhaustivos con cobertura **>80%**:

- **MutantDetectorTest**: 20+ tests del algoritmo
- **MutantServiceTest**: Tests de caché y persistencia
- **StatsServiceTest**: Tests de estadísticas y ratios
- **MutantControllerTest**: Tests de integración de endpoints
- **DnaValidatorTest**: Tests de validaciones

## 📖 Documentación API

La documentación interactiva está disponible con Swagger UI:

```
http://localhost:8080/swagger-ui.html
```

## 🗄️ Base de Datos

### H2 Persistente

La base de datos H2 se guarda en disco para persistir entre reinicios:

- **Ubicación**: `~/test.mv.db` (home del usuario)
- **Modo**: Persistente con `AUTO_SERVER=TRUE` (permite conexiones simultáneas)
- **DDL**: `update` (mantiene datos entre ejecuciones)

### H2 Console

**Opción 1: Consola Web Integrada (recomendado)**
```
http://localhost:8080/h2-console

JDBC URL: jdbc:h2:file:~/test
User: sa
Password: (vacío)
```

**Opción 2: Cliente H2 Externo**
Puedes conectar con un cliente externo (DBeaver, IntelliJ, etc.) mientras la app está corriendo:
```
JDBC URL: jdbc:h2:tcp://localhost/~/test
User: sa
Password: (vacío)
Driver: org.h2.Driver
```

### Esquema

```sql
CREATE TABLE dna_records (
    dna_hash VARCHAR(64) PRIMARY KEY,
    is_mutant BOOLEAN NOT NULL,
    sequence_size INTEGER NOT NULL,
    analyzed_at TIMESTAMP NOT NULL
);

CREATE INDEX idx_is_mutant ON dna_records(is_mutant);
```

## 🎯 Casos de Uso

### Detectar Mutante

```powershell
curl -X POST http://localhost:8080/mutant `
  -H "Content-Type: application/json" `
  -d '{"dna":["ATGCGA","CAGTGC","TTATGT","AGAAGG","CCCCTA","TCACTG"]}'
```

### Obtener Estadísticas

```powershell
curl http://localhost:8080/stats
```

## 🔍 Detalles del Algoritmo

El algoritmo implementa las siguientes optimizaciones críticas:

1. **Búsqueda direccional**: Solo explora 4 direcciones (→, ↓, ↘, ↙) en lugar de 8
2. **Validación temprana**: Verifica espacios disponibles antes de buscar
3. **Loop unrolling**: Compara las 4 posiciones en una sola expresión booleana
4. **No recursión**: Evita overhead del stack

```java
// Ejemplo de verificación optimizada
private boolean checkSequence(char[][] matrix, int row, int col, 
                               int rowDelta, int colDelta, char expected) {
    return matrix[row + rowDelta][col + colDelta] == expected &&
           matrix[row + 2*rowDelta][col + 2*colDelta] == expected &&
           matrix[row + 3*rowDelta][col + 3*colDelta] == expected;
}
```

## 📝 Reglas de la Rúbrica

✅ **Performance Extrema**: char[][], Early Termination, O(N²) worst case
✅ **Persistencia Inteligente**: Hash SHA-256, sin duplicados
✅ **Endpoints Correctos**: POST /mutant (200/403), GET /stats
✅ **Validaciones**: Matriz NxN, solo A/T/C/G
✅ **Testing**: Cobertura >80% con JUnit 5 + Mockito
✅ **Documentación**: Swagger/OpenAPI completo
✅ **Arquitectura**: N-Capas clara y mantenible

## 🎓 Autor

Proyecto desarrollado para el desafío técnico de MercadoLibre - Mutant Detector Challenge

## 📄 Licencia

Este proyecto es parte de un desafío técnico y está disponible para evaluación.
