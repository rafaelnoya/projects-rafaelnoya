import java.util.ArrayList;
import java.util.Scanner;

// Classe principal do jogo. Controla o menu, as acoes do jogador e o fluxo de rodadas ate vitoria/derrota.
public class Jogo {

    private static final int LINHAS = 5;
    private static final int COLUNAS = 10;

    private CampoBatalha campo;
    private Nave nave;
    private Scanner sc;

    private ArrayList<EstatisticaAlgoritmo> historico;
    private int rodada;
    private int invasoresEliminados;
    private boolean jogoAtivo;

    public Jogo() {
        this.campo = new CampoBatalha(LINHAS, COLUNAS);
        this.nave = new Nave(LINHAS);
        this.sc = new Scanner(System.in);
        this.historico = new ArrayList<EstatisticaAlgoritmo>();
        this.rodada = 1;
        this.invasoresEliminados = 0;
        this.jogoAtivo = true;
    }

    public void iniciar() {
        System.out.println("SPACE INVADERS - ALGORITHMIC DEFENSE");
        System.out.println("A Terra esta sendo atacada por alienigenas");
        System.out.println("Use os módulos de ordenação.\n");
        pausar();

        campo.gerarInvasores();

        // loop principal do jogo
        while (jogoAtivo) {
            verificarFimDeJogo();
            if (!jogoAtivo) break;

            exibirCabecalho();
            campo.exibir(nave.getLinhaAtual());
            exibirMenu();

            int op = lerOpcao();
            processarOpcao(op);
        }
    }

    private void exibirCabecalho() {
        System.out.println("Rodada: " + rodada
                + " | Invasores eliminados: " + invasoresEliminados
                + " | Linha atual: " + (nave.getLinhaAtual() + 1));
        System.out.println("Invasores no campo: " + campo.contarInvasoresRecursivo(0, 0));
    }

    private void exibirMenu() {
        System.out.println("O que deseja fazer?");
        System.out.println("1) Subir linha");
        System.out.println("2) Descer linha");
        System.out.println("3) Usar modulo especial (ordenar linha atual)");
        System.out.println("4) Atacar linha atual");
        System.out.println("5) Proxima rodada");
        System.out.println("6) Mostrar estatisticas");
        System.out.println("0) Sair");
        System.out.print("Opcao: ");
    }

    private int lerOpcao() {
        int op = sc.nextInt();
        sc.nextLine(); // consome o enter que sobra depois do nextInt
        return op;
    }

    private void processarOpcao(int op) {
        System.out.println();
        switch (op) {
            case 1:
                if (nave.subirLinha()) {
                    System.out.println("Nave foi para a linha " + (nave.getLinhaAtual() + 1));
                }
                break;
            case 2:
                if (nave.descerLinha()) {
                    System.out.println("Nave foi para a linha " + (nave.getLinhaAtual() + 1));
                }
                break;
            case 3:
                usarModuloEspecial();
                proximaRodada();
                break;
            case 4:
                atacar();
                proximaRodada();
                break;
            case 5:
                proximaRodada();
                break;
            case 6:
                mostrarEstatisticas();
                break;
            case 0:
                System.out.println("Encerrando partida...");
                jogoAtivo = false;
                relatorioFinal(false);
                break;
            default:
                System.out.println("Opcao invalida.");
        }

        if (op != 0) pausar();
    }

    private void usarModuloEspecial() {
        nave.exibirModulos();

        if (nave.todosModulosUsados()) {
            System.out.println("Todos os modulos ja foram utilizados nesta partida.");
        } else {
            System.out.print("Escolha o modulo (1-6) ou 0 para cancelar: ");
            int escolha = lerOpcao();

            if (escolha == 0) {
                System.out.println("Cancelado.");
            } else {
                int idx = escolha - 1;

                if (idx < 0 || idx >= 6) {
                    System.out.println("Modulo invalido.");
                } else if (!nave.moduloDisponivel(idx)) {
                    System.out.println("Esse modulo ja foi usado.");
                } else {
                    int linhaAtual = nave.getLinhaAtual();
                    int[] linha = campo.getLinha(linhaAtual);

                    // verifica se a linha tem invasor
                    boolean vazia = true;
                    for (int x : linha) {
                        if (x > 0) {
                            vazia = false;
                        }
                    }

                    if (vazia) {
                        System.out.println("Nenhum invasor nessa linha, o modulo nao foi aplicado.");
                    } else {
                        EstatisticaAlgoritmo stat = aplicarOrdenacao(idx, linha, linhaAtual + 1);

                        if (stat != null) {
                            nave.marcarModuloUsado(idx);
                            historico.add(stat);
                            System.out.println("Modulo '" + Nave.NOMES_MODULOS[idx] + "' ativado na linha " + (linhaAtual + 1) + "!");
                            System.out.println(stat);
                        }
                    }
                }
            }
        }
    }

