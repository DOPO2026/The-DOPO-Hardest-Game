package domain.ai;

import domain.collectibles.Moneda;
import domain.core.Nivel;
import domain.player.ControlJugador;
import domain.player.Direction;
import domain.player.Jugador;
import domain.world.Pared;
import domain.world.Zona;
import domain.world.ZonaInicial;

import java.util.*;

/**
 * IA experta: usa BFS para calcular la ruta óptima hacia las monedas
 * y luego hacia la ZonaInicial (su zona destino como jugador 1).
 */
public class MaquinaExperta implements ControlJugador {

    private static final int CELL  = 20; // píxeles por celda de la cuadrícula
    private static final int UMBRAL = CELL;

    private boolean[][] bloqueado;
    private int gW, gH;
    private int nivelW = -1, nivelH = -1;

    private List<int[]> waypoints = new ArrayList<>();
    private int wpIdx = 0;
    private int targetPx = Integer.MIN_VALUE, targetPy = Integer.MIN_VALUE;

    @Override
    public Direction decidirMovimiento(Nivel nivel) {
        List<Jugador> jugadores = nivel.getJugadores();
        if (jugadores.size() < 2) return Direction.QUIETO;
        Jugador yo = jugadores.get(1);

        if (nivelW != nivel.obtenerAncho() || nivelH != nivel.obtenerAlto()) {
            construirGrid(nivel);
        }

        int[] objetivo = encontrarObjetivo(nivel, yo);
        if (objetivo == null) return Direction.QUIETO;

        if (objetivo[0] != targetPx || objetivo[1] != targetPy || waypoints.isEmpty()) {
            targetPx = objetivo[0];
            targetPy = objetivo[1];
            waypoints = bfs(yo.obtenerPosX(), yo.obtenerPosY(), targetPx, targetPy);
            wpIdx = 0;
        }

        return navegar(yo);
    }

    // ── Navegación por waypoints ─────────────────────────────────────────────

    private Direction navegar(Jugador yo) {
        while (wpIdx < waypoints.size()) {
            int[] wp = waypoints.get(wpIdx);
            int cx = wp[0] * CELL + CELL / 2;
            int cy = wp[1] * CELL + CELL / 2;
            int dx = cx - yo.obtenerPosX();
            int dy = cy - yo.obtenerPosY();
            if (Math.abs(dx) < UMBRAL && Math.abs(dy) < UMBRAL) {
                wpIdx++;
                continue;
            }
            return dirFromDelta(dx, dy);
        }
        return Direction.QUIETO;
    }

    private Direction dirFromDelta(int dx, int dy) {
        boolean r = dx > 2, l = dx < -2, u = dy < -2, d = dy > 2;
        if (u) return Direction.NORTE;
        if (d) return Direction.SUR;
        if (r) return Direction.ESTE;
        if (l) return Direction.OESTE;
        return Direction.QUIETO;
    }

    // ── Cuadrícula de colisión ───────────────────────────────────────────────

    private void construirGrid(Nivel nivel) {
        nivelW = nivel.obtenerAncho();
        nivelH = nivel.obtenerAlto();
        gW = nivelW / CELL + 2;
        gH = nivelH / CELL + 2;
        bloqueado = new boolean[gW][gH];
        for (Pared p : nivel.getParedes()) {
            int x0 = Math.max(0, p.obtenerPosX() / CELL);
            int y0 = Math.max(0, p.obtenerPosY() / CELL);
            int x1 = Math.min(gW - 1, (p.obtenerPosX() + p.obtenerAncho()) / CELL);
            int y1 = Math.min(gH - 1, (p.obtenerPosY() + p.obtenerAlto())  / CELL);
            for (int x = x0; x <= x1; x++)
                for (int y = y0; y <= y1; y++)
                    bloqueado[x][y] = true;
        }
    }

    // ── Objetivo: moneda más cercana, luego ZonaInicial ──────────────────────

    private int[] encontrarObjetivo(Nivel nivel, Jugador yo) {
        double best = Double.MAX_VALUE;
        int[] tgt = null;
        for (Moneda m : nivel.getMonedas()) {
            if (!m.estaRecolectada()) {
                int cx = m.obtenerPosX() + m.obtenerAncho() / 2;
                int cy = m.obtenerPosY() + m.obtenerAlto()  / 2;
                double d = Math.hypot(cx - yo.obtenerPosX(), cy - yo.obtenerPosY());
                if (d < best) { best = d; tgt = new int[]{cx, cy}; }
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

    // ── BFS en cuadrícula ────────────────────────────────────────────────────

    private List<int[]> bfs(int startPx, int startPy, int endPx, int endPy) {
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
                if (bloqueado[nx][ny]) continue;
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
