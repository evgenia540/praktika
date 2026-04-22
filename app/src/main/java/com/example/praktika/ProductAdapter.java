package com.example.praktika;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {
    private List<Product> products;
    private Context context;

    public ProductAdapter(List<Product> products, Context context) {
        this.products = products;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_product_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Product product = products.get(position);
        holder.tvTitle.setText(product.getName());
        holder.tvCategory.setText("Категория");
        holder.tvPrice.setText(product.getPrice());

        boolean inCart = CartManager.isInCart(context, product.getName());
        if (inCart) {
            holder.btnAction.setText("Убрать");
            holder.btnAction.setBackgroundResource(R.drawable.bg_button_outline_blue);
            holder.btnAction.setTextColor(context.getColor(R.color.blue_main));
        } else {
            holder.btnAction.setText("Добавить");
            holder.btnAction.setBackgroundResource(R.drawable.bg_button_continue);
            holder.btnAction.setTextColor(context.getColor(R.color.white));
        }

        holder.itemView.setOnClickListener(v -> showProductBottomSheet(product));
        holder.btnAction.setOnClickListener(v -> {
            CartManager.toggle(context, product);
            notifyItemChanged(position);
            updateCartBar();
        });
    }

    private void showProductBottomSheet(Product product) {
        BottomSheetDialog dialog = new BottomSheetDialog(context);
        View sheet = LayoutInflater.from(context).inflate(R.layout.bottom_sheet_product, null);

        TextView title = sheet.findViewById(R.id.tvSheetTitle);
        TextView description = sheet.findViewById(R.id.tvSheetDescription);
        TextView consumption = sheet.findViewById(R.id.tvSheetConsumption);
        TextView addButton = sheet.findViewById(R.id.btnSheetAdd);
        ImageView close = sheet.findViewById(R.id.ivClose);

        title.setText(product.getName());
        description.setText(product.getDescription());
        consumption.setText("Ориентировочный расход: 1 шт");

        boolean inCart = CartManager.isInCart(context, product.getName());
        addButton.setText(inCart ? "Убрать из корзины" : "Добавить за " + product.getPrice());

        addButton.setOnClickListener(v -> {
            CartManager.toggle(context, product);
            dialog.dismiss();
            notifyDataSetChanged();
            updateCartBar();
        });

        close.setOnClickListener(v -> dialog.dismiss());

        dialog.setContentView(sheet);
        dialog.show();
    }

    private void updateCartBar() {
        if (context instanceof HomeActivity) {
            ((HomeActivity) context).refreshCart();
        } else if (context instanceof CatalogActivity) {
            ((CatalogActivity) context).refreshCart();
        }
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvCategory, tvPrice;
        Button btnAction;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvProductTitle);
            tvCategory = itemView.findViewById(R.id.tvProductCategory);
            tvPrice = itemView.findViewById(R.id.tvProductPrice);
            btnAction = itemView.findViewById(R.id.btnProductAction);
        }
    }
}