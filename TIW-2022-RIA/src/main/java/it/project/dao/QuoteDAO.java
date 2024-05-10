package it.project.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import it.project.bean.Option;
import it.project.bean.Product;
import it.project.bean.Quote;
import it.project.bean.User;

public class QuoteDAO {
	private Connection connection;

	public QuoteDAO(Connection connection) {
		this.connection = connection;
	}

	public List<Quote> getQuotesByUser(User user) throws SQLException {
		String query = "";
		if (!user.isAdmin())
			query = "SELECT * FROM `quote` WHERE `client` = ? ";
		else
			query = "SELECT * FROM `quote` WHERE `employee` = ? ";
		return getQuotesByQuery(user, query);
	}

	public List<Quote> getUnassignedQuotes(User user) throws SQLException {
		ArrayList<Quote> result = null;
		if (user.isAdmin()) {
			String query = "SELECT * FROM `quote` WHERE `employee` IS ?";
			User dummy = new User();
			dummy.setRole(true);
			result = getQuotesByQuery(dummy, query);
		}
		return result;
	}

	private ArrayList<Quote> getQuotesByQuery(User user, String query) throws SQLException {
		ArrayList<Quote> result = new ArrayList<>();
		ResultSet resultSet = null;
		PreparedStatement pStatement = null;
		try {
			pStatement = connection.prepareStatement(query);
			pStatement.setString(1, user.getUsername());
			resultSet = pStatement.executeQuery();
			while (resultSet.next()) {
				Quote newQuote = new Quote();
				// quote id
				newQuote.setId(resultSet.getInt("id"));
				// client+employee
				setClientAndEmployee(user, resultSet, newQuote);
				// product
				setProduct(resultSet, newQuote);
				// options
				setOptions(newQuote);
				// price
				if (resultSet.getFloat("price") == 0)
					newQuote.setPrice(null);
				else
					newQuote.setPrice(resultSet.getFloat("price"));
				// dates
				newQuote.setDateCreation(resultSet.getDate("dateCreation"));
				if (resultSet.getDate("dateValidation") != null)
					newQuote.setDateValidation(resultSet.getDate("dateValidation"));
				// finally
				result.add(newQuote);
				// System.out.println(newQuote.toString());
			}
			// System.out.println(resultSet);

		} catch (SQLException e) {
			System.out.println(e.getMessage());
			throw new SQLException(e);

		} finally {
			try {
				if (resultSet != null) {
					resultSet.close();
				}
			} catch (Exception e1) {
				System.out.println(e1.getMessage());
				throw new SQLException(e1);
			}
			try {
				if (pStatement != null) {
					pStatement.close();
				}
			} catch (Exception e2) {
				System.out.println(e2.getMessage());
				throw new SQLException(e2);
			}
		}
		return result;

	}

	private void setClientAndEmployee(User user, ResultSet resultSet, Quote newQuote) throws SQLException {
		if (user.isAdmin()) {
			// method call is from an employee
			User client = new User();
			client.setUsername(resultSet.getString("client"));
			client.setRole(false);
			if (user.getUsername() == null)
				newQuote.setEmployee(null);
			else
				newQuote.setEmployee(user);
			newQuote.setClient(client);
		} else {
			// method call is from a client
			User admin = null;
			if (resultSet.getString("employee") != null) {
				// this is a quote already assigned
				admin = new User();
				admin.setUsername(resultSet.getString("employee"));
				admin.setRole(true);
			}
			newQuote.setEmployee(admin);
			newQuote.setClient(user);
		}
	}

	private void setProduct(ResultSet resultSet, Quote newQuote) throws SQLException {
		ProductDAO pDao = new ProductDAO(this.connection);
		Integer prodId = resultSet.getInt("product");
		Product product = pDao.getProductById(prodId);
		newQuote.setProduct(product);

	}

	private void setOptions(Quote newQuote) throws SQLException {
		OptionDAO oDao = new OptionDAO(this.connection);
		ArrayList<Option> options = oDao.getOptionsByQuote(newQuote.getId());
		newQuote.setOptions(options);
	}

	public void addNewQuote(Quote quote) throws SQLException {
		int code;
		String query1 = "INSERT into `quote` (`price`, `dateCreation`, `dateValidation`, `client`, `employee`, `product`) VALUES (null, ?, null, ?, null, ?)";
		String query2 = "INSERT into `optionQuote` (`quote`, `product`, `option`) VALUES (?, ?, ?)";
		PreparedStatement pstatement = null;
		ResultSet resultSet = null;
		try {
			pstatement = connection.prepareStatement(query1, Statement.RETURN_GENERATED_KEYS);
			// pstatement.setObject(1, null);
			pstatement.setDate(1, new java.sql.Date(quote.getDateCreation().getTime()));
			pstatement.setString(2, quote.getClient().getUsername());
			// pstatement.setNull(4, Types.VARCHAR);
			pstatement.setInt(3, quote.getProduct().getCode());
			code = pstatement.executeUpdate();
			if (code == 0)
				throw new SQLException();
			resultSet = pstatement.getGeneratedKeys();
			resultSet.next();
			int id = resultSet.getInt(1);

			for (Option o : quote.getOptions()) {
				pstatement.close();
				pstatement = connection.prepareStatement(query2);
				pstatement.setInt(1, id);
				pstatement.setInt(2, quote.getProduct().getCode());
				pstatement.setInt(3, o.getCode());
				code = pstatement.executeUpdate();
				if (code == 0)
					throw new SQLException();
			}
		} catch (SQLException e) {
			throw new SQLException(e.getMessage());
		} finally {
			try {
				if (pstatement != null) {
					pstatement.close();
				}
			} catch (Exception e1) {

			}
		}
	}

	public void addPrice(User user, int quote, float price) throws SQLException {
		int code;
		String query = "UPDATE `quote` SET `price` = ?, `dateValidation` = ?, `employee` = ? WHERE `id` = ?";
		PreparedStatement pstatement = null;
		try {
			pstatement = connection.prepareStatement(query);
			pstatement.setFloat(1, price);
			pstatement.setDate(2, new java.sql.Date(System.currentTimeMillis()));
			pstatement.setString(3, user.getUsername());
			pstatement.setInt(4, quote);
			code = pstatement.executeUpdate();
			if (code == 0)
				throw new SQLException();
		} catch (SQLException e) {
			throw new SQLException(e.getMessage());
		} finally {
			try {
				if (pstatement != null) {
					pstatement.close();
				}
			} catch (Exception e1) {

			}
		}
	}

}
