/*
 * The MIT License (MIT)
 * Copyright (c) 2020 Leif Lindbäck
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction,including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so,subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package se.kth.iv1351.bankjdbc.integration;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import se.kth.iv1351.bankjdbc.model.Instrument;
import se.kth.iv1351.bankjdbc.model.InstrumentDTO;

import se.kth.iv1351.bankjdbc.model.Student;
import se.kth.iv1351.bankjdbc.model.Person;
import se.kth.iv1351.bankjdbc.model.Rental;

import java.sql.Types;

/**
 * This data access object (DAO) encapsulates all database calls in the bank
 * application. No code outside this class shall have any knowledge about the
 * database.
 */
public class BankDAO {
    private static final String INSTRUMENT_TABLE_NAME = "instrument";
    private static final String INSTRUMENT_PK_COLUMN_NAME = "instrument_id";
    private static final String TYPE_COLUMN_NAME = "type";
    private static final String BRAND_COLUMN_NAME = "brand";
    private static final String MONTHLY_PRICE_COLUMN_NAME = "monthly_price";
    private static final String STOCK_COLUMN_NAME = "stock";
    private static final String STUDENT_TABLE_NAME = "student";
    private static final String STUDENT_ID_COLUMN_NAME = "student_id";
    private static final String TOTAL_FEE_COLUMN_NAME = "total_fee";
    private static final String INSTRUMENT_QUOTA_COLUMN_NAME = "instrument_quota";
    private static final String PERSON_TABLE_NAME = "person";
    private static final String PERSON_ID_COLUMN_NAME = "person_id";
    private static final String PERSON_NUMBER_COLUMN_NAME = "person_number";
    private static final String FIRST_NAME_COLUMN_NAME = "first_name";
    private static final String LAST_NAME_COLUMN_NAME = "last_name";
    private static final String RENTALS_TABLE_NAME = "rentals";
    private static final String RENTAL_DATE_COLUMN_NAME = "rental_date";
    private static final String INSTRUMENT_ID_COLUMN_NAME = "instrument_id";
    private static final String RENTAL_END_COLUMN_NAME = "rental_end";

    private Connection connection;
    private PreparedStatement findAllInstrumentsStmt;
    private PreparedStatement findInstrumentByTypeAndBrandStmt;
    private PreparedStatement findStudentByStudentIdStmt;
    private PreparedStatement changeInstrumentQuotaStmt;
    private PreparedStatement changeFeeStmt;
    private PreparedStatement createRentalStmt;
    private PreparedStatement changeInstrumentStockStmt;
    private PreparedStatement findInstrumentsByStudentStmt;
    private PreparedStatement changeRentalStmt;
    private PreparedStatement findRentalStmt;

    /**
     * Constructs a new DAO object connected to the bank database.
     */
    public BankDAO() throws BankDBException {
        try {
            connectToBankDB();
            prepareStatements();
        } catch (ClassNotFoundException | SQLException exception) {
            throw new BankDBException("Could not connect to datasource.", exception);
        }
    }

    /**
     * Searches for all instruments rented by student with the specified student ID.
     *
     * @param studentId The student's student ID.
     * @return A list with all instruments currently rented by a student with the specified ID, 
     *         the list is empty if student currently has no rentals.
     * @throws BankDBException If failed to search for instruments.
     */
    public List<Instrument> findInstrumentsByStudentId(int studentId) throws BankDBException {
        String failureMsg = "Could not search for specified instruments.";
        ResultSet result = null;
        List<Instrument> instruments = new ArrayList<>();
        try {
            findInstrumentsByStudentStmt.setInt(1, studentId);
            result = findInstrumentsByStudentStmt.executeQuery();
            while (result.next()) {
                instruments.add(new Instrument(result.getInt(INSTRUMENT_PK_COLUMN_NAME),
                                         result.getString(TYPE_COLUMN_NAME),
                                         result.getString(BRAND_COLUMN_NAME),
                                         result.getInt(MONTHLY_PRICE_COLUMN_NAME),
                                         result.getInt(STOCK_COLUMN_NAME)
                                         )
                                );
            }
            connection.commit();
        } catch (SQLException sqle) {
            handleException(failureMsg, sqle);
        } finally {
            closeResultSet(failureMsg, result);
        }
        return instruments;
    }

