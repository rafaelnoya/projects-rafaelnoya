import java.util.Map;
import java.util.HashMap;

public class DataRecord {
    private String primaryKey;
    private Map<String, Comparable> fields;

    public DataRecord(String primaryKey) {
        this.primaryKey = primaryKey;
        this.fields = new HashMap<>();
        this.fields.put("id", primaryKey);
    }

    public void put(String fieldName, Comparable value) {
        this.fields.put(fieldName, value);
    }

    public Comparable get(String fieldName) {
        return this.fields.get(fieldName);
    }

    public String getPrimaryKey() {
        return this.primaryKey;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        boolean first = true;
        for (Map.Entry<String, Comparable> entry : fields.entrySet()) {
            if (!first) {
                sb.append(", ");
            }
            sb.append(entry.getKey()).append("=").append(entry.getValue());
            first = false;
        }
        sb.append("]");
        return sb.toString();
    }
}