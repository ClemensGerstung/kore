package core.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import com.typingsolutions.passwordmanager.R;
import core.Password;
import core.PasswordHistory;
import core.PasswordProvider;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class PasswordHistoryAdapter extends RecyclerView.Adapter<PasswordHistoryAdapter.ViewHolder> {

    private Context context;
    private LayoutInflater inflater;
    private int passwordIndex;

    public PasswordHistoryAdapter(Context context, int passwordIndex) {
        super();
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.passwordIndex = passwordIndex;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = inflater.inflate(R.layout.password_history_item_layout, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        PasswordProvider provider = PasswordProvider.getInstance();
        Password password = provider.get(passwordIndex);
        PasswordHistory history = password.getPasswordHistory().get(position + 1);

        DateFormat dateFormat = SimpleDateFormat.getDateInstance(SimpleDateFormat.MEDIUM, Locale.getDefault());
        String date = dateFormat.format(history.getChangedDate());

        holder.editText.setText(history.getValue());
        holder.editText.setHint(date);
    }

    @Override
    public int getItemCount() {
        return PasswordProvider.getInstance().size() - 1;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        final EditText editText;

        public ViewHolder(View itemView) {
            super(itemView);
            editText = (EditText) itemView.findViewById(R.id.passwordhistoryitemlayout_edittext_item);
        }
    }
}
