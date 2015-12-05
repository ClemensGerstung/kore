package core.data;

public class Transaction {
  private String query;
  private Object[] args;
  private Mode mode;

  public Transaction(Mode mode, String query, Object... args) {
    this.mode = mode;
    this.query = query;
    this.args = args;
  }

  public String getQuery() {
    return query;
  }

  public Object[] getArgs() {
    return args;
  }

  public Mode getMode() {
    return mode;
  }

  public enum Mode {
    Add, Update, Delete
  }
}
