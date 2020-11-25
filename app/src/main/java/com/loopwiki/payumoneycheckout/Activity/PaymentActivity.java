package com.loopwiki.payumoneycheckout.Activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.loopwiki.payumoneycheckout.Fragment.CartFragment;
import com.loopwiki.payumoneycheckout.Fragment.ProductsFragment;
import com.loopwiki.payumoneycheckout.Model.Product;
import com.loopwiki.payumoneycheckout.R;
import com.loopwiki.payumoneycheckout.Util.Helper;
import com.payumoney.core.PayUmoneyConstants;
import com.payumoney.core.PayUmoneySdkInitializer;
import com.payumoney.core.entity.TransactionResponse;
import com.payumoney.sdkui.ui.utils.PayUmoneyFlowManager;
import com.payumoney.sdkui.ui.utils.ResultModel;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PaymentActivity extends AppCompatActivity implements ProductsFragment.ProductInteractionListener, CartFragment.CartInteractionListener {
    public static final String TAG = PaymentActivity.class.getSimpleName();
    FragmentManager fragmentManager;
    ProductsFragment productsFragment;
    int cartCount = 0;
    @BindView(R.id.textViewCartCount)
    TextView textViewCartCount;
    @BindView(R.id.imageViewCart)
    ImageView imageViewCart;
    List<Product> products;

    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        ButterKnife.bind(this);
        fragmentManager = getSupportFragmentManager();
        productsFragment = ProductsFragment.newInstance();
        products = getProducts();
        productsFragment.products = products;
        fragmentManager.beginTransaction().replace(R.id.main_content, productsFragment).commit();
        imageViewCart.setOnClickListener(v -> {
            CartFragment cartFragment = new CartFragment();
            List<Product> productList = new ArrayList<>();
            for (Product product : products) {
                if (product.isAddedToCart()) {
                    productList.add(product);
                }
            }
            cartFragment.products = productList;
            fragmentManager.beginTransaction().replace(R.id.main_content, cartFragment).addToBackStack(ProductsFragment.TAG).commit();
        });

    }

    // Callback from Products fragment when product is added
    @Override
    public void ProductAddedToCart(Product product) {
        cartCount++;
        textViewCartCount.setVisibility(View.VISIBLE);
        textViewCartCount.setText(String.valueOf(cartCount));
        Toast.makeText(this, getString(R.string.product_added), Toast.LENGTH_SHORT).show();
    }

    // Callback from Products fragment when product is removed
    @Override
    public void ProductRemovedFromCart(Product product) {
        cartCount--;
        textViewCartCount.setText(String.valueOf(cartCount));
        if (cartCount == 0) {
            textViewCartCount.setVisibility(View.GONE);
        }
        Toast.makeText(this, getString(R.string.product_removed), Toast.LENGTH_SHORT).show();
    }

    // method to create dummy product
    private List<Product> getProducts() {
        List<Product> products = new ArrayList<>();
        int[] ImageUrl = {R.drawable.one, R.drawable.two, R.drawable.three, R.drawable.four, R.drawable.five, R.drawable.six};
        String[] Title = {"HRX by Hrithik", "Crew STREET", "Royal Enfield", "Kook N Keech", "ADIDAS", "UNDER ARMOUR"};
        int[] Price = {5000, 2000, 1500, 3000, 1256, 700};
        boolean[] IsNew = {true, false, false, true, true, false};
        for (int i = 0; i < ImageUrl.length; i++) {
            Product product = new Product();
            product.setName(Title[i]);
            product.setImageResourceId(ImageUrl[i]);
            product.setNew(IsNew[i]);
            product.setPrice(Price[i]);
            products.add(product);
        }
        return products;

    }

    // Back button press method
    @Override
    public void onBackPressed() {
        if (fragmentManager.getBackStackEntryCount() == 0) {
            super.onBackPressed();
        } else {
            fragmentManager.popBackStackImmediate();
        }

    }

    // Method called when product is removed from cart
    @Override
    public void RemoveProduct(Product product) {
        int index = this.products.indexOf(product);
        Product ProductToRemove = this.products.get(index);
        ProductToRemove.setAddedToCart(false);
        ProductRemovedFromCart(product);
    }

    // method to clear cart
    public void clearCart() {
        for (Product product : products) {
            if (product.isAddedToCart()) {
                product.setAddedToCart(false);
            }
        }
    }

    // Method is called when we click on Pay button
    @Override
    public void ProceedToPay(int TotalPrice) {
        launchPayUMoneyFlow(TotalPrice);
    }

    // This method takes amount as parameter and launches PayUMoney checkout
    private void launchPayUMoneyFlow(double amount) {
        PayUmoneySdkInitializer.PaymentParam.Builder builder = new PayUmoneySdkInitializer.PaymentParam.Builder();

        String txnId = "0nf7" + System.currentTimeMillis();
        // String txnId = "TXNID720431525261327973";
        String phone = "7777777777";
        String productName = "Sample Product";
        String firstName = "loopwiki";
        String email = "sample@sample.com";
        String udf1 = "";
        String udf2 = "";
        String udf3 = "";
        String udf4 = "";
        String udf5 = "";

        //AppEnvironment appEnvironment = ((BaseApplication) getApplication()).getAppEnvironment();
        builder.setAmount(String.valueOf(amount))
                .setTxnId(txnId)
                .setPhone(phone)
                .setProductName(productName)
                .setFirstName(firstName)
                .setEmail(email)
                .setsUrl(getString(R.string.sUrl))
                .setfUrl(getString(R.string.fUrl))
                .setUdf1(udf1)
                .setUdf2(udf2)
                .setUdf3(udf3)
                .setUdf4(udf4)
                .setUdf5(udf5)
                .setIsDebug(true)
                .setKey(getString(R.string.MerchantKey))
                .setMerchantId(getString(R.string.MerchantId));

        try {
            PayUmoneySdkInitializer.PaymentParam mPaymentParams = builder.build();

            /*
             * Hash should always be generated from your server side.
             * */
            //    generateHashFromServer(mPaymentParams);

            /*            *//**
             * Do not use below code when going live
             * Below code is provided to generate hash from sdk.
             * It is recommended to generate hash from server side only.
             * */
            mPaymentParams = calculateServerSideHashAndInitiatePayment1(mPaymentParams);

            PayUmoneyFlowManager.startPayUMoneyFlow(mPaymentParams, PaymentActivity.this, R.style.AppTheme_Green, false);

        } catch (Exception e) {
            // some exception occurred
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
    // Method to create hash
    public static String hashCal(String str) {
        byte[] hashseq = str.getBytes();
        StringBuilder hexString = new StringBuilder();
        try {
            MessageDigest algorithm = MessageDigest.getInstance("SHA-512");
            algorithm.reset();
            algorithm.update(hashseq);
            byte messageDigest[] = algorithm.digest();
            for (byte aMessageDigest : messageDigest) {
                String hex = Integer.toHexString(0xFF & aMessageDigest);
                if (hex.length() == 1) {
                    hexString.append("0");
                }
                hexString.append(hex);
            }
        } catch (NoSuchAlgorithmException ignored) {
        }
        return hexString.toString();
    }


    /**
     * Note : Hash value must be calculated from server only for testing purpose you can generate
     * using this method
     * Thus function calculates the hash for transaction
     *
     * @param paymentParam payment params of transaction
     * @return payment params along with calculated merchant hash
     */
    private PayUmoneySdkInitializer.PaymentParam calculateServerSideHashAndInitiatePayment1(final PayUmoneySdkInitializer.PaymentParam paymentParam) {

        StringBuilder stringBuilder = new StringBuilder();
        HashMap<String, String> params = paymentParam.getParams();
        stringBuilder.append(params.get(PayUmoneyConstants.KEY)).append("|");
        stringBuilder.append(params.get(PayUmoneyConstants.TXNID)).append("|");
        stringBuilder.append(params.get(PayUmoneyConstants.AMOUNT)).append("|");
        stringBuilder.append(params.get(PayUmoneyConstants.PRODUCT_INFO)).append("|");
        stringBuilder.append(params.get(PayUmoneyConstants.FIRSTNAME)).append("|");
        stringBuilder.append(params.get(PayUmoneyConstants.EMAIL)).append("|");
        stringBuilder.append(params.get(PayUmoneyConstants.UDF1)).append("|");
        stringBuilder.append(params.get(PayUmoneyConstants.UDF2)).append("|");
        stringBuilder.append(params.get(PayUmoneyConstants.UDF3)).append("|");
        stringBuilder.append(params.get(PayUmoneyConstants.UDF4)).append("|");
        stringBuilder.append(params.get(PayUmoneyConstants.UDF5)).append("||||||");

        stringBuilder.append(R.string.MerchantSalt);

        String hash = hashCal(stringBuilder.toString());
        paymentParam.setMerchantHash(hash);

        return paymentParam;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result Code is -1 send from Payumoney activity
        Log.d("PaymentActivity", "request code " + requestCode + " resultcode " + resultCode);
        if (requestCode == PayUmoneyFlowManager.REQUEST_CODE_PAYMENT && resultCode == RESULT_OK && data !=
                null) {
            TransactionResponse transactionResponse = data.getParcelableExtra(PayUmoneyFlowManager
                    .INTENT_EXTRA_TRANSACTION_RESPONSE);

            ResultModel resultModel = data.getParcelableExtra(PayUmoneyFlowManager.ARG_RESULT);

            // Check which object is non-null
            if (transactionResponse != null && transactionResponse.getPayuResponse() != null) {
                if (transactionResponse.getTransactionStatus().equals(TransactionResponse.TransactionStatus.SUCCESSFUL)) {
                    //Success Transaction
                    Dialog dialog = Helper.getSuccessDialog(this);
                    TextView textViewGoHome = dialog.findViewById(R.id.textViewGoHome);
                    textViewGoHome.setOnClickListener(v -> {
                        dialog.dismiss();
                        clearCart();
                        cartCount = 0;
                        textViewCartCount.setVisibility(View.GONE);
                        fragmentManager.beginTransaction().replace(R.id.main_content, productsFragment).commit();
                    });
                    dialog.show();
                    Toast.makeText(this, "Payment Successful", Toast.LENGTH_SHORT).show();
                } else {
                    //Failure Transaction
                }

                // Response from Payumoney
                String payuResponse = transactionResponse.getPayuResponse();

                // Response from SURl and FURL
                String merchantResponse = transactionResponse.getTransactionDetails();

            } else if (resultModel != null && resultModel.getError() != null) {
                Log.d(TAG, "Error response : " + resultModel.getError().getTransactionResponse());
            } else {
                Log.d(TAG, "Both objects are null!");
            }
        }
    }
}
