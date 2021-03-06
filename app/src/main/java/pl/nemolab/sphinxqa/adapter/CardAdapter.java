package pl.nemolab.sphinxqa.adapter;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.List;

import pl.nemolab.sphinxqa.R;
import pl.nemolab.sphinxqa.model.Card;

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
            holder.checked = (CheckBox) item.findViewById(R.id.checkBox);
            holder.start = (TextView) item.findViewById(R.id.txtStart);
            holder.stop = (TextView) item.findViewById(R.id.txtStop);
            item.setTag(holder);
        }
        Holder holder = (Holder) item.getTag();
        Card card = getItem(position);
        String start = card.getPointerFront().getStart().getText();
        String stop = card.getPointerFront().getStop().getText();
        holder.front.setText(Html.fromHtml(card.getFront()));
        holder.back.setText(Html.fromHtml(card.getBack()));
        holder.front.setText(card.getFront());
        holder.back.setText(card.getBack());
        holder.nr.setText(String.valueOf(card.getNr()));
        holder.checked.setChecked(card.isChecked());
        holder.start.setText(start);
        holder.stop.setText(stop);
        return item;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    static class Holder {
        public TextView front;
        public TextView back;
        public TextView nr;
        public CheckBox checked;
        public TextView start;
        public TextView stop;
    }
}
