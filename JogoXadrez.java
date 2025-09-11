import java.util.Scanner;

public class JogoXadrez {
    private Tabuleiro tabuleiro;
    private Scanner scanner;
    private boolean jogoAtivo;

    public JogoXadrez() {
        tabuleiro = new Tabuleiro();
        scanner = new Scanner(System.in);
        jogoAtivo = true;
    }

    public void iniciarJogo() {
        System.out.println("=== JOGO DE XADREZ ===");
        System.out.println("Instruções:");
        System.out.println("- Digite as jogadas no formato: e2 e4");
        System.out.println("- Digite 'sair' para encerrar o jogo");
        System.out.println("- Digite 'tabuleiro' para ver o tabuleiro atual");
        System.out.println();

        while (jogoAtivo) {
            exibirTabuleiro();
            executarTurno();
        }
    }

    private void exibirTabuleiro() {
        System.out.println("\n  a b c d e f g h");
        System.out.println("  -----------------");
        
        Peca[][] casas = tabuleiro.getCasas();
        for (int y = 7; y >= 0; y--) {
            System.out.print((y + 1) + "|");
            for (int x = 0; x < 8; x++) {
                Peca peca = casas[y][x];
                if (peca == null) {
                    System.out.print(". ");
                } else {
                    char simbolo = obterSimboloPeca(peca);
                    System.out.print(simbolo + " ");
                }
            }
            System.out.println("|" + (y + 1));
        }
        System.out.println("  -----------------");
        System.out.println("  a b c d e f g h");
        
        // Mostrar de quem é a vez
        String jogador = tabuleiro.isVezBrancas() ? "BRANCAS" : "PRETAS";
        System.out.println("\nVez das: " + jogador);
    }

    private char obterSimboloPeca(Peca peca) {
        char simbolo;
        
        if (peca instanceof Rei) simbolo = 'K';
        else if (peca instanceof Rainha) simbolo = 'Q';
        else if (peca instanceof Torre) simbolo = 'R';
        else if (peca instanceof Bispo) simbolo = 'B';
        else if (peca instanceof Cavalo) simbolo = 'N'; // Knight
        else if (peca instanceof Peao) simbolo = 'P';
        else simbolo = '?';
        
        // Letra minúscula para peças pretas
        return peca.isCorBranca() ? simbolo : Character.toLowerCase(simbolo);
    }

    private void executarTurno() {
        boolean movimentoValido = false;
        
        while (!movimentoValido && jogoAtivo) {
            System.out.print("\nDigite sua jogada (ex: e2 e4): ");
            String input = scanner.nextLine().trim();
            
            if (input.equalsIgnoreCase("sair")) {
                jogoAtivo = false;
                System.out.println("Jogo encerrado!");
                return;
            }
            
            if (input.equalsIgnoreCase("tabuleiro")) {
                exibirTabuleiro();
                continue;
            }
            
            // Converter notação de xadrez para coordenadas numéricas
            int[] coordenadas = converterNotacao(input);
            if (coordenadas == null) {
                System.out.println("Formato inválido! Use: e2 e4");
                continue;
            }
            
            int x1 = coordenadas[0];
            int y1 = coordenadas[1];
            int x2 = coordenadas[2];
            int y2 = coordenadas[3];
            
            // Verificar se há peça na posição inicial
            Peca peca = tabuleiro.getCasas()[y1][x1];
            if (peca == null) {
                System.out.println("Não há peça na posição inicial!");
                continue;
            }
            
            // Verificar se é a peça do jogador atual
            if (peca.isCorBranca() != tabuleiro.isVezBrancas()) {
                System.out.println("Essa peça não é sua!");
                continue;
            }
            
            // Tentar mover a peça
            movimentoValido = tabuleiro.moverPeca(x1, y1, x2, y2);
            
            if (!movimentoValido) {
                System.out.println("Movimento inválido!");
            } else {
                System.out.println("Movimento realizado!");
                verificarFimDeJogo();
            }
        }
    }

    private int[] converterNotacao(String input) {
        // Formato esperado: "e2 e4"
        String[] partes = input.split(" ");
        if (partes.length != 2) {
            return null;
        }
        
        String origem = partes[0].toLowerCase();
        String destino = partes[1].toLowerCase();
        
        if (origem.length() != 2 || destino.length() != 2) {
            return null;
        }
        
        try {
            int x1 = origem.charAt(0) - 'a';
            int y1 = Character.getNumericValue(origem.charAt(1)) - 1;
            int x2 = destino.charAt(0) - 'a';
            int y2 = Character.getNumericValue(destino.charAt(1)) - 1;
            
            // Validar coordenadas
            if (x1 < 0 || x1 > 7 || y1 < 0 || y1 > 7 || 
                x2 < 0 || x2 > 7 || y2 < 0 || y2 > 7) {
                return null;
            }
            
            return new int[]{x1, y1, x2, y2};
        } catch (Exception e) {
            return null;
        }
    }

    private void verificarFimDeJogo() {
        boolean corAtual = tabuleiro.isVezBrancas();
        
        if (tabuleiro.estaEmChequeMate(!corAtual)) {
            String vencedor = corAtual ? "BRANCAS" : "PRETAS";
            System.out.println("=== XEQUE-MATE ===");
            System.out.println("As " + vencedor + " venceram!");
            jogoAtivo = false;
        } else if (tabuleiro.estaEmCheque(!corAtual)) {
            String jogador = !corAtual ? "BRANCAS" : "PRETAS";
            System.out.println("XEQUE! As " + jogador + " estão em cheque!");
        }
    }

    public static void main(String[] args) {
        JogoXadrez jogo = new JogoXadrez();
        jogo.iniciarJogo();
    }
}