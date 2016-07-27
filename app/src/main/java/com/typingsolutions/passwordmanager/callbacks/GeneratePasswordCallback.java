package com.typingsolutions.passwordmanager.callbacks;

import android.view.View;
import android.widget.EditText;
import com.typingsolutions.passwordmanager.BaseActivity;
import com.typingsolutions.passwordmanager.BaseClickCallback;
import core.Utils;

public class GeneratePasswordCallback extends BaseClickCallback {
  private EditText mEditText;

  public GeneratePasswordCallback(BaseActivity activity, EditText editText) {
    super(activity);
    this.mEditText = editText;
  }

  @Override
  public void setValues(Object... values) {

  }

  @Override
  public void onClick(View v) {
    /*final String[] QUERY_PROJECTION = {
        UserDictionary.Words._ID,
        UserDictionary.Words.WORD
    };
    Cursor cursor = context.getContentResolver()
        .query(UserDictionary.Words.CONTENT_URI,
            QUERY_PROJECTION,
            "(locale IS NULL) or (locale=?)",
            new String[] { Locale.getDefault().toString() },
            null);*/

    int minLength = 10;
    int maxLength = 15;
    int lowerCase = 0, upperCase = 0, decimals = 0, specialChars = 0;

    int length = minLength + (int) (Math.random() * (maxLength - minLength));

    char[] array = Utils.AVAILABLE_CHARACTERS.toCharArray();
    StringBuilder builder = new StringBuilder();

    for (int i = 0; i < length; i++) {
      int rand = (int) (Math.random() * array.length);

      if ((lowerCase + upperCase + decimals + specialChars) >= 8) {
        upperCase += rand < 26 ? 1 : 0;
        lowerCase += rand > 25 && rand < 52 ? 1 : 0;
        decimals += rand < 51 && rand < 62 ? 1 : 0;
        specialChars += rand > 61 ? 1 : 0;
      } else {
        boolean exit = true;
        int round = 0;
        do {
          if (upperCase <= 2 && rand < 26) {
            upperCase++;
          } else if (lowerCase <= 2 && rand > 25 && rand < 52) {
            lowerCase++;
          } else if (decimals <= 2 && rand < 51 && rand < 62) {
            decimals++;
          } else if (specialChars <= 2 && rand > 61) {
            specialChars++;
          } else {
            exit = false;
          }
          rand = (int) (Math.random() * array.length);
          round++;
        } while (exit && round < 10);
      }

      builder.append(array[rand]);
    }

    mEditText.setText("");
    mEditText.append(builder.toString());
  }


}
