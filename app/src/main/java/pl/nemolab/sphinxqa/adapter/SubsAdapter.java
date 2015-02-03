package pl.nemolab.sphinxqa.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import pl.nemolab.sphinxqa.R;
import pl.nemolab.sphinxqa.model.Subs;

/**
 * Created by senator on 2015-02-03.
 */
public class SubsAdapter extends ArrayAdapter<Subs> {
    public static final int LAYOUT = R.layout.item_subs;

    public SubsAdapter(Context context, List<Subs> items) {
        super(context, LAYOUT, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        Holder holder;
        if (view == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            view = inflater.inflate(LAYOUT, parent, false);
            holder = new Holder();
            holder.src = (TextView) view.findViewById(R.id.txtSrc);
            holder.dst = (TextView) view.findViewById(R.id.txtDst);
            holder.start = (TextView) view.findViewById(R.id.txtStart);
            holder.stop = (TextView) view.findViewById(R.id.txtStop);
            view.setTag(holder);
        } else {
            holder = (Holder) view.getTag();
        }
        Subs item = getItem(position);
        holder.src.setText(item.getSrc());
        holder.dst.setText(item.getDst());
        holder.start.setText(item.getStart());
        holder.stop.setText(item.getStop());
        return view;
    }

    static class Holder {
        public TextView src;
        public TextView dst;
        public TextView start;
        public TextView stop;
    }
}
