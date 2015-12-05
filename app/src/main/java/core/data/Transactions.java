package core.data;

import android.content.Context;
import core.DatabaseProvider;
import core.Dictionary;

import java.util.ArrayList;
import java.util.List;

public class Transactions {
  private List<Transaction> transactions;
  private int passwordId = -1;
  private int historyId = -1;

  public Transactions() {
    transactions = new ArrayList<>();
  }

  public Transaction addTransaction(String query, Object... params) {
    Transaction transaction = new Transaction(Transaction.Mode.Add, query, params);
    transactions.add(transaction);

    return transaction;
  }


  public int commitAllTransactions(Context context) {
    DatabaseProvider provider = DatabaseProvider.getConnection(context);

    for (Transaction trans : transactions) {
      switch (trans.getMode()) {
        case Add:
          provider.insert(trans.getQuery(), trans.getArgs());
          break;
      }
    }

    return transactions.size();
  }

  public Transaction redoLastTransaction() {
    return transactions.remove(transactions.size() - 1);
  }

  public void logout() {
    transactions.clear();
    transactions = null;
  }

  public void setIdsFromPassword(Password password) {
    if (password.getId() > passwordId)
      passwordId = password.getId();

    for (Integer i : password.getPasswordIds()) {
      if (i > historyId)
        historyId = i;
    }
  }

  public int getNewPasswordId() {
    passwordId += 1;
    return passwordId;
  }

  public int getNewHistoryId() {
    historyId += 1;
    return historyId;
  }
}
