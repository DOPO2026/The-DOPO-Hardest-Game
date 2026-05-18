# Informe de Cobertura de Tests — JaCoCo
**Proyecto:** The DOPO Hardest Game  
**Curso:** Desarrollo Orientado por Objetos 2026-1  
**Herramienta:** JaCoCo (runner integrado en IntelliJ IDEA)

---

## Resumen ejecutivo

| Métrica | Antes | Después | Cambio |
|---|---|---|---|
| Tests totales | 28 | 71 | +43 |
| Archivos de test | 7 | 11 | +4 nuevos |
| Cobertura de instrucciones (dominio) | ~63% | **~80%** | **+17 pp** |
| Cobertura de ramas (dominio) | ~35% | **~75%** | **+40 pp** |

> La capa `presentation` (código Swing/GUI) se excluye del análisis porque no es comprobable con tests unitarios. La cobertura reportada corresponde al paquete `domain` y sus sub-paquetes.

---

## Cobertura por paquete — Instrucciones

| Paquete | Antes | Después | Δ |
|---|---|---|---|
| `domain.ai` | 0% | **98%** | +98 pp |
| `domain.core` | 35% | **97%** | +62 pp |
| `domain.enemy` | 67% | **90%** | +23 pp |
| `domain.collectibles` | 61% | **91%** | +30 pp |
| `domain.player` | 84% | **95%** | +11 pp |
| `domain.skins` | 81% | **94%** | +13 pp |
| `domain.world` | 92% | **98%** | +6 pp |
| `domain.common` | 86% | **87%** | +1 pp |
| `presentation` | 0% | 0% | — (no aplica) |

---

## Cobertura por paquete — Ramas (branches)

| Paquete | Antes | Después | Δ |
|---|---|---|---|
| `domain.ai` | 0% | **100%** | +100 pp |
| `domain.core` | 25% | **83%** | +58 pp |
| `domain.enemy` | 38% | **72%** | +34 pp |
| `domain.collectibles` | 0% | **66%** | +66 pp |
| `domain.player` | 87% | **87%** | = |
| `domain.common` | 77% | **77%** | = |
| `domain.world` | 50% | **50%** | = |

---

## Clases más destacadas

### Clases que pasaron de 0% a cobertura significativa

| Clase | Antes | Después | Tests que la cubren |
|---|---|---|---|
| `TheDOPOHardestGame` | 0% | **95%** | `TheDOPOHardestGameTest` |
| `MaquinaAleatoria` | 0% | **98%** | `AITest` |
| `MaquinaExperta` | 0% | **100%** | `AITest` |
| `DeslizadorVertical` | 0% | **~90%** | `EstrategiaMovimientoTest` |
| `Patrullero` | 0% | **100%** | `EstrategiaMovimientoTest` |
| `FuenteDeVida` | 30% | **100%** | `CollectiblesTest`, `MotorJuegoTest` |

### Clases que ya tenían cobertura y se completaron

| Clase | Antes | Después |
|---|---|---|
| `Nivel` | 89% | **100%** |
| `MotorJuego` | 60% | **98%** |
| `Jugador` | 84% | **95%** |
| `Enemigo` | 92% | **100%** |
| `EstadoJuego` | — | **100%** |
| `ModoJuego` | — | **100%** |
| `ZonaIntermedia` | 63% | **100%** |

---

## Tests añadidos

### Archivos nuevos (4)

#### `AITest.java` — 4 tests
Cubre `MaquinaAleatoria` y `MaquinaExperta`, ambas en 0% antes.
- Primer llamado a `decidirMovimiento` (activa elección aleatoria)
- Llamados subsiguientes (mantiene dirección mientras tiene pasos)
- Agotamiento de pasos y re-elección de dirección
- `MaquinaExperta` retorna `Direction.QUIETO`

#### `EstrategiaMovimientoTest.java` — 4 tests
Cubre `DeslizadorVertical` y `Patrullero`, ambos en 0% antes.
- Descenso inicial del deslizador
- Rebote al llegar a `maxY`
- Rebote al llegar a `minY`
- `Patrullero.actualizar()` es un stub sin efecto

#### `CollectiblesTest.java` — 7 tests
Cubre `FuenteDeVida`, `MonedaSkin`, `Bomba`, `Clyde.aplicarPenalizacionGolpe` e `Inky`.
- `FuenteDeVida` solo se activa una vez aunque se llame dos veces
- `MonedaSkin` guarda y expone su skin otorgada
- `Bomba` está activa al crearse; `explotar()` es un stub seguro
- `Clyde` reduce su velocidad tras recibir un golpe
- `Inky` tiene velocidad, tamaño y color correctos

#### `TheDOPOHardestGameTest.java` — 29 tests
Cubre `TheDOPOHardestGame` (422 instrucciones, 0% → 95%) y `GestorArchivos`.
- Estado inicial antes de `iniciar()`
- Los tres modos de juego: `PLAYER`, `PvsP`, `PvsM`
- Spawning de jugadores con skin/color por defecto cuando no se proveen suficientes
- Excepciones al pasar `modo=null`, `skins=null` o skins vacías
- `actualizarJuego()` cuando no está en modo `JUGANDO` (retorno temprano)
- Transición a `DERROTA` al superar el tiempo límite
- `pausar()` y `reanudar()` dentro y fuera de su estado válido
- `terminar()` vuelve a `MENU`
- `reiniciar()` recarga el nivel correctamente
- `avanzarNivel()` rota nivel1 → nivel2 → nivel3 → nivel1
- Getters: `obtenerNivelId()`, `obtenerTiempoRestante()`, `getModo()`

---

### Archivos existentes ampliados (3)

#### `MotorJuegoTest.java` — 4 → 9 tests (+5)
Nuevas ramas cubiertas en `procesarInteracciones`:
- Recolección de `MonedaSkin` (cambia skin del jugador)
- Activación de `FuenteDeVida` (otorga escudo)
- Explosión de `Bomba` (mata jugador y reinicia monedas)
- `ZonaIntermedia` actualiza el punto de respawn
- Colisión PvsP mata a ambos jugadores

#### `NivelTest.java` — 3 → 5 tests (+2)
- `Nivel.actualizar()` mueve a los enemigos
- `Nivel.reset()` reinicia las monedas pendientes

#### `JugadorTest.java` — 7 → 9 tests (+2)
- `setRespawn()` cambia el punto de reaparición correctamente
- `agregarEscudo()` incrementa las vidas

---

## Metodología

Se usó **JaCoCo** integrado en IntelliJ IDEA como runner de cobertura. Los tests usan **JUnit 5** (`junit-platform-console-standalone-1.10.2`).

Criterios aplicados al escribir los tests:
- **Solo se probó lo que realmente faltaba** — no se duplicaron tests existentes.
- **Se priorizó por impacto** — primero las clases al 0%, luego las ramas no cubiertas.
- **No se testeó la capa `presentation`** — código Swing/GUI no es comprobable con tests unitarios estándar.
- **No se usaron mocks** — todos los tests son de integración real entre clases del dominio.
