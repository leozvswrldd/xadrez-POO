import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;

/**
 * Representa um ponto (coordenada) no tabuleiro.
 * Usamos uma classe própria para não depender de java.awt.Point na lógica do jogo.
 */
class Ponto {
    int linha;
    int coluna;

    Ponto(int linha, int coluna) {
        this.linha = linha;
        this.coluna = coluna;
    }
}

/**
 * Representa um movimento de uma origem para um destino.
 */
class Movimento {
    Ponto origem;
    Ponto destino;

    Movimento(Ponto origem, Ponto destino) {
        this.origem = origem;
        this.destino = destino;
    }
}


/**
 * Classe principal que cria a interface gráfica e contém toda a lógica do jogo e da IA.
 */
public class Interface extends JFrame {

    // --- Parte Gráfica (View) ---
    private final JPanel painelTabuleiro;
    private final JButton[][] casas = new JButton[8][8];
    private final JLabel statusLabel;

    // --- Lógica do Jogo (Model) ---
    private char[][] tabuleiro = new char[8][8];
    private boolean turnoDoBranco = true;
    private Ponto pecaSelecionada = null;

    // Constantes para as peças (Unicode)
    private static final char REI_B = '♔';
    private static final char RAINHA_B = '♕';
    private static final char TORRE_B = '♖';
    private static final char BISPO_B = '♗';
    private static final char CAVALO_B = '♘';
    private static final char PEAO_B = '♙';
    private static final char REI_P = '♚';
    private static final char RAINHA_P = '♛';
    private static final char TORRE_P = '♜';
    private static final char BISPO_P = '♝';
    private static final char CAVALO_P = '♞';
    private static final char PEAO_P = '♟';
    private static final char VAZIO = ' ';
    
    // Profundidade da busca da IA. Aumentar deixa a IA mais forte, mas mais lenta.
    private static final int PROFUNDIDADE_IA = 3;


