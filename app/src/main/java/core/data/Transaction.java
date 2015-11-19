package core.data;

import android.database.DatabaseUtils;
import core.Dictionary;

public class Transaction {
    private String query;
    private Dictionary<String, String> parameters;

    public Transaction(String query, Dictionary<String, String> parameters) {
        this.query = query;
        this.parameters = parameters;
    }

    public String getTranslatedQuery() {
        String q = query;
        Dictionary<String, String> tmp = new Dictionary<>(parameters);
        for (Dictionary.Element element : tmp) {
            if (!q.contains(element.getKey().toString())) continue;
            String value = (String) element.getValue();
            value = DatabaseUtils.sqlEscapeString(value);
            q = q.replaceAll((String) element.getKey(), value);
            tmp.removeByKey((String) element.getKey());
        }
        return q;
    }
}
