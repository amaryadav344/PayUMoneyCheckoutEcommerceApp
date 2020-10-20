package com.loopwiki.payumoneycheckout.Adapter;

import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.loopwiki.payumoneycheckout.Model.Product;
import com.loopwiki.payumoneycheckout.R;

import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CartAdapter extends RecyclerView.Adapter {
    // 1) ITEM_TYPE_PRODUCT - Product will be shown
    // 2) ITEM_TYPE_EMPTY - When cart will be empty it will show this cart is empty layout
    private static final int ITEM_TYPE_PRODUCT = 1;
    private static final int ITEM_TYPE_EMPTY = 0;
    // List of products that are added to cart
    private List<Product> products;
    private CartCallbacks cartCallbacks;

    public void setCartCallbacks(CartCallbacks cartCallbacks) {
        this.cartCallbacks = cartCallbacks;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder holder;
        if (viewType == ITEM_TYPE_PRODUCT) {
            View row = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_item, parent, false);
            holder = new CartItemHolder(row);
        } else {
            View row = LayoutInflater.from(parent.getContext()).inflate(R.layout.empty_item, parent, false);
            holder = new EmptyCartHolder(row);
        }

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof CartItemHolder) {
            Product currentProduct = products.get(position);
            CartItemHolder productHolder = (CartItemHolder) holder;
            productHolder.textViewProductName.setText(currentProduct.getName());
            Drawable image = holder.itemView.getContext().getResources().getDrawable(currentProduct.getImageResourceId());
            productHolder.imageViewProduct.setImageDrawable(image);
            productHolder.textViewProductPrice.setText(String.format(Locale.US, "%s%d", holder.itemView.getContext().getString(R.string.ruppi_symbol), currentProduct.getPrice()));
        }
    }

    @Override
    public int getItemCount() {
        return products.size() == 0 ? 1 : products.size();
    }

    @Override
    public int getItemViewType(int position) {
        return products.size() == 0 ? ITEM_TYPE_EMPTY : ITEM_TYPE_PRODUCT;
    }

    public interface CartCallbacks {
        void RemoveProduct(Product product);
    }

    class CartItemHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.imageViewProduct)
        ImageView imageViewProduct;
        @BindView(R.id.textViewProductName)
        TextView textViewProductName;
        @BindView(R.id.textViewProductPrice)
        TextView textViewProductPrice;
        @BindView(R.id.ImageButtonDelete)
        ImageButton ImageButtonDelete;

        CartItemHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            ImageButtonDelete.setOnClickListener(v -> {
                Product ProductToRemove = products.get(getAdapterPosition());
                int index = products.indexOf(ProductToRemove);
                products.remove(index);
                notifyItemRemoved(index);
                cartCallbacks.RemoveProduct(ProductToRemove);

            });
        }
    }

    public class EmptyCartHolder extends RecyclerView.ViewHolder {
        EmptyCartHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
