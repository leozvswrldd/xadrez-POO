public class Torre extends Peca {
  
     public Torre(boolean corBranca, int x, int y) {
        super(corBranca, x, y);
    }


    @Override
    public boolean podeMover(int x2, int y2, Peca[][] tabuleiro) {
        if (!dentroDoTabuleiro(x2, y2)) return false;
        
        if (tabuleiro[y2][x2] != null && tabuleiro[y2][x2].isCorBranca() == this.isCorBranca()) {
            return false;
        }
        
        if ((getX() == x2) || (getY() == y2)) {
            return caminhoLivre(getX(), getY(), x2, y2, tabuleiro);
        }
        
        return false;
    }
}
