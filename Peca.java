import java.util.List;

public abstract class Peca {
    private boolean corBranca;
    private int x;
    private int y;

    public Peca(boolean corBranca, int x, int y) {
        this.corBranca = corBranca;
        this.x = x;
        this.y = y;
    }

    // Removed duplicate constructor

    public boolean isCorBranca() {
        return corBranca;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    protected boolean dentroDoTabuleiro(int x, int y) {
        return x >= 0 && x < 8 && y >= 0 && y < 8;
    }

    protected boolean caminhoLivre(int x1, int y1, int x2, int y2, Peca[][] tabuleiro) {
        int dx = Integer.compare(x2, x1);
        int dy = Integer.compare(y2, y1);
        
        int x = x1 + dx;
        int y = y1 + dy;
        
        while (x != x2 || y != y2) {
            if (tabuleiro[y][x] != null) {
                return false;
            }
            x += dx;
            y += dy;
        }
        
        return true;
    }

    public abstract boolean podeMover(int x2, int y2, Peca[][] tabuleiro);

    public List<int[]> obterMovimentosValidos(Peca[][] tabuleiro) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'obterMovimentosValidos'");
    }
}