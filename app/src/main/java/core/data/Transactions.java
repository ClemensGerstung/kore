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

  public Transaction addTransaction(String query, Dictionary.Element... params) {
    Dictionary<String, String> dic = new Dictionary<>();
    for (Dictionary.Element elem : params) {
      dic.addLast((String) elem.getKey(), (String) elem.getValue());
    }

    Transaction transaction = null;
    transactions.add(transaction);

    return transaction;
  }

  public int commitAllTransactions(Context context, String password) {

    for (Transaction trans : transactions) {

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
