import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class CSVLoader {

    public static int load(String csvFile, StorageModule storage) {
        String line = "";
        String cvsSplitBy = ",";
        int recordsLoaded = 0;
        String[] headers = null;

        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {

            if ((line = br.readLine()) != null) {
                headers = line.split(cvsSplitBy);
                for (int i = 0; i < headers.length; i++) {
                    headers[i] = headers[i].trim();
                }
            } else {
                return 0;
            }

            while ((line = br.readLine()) != null) {
                String[] data = line.split(cvsSplitBy);
                if (data.length != headers.length) continue;

                String pk = data[0].trim();
                DataRecord record = new DataRecord(pk);

                for(int i = 1; i < headers.length; i++) {
                    record.put(headers[i], data[i].trim());
                }

                storage.insert(record);
                recordsLoaded++;
            }

        } catch (IOException e) {
            System.err.println("[ERRO] Nao foi possivel ler o arquivo: " + e.getMessage());
            return -1;
        }

        return recordsLoaded;
    }
}