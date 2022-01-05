package com.codecool.shop.controller;

import com.codecool.shop.dao.CartDao;
import com.codecool.shop.dao.ProductCategoryDao;
import com.codecool.shop.dao.ProductDao;
import com.codecool.shop.dao.SupplierDao;
import com.codecool.shop.dao.implementation.CartDaoMem;
import com.codecool.shop.dao.implementation.ProductCategoryDaoMem;
import com.codecool.shop.dao.implementation.ProductDaoMem;
import com.codecool.shop.dao.implementation.SupplierDaoMem;
import com.codecool.shop.model.Cart;
import com.codecool.shop.model.Product;
import com.codecool.shop.service.ProductService;
import com.codecool.shop.config.TemplateEngineUtil;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@WebServlet(urlPatterns = {"/"})
public class ProductController extends HttpServlet {

    TemplateEngine engine;
    WebContext context;
    Cart cart;
    HttpSession session;
    ProductDao productDataStore;
    ProductCategoryDao productCategoryDataStore;
    SupplierDao supplierDataStore;
    ProductService productService;

    private void setData(HttpServletRequest req, HttpServletResponse resp) {

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
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        setData(req, resp);


        String category = req.getParameter("category");
        int categoryId = (category != null) ? Integer.parseInt(category) : 1;
        String supplier = req.getParameter("supplier");
        int supplierId = (supplier != null) ? Integer.parseInt(supplier) : 0;

        if (supplier == null && category == null) {
            context.setVariable("products", productService.getAllProducts());
        } else if (supplier == null) {
            context.setVariable("category", productService.getProductCategory(categoryId));
            context.setVariable("products", productService.getProductsForCategory(categoryId));
        } else if (category == null) {
            context.setVariable("supplier", productService.getProductSupplier(supplierId));
            context.setVariable("products", productService.getProductsForSupplier(supplierId));
        }

        context.setVariable("categories", productCategoryDataStore.getAll());
        context.setVariable("suppliers", supplierDataStore.getAll());

        engine.process("product/index.html", context, resp.getWriter());
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("text/html");

        setData(req, resp);
        System.out.println("check");


        String productId = req.getParameter("productId");
        int id = Integer.parseInt(productId);

        Product product = productDataStore.find(id);
        cart.addProduct(product, product.getDefaultPrice());
        session.setAttribute("cart", cart);


        doGet(req, resp);


    }
}
