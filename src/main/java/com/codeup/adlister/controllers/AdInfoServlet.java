package com.codeup.adlister.controllers;

import com.codeup.adlister.dao.DaoFactory;
import com.codeup.adlister.models.Ad;
import com.codeup.adlister.models.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "controllers.AdInfoServlet", urlPatterns = "/showAd")
public class AdInfoServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Long id = Long.parseLong(request.getParameter("id"));
        Ad ad = DaoFactory.getAdsDao().showAd(id);
        User user = DaoFactory.getUsersDao().showUserInformation(ad.getUserId());


        request.setAttribute("ad", ad);
        request.setAttribute("user", user);
        request.getRequestDispatcher("/WEB-INF/ads/showAd.jsp").forward(request,response);
    }
}
