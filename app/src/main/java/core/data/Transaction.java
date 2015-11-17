package core.data;

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
        q = q.replaceAll()

        return null;
    }
}
