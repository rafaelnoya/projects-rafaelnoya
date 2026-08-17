import java.util.Scanner;
import java.util.List;
import java.util.Random;
import java.util.ArrayList;

public class DatabaseSimulator {

    private StorageModule storage;
    private IndexManager indexManager;

    public DatabaseSimulator() {
        this.storage = new StorageModule();
        this.indexManager = new IndexManager(this.storage);
    }

    public void run() {
        Scanner scanner = new Scanner(System.in);
        String command;

        System.out.println("--- Simulador de Indice de Banco de Dados ---");
        System.out.println("Comandos: load, create-index, select-no-index, select-with-index, insert, delete, benchmark, status, exit");

        while (true) {
            System.out.print("> ");
            command = scanner.nextLine();
            String[] parts = command.split("\\s+");
            if (parts.length == 0) continue;

            String action = parts[0].toLowerCase();

            try {
                switch (action) {
                    case "load":
                        handleLoad(parts);
                        break;
                    case "create-index":
                        handleCreateIndex(parts);
                        break;
                    case "select-no-index":
                        handleSelectNoIndex(parts);
                        break;
                    case "select-with-index":
                        handleSelectWithIndex(parts);
                        break;
                    case "insert":
                        handleInsert(command);
                        break;
                    case "delete":
                        handleDelete(parts);
                        break;
                    case "benchmark":
                        handleBenchmark();
                        break;
                    case "status":
                        System.out.println("[INFO] " + indexManager.getIndexStatus());
                        System.out.println("[INFO] Total de registros: " + storage.getRecordCount());
                        break;
                    case "exit":
                        System.out.println("Encerrando...");
                        scanner.close();
                        return;
                    default:
                        System.out.println("[ERRO] Comando desconhecido: " + action);
                }
            } catch (Exception e) {
                System.out.println("[ERRO] Falha ao executar comando: " + e.getMessage());
            }
        }
    }

    private void handleLoad(String[] parts) {
        if (parts.length < 2) {
            System.out.println("[ERRO] Uso: load <arquivo.csv>");
            return;
        }
        long startTime = System.nanoTime();
        int count = CSVLoader.load(parts[1], this.storage);
        long endTime = System.nanoTime();
        if (count >= 0) {
            System.out.printf("[OK] %d registros carregados. Tempo: %d µs\n", count, (endTime - startTime) / 1000);
        }
    }

    private void handleCreateIndex(String[] parts) {
        if (parts.length < 4) {
            System.out.println("[ERRO] Uso: create-index <btree|bplustree> <ordem> <campo>");
            return;
        }
        String type = parts[1];
        int order = Integer.parseInt(parts[2]);
        String field = parts[3];

        long startTime = System.nanoTime();
        boolean success = indexManager.createIndex(field, type, order);
        long endTime = System.nanoTime();

        if (success) {
            System.out.printf("[OK] Indice criado. Tempo: %d µs\n", (endTime - startTime) / 1000);
            System.out.println("[INFO] " + indexManager.getIndexStatus());
        } else {
            System.out.println("[ERRO] Nao foi possivel criar o indice. Verifique o tipo e o campo.");
        }
    }

    private void handleSelectNoIndex(String[] parts) {
        if (parts.length < 2) {
            System.out.println("[ERRO] Uso: select-no-index <valor_chave>");
            return;
        }
        String key = parts[1];
        long comparisons = 0;
        DataRecord foundRecord = null;

        long startTime = System.nanoTime();
        List<DataRecord> allRecords = storage.getSequentialView();

        String searchField = indexManager.getIndexedField();
        if (searchField == null) searchField = "id";

        for (DataRecord record : allRecords) {
            comparisons++;
            Object val = record.get(searchField);
            if (val != null && val.toString().equals(key)) {
                foundRecord = record;
                break;
            }
        }
        long endTime = System.nanoTime();

        if (foundRecord != null) {
            System.out.println("[FOUND] " + foundRecord);
        } else {
            System.out.println("[NOT FOUND] Registro com " + searchField + "=" + key + " nao encontrado.");
        }
        System.out.printf("[INFO] Comparações: %d | Tempo: %d µs\n", comparisons, (endTime - startTime) / 1000);
    }

    private void handleSelectWithIndex(String[] parts) {
        if (parts.length < 2) {
            System.out.println("[ERRO] Uso: select-with-index <valor_chave>");
            return;
        }
        if (indexManager.getIndex() == null) {
            System.out.println("[ERRO] Nenhum indice foi criado. Use 'create-index'.");
            return;
        }

        Index<String> index = indexManager.getIndex();
        index.resetComparisons();

        String key = parts[1];

        long startTime = System.nanoTime();
        DataRecord foundRecord = index.search(key);
        long endTime = System.nanoTime();
        long comparisons = index.getComparisons();

        if (foundRecord != null) {
            System.out.println("[FOUND] " + foundRecord);
        } else {
            System.out.println("[NOT FOUND] Chave " + key + " nao encontrada no indice.");
        }
        System.out.printf("[INFO] Comparações: %d | Tempo: %d µs\n", comparisons, (endTime - startTime) / 1000);
    }

