package com.typingsolutions.passwordmanager;

import android.content.res.ColorStateList;
import android.support.annotation.Nullable;
import com.typingsolutions.passwordmanager.activities.LoginActivity;
import net.sqlcipher.database.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseDatabaseActivity extends BaseActivity {
  protected static BaseDatabaseConnection connection;
  private static List<IContainer> items = new ArrayList<>();
  private static List<IListChangedListener<IContainer>> itemsChangedListener = new ArrayList<>();

  public static boolean logout = true;

  public static void useEstablishedConnection(BaseDatabaseConnection connection) {
    BaseDatabaseActivity.connection = (BaseDatabaseConnection) connection.clone();
  }

  @Nullable
  public static SQLiteDatabase getDatabase() {
    return connection != null ? connection.getDatabase() : null;
  }

  public void registerListChangedListener(IListChangedListener<IContainer> listener) {
    itemsChangedListener.add(listener);
  }

  public void unregisterChangedListener(IListChangedListener<IContainer> listener) {
    itemsChangedListener.remove(listener);
  }

  protected void clearChangeListener() {
    itemsChangedListener.clear();
  }

  public void addContainerItem(IContainer container) {
    items.add(container);
    for (IListChangedListener<IContainer> listener : itemsChangedListener) {
      listener.onItemAdded(items.size() - 1, container);
    }
  }

  public void removeContainerItem(IContainer container) {
    int index = items.indexOf(container);
    items.remove(index);
    for (IListChangedListener<IContainer> listener : itemsChangedListener) {
      listener.onItemRemoved(index, container.clone());
    }
  }

  public void changeContainerItem(int index, IContainer container) {
    IContainer old = items.get(index);
    items.set(index, container);
    for (IListChangedListener<IContainer> listener : itemsChangedListener) {
      listener.onItemChanged(index, old.clone(), container);
    }
  }

  public int containerCount() {
    return items.size();
  }

  public IContainer getContainerAt(int index) {
    return items.get(index);
  }

  public void clearContainer() {
    items.clear();
  }

  @Override
  protected void onResume() {
    super.onResume();

    if (connection == null && logout) {
      this.startActivity(LoginActivity.class, true);
    }
  }

  @Override
  protected void onStop() {
    if (logout && connection != null) {
      connection.close();
      connection = null;
    }
    super.onStop();
  }
}
