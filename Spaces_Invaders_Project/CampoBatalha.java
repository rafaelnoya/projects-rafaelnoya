import java.util.Random;

// Campo de batalha
public class CampoBatalha {

    private int[][] matriz;
    private int linhas;
    private int colunas;
    private Random random;

    public CampoBatalha(int linhas, int colunas) {
        this.linhas = linhas;
        this.colunas = colunas;
        this.matriz = new int[linhas][colunas];
        this.random = new Random();
    }

    // Mostra o campo no console, destacando a linha em que a nave esta
    public void exibir(int linhaNave) {
        System.out.println();
        for (int i = 0; i < linhas; i++) {
            if (i == linhaNave) {
                System.out.print("  Nave >> ");
            } else {
                System.out.print("         ");
            }
            System.out.print("[ ");
            for (int j = 0; j < colunas; j++) {
                System.out.printf("%3d", matriz[i][j]);
                if (j < colunas - 1) System.out.print(", ");
            }
            System.out.println(" ]");
        }
        System.out.println();
    }

    // A cada rodada, gera invasores na ultima coluna
    public boolean gerarInvasores() {
        boolean gerou = false;
        for (int i = 0; i < linhas; i++) {
            if (random.nextInt(100) < 70) {
                if (matriz[i][colunas - 1] == 0) {
                    int energia = 10 + random.nextInt(90);
                    matriz[i][colunas - 1] = energia;
                    gerou = true;
                }
            }
        }
        return gerou;
    }

    // Move todos os invasores uma coluna para a esquerda
    public void avancarInvasores() {
        for (int i = 0; i < linhas; i++) {
            for (int j = 0; j < colunas - 1; j++) {
                if (matriz[i][j] == 0 && matriz[i][j + 1] > 0) {
                    matriz[i][j] = matriz[i][j + 1];
                    matriz[i][j + 1] = 0;
                }
            }
        }
    }

    // Campo totalmente preenchido = derrota
    public boolean estaCheio() {
        for (int i = 0; i < linhas; i++) {
            for (int j = 0; j < colunas; j++) {
                if (matriz[i][j] == 0) return false;
            }
        }
        return true;
    }

    // Todos os invasores eliminados = vitoria
    public boolean estaVazio() {
        for (int i = 0; i < linhas; i++) {
            for (int j = 0; j < colunas; j++) {
                if (matriz[i][j] > 0) return false;
            }
        }
        return true;
    }

    // Ataca o invasor mais a frente na linha
    public boolean atacarLinha(int linhaAtaque) {
        int pos = localizarPrimeiroInvasorRecursivo(linhaAtaque, 0);

        if (pos == -1) {
            System.out.println("Nao tem invasor nessa linha para atacar.");
            return false;
        }

        matriz[linhaAtaque][pos] -= 5;

        if (matriz[linhaAtaque][pos] <= 0) {
            System.out.println("Invasor da coluna " + pos + " eliminado!");
            matriz[linhaAtaque][pos] = 0;
            return true;
        } else {
            System.out.println("Invasor da coluna " + pos + " atingido. Energia restante: " + matriz[linhaAtaque][pos]);
            return false;
        }
    }

    // Percorre uma linha procurando o primeiro invasor
    public int localizarPrimeiroInvasorRecursivo(int linha, int coluna) {
        if (coluna >= colunas) return -1; // caso base: acabou a linha
        if (matriz[linha][coluna] > 0) return coluna;
        return localizarPrimeiroInvasorRecursivo(linha, coluna + 1);
    }

    // Conta quantos invasores ainda existem no campo inteiro
    public int contarInvasoresRecursivo(int linha, int coluna) {
        if (linha >= linhas) return 0; // caso base: passou da ultima linha
        if (coluna >= colunas) return contarInvasoresRecursivo(linha + 1, 0);

        if (matriz[linha][coluna] > 0) {
            return 1 + contarInvasoresRecursivo(linha, coluna + 1);
        } else {
            return contarInvasoresRecursivo(linha, coluna + 1);
        }
    }

    // Retorna a linha da matriz
    public int[] getLinha(int linha) {
        return matriz[linha];
    }

    public int getLinhas() {
        return linhas;
    }

    public int getColunas() {
        return colunas;
    }
}