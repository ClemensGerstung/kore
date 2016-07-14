package core.async;


public interface ISqlTaskCallback<TResult> {
  void executed(TResult result);
  void failed(String message);
}
