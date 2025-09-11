public class Rei extends Peca {
    public Rei(boolean corBranca, int x, int y) {
        super(corBranca, x, y);
    }

    @Override
    public boolean podeMover(int x2, int y2, Peca[][] tabuleiro) {
        // Implementação específica do Rei
        int dx = Math.abs(x2 - getX());
        int dy = Math.abs(y2 - getY());
        return (dx <= 1 && dy <= 1) && (dx > 0 || dy > 0);
    }

    
}