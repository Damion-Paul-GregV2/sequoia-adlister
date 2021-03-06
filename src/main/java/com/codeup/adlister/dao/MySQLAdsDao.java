package com.codeup.adlister.dao;

import com.codeup.adlister.models.Ad;
import com.mysql.cj.jdbc.Driver;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MySQLAdsDao extends Dao implements Ads {

    public MySQLAdsDao(Config config) {
        super(config);
    }

    @Override
    public List<Ad> all() {
        PreparedStatement stmt = null;
        try {
            stmt = connection.prepareStatement("SELECT * FROM ads");
            ResultSet rs = stmt.executeQuery();
            return createAdsFromResults(rs);
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving all ads.", e);
        }
    }

    @Override
    public Ad findById(Long id) {
        String sql = "SELECT * FROM ads WHERE id = ?";
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Ad(
                        rs.getLong("id"),
                        rs.getLong("user_id"),
                        rs.getString("title"),
                        rs.getString("description")
                );

            } else {
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("error finding one ad by id", e);
        }
    }

    @Override
    public Ad showAd(Long id) {
        String query = "SELECT * FROM ads WHERE id = ? LIMIT 1";
        try {
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();
            rs.next();
            return extractAd(rs);
        } catch (SQLException e) {
            throw new RuntimeException("Error finding Ad");
        }
    }

    @Override
    public Long insert(Ad ad, String[] categoriesIds) {
        try {
            String insertQuery = "INSERT INTO ads(user_id, title, description) VALUES (?, ?, ?)";
            PreparedStatement stmt = connection.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS);
            stmt.setLong(1, ad.getUserId());
            stmt.setString(2, ad.getTitle());
            stmt.setString(3, ad.getDescription());
            stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();
            rs.next();
           Long adId = rs.getLong(1);
            for (String categoryId :
                    categoriesIds) {
                String insert = "INSERT INTO categories_ads(categories_id,ads_id) VALUES(?,?)";
                stmt = connection.prepareStatement(insert);
                stmt.setLong(1,Long.parseLong(categoryId));
                stmt.setLong(2,adId);
                stmt.executeUpdate();
            }


           return adId;
        } catch (SQLException e) {
            throw new RuntimeException("Error creating a new ad.", e);
        }
    }

    private Ad extractAd(ResultSet rs) throws SQLException {
        return new Ad(
                rs.getLong("id"),
                rs.getLong("user_id"),
                rs.getString("title"),
                rs.getString("description")
        );
    }

    private List<Ad> createAdsFromResults(ResultSet rs) throws SQLException {
        List<Ad> ads = new ArrayList<>();
        while (rs.next()) {
            ads.add(extractAd(rs));
        }
        return ads;
    }

    @Override
    public List<Ad> search(String searchQuery) {
        PreparedStatement stmt = null;
        try {
            stmt = connection.prepareStatement("SELECT * FROM ads WHERE title LIKE ? or description LIKE ?");
            stmt.setString(1, "%" + searchQuery + "%");
            stmt.setString(2, "%" + searchQuery + "%");
            ResultSet rs = stmt.executeQuery();
            return createAdsFromResults(rs);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }

    }

    @Override
    public void updateAd(Ad ad) {
        try {
            PreparedStatement stmt = connection.prepareStatement(
                    "UPDATE ads SET title = ?, description =? WHERE id=?"
            );
            stmt.setString(1, ad.getTitle());
            stmt.setString(2, ad.getDescription());
            stmt.setLong(3, ad.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error updating an ad.", e);
        }
    }

    @Override
    public void deleteAd(Long id) {
        try {
            // Delete from ads_categories whre the ad_id = id
            PreparedStatement stmt = connection.prepareStatement(
                    "DELETE FROM categories_ads WHERE ads_id = ?"
            );
            stmt.setLong(1, id);
            stmt.executeUpdate();

            stmt = connection.prepareStatement(
                    "DELETE FROM ads WHERE id = ?"
            );
            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting an ad.", e);
        }
    }


    @Override
    public List<Ad> showUsersAds(Long id) {
        try {
            PreparedStatement stmt = connection.prepareStatement(
                    "SELECT * FROM ads a inner join users u ON a.user_id = u.id WHERE u.id = ?");
            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();
            return createAdsFromResults(rs);
        } catch (SQLException e) {
            throw new RuntimeException("error showing users ads", e);
        }
    }

    @Override
    public List<Ad> searchCategory(Long id){
        try{
            PreparedStatement stmt = connection.prepareStatement(
                    "SELECT * FROM ads JOIN categories_ads ON ads.id = ads_id WHERE categories_id = ?"
            );
            stmt.setLong(1,id);
            ResultSet rs = stmt.executeQuery();
            return createAdsFromResults(rs);
        }catch(SQLException e){
            throw new RuntimeException("error retrieving category", e);
        }
    }


}
