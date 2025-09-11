import java.util.ArrayList;
import java.util.List;

public class Peao extends Peca {
    public Peao(boolean corBranca, int x, int y) {
        super(corBranca, x, y);
    }

    @Override
    public boolean podeMover(int x2, int y2, Peca[][] tabuleiro) {
        // Verifica se a posição está dentro do tabuleiro
        if (!dentroDoTabuleiro(x2, y2)) return false;
        
        int direcao = isCorBranca() ? 1 : -1;
        int dx = x2 - getX();
        int dy = y2 - getY();
        
        // Movimento para frente
        if (dx == 0) {
            // Movimento normal (1 casa)
            if (dy == direcao && tabuleiro[y2][x2] == null) {
                return true;
            }
            
            // Primeiro movimento (2 casas)
            boolean posicaoInicial = (isCorBranca() && getY() == 1) || (!isCorBranca() && getY() == 6);
            if (posicaoInicial && dy == 2 * direcao && tabuleiro[y2][x2] == null && 
                tabuleiro[getY() + direcao][getX()] == null) {
                return true;
            }
        }
        
        // Captura (movimento diagonal)
        if (Math.abs(dx) == 1 && dy == direcao) {
            // Captura normal
            if (tabuleiro[y2][x2] != null && tabuleiro[y2][x2].isCorBranca() != this.isCorBranca()) {
                return true;
            }
            
            // Captura en passant
            Peca pecaAdjacente = tabuleiro[getY()][x2];
            if (pecaAdjacente instanceof Peao && pecaAdjacente.isCorBranca() != this.isCorBranca()) {
                // Aqui precisaríamos de uma referência ao último movimento para verificar se foi um avanço de duas casas
                // Esta verificação seria feita na classe do tabuleiro/jogo
                return true;
            }
        }
        
        return false;
    }

    @Override
    public List<int[]> obterMovimentosValidos(Peca[][] tabuleiro) {
        List<int[]> movimentos = new ArrayList<>();
        
        int direcao = isCorBranca() ? 1 : -1;
        
        // Movimento para frente (1 casa)
        int novoY = getY() + direcao;
        if (dentroDoTabuleiro(getX(), novoY) && tabuleiro[novoY][getX()] == null) {
            movimentos.add(new int[]{getX(), novoY});
            
            // Movimento para frente (2 casas) - primeiro movimento
            if (isPrimeiroMovimento()) {
                novoY = getY() + 2 * direcao;
                if (dentroDoTabuleiro(getX(), novoY) && tabuleiro[novoY][getX()] == null && 
                    tabuleiro[getY() + direcao][getX()] == null) {
                    movimentos.add(new int[]{getX(), novoY});
                }
            }
        }
        
        // Capturas (diagonais)
        int[] capturasX = {getX() - 1, getX() + 1};
        for (int x : capturasX) {
            novoY = getY() + direcao;
            if (dentroDoTabuleiro(x, novoY) && tabuleiro[novoY][x] != null && 
                tabuleiro[novoY][x].isCorBranca() != this.isCorBranca()) {
                movimentos.add(new int[]{x, novoY});
            }
            
            // Captura en passant (seria verificada na classe do tabuleiro/jogo)
            // Aqui apenas adicionamos a possibilidade se as condições forem atendidas
        }
        
        return movimentos;
    }

    private boolean isPrimeiroMovimento() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'isPrimeiroMovimento'");
    }
}