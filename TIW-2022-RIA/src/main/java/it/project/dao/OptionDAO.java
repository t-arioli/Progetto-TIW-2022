package it.project.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import it.project.bean.Option;

public class OptionDAO {
	private Connection connection;

	public OptionDAO(Connection connection) {
		this.connection = connection;
	}

	public ArrayList<Option> getOptionsByProduct(int prodId) throws SQLException {
		ArrayList<Option> result = new ArrayList<>();
		String query = "SELECT * FROM `option` WHERE `product` = ?";

		ResultSet resultSet = null;
		PreparedStatement pStatement = null;
		try {
			pStatement = connection.prepareStatement(query);
			pStatement.setInt(1, prodId);
			resultSet = pStatement.executeQuery();

			while (resultSet.next()) {
				Option newOp = new Option();
				newOp.setCode(resultSet.getInt("code"));
				newOp.setProduct(prodId);
				newOp.setName(resultSet.getString("name"));
				newOp.setStatus(resultSet.getBoolean("isOnOffer"));
				result.add(newOp);
			}
			return result;
		} catch (SQLException e) {
			throw new SQLException(e);
		} finally {
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
	}

	public ArrayList<Option> getOptionsByQuote(int pId) throws SQLException {
		ArrayList<Option> result = new ArrayList<>();
		String query1 = "SELECT `option`, `product` FROM `optionQuote` WHERE `quote` = ?";
		String query2 = "SELECT * FROM `option` WHERE `code` = ? AND `product` = ?";

		ResultSet resultSet = null;
		PreparedStatement pStatement = null;
		try {
			// phase 1: find all the options in the quote
			pStatement = connection.prepareStatement(query1);
			pStatement.setInt(1, pId);
			resultSet = pStatement.executeQuery();

			while (resultSet.next()) {
				Option option = new Option();
				option.setCode(resultSet.getInt("option"));
				option.setProduct(resultSet.getInt("product"));
				result.add(option);
			}
			pStatement.close();
			if (result.size() == 0)
				throw new SQLException("Nessuna opzione corrispondente");
			// phase 2: find all the details for each option
			pStatement = connection.prepareStatement(query2);
			for (Option o : result) {
				pStatement.setInt(1, o.getCode());
				pStatement.setInt(2, o.getProduct());
				resultSet = pStatement.executeQuery();
				if (resultSet.next()) {
					o.setName(resultSet.getString("name"));
					o.setStatus(resultSet.getBoolean("isOnOffer"));
				}
			}

			return result;

		} catch (SQLException e) {
			throw new SQLException(e);
		} finally {
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
	}
}
