package com.codecool.shop.model;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class Cart {
    private static final AtomicInteger count = new AtomicInteger(1);
    private int id;
    private transient List<LineItem> products = new ArrayList<>();
    private float total;
    private int quantity;

    public Cart(int id) {
        this.id = id;
    }

    public Cart(List<LineItem> products, int id) {
        this.id = id;
        this.products = products;
    }

    public Cart() {
        this.products = new ArrayList<>();
    }

    public int getId() {
        return this.id;
    }

    public List<LineItem> getProducts() {
        return products;
    }

    public float getTotal() {
        calculateTotal();
        return total;
    }

    public void calculateTotal() {
        total = 0;
        products.forEach(product -> {
            total += product.getQuantity() * product.getUnitPrice();
        });
    }

    public void addToCart(LineItem lineItem) {
        products.add(lineItem);
    }

    public void addProduct(Product product, float price) {
        boolean found = false;
        for (LineItem item : products) {
            //if found increase quantity
            if (item.getProduct().getId() == product.getId()) {
                item.setQuantity(item.getQuantity() + 1);
                increaseQuantity();
                found = true;
            }
        }

        //if not found add new item
        if (!found) {
            products.add(new LineItem(product, price));
            increaseQuantity();
        }
    }

    public int getQuantity() {
        return quantity;
    }

    public int getNumberOfProducts() {
        int nrItems = 0;
        for (LineItem item : products) {
            nrItems += item.getQuantity();
        }
        return nrItems;
    }

    public int getQuantityOfProduct(Product product) {
        for (LineItem item : products) {
            if (item.getProduct().getId() == product.getId()) {
                return item.getQuantity();
            }
        }
        return 0;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void increaseQuantity() {
        quantity += 1;
    }

    public void removeProduct(Product product) {
        for (LineItem item : products) {
            if (item.getProduct().getId() == product.getId()) {
                if (item.getQuantity() > 1) {
                    item.setQuantity(item.getQuantity() - 1);
                } else {
                    products.remove(item);
                }
                break;
            }
        }
    }

}
