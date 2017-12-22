package scenehub.libgen;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class DataAdapter extends RecyclerView.Adapter<DataAdapter.MyViewHolder> {

    private ArrayList<Book> books;
    private Context context;

    public DataAdapter(ArrayList<Book> books, Context context) {
        this.books = books;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_row, viewGroup, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder myViewHolder, int i) {
        final Book book = books.get(i);

        myViewHolder.tv_author.setText(book.getAuthor());
        myViewHolder.tv_title.setText(book.getTitle());
        myViewHolder.tv_year.setText(book.getYear());
        myViewHolder.tv_extension.setText(book.getExtension());
        
        myViewHolder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DetailsActivity.class);
                intent.putExtra("book", book);
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return books.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder{
        private TextView tv_title,tv_author,tv_year, tv_extension;
        private RelativeLayout relativeLayout;

        public MyViewHolder(View view) {
            super(view);

            relativeLayout = view.findViewById(R.id.card);
            tv_title = view.findViewById(R.id.tv_title);
            tv_author = view.findViewById(R.id.tv_author);
            tv_year = view.findViewById(R.id.tv_year);
            tv_extension = view.findViewById(R.id.tv_extension);
        }

    }

}