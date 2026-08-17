import java.util.Scanner;
public class JogoKlondike {
    private Baralho baralho;
    private MonteDeCompra monteDeCompra;
    private MonteDeDescarte monteDeDescarte;
    private ListaManual<Fundacao> fundacoes;
    private ListaManual<ColunaTabuleiro> colunasTabuleiro;
    private Scanner scanner;

    public JogoKlondike() {
        baralho = new Baralho();

        fundacoes = new ListaManual<>();
        for (Naipe n : Naipe.values()) {
            fundacoes.adicionar(new Fundacao(n));
        }

        colunasTabuleiro = new ListaManual<>();
        for (int i = 0; i < 7; i++) {
            colunasTabuleiro.adicionar(new ColunaTabuleiro());
        }

        distribuirCartas();

        monteDeDescarte = new MonteDeDescarte();
        scanner = new Scanner(System.in);
    }

    private void distribuirCartas() {
        for (int i = 0; i < 7; i++) {
            for (int j = i; j < 7; j++) {
                Carta carta = baralho.comprarCarta();
                if (carta != null) {
                    if (i == j) {
                        carta.virar();
                    }
                    colunasTabuleiro.get(j).adicionarCartaNaDistribuicao(carta);
                }
            }
        }
        monteDeCompra = new MonteDeCompra(baralho.getCartasRestantes());
    }

    public void mostrarJogo() {
        System.out.println("\n========================================");
        System.out.println("        Paciência Klondike");
        System.out.println("========================================");

        String compraStr = monteDeCompra.estaVazia() ? "[ ]" : "[C]";
        String descarteStr = monteDeDescarte.verTopo() == null ? "[ ]" : monteDeDescarte.verTopo().toString();
        System.out.println("Compra: " + compraStr + " (" + monteDeCompra.tamanho() + ")    Descarte: " + descarteStr);

        System.out.print("Fundações: ");
        for (int i = 0; i < fundacoes.tamanho(); i++) {
            System.out.print("F" + i + ":" + fundacoes.get(i).toString() + "  ");
        }
        System.out.println("\n----------------------------------------");

        System.out.println("Tabuleiro:");
        for (int i = 0; i < colunasTabuleiro.tamanho(); i++) {
            System.out.println("T" + i + ": " + colunasTabuleiro.get(i).toString());
        }
        System.out.println("========================================");
    }

    public boolean verificarVitoria() {
        int totalNasFundacoes = 0;
        for (Fundacao f : fundacoes) {
            totalNasFundacoes += f.tamanho();
        }
        return totalNasFundacoes == 52;
    }

    private void comprarDoMonte() {
        if (!monteDeCompra.estaVazia()) {
            Carta comprada = monteDeCompra.comprar();
            monteDeDescarte.adicionarCarta(comprada);
            System.out.println("Comprou " + comprada + " para o descarte.");
        } else if (!monteDeDescarte.estaVazia()) {
            monteDeCompra.receberCartasDoDescarte(monteDeDescarte.getTodasCartasParaReciclar());
            System.out.println("Descarte reciclado para o monte de compra.");
        } else {
            System.out.println("Monte de compra e descarte vazios.");
        }
    }

    private void moverDescarteParaFundacao(int idxFundacao) {
        if (idxFundacao < 0 || idxFundacao >= fundacoes.tamanho()) {
            System.out.println("Índice de fundação inválido."); return;
        }
        if (monteDeDescarte.estaVazia()) {
            System.out.println("Descarte vazio."); return;
        }

        Carta carta = monteDeDescarte.verTopo();
        if (fundacoes.get(idxFundacao).adicionarCarta(carta)) {
            monteDeDescarte.removerTopo();
            System.out.println("Moveu " + carta + " do descarte para Fundação F" + idxFundacao + ".");
        } else {
            System.out.println("Não pode mover " + carta + " para Fundação F" + idxFundacao + ".");
        }
    }

    private void moverDescarteParaTabuleiro(int idxColuna) {
        if (idxColuna < 0 || idxColuna >= colunasTabuleiro.tamanho()) {
            System.out.println("Índice de coluna inválido."); return;
        }
        if (monteDeDescarte.estaVazia()) {
            System.out.println("Descarte vazio."); return;
        }

        Carta carta = monteDeDescarte.verTopo();
        ListaManual<Carta> sequencia = new ListaManual<>();
        sequencia.adicionar(carta);

        if (colunasTabuleiro.get(idxColuna).adicionarSequencia(sequencia)) {
            monteDeDescarte.removerTopo();
            System.out.println("Moveu " + carta + " do descarte para Tabuleiro T" + idxColuna + ".");
        } else {
            System.out.println("Não pode mover " + carta + " para Tabuleiro T" + idxColuna + ".");
        }
    }

    private void moverTabuleiroParaFundacao(int idxColuna, int idxFundacao) {
        if (idxColuna < 0 || idxColuna >= colunasTabuleiro.tamanho() ||
                idxFundacao < 0 || idxFundacao >= fundacoes.tamanho()) {
            System.out.println("Índice de coluna ou fundação inválido."); return;
        }
        ColunaTabuleiro coluna = colunasTabuleiro.get(idxColuna);
        if (coluna.estaVazia()) {
            System.out.println("Coluna T" + idxColuna + " está vazia."); return;
        }

        Carta carta = coluna.verTopo();
        if (fundacoes.get(idxFundacao).adicionarCarta(carta)) {
            coluna.removerCartasDoTopo(1);
            System.out.println("Moveu " + carta + " de T" + idxColuna + " para Fundação F" + idxFundacao + ".");
        } else {
            System.out.println("Não pode mover " + carta + " de T" + idxColuna + " para Fundação F" + idxFundacao + ".");
        }
    }

