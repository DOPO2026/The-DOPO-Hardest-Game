package domain.ai;

import domain.collectibles.Moneda;
import domain.core.Nivel;
import domain.enemy.Enemigo;
import domain.player.ControlJugador;
import domain.player.Direction;
import domain.player.Jugador;
import domain.world.Pared;
import domain.world.Zona;
import domain.world.ZonaInicial;

import java.util.*;

public class MaquinaExperta implements ControlJugador {

    private static final int CELL          = 20;
    private static final int UMBRAL        = CELL;
    private static final int RECALC_FRAMES = 25; // recalcula path cada N frames
    private static final int MARGEN_ENEMIGO = 1; // celdas de buffer alrededor de enemigos

    private boolean[][] baseGrid; // solo paredes (estático por nivel)
    private int gW, gH;
    private int nivelW = -1, nivelH = -1;

    private List<int[]> waypoints = new ArrayList<>();
    private int wpIdx    = 0;
    private int targetPx = Integer.MIN_VALUE;
    private int targetPy = Integer.MIN_VALUE;
    private int frame    = 0;

    @Override
    public Direction decidirMovimiento(Nivel nivel) {
        List<Jugador> jugadores = nivel.getJugadores();
        if (jugadores.size() < 2) return Direction.QUIETO;
        Jugador yo = jugadores.get(1);

        if (nivelW != nivel.obtenerAncho() || nivelH != nivel.obtenerAlto()) {
            construirBaseGrid(nivel);
        }

        boolean[][] grid = gridConEnemigos(nivel);

        int[] objetivo = encontrarObjetivo(nivel, yo);
        if (objetivo == null) return Direction.QUIETO;

        frame++;
        boolean nuevoObjetivo      = objetivo[0] != targetPx || objetivo[1] != targetPy;
        boolean mismaCeldaObjetivo = (yo.obtenerPosX() / CELL == objetivo[0] / CELL)
                                  && (yo.obtenerPosY() / CELL == objetivo[1] / CELL);
        boolean necesitaRecalculo  = nuevoObjetivo
                || (waypoints.isEmpty() && !mismaCeldaObjetivo)
                || frame % RECALC_FRAMES == 0;

        if (necesitaRecalculo) {
            targetPx  = objetivo[0];
            targetPy  = objetivo[1];
            waypoints = bfs(yo.obtenerPosX(), yo.obtenerPosY(), targetPx, targetPy, grid);
            wpIdx     = 0;
        }

        return navegar(yo);
    }

    // ── Grid estático: solo paredes ──────────────────────────────────────────

    private void construirBaseGrid(Nivel nivel) {
        nivelW = nivel.obtenerAncho();
        nivelH = nivel.obtenerAlto();
        gW = nivelW / CELL + 2;
        gH = nivelH / CELL + 2;
        baseGrid = new boolean[gW][gH];
        for (Pared p : nivel.getParedes()) {
            int x0 = Math.max(0, p.obtenerPosX() / CELL);
            int y0 = Math.max(0, p.obtenerPosY() / CELL);
            int x1 = Math.min(gW - 1, (p.obtenerPosX() + p.obtenerAncho()) / CELL);
            int y1 = Math.min(gH - 1, (p.obtenerPosY() + p.obtenerAlto())  / CELL);
            for (int x = x0; x <= x1; x++)
                for (int y = y0; y <= y1; y++)
                    baseGrid[x][y] = true;
        }
    }

    // ── Grid dinámico: paredes + margen alrededor de enemigos ────────────────

    private boolean[][] gridConEnemigos(Nivel nivel) {
        boolean[][] g = new boolean[gW][gH];
        for (int x = 0; x < gW; x++)
            g[x] = Arrays.copyOf(baseGrid[x], gH);
        for (Enemigo e : nivel.getEnemigos()) {
            int ex = e.obtenerPosX() / CELL;
            int ey = e.obtenerPosY() / CELL;
            for (int dx = -MARGEN_ENEMIGO; dx <= MARGEN_ENEMIGO + 1; dx++)
                for (int dy = -MARGEN_ENEMIGO; dy <= MARGEN_ENEMIGO + 1; dy++) {
                    int nx = ex + dx, ny = ey + dy;
                    if (nx >= 0 && ny >= 0 && nx < gW && ny < gH)
                        g[nx][ny] = true;
                }
        }
        return g;
    }

    // ── Objetivo: moneda más cercana, luego ZonaInicial ──────────────────────

