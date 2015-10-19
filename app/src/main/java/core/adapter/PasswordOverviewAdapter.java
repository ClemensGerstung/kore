package core.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewManager;
import android.widget.TextView;
import com.typingsolutions.passwordmanager.R;
import com.typingsolutions.passwordmanager.activities.PasswordDetailActivity;
import com.typingsolutions.passwordmanager.fragments.LoginPasswordFragment;
import core.Password;
import core.PasswordHistory;
import core.PasswordProvider;
import core.UserProvider;

import java.util.ArrayList;
import java.util.List;

public class PasswordOverviewAdapter extends RecyclerView.Adapter<PasswordOverviewAdapter.ViewHolder> {

    private Context context;
    private LayoutInflater inflater;
    private List<Password> localPasswords;
    private boolean useFiltered;

    private static final String PASSWORD_FILTER_PREFIX = "pw:";
    private static final String USERNAME_FILTER_PREFIX = "us:";
    private static final String PROGRAM_FILTER_PREFIX = "pr:";

    private static final int IS_NOT_FILTERED = 0;
    private static final int IS_PASSWORD_FILTERED = 1;
    private static final int IS_USERNAME_FILTERED = 2;
    private static final int IS_PROGRAM_FILTERED = 4;
    private boolean safe;

    public PasswordOverviewAdapter(Context context) {
        super();
        this.context = context;
        inflater = LayoutInflater.from(context);
        localPasswords = new ArrayList<>();
        useFiltered = false;
    }

    private PasswordProvider getProvider() {
        int userId = UserProvider.getInstance(context).getId();
        PasswordProvider provider = PasswordProvider.getInstance(context, userId);
        return provider;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
        View view = inflater.inflate(R.layout.password_list_item_layout, viewGroup, false);

        safe = UserProvider.getInstance(context).isSafe();

        Password password = useFiltered ? localPasswords.get(position) : getProvider().get(position);
        ViewHolder viewHolder = new ViewHolder(view);
        if(safe) {
            viewHolder.makeSafe();
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        Password password = useFiltered ? localPasswords.get(position) : getProvider().get(position);
        PasswordHistory history = password.getPasswordHistory().get(0);


        if (!safe) {
            viewHolder.password.setText(history.getValue());
        }
        viewHolder.username.setText(password.getUsername());
        viewHolder.program.setText(password.getProgram());
        viewHolder.id = password.getId();
    }

    @Override
    public int getItemCount() {
        return useFiltered ? localPasswords.size() : getProvider().size();
    }

    public synchronized void filter(String query) {
        localPasswords.clear();
        if (query.startsWith(PASSWORD_FILTER_PREFIX)) {
            String filter = query.replace(PASSWORD_FILTER_PREFIX, "");
            filter(filter, IS_PASSWORD_FILTERED);
        } else if (query.startsWith(USERNAME_FILTER_PREFIX)) {
            String filter = query.replace(USERNAME_FILTER_PREFIX, "");
            filter(filter, IS_USERNAME_FILTERED);
        } else if (query.startsWith(PROGRAM_FILTER_PREFIX)) {
            String filter = query.replace(PROGRAM_FILTER_PREFIX, "");
            filter(filter, IS_PROGRAM_FILTERED);
        } else {
            filter(query, IS_NOT_FILTERED);
        }

        notifyDataSetChanged();
        useFiltered = true;
    }

    private void filter(String query, int flag) {
        PasswordProvider provider = getProvider();
        for (int i = 0; i < provider.size(); i++) {
            Password password = provider.get(i);
            if (matches(password, query, flag)) {
                if (localPasswords.contains(password)) continue;
                localPasswords.add(password);
            }
        }
    }

    private boolean matches(Password password, String simpleQuery, int filterFlags) {
        boolean returnValue = false;

        String program = password.getProgram();
        String passwordValue = password.getFirstItem().getValue();
        String username = password.getUsername();

        switch (filterFlags) {
            case 0: // IS_NOT_FILTERED
                returnValue = program.contains(simpleQuery) || passwordValue.contains(simpleQuery) || username.contains(simpleQuery);
                break;
            case 1: // IS_PASSWORD_FILTERED
                returnValue = passwordValue.contains(simpleQuery);
                break;
            case 2: // IS_USERNAME_FILTERED
                returnValue = username.contains(simpleQuery);
                break;
            case 4: // IS_PROGRAM_FILTERED
                returnValue = program.contains(simpleQuery);
                break;
        }

        return returnValue;
    }

    public void resetFilter() {
        useFiltered = false;
        localPasswords.clear();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final TextView program;
        final TextView username;
        final TextView password;
        int id;
        private boolean safe = false;

        public ViewHolder(View itemView) {
            super(itemView);

            program = (TextView) itemView.findViewById(R.id.passwordlistitemlayout_textview_program);
            username = (TextView) itemView.findViewById(R.id.passwordlistitemlayout_textview_username);
            password = (TextView) itemView.findViewById(R.id.passwordlistitemlayout_textview_password);

            itemView.setOnClickListener(this);
        }

        void makeSafe() {
            ViewManager parent = (ViewManager) password.getParent();
            parent.removeView(password);

            safe = true;
        }

        @Override
        public void onClick(View v) {
            if(safe) {
                AlertDialog dialog = new AlertDialog.Builder(context)
                        .setTitle("Reenter your password")
                        .setView(R.layout.reenter_password_layout)
                        .setPositiveButton("OK", null)
                        .create();
                dialog.show();
            } else {
                Context context = PasswordOverviewAdapter.this.context;
                Intent intent = new Intent(context, PasswordDetailActivity.class);
                intent.putExtra(PasswordDetailActivity.START_DETAIL_INDEX, id);
                context.startActivity(intent);
            }
        }

    }


}
