package core.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CursorAdapter;
import android.widget.TextView;
import com.typingsolutions.passwordmanager.R;
import core.Password;
import core.PasswordHistory;
import core.PasswordProvider;
import core.UserProvider;

import java.util.ArrayList;
import java.util.List;

public class PasswordOverviewAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater inflater;

    public PasswordOverviewAdapter(Context context) {
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    private PasswordProvider getProvider() {
        int userId = UserProvider.getInstance(context).getId();
        PasswordProvider provider = PasswordProvider.getInstance(context, userId);
        return provider;
    }

    @Override
    public int getCount() {
        PasswordProvider provider = getProvider();
        return provider.size();
    }

    @Override
    public Object getItem(int position) {
        PasswordProvider provider = getProvider();
        return provider.get(position);
    }

    @Override
    public long getItemId(int position) {
        PasswordProvider provider = getProvider();
        Password password = provider.get(position);
        return password.getId();
    }

    @Override
    public View getView(int position, View recycledView, ViewGroup parent) {
        View view = recycledView;
        ViewHolder viewHolder;

        if (recycledView == null) {
            view = inflater.inflate(R.layout.password_list_item_layout, parent);
            viewHolder = new ViewHolder();
            viewHolder.program = (TextView) view.findViewById(R.id.passwordlistitemlayout_textview_program);
            viewHolder.username = (TextView) view.findViewById(R.id.passwordlistitemlayout_textview_username);
            viewHolder.password = (TextView) view.findViewById(R.id.passwordlistitemlayout_textview_password);

            view.setTag(ViewHolder.TAG, viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag(ViewHolder.TAG);
        }

        Password password = getProvider().get(position);
        PasswordHistory passwordHistory = password.getPasswordHistory().get(0);

        viewHolder.program.setText(password.getProgram());
        viewHolder.username.setText(password.getUsername());
        viewHolder.program.setText(passwordHistory.getValue());

        return view;
    }

    private class ViewHolder {
        static final int TAG = 0xb17c;

        TextView program;
        TextView username;
        TextView password;
    }
}
