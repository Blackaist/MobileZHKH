package ru.smarthzkh.blackstork.other;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

import ru.smarthzkh.blackstork.R;
import ru.smarthzkh.blackstork.fragments.FragmentInfo;


public class BillingListAdapter extends RecyclerView.Adapter<BillingListAdapter.ViewHolder> {

    Context ctx;
    LayoutInflater Inflater;
    private int length;
    private JSONArray jsonArray;
    private FragmentActivity fragmentActivity;
    private final boolean payed;

    public BillingListAdapter(Context context, final boolean[] flags, FragmentActivity fragmentActivity) {
        this.fragmentActivity = fragmentActivity;

        payed = flags[0];
        String [] openedFlags = new String[4];
        openedFlags[0] = payed ? "yes" : "no";
        for(int i = 1; i < 4; i++)
            openedFlags[i] = flags[i] ? String.valueOf(i - 1) : "";

        ctx = context.getApplicationContext();
        Inflater = (LayoutInflater) ctx
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        SaveLoadFile sl = new SaveLoadFile(Objects.requireNonNull(ctx));
        JSONArray oJsonArray = sl.Read();
        jsonArray = oJsonArray;
        if(jsonArray != null) {
            for (int i = 0; i < oJsonArray.length(); i++) {
                try {

                    if (!jsonArray.getJSONObject(i).getString("payed").equals(openedFlags[0]) ||
                            openedFlags[Integer.valueOf(jsonArray.getJSONObject(i).getString("mode")) + 1].equals("")) {
                        jsonArray.remove(i);
                        i--;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            if (jsonArray != null) {
                length = jsonArray.length();
            } else length = 0;
        } else length = 0;

    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        CardView cv;
        TextView personName;
        TextView personAge;
        ImageView personPhoto;
        ViewHolder(View itemView) {
            super(itemView);
            cv = itemView.findViewById(R.id.cv);
            personName =  itemView.findViewById(R.id.person_name);
            personAge =   itemView.findViewById(R.id.person_age);
            personPhoto = itemView.findViewById(R.id.person_photo);
        }

    }


    @NonNull
    @Override
    public BillingListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);

        return new ViewHolder(v);
    }

    public void update(int i) {
        length = jsonArray.length();

        this.notifyDataSetChanged();
        this.notifyItemRemoved(i);
    }

    @Override
    public void onBindViewHolder(@NonNull final BillingListAdapter.ViewHolder holder, int position) {
        if(jsonArray != null) {
            try {
                final int staticPosition = position;
                String mode = jsonArray.getJSONObject(position).getString("mode");
                String date = jsonArray.getJSONObject(position).getString("paymperiod");
                date = date.substring(0, 2) + "." + date.substring(2, 6);
                final double sum = Double.parseDouble(jsonArray.getJSONObject(position).getString("sum")) / 100;
                holder.personName.setText(date + " " + jsonArray.getJSONObject(position).getString("purpose") + "\n");
                holder.personAge.setText("Сумма: " + sum + " р.");
                holder.personPhoto.setImageResource(Bill.getImageByMode(mode));

                holder.cv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ContextThemeWrapper ctw = new ContextThemeWrapper(fragmentActivity, R.style.Theme_Design_NoActionBar);
                        AlertDialog.Builder builder = new AlertDialog.Builder(ctw);

                        if(!payed) {
                            builder.setTitle("Квитанция").setMessage("Вы хотите оплатить квитанцию?\nСтоимость: " + sum + " р.");
                            builder.setPositiveButton("Оплатить", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    try {
                                        SaveLoadFile sl = new SaveLoadFile(Objects.requireNonNull(fragmentActivity));
                                        Intent intent = new Intent(Intent.ACTION_VIEW, Bill.payBill(jsonArray.getJSONObject(staticPosition)));
                                        fragmentActivity.startActivity(intent);
                                        jsonArray.getJSONObject(staticPosition).put("payed", "yes");
                                        sl.Write(jsonArray.getJSONObject(staticPosition));
                                        jsonArray.remove(staticPosition);
                                        update(staticPosition);
                                    } catch (JSONException | NoSuchAlgorithmException | UnsupportedEncodingException | InvalidKeyException | URISyntaxException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                            builder.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // User cancelled the dialog
                                }
                            });
                        }
                        else {
                            builder.setTitle("Квитанция").setMessage("Вы хотите переместить квитанцию в неоплаченные?");
                            builder.setPositiveButton("Да", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    try {
                                        jsonArray.getJSONObject(staticPosition).put("payed", "no");
                                        SaveLoadFile sl = new SaveLoadFile(Objects.requireNonNull(fragmentActivity));
                                        sl.Write(jsonArray.getJSONObject(staticPosition));
                                        jsonArray.remove(staticPosition);
                                        update(staticPosition);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                            builder.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // User cancelled the dialog
                                }
                            });
                        }

                        builder.setNeutralButton("Открыть", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Bundle extras = new Bundle();
                                try {
                                    extras.putSerializable("HashMap", (Serializable) jsonArray.getJSONObject(staticPosition).toString());
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                fragmentActivity.getIntent().putExtras(extras);
                                android.support.v4.app.FragmentTransaction fragTrans = fragmentActivity.getSupportFragmentManager().beginTransaction();
                                fragTrans.replace(R.id.container, new FragmentInfo());
                                fragTrans.commitAllowingStateLoss();
                            }
                        });

                        builder.show();
                    }
                });

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public int getItemCount() {
        return length;
    }


    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }


}