    public Interface() {
        setTitle("Jogo de Xadrez Completo");
        setSize(700, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        painelTabuleiro = new JPanel(new GridLayout(8, 8));
        statusLabel = new JLabel("Turno das Brancas. Selecione uma peça.", SwingConstants.CENTER);
        
        inicializarCasas();
        add(painelTabuleiro, BorderLayout.CENTER);
        add(statusLabel, BorderLayout.SOUTH);

        iniciarNovoJogo();
    }

    /**
     * Inicializa os botões (casas) do tabuleiro e adiciona os listeners de clique.
     */
    private void inicializarCasas() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                casas[i][j] = new JButton();
                casas[i][j].setFont(new Font("Serif", Font.PLAIN, 50));
                
                final int linha = i;
                final int coluna = j;
                
                casas[i][j].addActionListener(e -> aoClicarNaCasa(linha, coluna));
                painelTabuleiro.add(casas[i][j]);
            }
        }
    }
    
    /**
     * Configura o tabuleiro para o estado inicial de um jogo de xadrez.
     */
    private void iniciarNovoJogo() {
        tabuleiro = new char[][]{
            {TORRE_P, CAVALO_P, BISPO_P, RAINHA_P, REI_P, BISPO_P, CAVALO_P, TORRE_P},
            {PEAO_P, PEAO_P, PEAO_P, PEAO_P, PEAO_P, PEAO_P, PEAO_P, PEAO_P},
            {VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO},
            {VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO},
            {VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO},
            {VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO},
            {PEAO_B, PEAO_B, PEAO_B, PEAO_B, PEAO_B, PEAO_B, PEAO_B, PEAO_B},
            {TORRE_B, CAVALO_B, BISPO_B, RAINHA_B, REI_B, BISPO_B, CAVALO_B, TORRE_B}
        };
        turnoDoBranco = true;
        pecaSelecionada = null;
        atualizarInterface();
        statusLabel.setText("Turno das Brancas. Selecione uma peça.");
    }

    /**
     * Redesenha o tabuleiro na interface gráfica com base no estado do tabuleiro lógico.
     */
    private void atualizarInterface() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                casas[i][j].setText(String.valueOf(tabuleiro[i][j]));
                if ((i + j) % 2 == 0) {
                    casas[i][j].setBackground(new Color(238, 238, 210)); // Cor clara
                } else {
                    casas[i][j].setBackground(new Color(118, 150, 86)); // Cor escura
                }
            }
        }
    }

    /**
     * Lógica principal que é executada quando o usuário clica em uma casa.
     */
    private void aoClicarNaCasa(int linha, int coluna) {
        if (!turnoDoBranco) return; // Se não for turno do jogador, não faz nada

        if (pecaSelecionada == null) {
            // Primeiro clique: Tentando selecionar uma peça
            if (tabuleiro[linha][coluna] != VAZIO && ehPecaDoJogador(tabuleiro[linha][coluna], true)) {
                pecaSelecionada = new Ponto(linha, coluna);
                statusLabel.setText("Peça selecionada. Escolha o destino.");
                casas[linha][coluna].setBackground(Color.YELLOW); // Destaque visual
            }
        } else {
            // Segundo clique: Tentando mover a peça selecionada
            if (ehMovimentoValido(pecaSelecionada, new Ponto(linha, coluna))) {
                moverPeca(pecaSelecionada, new Ponto(linha, coluna));
                turnoDoBranco = false;
                pecaSelecionada = null;
                atualizarInterface();
                statusLabel.setText("Turno das Pretas (IA). Pensando...");
                
                // Dispara a jogada da IA em uma nova thread para não travar a interface
                new SwingWorker<Void, Void>() {
                    @Override
                    protected Void doInBackground() throws Exception {
                        Thread.sleep(500); // Pequena pausa para dar a sensação de "pensamento"
                        executarJogadaIA();
                        return null;
                    }
                }.execute();

            } else {
                // Movimento inválido ou clique na mesma peça para desmarcar
                pecaSelecionada = null;
                atualizarInterface();
                statusLabel.setText("Movimento inválido. Selecione uma peça.");
            }
        }
    }

    /**
     * Move a peça no tabuleiro lógico.
     */
    private void moverPeca(Ponto origem, Ponto destino) {
        tabuleiro[destino.linha][destino.coluna] = tabuleiro[origem.linha][origem.coluna];
        tabuleiro[origem.linha][origem.coluna] = VAZIO;
    }

    /**
     * Verifica se um movimento é válido (validação simplificada).
     */
    private boolean ehMovimentoValido(Ponto origem, Ponto destino) {
        char peca = tabuleiro[origem.linha][origem.coluna];
        char pecaDestino = tabuleiro[destino.linha][destino.coluna];
        
        // Não pode capturar uma peça da mesma cor
        if (pecaDestino != VAZIO && ehPecaDoJogador(peca, ehBranca(peca)) == ehPecaDoJogador(pecaDestino, ehBranca(peca))) {
            return false;
        }
        
        // Lógica de movimento simplificada (não inclui todas as regras)
        // Adicionar regras mais complexas aqui (peão, cavalo, etc.)
        return true;
    }

    private boolean ehPecaDoJogador(char peca, boolean ehBranco) {
        if (ehBranco) {
            return "♔♕♖♗♘♙".indexOf(peca) != -1;
        } else {
            return "♚♛♜♝♞♟".indexOf(peca) != -1;
        }
    }

    private boolean ehBranca(char peca) {
        return "♔♕♖♗♘♙".indexOf(peca) != -1;
    }

    // --- Inteligência Artificial (Minimax) ---

    /**
     * Ponto de entrada para a jogada da IA.
     */
    private void executarJogadaIA() {
        Movimento melhorMovimento = encontrarMelhorMovimento(PROFUNDIDADE_IA);
        if (melhorMovimento != null) {
            moverPeca(melhorMovimento.origem, melhorMovimento.destino);
            turnoDoBranco = true;
            SwingUtilities.invokeLater(() -> {
                atualizarInterface();
                statusLabel.setText("Turno das Brancas. Selecione uma peça.");
            });
        } else {
             SwingUtilities.invokeLater(() -> statusLabel.setText("Fim de Jogo ou empate!"));
        }
    }

    /**
     * Encontra o melhor movimento para a IA usando o algoritmo Minimax.
     */
    private Movimento encontrarMelhorMovimento(int profundidade) {
        Movimento melhorMovimento = null;
        int melhorValor = Integer.MIN_VALUE;

        List<Movimento> movimentos = gerarMovimentosPossiveis(true); // Gerar movimentos para as Pretas (IA)

        for (Movimento movimento : movimentos) {
            // Simula o movimento
            char pecaCapturada = tabuleiro[movimento.destino.linha][movimento.destino.coluna];
            tabuleiro[movimento.destino.linha][movimento.destino.coluna] = tabuleiro[movimento.origem.linha][movimento.origem.coluna];
            tabuleiro[movimento.origem.linha][movimento.origem.coluna] = VAZIO;

            int valorDoMovimento = minimax(profundidade - 1, false); // Próximo turno é do minimizador (Brancas)

            // Desfaz o movimento
            tabuleiro[movimento.origem.linha][movimento.origem.coluna] = tabuleiro[movimento.destino.linha][movimento.destino.coluna];
            tabuleiro[movimento.destino.linha][movimento.destino.coluna] = pecaCapturada;
            
            if (valorDoMovimento > melhorValor) {
                melhorValor = valorDoMovimento;
                melhorMovimento = movimento;
            }
        }
        return melhorMovimento;
    }

    /**
     * Implementação do algoritmo Minimax.
     * Retorna o valor da melhor jogada a partir da posição atual.
     */
    private int minimax(int profundidade, boolean maximizando) {
        if (profundidade == 0) {
            return avaliarTabuleiro();
        }

        if (maximizando) { // Turno da IA (Pretas)
            int melhorValor = Integer.MIN_VALUE;
            for (Movimento movimento : gerarMovimentosPossiveis(true)) {
                // Simula e desfaz o movimento para explorar a árvore
                char pecaCapturada = tabuleiro[movimento.destino.linha][movimento.destino.coluna];
                tabuleiro[movimento.destino.linha][movimento.destino.coluna] = tabuleiro[movimento.origem.linha][movimento.origem.coluna];
                tabuleiro[movimento.origem.linha][movimento.origem.coluna] = VAZIO;
                
                melhorValor = Math.max(melhorValor, minimax(profundidade - 1, false));

                tabuleiro[movimento.origem.linha][movimento.origem.coluna] = tabuleiro[movimento.destino.linha][movimento.destino.coluna];
                tabuleiro[movimento.destino.linha][movimento.destino.coluna] = pecaCapturada;
            }
            return melhorValor;
        } else { // Turno do Jogador (Brancas)
            int piorValor = Integer.MAX_VALUE;
            for (Movimento movimento : gerarMovimentosPossiveis(false)) {
                 // Simula e desfaz o movimento para explorar a árvore
                char pecaCapturada = tabuleiro[movimento.destino.linha][movimento.destino.coluna];
                tabuleiro[movimento.destino.linha][movimento.destino.coluna] = tabuleiro[movimento.origem.linha][movimento.origem.coluna];
                tabuleiro[movimento.origem.linha][movimento.origem.coluna] = VAZIO;

                piorValor = Math.min(piorValor, minimax(profundidade - 1, true));

                tabuleiro[movimento.origem.linha][movimento.origem.coluna] = tabuleiro[movimento.destino.linha][movimento.destino.coluna];
                tabuleiro[movimento.destino.linha][movimento.destino.coluna] = pecaCapturada;
            }
            return piorValor;
        }
    }
    
    /**
     * Gera uma lista de todos os movimentos pseudo-legais para um jogador.
     * Esta é uma implementação muito simplificada.
     */
    private List<Movimento> gerarMovimentosPossiveis(boolean paraPretas) {
        List<Movimento> movimentos = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                char peca = tabuleiro[i][j];
                if (peca != VAZIO && ehBranca(peca) != paraPretas) {
                     // Lógica de geração de movimentos super simplificada:
                     // Qualquer peça pode se mover uma casa em qualquer direção se estiver vazia ou for inimiga.
                     // Um jogo real precisaria de regras específicas por peça.
                    for (int di = -1; di <= 1; di++) {
                        for (int dj = -1; dj <= 1; dj++) {
                            if (di == 0 && dj == 0) continue;
                            int ni = i + di;
                            int nj = j + dj;
                            if (ni >= 0 && ni < 8 && nj >= 0 && nj < 8) {
                                Ponto origem = new Ponto(i, j);
                                Ponto destino = new Ponto(ni, nj);
                                if (ehMovimentoValido(origem, destino)) {
                                    movimentos.add(new Movimento(origem, destino));
                                }
                            }
                        }
                    }
                }
            }
        }
        return movimentos;
    }


    /**
     * Função de avaliação simples baseada no material.
     * Positivo favorece as Pretas (IA), negativo favorece as Brancas.
     */
    private int avaliarTabuleiro() {
        int pontuacao = 0;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                pontuacao += getValorPeca(tabuleiro[i][j]);
            }
        }
        return pontuacao;
    }

    private int getValorPeca(char peca) {
        switch (peca) {
            case PEAO_B: return -10;
            case CAVALO_B: return -30;
            case BISPO_B: return -30;
            case TORRE_B: return -50;
            case RAINHA_B: return -90;
            case REI_B: return -900;
            
            case PEAO_P: return 10;
            case CAVALO_P: return 30;
            case BISPO_P: return 30;
            case TORRE_P: return 50;
            case RAINHA_P: return 90;
            case REI_P: return 900;
            
            default: return 0;
        }
    }


    /**
     * Ponto de entrada da aplicação.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Interface().setVisible(true));
    }
}