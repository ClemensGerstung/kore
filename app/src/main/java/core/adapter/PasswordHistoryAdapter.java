package core.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.typingsolutions.passwordmanager.R;
import core.data.Password;
import core.data.PasswordHistory;
import core.data.UserProvider;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class PasswordHistoryAdapter extends RecyclerView.Adapter<PasswordHistoryAdapter.ViewHolder> {

    private Context context;
    private LayoutInflater inflater;
    private int passwordId;

    public PasswordHistoryAdapter(Context context, int passwordIndex) {
        super();
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.passwordId = passwordIndex;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = inflater.inflate(R.layout.password_history_item_layout, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Password password = UserProvider.getInstance(context).getPasswordById(passwordId);
        PasswordHistory history = password.getItemAt(position + 1);

        DateFormat dateFormat = SimpleDateFormat.getDateInstance(SimpleDateFormat.MEDIUM, Locale.getDefault());
        String date = dateFormat.format(history.getChangedDate());

        holder.password.setText(history.getValue());
        holder.date.setText(date);
    }

    @Override
    public int getItemCount() {
        Password password = UserProvider.getInstance(context).getPasswordById(passwordId);
        return password.getHistoryCount();
    }


    public class ViewHolder extends RecyclerView.ViewHolder  {
        final TextView password;
        final TextView date;

        public ViewHolder(View itemView) {
            super(itemView);
            password = (TextView) itemView.findViewById(R.id.passwordhistoryitemlayout_textview_password);
            date = (TextView) itemView.findViewById(R.id.passwordhistoryitemlayout_textview_date);
        }
    }
}