    private void handleInsert(String command) {
        String[] parts = command.split(",", 2);
        if (parts.length < 2) {
            System.out.println("[ERRO] Uso: insert <id>, <campo1=valor1>, <campo2=valor2>...");
            return;
        }

        String id = parts[0].substring(parts[0].indexOf(" ") + 1).trim();
        DataRecord newRecord = new DataRecord(id);

        String[] fields = parts[1].split(",");
        for (String field : fields) {
            String[] kv = field.split("=");
            if (kv.length == 2) {
                newRecord.put(kv[0].trim(), kv[1].trim());
            }
        }

        Index<String> index = indexManager.getIndex();
        if (index != null) index.resetComparisons();

        long startTime = System.nanoTime();
        boolean success = indexManager.insert(newRecord);
        long endTime = System.nanoTime();
        long comparisons = (index != null) ? index.getComparisons() : 0;

        if (success) {
            System.out.println("[OK] Registro inserido com sucesso.");
            if (index != null) {
                System.out.printf("[INFO] Comparações (Indice): %d | Altura da árvore: %d\n", comparisons, index.getHeight());
            }
        } else {
            System.out.println("[ERRO] Chave duplicada: id=" + id + " ja existe.");
        }
    }

    private void handleDelete(String[] parts) {
        if (parts.length < 2) {
            System.out.println("[ERRO] Uso: delete <valor_chave_primaria>");
            return;
        }
        String key = parts[1];

        DataRecord deletedRecord = indexManager.delete(key);

        if (deletedRecord != null) {
            System.out.println("[OK] Registro " + key + " removido e indice atualizado.");
        } else {
            System.out.println("[ERRO] Chave primária " + key + " nao encontrada.");
        }
    }

    private void handleBenchmark() {
        int numSearches = 1000;
        List<DataRecord> allRecords = storage.getSequentialView();
        if (allRecords.isEmpty()) {
            System.out.println("[ERRO] Carregue dados (load) antes de executar o benchmark.");
            return;
        }

        String searchField = indexManager.getIndexedField();
        if (searchField == null) searchField = "id";

        System.out.printf("[INFO] Executando %d buscas pelo campo '%s'...\n", numSearches, searchField);

        Random rand = new Random();
        List<String> keysToSearch = new ArrayList<>();
        for(int i = 0; i < numSearches; i++) {
            DataRecord r = allRecords.get(rand.nextInt(allRecords.size()));
            Object val = r.get(searchField);
            if (val != null) {
                keysToSearch.add(val.toString());
            }
        }

        long totalTimeNoIndex = 0;
        long totalCompNoIndex = 0;

        for (String key : keysToSearch) {
            long startTime = System.nanoTime();
            long comparisons = 0;
            for (DataRecord record : allRecords) {
                comparisons++;
                Object val = record.get(searchField);
                if (val != null && val.toString().equals(key)) {
                    break;
                }
            }
            long endTime = System.nanoTime();
            totalTimeNoIndex += (endTime - startTime);
            totalCompNoIndex += comparisons;
        }

        System.out.println("\n--- Benchmark: Busca Sequencial (Sem Indice) ---");
        System.out.printf("[RESULT] Tempo médio de busca: %.2f µs\n", (double)totalTimeNoIndex / numSearches / 1000.0);
        System.out.printf("[RESULT] Média de comparações: %.2f\n", (double)totalCompNoIndex / numSearches);

        Index<String> index = indexManager.getIndex();
        if (index != null) {
            long totalTimeIndex = 0;
            long totalCompIndex = 0;

            for (String keyStr : keysToSearch) {
                index.resetComparisons();
                long startTime = System.nanoTime();
                index.search(keyStr);
                long endTime = System.nanoTime();
                totalTimeIndex += (endTime - startTime);
                totalCompIndex += index.getComparisons();
            }

            System.out.println("\n--- Benchmark: Busca Indexada (" + indexManager.getIndexStatus() + ") ---");
            System.out.printf("[RESULT] Tempo médio de busca: %.2f µs\n", (double)totalTimeIndex / numSearches / 1000.0);
            System.out.printf("[RESULT] Média de comparações: %.2f\n", (double)totalCompIndex / numSearches);
        } else {
            System.out.println("\n[INFO] Nenhum indice criado para o benchmark de busca indexada.");
        }
    }


    public static void main(String[] args) {
        DatabaseSimulator simulator = new DatabaseSimulator();
        simulator.run();
    }
}