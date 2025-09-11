import java.util.ArrayList;
import java.util.List;

public class Cavalo extends Peca {
    public Cavalo(boolean corBranca, int x, int y) {
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
        
        // Movimento em L do cavalo
        return (dx == 2 && dy == 1) || (dx == 1 && dy == 2);
    }

    @Override
    public List<int[]> obterMovimentosValidos(Peca[][] tabuleiro) {
        List<int[]> movimentos = new ArrayList<>();
        
        // Todos os movimentos possíveis do cavalo (em L)
        int[] dx = {-2, -1, 1, 2, -2, -1, 1, 2};
        int[] dy = {-1, -2, -2, -1, 1, 2, 2, 1};
        
        for (int i = 0; i < 8; i++) {
            int novoX = getX() + dx[i];
            int novoY = getY() + dy[i];
            
            if (podeMover(novoX, novoY, tabuleiro)) {
                movimentos.add(new int[]{novoX, novoY});
            }
        }
        
        return movimentos;
    }
}