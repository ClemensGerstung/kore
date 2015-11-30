package core.data;

public class Transaction {
  private String query;
  private String[] args;

  public Transaction(String query, String... args) {
    this.query = query;
    this.args = args;
  }

}
