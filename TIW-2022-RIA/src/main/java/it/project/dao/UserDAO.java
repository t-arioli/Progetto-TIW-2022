package it.project.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.lang.StringUtils;

import it.project.bean.User;

public class UserDAO {
	private Connection connection;

	public UserDAO(Connection connection) {
		this.connection = connection;
	}

	public User checkCredentials(String username, String password) throws SQLException {
		User result = null;
		String query = "SELECT `username`, `isClient`, `isEmployee` FROM `user` WHERE `username` = ? AND `password` = ?";
		ResultSet resultSet = null;
		PreparedStatement pStatement = null;
		try {
			pStatement = connection.prepareStatement(query);
			pStatement.setString(1, username);
			pStatement.setString(2, password);
			resultSet = pStatement.executeQuery();

			if (resultSet.next()) {
				result = new User();
				result.setUsername(resultSet.getString("username"));

				boolean isAdmin = resultSet.getBoolean("isEmployee");
				boolean isClient = resultSet.getBoolean("isClient");
				if (isAdmin == true && isClient == false)
					result.setRole(true);
				if (isAdmin == false && isClient == true)
					result.setRole(false);
				if ((isAdmin == true && isClient == true) || (isAdmin == false && isClient == false))
					throw new SQLException();//db implementation error
			}
			return result;

		} catch (SQLException e) {
			System.out.println(e.getMessage());
			throw new SQLException(e.getMessage());
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
	}
	
	public boolean checkUsername(String username) throws SQLException {
		String query = "SELECT `username` FROM `user` WHERE `username` = ? AND `isClient` = true";
		ResultSet resultSet = null;
		PreparedStatement pStatement = null;
		try {
			pStatement = connection.prepareStatement(query);
			pStatement.setString(1, username);
			resultSet = pStatement.executeQuery();

			if (resultSet.next()) {
				throw new SQLException("Username already present");
			}
			return false;
		} catch (SQLException e) {
			System.out.println(e.getMessage());
			throw new SQLException(e.getMessage());
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
	}

	public int register(String username, String password) throws SQLException {
		int code = 0;
		// only clients can register through this way. Employees need to get their
		// credentials from db admin
		String query = "INSERT into `user` (`username`, `password`, `isClient`, `isEmployee`) VALUES(?, ?, true, false)";
		PreparedStatement pstatement = null;
		try {
			pstatement = connection.prepareStatement(query);
			if (StringUtils.isAlphanumeric(username) && StringUtils.isAlphanumeric(password)) {
				pstatement.setString(1, username);
				pstatement.setString(2, password);
				code = pstatement.executeUpdate();
			} else
				throw new SQLException();
		} catch (SQLException e) {
			// System.out.println(e.getMessage());
			throw new SQLException(e.getMessage());
		} finally {
			try {
				if (pstatement != null) {
					pstatement.close();
				}
			} catch (Exception e1) {
				// System.out.println(e1.getMessage());
				throw new SQLException(e1);
			}
		}
		return code;
	}

}
