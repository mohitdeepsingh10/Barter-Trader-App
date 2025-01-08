package com.example.group4planninggame.managers;

import com.example.group4planninggame.models.Preferences;
import com.example.group4planninggame.utils.Constants;
import com.google.firebase.database.FirebaseDatabase;
import com.example.group4planninggame.models.Product;

public class Crud {
    private final UserManager userManager;
    private final ExchangeOfferManager exchangeOfferManager;
    private final ProductManager productManager;

    public Crud() {
        FirebaseDatabase database = FirebaseDatabase.getInstance(Constants.FIREBASE_DATABASE);
        userManager = new UserManager(database);
        exchangeOfferManager = new ExchangeOfferManager(database);
        productManager = new ProductManager(database);
    }

    public String getExtractedPassword(String email) {
        return userManager.getExtractedPassword(email);
    }

    public String getExtractedRole(String email) {
        return userManager.getExtractedRole(email);
    }

    public void writeUser(String email, String pwd, String role) {
        userManager.writeUser(email, pwd, role);
    }

    public boolean checkUserInDB(String email) {
        return userManager.checkUserInDB(email);
    }

    public void removeUser(String email) {
        userManager.removeUser(email);
    }

    public void updateUserRole(String email, String role) {
        userManager.updateUserRole(email, role);
    }

    public void saveExchangeOffer(String itemName, String userEmail, String itemCondition, String itemCategory, String posterEmail, String wantedProduct) {
        exchangeOfferManager.saveExchangeOffer(itemName, userEmail, itemCondition, itemCategory, posterEmail, wantedProduct);
    }


    public interface ItemAlreadyListedCallback {
        void onCheckComplete(boolean isAlreadyListed);
    }

    public void addProductToDatabase(String userEmail, String title, String condition,
                                     String category, String description, Double lat, Double lon, String datePosted){
        productManager.addProductToDatabase(userEmail, title, condition, category, description, lat, lon, datePosted );
    }

    public void updateProduct(String initialID, String title, String condition,
                              String category, String description) {
        productManager.updateProduct(initialID, title, condition, category, description);
    }

    public void removeProduct(String initialID) {
        productManager.removeProduct(initialID);
    }

    public Product getProduct(String initialID) {
        return productManager.getProduct(initialID);
    }

    public boolean isProductInDB(String initialID) {
        return productManager.isProductInDB(initialID);
    }

    public void isItemAlreadyListed(String itemName, String userEmail, ItemAlreadyListedCallback callback) {
        exchangeOfferManager.isItemAlreadyListed(itemName, userEmail, callback);
    }

    public Preferences readUserPreferences(){
        return userManager.getUserPreferences();
    }
    //TODO
    public void writeUserPreferences(Preferences preferences){
        userManager.updateUserPreferences(preferences);
    }
}
