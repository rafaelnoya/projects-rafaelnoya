import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

public class StorageModule {

    private Map<String, DataRecord> mainTable;
    private List<DataRecord> sequentialView;

    public StorageModule() {
        this.mainTable = new HashMap<>();
        this.sequentialView = new ArrayList<>();
    }

    public boolean insert(DataRecord record) {
        if (mainTable.containsKey(record.getPrimaryKey())) {
            return false;
        }
        mainTable.put(record.getPrimaryKey(), record);
        sequentialView.add(record);
        return true;
    }

    public DataRecord delete(String primaryKey) {
        DataRecord record = mainTable.remove(primaryKey);
        if (record != null) {
            sequentialView.remove(record);
        }
        return record;
    }

    public DataRecord findByPrimaryKey(String primaryKey) {
        return mainTable.get(primaryKey);
    }

    public List<DataRecord> getSequentialView() {
        return new ArrayList<>(sequentialView);
    }

    public int getRecordCount() {
        return this.sequentialView.size();
    }
}