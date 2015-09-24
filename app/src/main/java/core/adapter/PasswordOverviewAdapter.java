package core.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
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

public class PasswordOverviewAdapter extends RecyclerView.Adapter<PasswordOverviewAdapter.ViewHolder> {

    private Context context;
    private LayoutInflater inflater;

    public PasswordOverviewAdapter(Context context) {
        super();
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    private PasswordProvider getProvider() {
        int userId = UserProvider.getInstance(context).getId();
        PasswordProvider provider = PasswordProvider.getInstance(context, userId);
        return provider;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
        View view = inflater.inflate(R.layout.password_list_item_layout, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        Password password = getProvider().get(position);
        PasswordHistory history = password.getPasswordHistory().get(0);

        viewHolder.password.setText(history.getValue());
        viewHolder.username.setText(password.getUsername());
        viewHolder.program.setText(password.getProgram());
    }

    @Override
    public int getItemCount() {
        return getProvider().size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        final TextView program;
        final TextView username;
        final TextView password;

        public ViewHolder(View itemView) {
            super(itemView);
            program = (TextView) itemView.findViewById(R.id.passwordlistitemlayout_textview_program);
            username = (TextView) itemView.findViewById(R.id.passwordlistitemlayout_textview_username);
            password = (TextView) itemView.findViewById(R.id.passwordlistitemlayout_textview_password);
        }
    }
}
