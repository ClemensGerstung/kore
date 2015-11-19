package core.data;

import android.content.Context;
import core.DatabaseProvider;
import core.Dictionary;

import java.util.ArrayList;
import java.util.List;

public class Transactions {
    private List<Transaction> transactions;

    public Transactions() {
        transactions = new ArrayList<>();
    }

    public Transaction addTransaction(String query, Dictionary.Element... params) {
        Dictionary<String, String> dic = new Dictionary<>();
        for (Dictionary.Element elem : params) {
            dic.addLast((String) elem.getKey(), (String) elem.getValue());
        }

        Transaction transaction = new Transaction(query, dic);
        transactions.add(transaction);

        return transaction;
    }

    public int commitAllTransactions(Context context, String password) {
        StringBuilder builder = new StringBuilder();

        for (Transaction trans : transactions) {
            String query = trans.getTranslatedQuery();

            builder.append(query);

            if (!query.endsWith(";"))
                builder.append(";");
        }

        DatabaseProvider.getConnection(context, password).rawQuery(builder.toString());

        return transactions.size();
    }

    public Transaction redoLastTransaction() {
        return transactions.remove(transactions.size() - 1);
    }

    public void logout() {
        transactions.clear();
        transactions = null;
    }
}
