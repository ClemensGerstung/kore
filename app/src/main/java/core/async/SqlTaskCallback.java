package core.async;


public interface SqlTaskCallback<TResult> {
  void executed(TResult result);
  void failed(String message);
}