    // chama o algoritmo certo conforme o indice do modulo escolhido
    private EstatisticaAlgoritmo aplicarOrdenacao(int idx, int[] linha, int numLinha) {
        switch (idx) {
            case 0:
                return Ordenadores.selectionSort(linha, numLinha);
            case 1:
                return Ordenadores.insertionSort(linha, numLinha);
            case 2:
                return Ordenadores.bubbleSort(linha, numLinha);
            case 3:
                return Ordenadores.mergeSort(linha, numLinha);
            case 4:
                return Ordenadores.quickSort(linha, numLinha);
            case 5:
                return Ordenadores.heapSort(linha, numLinha);
            default:
                return null;
        }
    }

    private void atacar() {
        System.out.println("Atacando linha " + (nave.getLinhaAtual() + 1) + "...");
        boolean eliminou = campo.atacarLinha(nave.getLinhaAtual());
        if (eliminou) {
            invasoresEliminados++;
            System.out.println("Total de invasores eliminados: " + invasoresEliminados);
        }
    }

    private void proximaRodada() {
        System.out.println("Avancando para a proxima rodada...");
        campo.avancarInvasores();
        campo.gerarInvasores();
        rodada++;
        System.out.println("Rodada " + rodada + " iniciada!");
    }

    private void verificarFimDeJogo() {
        if (campo.estaCheio()) {
            System.out.println("\nDERROTA! O campo foi completamente tomado pelos invasores.");
            jogoAtivo = false;
            relatorioFinal(false);
        } else if (campo.estaVazio() && rodada > 1) {
            System.out.println("\nVITORIA! Todos os invasores foram eliminados!");
            jogoAtivo = false;
            relatorioFinal(true);
        }
    }

    private void mostrarEstatisticas() {
        System.out.println("\nEstatisticas dos modulos utilizados");
        if (historico.size() == 0) {
            System.out.println("Nenhum modulo foi utilizado ainda.");
        } else {
            for (int i = 0; i < historico.size(); i++) {
                System.out.println((i + 1) + ") " + historico.get(i));
            }
        }
        System.out.println();
    }

    private void relatorioFinal(boolean vitoria) {
        System.out.println("\nRELATORIO FINAL");
        if (vitoria) {
            System.out.println("Resultado: VITORIA");
        } else {
            System.out.println("Resultado: DERROTA");
        }
        System.out.println("Rodadas sobrevividas: " + rodada);
        System.out.println("Invasores eliminados: " + invasoresEliminados);
        System.out.println("Invasores restantes: " + campo.contarInvasoresRecursivo(0, 0));

        System.out.println("\nRanking de desempenho dos modulos");
        if (historico.size() == 0) {
            System.out.println("Nenhum modulo foi utilizado.");
        } else {
            // copia o historico pra nao alterar a ordem original
            ArrayList<EstatisticaAlgoritmo> ranking = new ArrayList<EstatisticaAlgoritmo>();
            for (int i = 0; i < historico.size(); i++) {
                ranking.add(historico.get(i));
            }
            ordenarRankingPorComparacoes(ranking);

            for (int i = 0; i < ranking.size(); i++) {
                EstatisticaAlgoritmo e = ranking.get(i);
                System.out.println((i + 1) + ") " + e.getNomeAlgoritmo()
                        + " | Comparacoes: " + e.getComparacoes()
                        + " | Trocas: " + e.getTrocas()
                        + " | Tempo: " + e.getTempoMs() + " ms");
            }
        }

        System.out.println("\nFim de jogo!");
    }

    // ordena o ranking por comparacoes usando insertion sort
    private void ordenarRankingPorComparacoes(ArrayList<EstatisticaAlgoritmo> lista) {
        int n = lista.size();
        for (int i = 1; i < n; i++) {
            EstatisticaAlgoritmo chave = lista.get(i);
            int j = i - 1;
            while (j >= 0 && lista.get(j).getComparacoes() > chave.getComparacoes()) {
                lista.set(j + 1, lista.get(j));
                j--;
            }
            lista.set(j + 1, chave);
        }
    }

    private void pausar() {
        System.out.println("(Pressione Enter para continuar...)");
        sc.nextLine();
    }
}