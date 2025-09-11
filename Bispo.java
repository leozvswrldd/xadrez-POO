import java.util.ArrayList;
import java.util.List;

public class Bispo extends Peca {
    public Bispo(boolean corBranca, int x, int y) {
        super(corBranca, x, y);
    }

    @Override
    public boolean podeMover(int x2, int y2, Peca[][] tabuleiro) {
        // Verifica se a posição está dentro do tabuleiro
        if (!dentroDoTabuleiro(x2, y2)) return false;
        
        // Verifica se a posição de destino não tem uma peça da mesma cor
        if (tabuleiro[y2][x2] != null && tabuleiro[y2][x2].isCorBranca() == this.isCorBranca()) {
            return false;
        }
        
        int dx = Math.abs(x2 - getX());
        int dy = Math.abs(y2 - getY());
        
        // Movimento apenas na diagonal
        if (dx == dy) {
            // Verifica se o caminho está livre
            return caminhoLivre(getX(), getY(), x2, y2, tabuleiro);
        }
        
        return false;
    }

    @Override
    public List<int[]> obterMovimentosValidos(Peca[][] tabuleiro) {
        List<int[]> movimentos = new ArrayList<>();
        
        // Direções do bispo (diagonais)
        int[] dx = {-1, 1, -1, 1};
        int[] dy = {-1, -1, 1, 1};
        
        for (int i = 0; i < 4; i++) {
            int passo = 1;
            while (true) {
                int novoX = getX() + dx[i] * passo;
                int novoY = getY() + dy[i] * passo;
                
                if (!dentroDoTabuleiro(novoX, novoY)) break;
                
                if (tabuleiro[novoY][novoX] == null) {
                    movimentos.add(new int[]{novoX, novoY});
                } else {
                    if (tabuleiro[novoY][novoX].isCorBranca() != this.isCorBranca()) {
                        movimentos.add(new int[]{novoX, novoY});
                    }
                    break;
                }
                
                passo++;
            }
        }
        
        return movimentos;
    }
}