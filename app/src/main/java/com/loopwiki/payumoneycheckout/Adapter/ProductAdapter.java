package com.loopwiki.payumoneycheckout.Adapter;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.loopwiki.payumoneycheckout.Model.Product;
import com.loopwiki.payumoneycheckout.R;

import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ProductAdapter extends RecyclerView.Adapter {
    private List<Product> products;
    private ProductAdapterCallBack productAdapterCallBack;

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    public void setProductAdapterCallBack(ProductAdapterCallBack productAdapterCallBack) {
        this.productAdapterCallBack = productAdapterCallBack;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View row = LayoutInflater.from(parent.getContext()).inflate(R.layout.product, parent, false);
        return new ProductHolder(row);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Product currentProduct = products.get(position);
        ProductHolder productHolder = (ProductHolder) holder;
        productHolder.textViewProductName.setText(currentProduct.getName());
        productHolder.textViewProductPrice.setText(String.format(Locale.US, "%s%d", holder.itemView.getContext().getString(R.string.ruppi_symbol), currentProduct.getPrice()));

        Drawable image = holder.itemView.getContext().getResources().getDrawable(currentProduct.getImageResourceId());
        productHolder.imageViewProduct.setImageDrawable(image);
        if (currentProduct.isAddedToCart()) {
            productHolder.imageButtonAddToCart.setColorFilter(ContextCompat.getColor(productHolder.itemView.getContext(), R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
        } else {
            productHolder.imageButtonAddToCart.setColorFilter(ContextCompat.getColor(productHolder.itemView.getContext(), android.R.color.tertiary_text_light), PorterDuff.Mode.SRC_IN);
        }
        if (currentProduct.isNew()) {
            productHolder.ImageViewNew.setVisibility(View.VISIBLE);
        } else {
            productHolder.ImageViewNew.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public interface ProductAdapterCallBack {
        void ProductAddedToCart(Product product);

        void ProductRemovedFromCart(Product product);
    }

    class ProductHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.imageViewProduct)
        ImageView imageViewProduct;
        @BindView(R.id.textViewProductName)
        TextView textViewProductName;
        @BindView(R.id.textViewProductPrice)
        TextView textViewProductPrice;
        @BindView(R.id.imageButtonAddToCart)
        ImageButton imageButtonAddToCart;
        @BindView(R.id.ImageViewNew)
        ImageView ImageViewNew;


        ProductHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            imageButtonAddToCart.setOnClickListener(v -> {
                Product product = products.get(getAdapterPosition());
                if (product.isAddedToCart()) {
                    productAdapterCallBack.ProductRemovedFromCart(product);
                    imageButtonAddToCart.setColorFilter(ContextCompat.getColor(v.getContext(), android.R.color.tertiary_text_light), PorterDuff.Mode.SRC_IN);
                    product.setAddedToCart(false);
                } else {
                    productAdapterCallBack.ProductAddedToCart(product);
                    imageButtonAddToCart.setColorFilter(ContextCompat.getColor(v.getContext(), R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
                    product.setAddedToCart(true);
                }

            });
        }
    }
}
