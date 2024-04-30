package it.project.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import it.project.bean.Product;

public class ProductDAO {
	private Connection connection;
	
	public ProductDAO(Connection connection) {
		this.connection = connection;
	}
	
	public ArrayList<Product> getProductsList() throws SQLException{
		ArrayList<Product> result = new ArrayList<>();
		String query = "SELECT * FROM `product`";
		
		ResultSet resultSet = null;
		PreparedStatement pStatement = null;
		try {
			pStatement = connection.prepareStatement(query);
			resultSet = pStatement.executeQuery();
			while(resultSet.next()) {
				Product p = new Product();
				p.setCode(resultSet.getInt("code"));
				p.setName(resultSet.getString("name"));
				p.setImageUrl(resultSet.getString("imageUrl"));
				result.add(p);
			}
			
		}catch(SQLException e) {
			throw new SQLException(e);
		}
		finally {
			try {
				if (resultSet != null) {
					resultSet.close();
				}
			} catch (Exception e1) {
				throw new SQLException(e1);
			}
			try {
				if (pStatement != null) {
					pStatement.close();
				}
			} catch (Exception e2) {
				throw new SQLException(e2);
			}
		}
		return result;
		
	}
	
	public Product getProductById(Integer id) throws SQLException {
		Product result = null;
		String query = "SELECT * FROM `product` WHERE `code` = ?";
		
		ResultSet resultSet = null;
		PreparedStatement pStatement = null;
		try {
			pStatement = connection.prepareStatement(query);
			pStatement.setInt(1, id);
			resultSet = pStatement.executeQuery();
			
			while(resultSet.next()) {
				if(resultSet.getInt("code") == id) {
					result = new Product();
					result.setCode(resultSet.getInt("code"));
					result.setName(resultSet.getString("name"));
					result.setImageUrl(resultSet.getString("imageUrl"));
				}
			}
			
		}catch(SQLException e) {
			throw new SQLException(e);
		}
		finally {
			try {
				if (resultSet != null) {
					resultSet.close();
				}
			} catch (Exception e1) {
				throw new SQLException(e1);
			}
			try {
				if (pStatement != null) {
					pStatement.close();
				}
			} catch (Exception e2) {
				throw new SQLException(e2);
			}
		}
		return result;
		
	}
}