    /**
     * Retrieves all instruments that are in stock.
     *
     * @return A list with instruments that are in stock. The list is empty if all are
     *         out of stock.
     * @throws BankDBException If failed to find instruments.
     */
    public List<Instrument> findAllInstruments() throws BankDBException {
        String failureMsg = "Could not list instruments.";
        List<Instrument> instruments = new ArrayList<>();
        try (ResultSet result = findAllInstrumentsStmt.executeQuery()) {
            while (result.next()) {
                if(result.getInt(STOCK_COLUMN_NAME) > 0){
                instruments.add(new Instrument(result.getInt(INSTRUMENT_PK_COLUMN_NAME),
                                         result.getString(TYPE_COLUMN_NAME),
                                         result.getString(BRAND_COLUMN_NAME),
                                         result.getInt(MONTHLY_PRICE_COLUMN_NAME),
                                         result.getInt(STOCK_COLUMN_NAME)
                                         )
                                );
                }
            }
            connection.commit();
        } catch (SQLException sqle) {
            handleException(failureMsg, sqle);
        }
        return instruments;
    }

    /**
     * Searches for an instrument of the specified type and brand.
     *
     * @param type The type of instrument
     * @param brand The brand of the instrument
     * @return An instrument fitting the criteria.
     * @throws BankDBException If failed to search for accounts.
     */
    public Instrument findInstrumentByTypeAndBrand(String type, String brand) throws BankDBException {
        String failureMsg = "Could not search for instrument of specified type and brand.";
        ResultSet result = null;
        try {
            findInstrumentByTypeAndBrandStmt.setString(1, type);
            findInstrumentByTypeAndBrandStmt.setString(2, brand);
            
            result = findInstrumentByTypeAndBrandStmt.executeQuery();
            if (result.next()) {
                if(result.getInt(STOCK_COLUMN_NAME) > 0){
                        return new Instrument(result.getInt(INSTRUMENT_PK_COLUMN_NAME),
                                         result.getString(TYPE_COLUMN_NAME),
                                         result.getString(BRAND_COLUMN_NAME),
                                         result.getInt(MONTHLY_PRICE_COLUMN_NAME),
                                         result.getInt(STOCK_COLUMN_NAME)
                        );
                }
            }
            connection.commit();
        } catch (SQLException sqle) {
            handleException(failureMsg, sqle);
        } finally {
            closeResultSet(failureMsg, result);
        }
        return null;
    }

    /**
     * Searches for a student according to student ID.
     *
     * @param studentId The student's student ID
     * @return The student whose ID corresponds with the specified student ID.
     * @throws BankDBException If failed to search for student.
     */
    public Student findStudentByStudentId(int studentId) throws BankDBException {
        String failureMsg = "Could not search for specified student.";
        ResultSet result = null;
        try {
            findStudentByStudentIdStmt.setInt(1, studentId);
            result = findStudentByStudentIdStmt.executeQuery();
            if (result.next()) {
                return new Student(result.getInt(PERSON_ID_COLUMN_NAME),
                                result.getString(PERSON_NUMBER_COLUMN_NAME),
                                result.getString(FIRST_NAME_COLUMN_NAME),
                                result.getString(LAST_NAME_COLUMN_NAME),
                                result.getInt(STUDENT_ID_COLUMN_NAME),
                                result.getInt(TOTAL_FEE_COLUMN_NAME),
                                result.getInt(INSTRUMENT_QUOTA_COLUMN_NAME));
            }
            connection.commit();
        } catch (SQLException sqle) {
            handleException(failureMsg, sqle);
        } finally {
            closeResultSet(failureMsg, result);
        }
        return null;
    }

    /**
     * Changes the total fee and instrument quota of the input <code>Student</code>, 
     * the stock of the input <code>Instrument</code>, and sets the rental date of 
     * the input <code>Rental</code> in the database.
     *
     * @param student The student whose info is being updated in the database.
     * @param instrument The instrument of which stock is being updated in database.
     * @param rental The rental of which info is being updated in database.
     * @throws BankDBException If unable to perform the updates.
     */
    public void updateRentalsRent(Student student, Instrument instrument, Rental rental) throws BankDBException {
        String failureMsg = "Could not update records of student: " + student;
        try {
            changeFeeStmt.setInt(1, student.getTotalFee());
            changeFeeStmt.setInt(2, student.getStudentId());

            changeInstrumentQuotaStmt.setInt(1, student.getInstrumentQuota());
            changeInstrumentQuotaStmt.setInt(2, student.getStudentId());

            createRentalStmt.setInt(1, rental.getStudentId());
            createRentalStmt.setInt(2, rental.getInstrumentId());
            createRentalStmt.setTimestamp(3, rental.getRentalDate());

            changeInstrumentStockStmt.setInt(1, instrument.getStock());
            changeInstrumentStockStmt.setInt(2, rental.getInstrumentId());

            int updatedRows = changeFeeStmt.executeUpdate();
            if (updatedRows != 1) {
                handleException(failureMsg, null);
            }

            updatedRows = changeInstrumentQuotaStmt.executeUpdate();
            if (updatedRows != 1) {
                handleException(failureMsg, null);
            }

            updatedRows = createRentalStmt.executeUpdate();
            if (updatedRows != 1) {
                handleException(failureMsg, null);
            }

            updatedRows = changeInstrumentStockStmt.executeUpdate();
            if (updatedRows != 1) {
                handleException(failureMsg, null);
            }

            connection.commit();
        } catch (SQLException sqle) {
            handleException(failureMsg, sqle);
        }
    }

