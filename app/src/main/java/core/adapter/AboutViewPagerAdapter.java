package core.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.ListFragment;
import android.support.v4.content.ContextCompat;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.DynamicDrawableSpan;
import android.text.style.ImageSpan;
import com.typingsolutions.passwordmanager.R;
import com.typingsolutions.passwordmanager.fragments.AboutFragment;
import com.typingsolutions.passwordmanager.fragments.LicenseFragment;

public class AboutViewPagerAdapter extends FragmentPagerAdapter {

  private Context context;
  private Fragment[] contents = new Fragment[2];
  private Drawable icon;

  public AboutViewPagerAdapter(Context context, FragmentManager fragmentManager) {
    super(fragmentManager);
    this.context = context;
    contents[1] = new LicenseFragment();
    contents[0] = new AboutFragment();
  }

  @Override
  public Fragment getItem(int position) {
    return contents[position];
  }

  @Override
  public CharSequence getPageTitle(int position) {
    return "";
  }



  @Override
  public int getCount() {
    return 2;
  }
}
