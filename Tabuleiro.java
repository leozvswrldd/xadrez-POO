import java.util.ArrayList;
import java.util.List;

public class Tabuleiro {
    private Peca[][] casas;
    private boolean vezBrancas;
    private Peca ultimaPecaMovida;
    private int[] ultimoMovimento;

    public Tabuleiro() {
        casas = new Peca[8][8];
        vezBrancas = true;
        inicializarTabuleiro();
    }

    private void inicializarTabuleiro() {
        // Peças brancas
        casas[0][0] = new Torre(true, 0, 0);
        casas[0][1] = new Cavalo(true, 1, 0);
        casas[0][2] = new Bispo(true, 2, 0);
        casas[0][3] = new Rainha(true, 3, 0);
        casas[0][4] = new Rei(true, 4, 0);
        casas[0][5] = new Bispo(true, 5, 0);
        casas[0][6] = new Cavalo(true, 6, 0);
        casas[0][7] = new Torre(true, 7, 0);
        
        for (int i = 0; i < 8; i++) {
            casas[1][i] = new Peao(true, i, 1);
        }
        
        // Peças pretas
        casas[7][0] = new Torre(false, 0, 7);
        casas[7][1] = new Cavalo(false, 1, 7);
        casas[7][2] = new Bispo(false, 2, 7);
        casas[7][3] = new Rainha(false, 3, 7);
        casas[7][4] = new Rei(false, 4, 7);
        casas[7][5] = new Bispo(false, 5, 7);
        casas[7][6] = new Cavalo(false, 6, 7);
        casas[7][7] = new Torre(false, 7, 7);
        
        for (int i = 0; i < 8; i++) {
            casas[6][i] = new Peao(false, i, 6);
        }
    }

    public boolean moverPeca(int x1, int y1, int x2, int y2) {
        Peca peca = casas[y1][x1];
        
        if (peca == null || peca.isCorBranca() != vezBrancas) {
            return false;
        }
        
        if (!peca.podeMover(x2, y2, casas)) {
            return false;
        }
        
        // Simular movimento para verificar se não deixa o rei em cheque
        Peca pecaCapturada = casas[y2][x2];
        casas[y2][x2] = peca;
        casas[y1][x1] = null;
        
        boolean emCheque = estaEmCheque(vezBrancas);
        
        // Desfazer movimento simulado
        casas[y1][x1] = peca;
        casas[y2][x2] = pecaCapturada;
        
        if (emCheque) {
            return false;
        }
        
        // Executar movimento real
        casas[y2][x2] = peca;
        casas[y1][x1] = null;
        peca.setX(x2);
        peca.setY(y2);
        
        // Promoção do peão
        if (peca instanceof Peao && (y2 == 0 || y2 == 7)) {
            casas[y2][x2] = new Rainha(peca.isCorBranca(), x2, y2);
        }
        
        ultimaPecaMovida = peca;
        ultimoMovimento = new int[]{x1, y1, x2, y2};
        vezBrancas = !vezBrancas;
        
        return true;
    }

    public boolean estaEmCheque(boolean corBranca) {
        int reiX = -1, reiY = -1;
        
        // Encontrar o rei
        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                Peca peca = casas[y][x];
                if (peca instanceof Rei && peca.isCorBranca() == corBranca) {
                    reiX = x;
                    reiY = y;
                    break;
                }
            }
        }
        
        // Verificar se alguma peça adversária pode capturar o rei
        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                Peca peca = casas[y][x];
                if (peca != null && peca.isCorBranca() != corBranca) {
                    if (peca.podeMover(reiX, reiY, casas)) {
                        return true;
                    }
                }
            }
        }
        
        return false;
    }

    public boolean estaEmChequeMate(boolean corBranca) {
        if (!estaEmCheque(corBranca)) {
            return false;
        }
        
        // Verificar se existe algum movimento que tire do cheque
        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                Peca peca = casas[y][x];
                if (peca != null && peca.isCorBranca() == corBranca) {
                    List<int[]> movimentos = new ArrayList<>();
                    
                    // Obter movimentos válidos (simplificado)
                    for (int y2 = 0; y2 < 8; y2++) {
                        for (int x2 = 0; x2 < 8; x2++) {
                            if (peca.podeMover(x2, y2, casas)) {
                                movimentos.add(new int[]{x2, y2});
                            }
                        }
                    }
                    
                    for (int[] movimento : movimentos) {
                        int x2 = movimento[0];
                        int y2 = movimento[1];
                        
                        // Simular movimento
                        Peca temp = casas[y2][x2];
                        casas[y2][x2] = peca;
                        casas[y][x] = null;
                        
                        boolean aindaEmCheque = estaEmCheque(corBranca);
                        
                        // Desfazer simulação
                        casas[y][x] = peca;
                        casas[y2][x2] = temp;
                        
                        if (!aindaEmCheque) {
                            return false;
                        }
                    }
                }
            }
        }
        
        return true;
    }

    public Peca[][] getCasas() {
        return casas;
    }

    public boolean isVezBrancas() {
        return vezBrancas;
    }
}