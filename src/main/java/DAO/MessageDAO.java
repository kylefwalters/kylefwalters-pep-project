package DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import Util.ConnectionUtil;
import Model.Message;

public class MessageDAO {
    /**
    * Searches database for message with matching message_id
    * @param message_id id of the message to search for
    * @return returns message that matches message_id, if one is not found returns null instead
    */
    public Message getMessage(int message_id) {
        Connection connection = ConnectionUtil.getConnection();
        try {
            String sql = "SELECT * FROM message WHERE message_id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);

            preparedStatement.setInt(1, message_id);

            ResultSet rs = preparedStatement.executeQuery();
            if(rs.next()) {
                Message foundMessage = new Message();
                foundMessage.setMessage_id(rs.getInt("message_id"));
                foundMessage.setMessage_text(rs.getString("message_text"));
                foundMessage.setPosted_by(rs.getInt("posted_by"));
                foundMessage.setTime_posted_epoch((int)rs.getLong("time_posted_epoch"));
                return foundMessage;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    /**
    * Retrieves all messages posted by account_id
    * @param account_id id of the account messages are posted by
    * @return returns list of messages sent by account
    */
    public List<Message> getAllMessagesForUser(int account_id) {
        List<Message> messages = new ArrayList<Message>();
        Connection connection = ConnectionUtil.getConnection();
        try {
            String sql = "SELECT * FROM message WHERE posted_by = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);

            preparedStatement.setInt(1, account_id);

            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                Message message = new Message();
                message.setMessage_id(rs.getInt("message_id"));
                message.setMessage_text(rs.getString("message_text"));
                message.setPosted_by(rs.getInt("posted_by"));
                message.setTime_posted_epoch((int)rs.getLong("time_posted_epoch"));
                messages.add(message);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return messages;
    }

    /**
    * Retrieves all messages from database
    * @return returns list of messages sent by account
    */
    public List<Message> getAllMessages() {
        List<Message> messages = new ArrayList<Message>();
        Connection connection = ConnectionUtil.getConnection();
        try {
            String sql = "SELECT * FROM message";
            Statement preparedStatement = connection.createStatement();

            ResultSet rs = preparedStatement.executeQuery(sql);
            while (rs.next()) {
                Message message = new Message();
                message.setMessage_id(rs.getInt("message_id"));
                message.setMessage_text(rs.getString("message_text"));
                message.setPosted_by(rs.getInt("posted_by"));
                message.setTime_posted_epoch((int)rs.getLong("time_posted_epoch"));
                messages.add(message);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return messages;
    }

    /**
    * Adds message to dsatabase if message is not blank, no longer than 255 characters, and posted_by refers to a real user
    * @param message the message to be added to the databse
    * @return returns message with new message_id if successfully added to databse, returns null otherwise
    */
    public Message insertMessage(Message message) {
        Connection connection = ConnectionUtil.getConnection();
        try {
            String sql = "INSERT INTO message (posted_by, message_text, time_posted_epoch) VALUES (?, ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            preparedStatement.setInt(1, message.getPosted_by());
            preparedStatement.setString(2, message.getMessage_text());
            preparedStatement.setLong(3, message.getTime_posted_epoch());

            preparedStatement.executeUpdate();
            ResultSet pkeyResultSet = preparedStatement.getGeneratedKeys();
            if(pkeyResultSet.next()) {
                int message_id = pkeyResultSet.getInt("message_id");
                return new Message(message_id, message.getPosted_by(), message.getMessage_text(), message.getTime_posted_epoch());
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    /**
    * Removes message from database with matching message_id
    * @param message_id id of the message to search for
    * @return returns message that matches message_id, if one is not found returns null instead
    */
    public Message deleteMessage(int message_id) {
        Connection connection = ConnectionUtil.getConnection();
        try {
            Message foundMessage = getMessage(message_id);

            String sql = "DELETE FROM message WHERE message_id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);

            preparedStatement.setInt(1, message_id);

            int result = preparedStatement.executeUpdate();
            if(result > 0) {
                return foundMessage;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    /**
    * Updates message with matching message_id
    * @param message_id id of the message to search for
    * @return returns message that matches message_id with updated message_text, if one is not found returns null instead
    */
    public Message updateMessage(int message_id, String message_text) {
        Connection connection = ConnectionUtil.getConnection();
        try {
            Message foundMessage = getMessage(message_id);

            String sql = "UPDATE message SET message_text = ? WHERE message_id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);

            preparedStatement.setString(1, message_text);
            preparedStatement.setInt(2, message_id);

            int result = preparedStatement.executeUpdate();
            if(result > 0) {
                foundMessage.setMessage_text(message_text);
                return foundMessage;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }
}