    /**
     * Changes the total fee and instrument quota of the input <code>Student</code>, 
     * the stock of the input <code>Instrument</code>, and sets the date of the rental
     * ending for the input <code>Rental</code> in the database.
     *
     * @param student The student whose info is being updated in the database.
     * @param instrument The instrument of which stock is being updated in database.
     * @param rental The rental of which info is being updated in database.
     * @throws BankDBException If unable to perform the updates.
     */
    public void updateRentalsReturn(Student student, Instrument instrument, Rental rental) throws BankDBException {
        String failureMsg = "Could not update records of student: " + student;
        try {
            changeFeeStmt.setInt(1, student.getTotalFee());
            changeFeeStmt.setInt(2, student.getStudentId());

            changeInstrumentQuotaStmt.setInt(1, student.getInstrumentQuota());
            changeInstrumentQuotaStmt.setInt(2, student.getStudentId());

            changeRentalStmt.setTimestamp(1, rental.getRentalEnd());
            changeRentalStmt.setInt(2, student.getStudentId());
            changeRentalStmt.setInt(3, instrument.getInstrumentId());
            changeRentalStmt.setTimestamp(4, rental.getRentalDate());

            changeInstrumentStockStmt.setInt(1, instrument.getStock());
            changeInstrumentStockStmt.setInt(2, rental.getInstrumentId());

            int updatedRows = changeFeeStmt.executeUpdate();
            if (updatedRows != 1) {
                handleException(failureMsg, null);
            }

            updatedRows = changeInstrumentQuotaStmt.executeUpdate();
            if (updatedRows != 1) {
                handleException(failureMsg, null);
            }

            updatedRows = changeRentalStmt.executeUpdate();
            if (updatedRows != 1) {
                handleException(failureMsg, null);
            }

            updatedRows = changeInstrumentStockStmt.executeUpdate();
            if (updatedRows != 1) {
                handleException(failureMsg, null);
            }

            connection.commit();
        } catch (SQLException sqle) {
            handleException(failureMsg, sqle);
        }
    }

    /**
     * Searches for a rental according to the input student ID and input <code>Instrument</code>.
     *
     * @param studentId
     * @param instrument 
     * @return Rental fitting the criteria.
     * @throws BankDBException If failed to find rental.
     */
    public Rental findRental(int studentId, Instrument instrument) throws BankDBException {
        String failureMsg = "Could not search for specified rental.";
        ResultSet result = null;
        try {
            findRentalStmt.setInt(1, studentId);
            findRentalStmt.setInt(2, instrument.getInstrumentId());

            result = findRentalStmt.executeQuery();
            if (result.next()) {
                return new Rental(result.getInt(STUDENT_ID_COLUMN_NAME),
                                   result.getInt(INSTRUMENT_ID_COLUMN_NAME),
                                   result.getTimestamp(RENTAL_DATE_COLUMN_NAME));
            }
            connection.commit();
        } catch (SQLException sqle) {
            handleException(failureMsg, sqle);
        } finally {
            closeResultSet(failureMsg, result);
        }
        return null;

    }

