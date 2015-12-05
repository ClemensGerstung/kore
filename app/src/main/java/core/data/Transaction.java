package core.data;

public class Transaction {
  private String query;
  private String[] args;
  private Mode mode;

  public Transaction(Mode mode, String query, String... args) {
    this.mode = mode;
    this.query = query;
    this.args = args;
  }

  public String getQuery() {
    return query;
  }

  public String[] getArgs() {
    return args;
  }

  public Mode getMode() {
    return mode;
  }

  public enum Mode {
    Add, Update, Delete
  }
}
