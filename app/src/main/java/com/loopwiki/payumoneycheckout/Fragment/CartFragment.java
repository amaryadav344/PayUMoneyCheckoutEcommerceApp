package com.loopwiki.payumoneycheckout.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.loopwiki.payumoneycheckout.Adapter.CartAdapter;
import com.loopwiki.payumoneycheckout.Model.Product;
import com.loopwiki.payumoneycheckout.R;
import com.loopwiki.payumoneycheckout.Util.LinearSpacingDecoration;

import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;


public class CartFragment extends Fragment implements CartAdapter.CartCallbacks {
    public List<Product> products;
    @BindView(R.id.recyclerViewCart)
    RecyclerView recyclerViewCart;
    @BindView(R.id.buttonPay)
    Button buttonPay;
    View view;
    private CartInteractionListener mListener;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view != null) {
            return view;
        }
        view = inflater.inflate(R.layout.fragment_cart, container, false);
        ButterKnife.bind(this, view);
        recyclerViewCart.setLayoutManager(new LinearLayoutManager(getContext()));
        CartAdapter cartAdapter = new CartAdapter();
        cartAdapter.setCartCallbacks(this);
        cartAdapter.setProducts(products);
        recyclerViewCart.setAdapter(cartAdapter);
        recyclerViewCart.addItemDecoration(new LinearSpacingDecoration(20));

        buttonPay.setText(String.format(Locale.US, "Proceed to Pay %s%d", getActivity().getString(R.string.ruppi_symbol), getTotal(products)));
        buttonPay.setOnClickListener(v -> {
            mListener.ProceedToPay(getTotal(products));
        });
        setPayButtonVisibility();
        return view;
    }

    private int getTotal(List<Product> products) {
        int TotalPrice = 0;
        for (Product product : products) {
            TotalPrice = TotalPrice + product.getPrice();
        }
        return TotalPrice;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof CartInteractionListener) {
            mListener = (CartInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement CartInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void RemoveProduct(Product product) {
        mListener.RemoveProduct(product);
        buttonPay.setText(String.format(Locale.US, "Proceed to Pay %s%d", getActivity().getString(R.string.ruppi_symbol), getTotal(products)));
        setPayButtonVisibility();
    }

    public void setPayButtonVisibility() {
        if (products.size() == 0) {
            buttonPay.setVisibility(View.GONE);
        } else {
            buttonPay.setVisibility(View.VISIBLE);
        }

    }


    public interface CartInteractionListener {
        void RemoveProduct(Product product);

        void ProceedToPay(int TotalPrice);
    }
}