    private void connectToBankDB() throws ClassNotFoundException, SQLException {
        connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/soundgood_db",
                                                 "postgres", "example");
        // connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/bankdb",
        //                                          "root", "javajava");
        connection.setAutoCommit(false);
    }

    private void prepareStatements() throws SQLException {
        findAllInstrumentsStmt = connection.prepareStatement("SELECT i." + INSTRUMENT_PK_COLUMN_NAME
            + ", i." + TYPE_COLUMN_NAME + ", i." + BRAND_COLUMN_NAME + ", i." + MONTHLY_PRICE_COLUMN_NAME 
            + ", i." + STOCK_COLUMN_NAME + " FROM " + INSTRUMENT_TABLE_NAME + " AS i");

        findInstrumentByTypeAndBrandStmt = connection.prepareStatement("SELECT i." + INSTRUMENT_PK_COLUMN_NAME
            + ", i." + TYPE_COLUMN_NAME + ", i." + BRAND_COLUMN_NAME + ", i." + MONTHLY_PRICE_COLUMN_NAME 
            + ", i." + STOCK_COLUMN_NAME + " FROM " + INSTRUMENT_TABLE_NAME + " AS i WHERE " + " i." 
            + TYPE_COLUMN_NAME + " = ? AND " + "i." + BRAND_COLUMN_NAME + " = ?");

        findStudentByStudentIdStmt = connection.prepareStatement("SELECT s." + STUDENT_ID_COLUMN_NAME
            + ", p." + PERSON_ID_COLUMN_NAME + ", p." + PERSON_NUMBER_COLUMN_NAME + ", p." + FIRST_NAME_COLUMN_NAME 
            + ", p." + LAST_NAME_COLUMN_NAME + ", s." + TOTAL_FEE_COLUMN_NAME + ", s." + INSTRUMENT_QUOTA_COLUMN_NAME 
            + " FROM " + STUDENT_TABLE_NAME + " s JOIN " + PERSON_TABLE_NAME + " p ON p." + PERSON_ID_COLUMN_NAME 
            + " = s." + PERSON_ID_COLUMN_NAME + " WHERE " + STUDENT_ID_COLUMN_NAME + " = ?");

        changeFeeStmt = connection.prepareStatement("UPDATE " + STUDENT_TABLE_NAME
            + " SET " + TOTAL_FEE_COLUMN_NAME + " = ? WHERE " + STUDENT_ID_COLUMN_NAME + " = ? ");

        changeInstrumentQuotaStmt = connection.prepareStatement("UPDATE " + STUDENT_TABLE_NAME
            + " SET " + INSTRUMENT_QUOTA_COLUMN_NAME + " = ? WHERE " + STUDENT_ID_COLUMN_NAME + " = ? ");

        createRentalStmt = connection.prepareStatement("INSERT INTO " + RENTALS_TABLE_NAME
            + "(" + STUDENT_ID_COLUMN_NAME + ", " + INSTRUMENT_ID_COLUMN_NAME + ", " + RENTAL_DATE_COLUMN_NAME
            + ") VALUES (?, ?, ?)");

        changeInstrumentStockStmt = connection.prepareStatement("UPDATE " + INSTRUMENT_TABLE_NAME
            + " SET " + STOCK_COLUMN_NAME + " = ? WHERE " + INSTRUMENT_ID_COLUMN_NAME + " = ? ");

        findInstrumentsByStudentStmt = connection.prepareStatement("SELECT r." + STUDENT_ID_COLUMN_NAME
            + ", i." + INSTRUMENT_ID_COLUMN_NAME + ", i." + TYPE_COLUMN_NAME + ", i." + BRAND_COLUMN_NAME 
            + ", i." + MONTHLY_PRICE_COLUMN_NAME + ", i." + STOCK_COLUMN_NAME + ", r." + RENTAL_DATE_COLUMN_NAME 
            + ", r." + RENTAL_END_COLUMN_NAME + " FROM " + RENTALS_TABLE_NAME + " r JOIN " + INSTRUMENT_TABLE_NAME 
            + " i ON i." + INSTRUMENT_ID_COLUMN_NAME + " = r." + INSTRUMENT_ID_COLUMN_NAME + " WHERE " 
            + STUDENT_ID_COLUMN_NAME + " = ? AND " + RENTAL_END_COLUMN_NAME + " IS NULL");

        findRentalStmt = connection.prepareStatement("SELECT r." + STUDENT_ID_COLUMN_NAME + ", r." 
            + INSTRUMENT_ID_COLUMN_NAME + ", r." + RENTAL_DATE_COLUMN_NAME + " FROM " + RENTALS_TABLE_NAME 
            + " AS r WHERE " + STUDENT_ID_COLUMN_NAME + " = ? AND " + INSTRUMENT_ID_COLUMN_NAME + " = ? AND " 
            + RENTAL_END_COLUMN_NAME + " IS NULL");

        changeRentalStmt = connection.prepareStatement("UPDATE " + RENTALS_TABLE_NAME
            + " SET " + RENTAL_END_COLUMN_NAME + " = ? WHERE " + STUDENT_ID_COLUMN_NAME + " = ? AND " 
            + INSTRUMENT_ID_COLUMN_NAME + " = ? AND " + RENTAL_DATE_COLUMN_NAME + " = ?");
    }

    private void handleException(String failureMsg, Exception cause) throws BankDBException {
        String completeFailureMsg = failureMsg;
        try {
            connection.rollback();
        } catch (SQLException rollbackExc) {
            completeFailureMsg = completeFailureMsg + 
            ". Also failed to rollback transaction because of: " + rollbackExc.getMessage();
        }

        if (cause != null) {
            throw new BankDBException(failureMsg, cause);
        } else {
            throw new BankDBException(failureMsg);
        }
    }

    private void closeResultSet(String failureMsg, ResultSet result) throws BankDBException {
        try {
            result.close();
        } catch (Exception e) {
            throw new BankDBException(failureMsg + " Could not close result set.", e);
        }
    }
}
