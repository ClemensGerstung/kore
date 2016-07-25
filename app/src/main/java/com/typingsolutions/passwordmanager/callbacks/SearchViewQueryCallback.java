package com.typingsolutions.passwordmanager.callbacks;

import android.support.v7.widget.SearchView;
import com.typingsolutions.passwordmanager.BaseCallback;
import com.typingsolutions.passwordmanager.activities.PasswordOverviewActivity;

public class SearchViewQueryCallback extends BaseCallback<PasswordOverviewActivity>
    implements SearchView.OnQueryTextListener {
  public SearchViewQueryCallback(PasswordOverviewActivity activity) {
    super(activity);
  }

  @Override
  public void setValues(Object... values) {

  }

  @Override
  public boolean onQueryTextSubmit(String query) {
    return false;
  }

  @Override
  public boolean onQueryTextChange(String newText) {
    mActivity.search(newText);

    return true;
  }
}
