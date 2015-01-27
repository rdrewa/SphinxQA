package pl.nemolab.sphinxqa.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.List;

import pl.nemolab.sphinxqa.R;
import pl.nemolab.sphinxqa.subs.Card;

public class CardAdapter extends ArrayAdapter<Card> {
    public static final int LAYOUT = R.layout.item_card;

    private List<Card> cards;
    public CardAdapter(Context context, List<Card> objects) {
        super(context, LAYOUT, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View item = convertView;
        if (item == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            item =  inflater.inflate(LAYOUT, parent, false);
            Holder holder = new Holder();
            holder.front = (TextView) item.findViewById(R.id.txtFront);
            holder.back = (TextView) item.findViewById(R.id.txtBack);
            holder.nr = (TextView) item.findViewById(R.id.txtNr);
            holder.export = (CheckBox) item.findViewById(R.id.checkExport);
            item.setTag(holder);
        }
        Holder holder = (Holder) item.getTag();
        Card card = getItem(position);
        holder.front.setText(card.getFront());
        holder.back.setText(card.getBack());
        holder.nr.setText(String.valueOf(card.getNr()));
        holder.export.setChecked(card.isExport());
        return item;
    }

    static class Holder {
        public TextView front;
        public TextView back;
        public TextView nr;
        public CheckBox export;
    }
}