    private void moverTabuleiroParaTabuleiro(int idxOrigem, int numCartas, int idxDestino) {
        if (idxOrigem < 0 || idxOrigem >= colunasTabuleiro.tamanho() ||
                idxDestino < 0 || idxDestino >= colunasTabuleiro.tamanho() ||
                idxOrigem == idxDestino) {
            System.out.println("Índices de coluna inválidos ou iguais."); return;
        }
        if (numCartas <= 0) {
            System.out.println("Número de cartas para mover deve ser positivo."); return;
        }

        ColunaTabuleiro origem = colunasTabuleiro.get(idxOrigem);
        ColunaTabuleiro destino = colunasTabuleiro.get(idxDestino);

        ListaManual<Carta> sequencia = origem.getSequenciaParaMover(numCartas);

        if (sequencia == null) {
            System.out.println("Não é possível mover " + numCartas + " carta(s) da coluna T" + idxOrigem + " (verifique se estão viradas e formam sequência).");
            return;
        }

        if (destino.adicionarSequencia(sequencia)) {
            origem.removerCartasDoTopo(numCartas);
            System.out.println("Moveu " + numCartas + " carta(s) de T" + idxOrigem + " para T" + idxDestino + ".");
        } else {
            System.out.println("Não pode mover a sequência de T" + idxOrigem + " para T" + idxDestino + ".");
        }
    }

    private void moverFundacaoParaTabuleiro(int idxFundacao, int idxColuna) {
        if (idxFundacao < 0 || idxFundacao >= fundacoes.tamanho() ||
                idxColuna < 0 || idxColuna >= colunasTabuleiro.tamanho()) {
            System.out.println("Índice de fundação ou coluna inválido."); return;
        }
        Fundacao fundacao = fundacoes.get(idxFundacao);
        if (fundacao.estaVazia()) {
            System.out.println("Fundação F" + idxFundacao + " está vazia."); return;
        }

        Carta carta = fundacao.verTopo();
        ListaManual<Carta> sequencia = new ListaManual<>();
        sequencia.adicionar(carta);

        if (colunasTabuleiro.get(idxColuna).adicionarSequencia(sequencia)) {
            fundacao.removerTopo();
            System.out.println("Moveu " + carta + " da Fundação F" + idxFundacao + " para Tabuleiro T" + idxColuna + ".");
        } else {
            System.out.println("Não pode mover " + carta + " da Fundação F" + idxFundacao + " para Tabuleiro T" + idxColuna + ".");
        }
    }

    public void jogar() {
        String entrada;
        while (true) {
            mostrarJogo();
            if (verificarVitoria()) {
                System.out.println("Parabéns! Você venceu!");
                break;
            }

            System.out.println("Ação (ex: 'comprar', 'df 0' (descarte->F0), 'dt 3' (descarte->T3),");
            System.out.println("'tf 2 1' (T2->F1), 'tt 0 1 2' (T0, 1 carta -> T2), 'ft 0 3' (F0->T3), 'sair'): ");
            entrada = scanner.nextLine().trim().toLowerCase();
            String[] partes = entrada.split("\\s+");

            if (partes.length == 0) continue;

            String comando = partes[0];

            try {
                switch (comando) {
                    case "sair":
                        System.out.println("Jogo terminado.");
                        scanner.close();
                        return;
                    case "comprar":
                        comprarDoMonte();
                        break;
                    case "df":
                        if (partes.length > 1) moverDescarteParaFundacao(Integer.parseInt(partes[1]));
                        else System.out.println("Uso: df <idx_fundacao>");
                        break;
                    case "dt":
                        if (partes.length > 1) moverDescarteParaTabuleiro(Integer.parseInt(partes[1]));
                        else System.out.println("Uso: dt <idx_coluna>");
                        break;
                    case "tf":
                        if (partes.length > 2) moverTabuleiroParaFundacao(Integer.parseInt(partes[1]), Integer.parseInt(partes[2]));
                        else System.out.println("Uso: tf <idx_col_orig> <idx_fund_dest>");
                        break;
                    case "tt":
                        if (partes.length > 3) moverTabuleiroParaTabuleiro(Integer.parseInt(partes[1]), Integer.parseInt(partes[2]), Integer.parseInt(partes[3]));
                        else System.out.println("Uso: tt <idx_col_orig> <num_cartas> <idx_col_dest>");
                        break;
                    case "ft":
                        if (partes.length > 2) moverFundacaoParaTabuleiro(Integer.parseInt(partes[1]), Integer.parseInt(partes[2]));
                        else System.out.println("Uso: ft <idx_fund_orig> <idx_col_dest>");
                        break;
                    default:
                        System.out.println("Comando inválido. Tente novamente.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Argumento inválido. Índices e número de cartas devem ser números.");
            } catch (Exception e) {
                System.out.println("Ocorreu um erro inesperado: " + e.getMessage());
            }
        }
        scanner.close();
    }

    public static void main(String[] args) {
        JogoKlondike jogo = new JogoKlondike();
        jogo.jogar();
    }
}