package core.async;


public interface SqlTaskCallback {
  void executed(int result);
  void failed(String message);
}
