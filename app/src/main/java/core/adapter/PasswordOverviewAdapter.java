package core.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.provider.ContactsContract;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.*;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.typingsolutions.passwordmanager.R;
import com.typingsolutions.passwordmanager.activities.PasswordDetailActivity;
import com.typingsolutions.passwordmanager.activities.PasswordOverviewActivity;
import core.DatabaseProvider;
import core.Utils;
import core.data.Password;
import core.data.PasswordHistory;
import core.data.PasswordProvider;

import java.util.ArrayList;
import java.util.List;

public class PasswordOverviewAdapter extends RecyclerView.Adapter<PasswordOverviewAdapter.ViewHolder> {

  private final SwipeRefreshLayout swipeRefreshLayout;
  private int currentId;
  private final DatabaseProvider.OnOpenListener onOpenListener = new DatabaseProvider.OnOpenListener() {
    @Override
    public void open() {
      passwordOverviewActivity.hideRefreshing();
      Intent intent = new Intent(context, PasswordDetailActivity.class);
      intent.putExtra(PasswordDetailActivity.START_DETAIL_INDEX, currentId);
      context.startActivity(intent);
    }

    @Override
    public void refused() {
      passwordOverviewActivity.hideRefreshing();
      passwordOverviewActivity.makeSnackBar();
    }
  };

  private Context context;
  private PasswordOverviewActivity passwordOverviewActivity;
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

  public PasswordOverviewAdapter(PasswordOverviewActivity context, SwipeRefreshLayout swipeRefreshLayout) {
    super();
    this.context = context;
    this.passwordOverviewActivity = context;
    this.swipeRefreshLayout = swipeRefreshLayout;
    inflater = LayoutInflater.from(context);
    localPasswords = new ArrayList<>();
    useFiltered = false;
  }

  @Override
  public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
    View view = inflater.inflate(R.layout.password_list_item_layout, viewGroup, false);

    safe = PasswordProvider.getInstance(context).isSafe();

    ViewHolder viewHolder = new ViewHolder(view);
    if (safe) {
      viewHolder.makeSafe();
    }

    return viewHolder;
  }

  @Override
  public void onBindViewHolder(ViewHolder viewHolder, int position) {
    Password password = useFiltered ? localPasswords.get(position) : PasswordProvider.getInstance(context).get(position);
    PasswordHistory history = password.getFirstHistoryItem();

    if (!safe) {
      viewHolder.password.setText(history.getValue());
      viewHolder.username.setText(password.getUsername());
    }

    viewHolder.program.setText(password.getProgram());
    viewHolder.id = password.getId();
    viewHolder.icon.setText(password.getProgram().toUpperCase().toCharArray(), 0, 1);
    try {
      String programHash = Utils.getHashedString(password.getProgram()).substring(0, 6);
      String passwordHash = Utils.getHashedString(password.getFirstItem()).substring(0, 6);

      int hexColor = Integer.parseInt(programHash, 16)
          | Integer.parseInt(passwordHash, 16)
          | 0xFF000000;

      viewHolder.icon
          .getBackground()
          .setColorFilter(hexColor, PorterDuff.Mode.MULTIPLY);
      viewHolder.icon.setGravity(Gravity.CENTER);
    } catch (Exception e) {
      Log.e(getClass().getSimpleName(), String.format("%s: %s", e.getClass().getSimpleName(), e.getMessage()));
    }
  }

  @Override
  public int getItemCount() {
    return useFiltered ? localPasswords.size() : PasswordProvider.getInstance(context).size();
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

    PasswordProvider provider = PasswordProvider.getInstance(context);
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
    String passwordValue = password.getFirstItem();
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

  public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, DialogInterface.OnClickListener {
    final TextView program;
    final TextView username;
    final TextView password;
    final TextView icon;
    int id;
    private boolean safe = false;

    public ViewHolder(View itemView) {
      super(itemView);

      program = (TextView) itemView.findViewById(R.id.passwordlistitemlayout_textview_program);
      username = (TextView) itemView.findViewById(R.id.passwordlistitemlayout_textview_username);
      password = (TextView) itemView.findViewById(R.id.passwordlistitemlayout_textview_password);
      icon = (TextView) itemView.findViewById(R.id.passwordlistitemlayout_textview_icon);
      /*icon.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          Toast.makeText(context, "Heeeey", Toast.LENGTH_LONG).show();
        }
      });*/

      itemView.setOnClickListener(this);
    }

    void makeSafe() {
      ViewManager parent = (ViewManager) password.getParent();
      parent.removeView(password);
      parent.removeView(username);

      safe = true;
    }

    @Override
    public void onClick(View v) {
      if (safe) {
        AlertDialog dialog = new AlertDialog.Builder(context)
            .setTitle("Reenter your password")
            .setView(R.layout.reenter_password_layout)
            .setPositiveButton("OK", this)
            .create();
        dialog.show();
      } else {
        Intent intent = new Intent(context, PasswordDetailActivity.class);
        intent.putExtra(PasswordDetailActivity.START_DETAIL_INDEX, id);
        context.startActivity(intent);
      }
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
      AlertDialog alert = (AlertDialog) dialog;
      EditText editText = (EditText) alert.findViewById(R.id.reenterpasswordlayout_edittext_password);
      String password = editText.getText().toString();

      swipeRefreshLayout.setRefreshing(true);
      DatabaseProvider.getConnection(context).tryOpen(password, onOpenListener);
      currentId = id;

      alert.dismiss();
    }
  }
}
