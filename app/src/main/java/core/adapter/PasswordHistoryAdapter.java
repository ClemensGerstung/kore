package core.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.typingsolutions.passwordmanager.R;

public class PasswordHistoryAdapter extends RecyclerView.Adapter<PasswordHistoryAdapter.ViewHolder> {

    private Context context;
    private LayoutInflater inflater;
    private int passwordIndex;
    private OnItemAddedCallback onItemAddedCallback;

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
//        TODO
//        PasswordProvider provider = PasswordProvider.getInstance();
//        Password password = provider.getById(passwordIndex);
//        PasswordHistory history = password.getPasswordHistory().get(position + 1);
//
//        DateFormat dateFormat = SimpleDateFormat.getDateInstance(SimpleDateFormat.MEDIUM, Locale.getDefault());
//        String date = dateFormat.format(history.getChangedDate());
//
//        holder.password.setText(history.getValue());
//        holder.date.setText(date);
//
//        if(onItemAddedCallback != null) {
//            onItemAddedCallback.onItemAdded(holder, position);
//        }
    }

    @Override
    public int getItemCount() {
//        PasswordProvider provider = PasswordProvider.getInstance();
//        Password password = provider.getById(passwordIndex);
//
//        return password.getPasswordHistory().size() - 1;
        return 0;
    }

    public void setOnItemAddedCallback(OnItemAddedCallback onItemAddedCallback) {
        this.onItemAddedCallback = onItemAddedCallback;
    }


    public interface OnItemAddedCallback {
        void onItemAdded(PasswordHistoryAdapter.ViewHolder viewHolder, int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder  {
        final TextView password;
        final TextView date;
        private boolean first;

        public ViewHolder(View itemView) {
            super(itemView);
            password = (TextView) itemView.findViewById(R.id.passwordhistoryitemlayout_textview_password);
            date = (TextView) itemView.findViewById(R.id.passwordhistoryitemlayout_textview_date);
            first = true;
        }
    }
}
