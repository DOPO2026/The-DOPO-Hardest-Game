package domain.enemy;

public class Acelerado extends Basico {
    public Acelerado(int dx, int dy, int limIzq, int limDer, int limSup, int limInf) {
        super(dx, dy, limIzq, limDer, limSup, limInf, 4);
    }

    @Override
    public String obtenerTipo() { return "ACELERADO"; }
}