    private int[] encontrarObjetivo(Nivel nivel, Jugador yo) {
        int bestDist = Integer.MAX_VALUE;
        int[] tgt = null;
        for (Moneda m : nivel.getMonedas()) {
            if (!m.estaRecolectada()) {
                int cx = m.obtenerPosX() + m.obtenerAncho() / 2;
                int cy = m.obtenerPosY() + m.obtenerAlto()  / 2;
                int dist = Math.abs(cx - yo.obtenerPosX()) + Math.abs(cy - yo.obtenerPosY());
                if (dist < bestDist) { bestDist = dist; tgt = new int[]{cx, cy}; }
            }
        }
        if (tgt != null) return tgt;
        for (Zona z : nivel.getZonas()) {
            if (z instanceof ZonaInicial) {
                return new int[]{
                    z.obtenerPosX() + z.obtenerAncho() / 2,
                    z.obtenerPosY() + z.obtenerAlto()  / 2
                };
            }
        }
        return null;
    }

    // ── Navegación por waypoints ─────────────────────────────────────────────

    private Direction navegar(Jugador yo) {
        while (wpIdx < waypoints.size()) {
            int[] wp = waypoints.get(wpIdx);
            int cx = wp[0] * CELL + CELL / 2;
            int cy = wp[1] * CELL + CELL / 2;
            int dx = cx - yo.obtenerPosX();
            int dy = cy - yo.obtenerPosY();
            if (Math.abs(dx) < UMBRAL && Math.abs(dy) < UMBRAL) { wpIdx++; continue; }
            return dirFromDelta(dx, dy);
        }
        // Waypoints agotados o BFS misma celda: ajuste fino directo al objetivo en píxeles
        if (targetPx != Integer.MIN_VALUE) {
            int dx = targetPx - yo.obtenerPosX();
            int dy = targetPy - yo.obtenerPosY();
            if (Math.abs(dx) > 3 || Math.abs(dy) > 3) return dirFromDelta(dx, dy);
        }
        return Direction.QUIETO;
    }

    private Direction dirFromDelta(int dx, int dy) {
        if (Math.abs(dy) >= Math.abs(dx)) {
            return dy < 0 ? Direction.NORTE : Direction.SUR;
        }
        return dx > 0 ? Direction.ESTE : Direction.OESTE;
    }

    // ── BFS con grid dinámico; fallback a solo paredes si no hay ruta ────────

    private List<int[]> bfs(int startPx, int startPy, int endPx, int endPy, boolean[][] grid) {
        List<int[]> ruta = bfsGrid(startPx, startPy, endPx, endPy, grid);
        if (!ruta.isEmpty()) return ruta;
        return bfsGrid(startPx, startPy, endPx, endPy, baseGrid);
    }

    private List<int[]> bfsGrid(int startPx, int startPy, int endPx, int endPy, boolean[][] grid) {
        int sgx = clampX(startPx / CELL), sgy = clampY(startPy / CELL);
        int egx = clampX(endPx   / CELL), egy = clampY(endPy   / CELL);
        if (sgx == egx && sgy == egy) return Collections.emptyList();

        int[] from = new int[gW * gH];
        Arrays.fill(from, -1);
        int startIdx = sgy * gW + sgx;
        from[startIdx] = startIdx;

        int[] dxArr = {0, 0, 1, -1};
        int[] dyArr = {-1, 1, 0, 0};

        Queue<Integer> q = new LinkedList<>();
        q.add(startIdx);
        int endIdx = -1;

        outer:
        while (!q.isEmpty()) {
            int cur = q.poll();
            int cx = cur % gW, cy = cur / gW;
            for (int d = 0; d < 4; d++) {
                int nx = cx + dxArr[d], ny = cy + dyArr[d];
                if (nx < 0 || ny < 0 || nx >= gW || ny >= gH) continue;
                if (grid[nx][ny]) continue;
                int nIdx = ny * gW + nx;
                if (from[nIdx] != -1) continue;
                from[nIdx] = cur;
                if (nx == egx && ny == egy) { endIdx = nIdx; break outer; }
                q.add(nIdx);
            }
        }

        if (endIdx == -1) return Collections.emptyList();

        List<int[]> path = new ArrayList<>();
        int cur = endIdx;
        while (from[cur] != cur) {
            path.add(0, new int[]{cur % gW, cur / gW});
            cur = from[cur];
        }
        return path;
    }

    private int clampX(int x) { return Math.max(0, Math.min(gW - 1, x)); }
    private int clampY(int y) { return Math.max(0, Math.min(gH - 1, y)); }
}
