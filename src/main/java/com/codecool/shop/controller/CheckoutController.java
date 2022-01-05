package com.codecool.shop.controller;

import com.codecool.shop.config.TemplateEngineUtil;
import com.codecool.shop.dao.ProductCategoryDao;
import com.codecool.shop.dao.ProductDao;
import com.codecool.shop.dao.SupplierDao;
import com.codecool.shop.dao.implementation.ProductCategoryDaoMem;
import com.codecool.shop.dao.implementation.ProductDaoMem;
import com.codecool.shop.dao.implementation.SupplierDaoMem;
import com.codecool.shop.model.Cart;
import com.codecool.shop.model.Product;
import com.codecool.shop.service.ProductService;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;

import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

@WebServlet(urlPatterns = {"/checkout"})
@MultipartConfig
public class CheckoutController extends HttpServlet {

    TemplateEngine engine;
    WebContext context;
    ProductDao productDataStore;
    ProductCategoryDao productCategoryDataStore;
    SupplierDao supplierDataStore;
    ProductService productService;
    Cart cart;
    HttpSession session;

    private void setData(HttpServletRequest req, HttpServletResponse resp){

        engine = TemplateEngineUtil.getTemplateEngine(req.getServletContext());
        context = new WebContext(req, resp, req.getServletContext());

        productDataStore = ProductDaoMem.getInstance();
        productCategoryDataStore = ProductCategoryDaoMem.getInstance();
        supplierDataStore = SupplierDaoMem.getInstance();
        productService = new ProductService(productDataStore, productCategoryDataStore, supplierDataStore);

        session = req.getSession();
        cart = (Cart) session.getAttribute("cart");
        if (cart == null) {
            cart = new Cart();
            session.setAttribute("cart", cart);
        }

    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("text/html");

        setData(req, resp);

        TemplateEngine engine = TemplateEngineUtil.getTemplateEngine(req.getServletContext());
        WebContext context = new WebContext(req, resp, req.getServletContext());

        engine.process("checkout.html", context, resp.getWriter());

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("text/html");

        setData(req, resp);
        String quantityParam = req.getParameter("quantity");
        String prodIdParam = req.getParameter("productId");

        int id = Integer.parseInt(prodIdParam);
        Product product = productDataStore.find(id);
        int newQuantity = Integer.parseInt(quantityParam);
        int oldQuantity = cart.getQuantityOfProduct(product);

        if (newQuantity > oldQuantity) {
            for (int i = 1; i <= newQuantity - oldQuantity; i++) {
                cart.addProduct(product, product.getDefaultPrice());
            }
        }
        if (newQuantity < oldQuantity) {
            for (int i = 1; i <= oldQuantity - newQuantity; i++) {
                cart.removeProduct(product);
            }
        }
        doGet(req, resp);
    }
}
