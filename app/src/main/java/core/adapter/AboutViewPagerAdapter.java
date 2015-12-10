package core.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.ListFragment;
import android.support.v4.content.ContextCompat;
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

  public AboutViewPagerAdapter(Context context, FragmentManager fragmentManager) {
    super(fragmentManager);
    this.context = context;
    contents[0] = new LicenseFragment();
    contents[1] = new AboutFragment();
  }

  @Override
  public Fragment getItem(int position) {
    return contents[0];
  }

  @Override
  public CharSequence getPageTitle(int position) {
    SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
    Drawable icon = position == 0
        ? ContextCompat.getDrawable(context, R.mipmap.info)
        : ContextCompat.getDrawable(context, R.mipmap.copyright);
    icon.setBounds(5, 5, icon.getIntrinsicWidth(), icon.getIntrinsicHeight());
    ImageSpan span = new ImageSpan(icon, DynamicDrawableSpan.ALIGN_BASELINE);

    spannableStringBuilder.setSpan(span, 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    return super.getPageTitle(position);
  }

  @Override
  public int getCount() {
    return 2;
  }
}
