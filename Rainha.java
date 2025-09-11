public class Rainha extends Peca {
    public Rainha(boolean corBranca, int x, int y) {
        super(corBranca, x, y);
    }

    @Override
    public boolean podeMover(int x2, int y2, Peca[][] tabuleiro) {
        // Implementação específica da Rainha
        int dx = Math.abs(x2 - getX());
        int dy = Math.abs(y2 - getY());
        return (getX() == x2 || getY() == y2 || dx == dy);
    }
}