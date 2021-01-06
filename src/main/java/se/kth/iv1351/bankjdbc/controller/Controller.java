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

package se.kth.iv1351.bankjdbc.controller;

import java.util.ArrayList;
import java.util.List;

import se.kth.iv1351.bankjdbc.integration.BankDAO;
import se.kth.iv1351.bankjdbc.integration.BankDBException;
import se.kth.iv1351.bankjdbc.model.StudentInstrumentException;
import se.kth.iv1351.bankjdbc.model.RejectedException;

import se.kth.iv1351.bankjdbc.model.Instrument;
import se.kth.iv1351.bankjdbc.model.InstrumentDTO;
import se.kth.iv1351.bankjdbc.model.Person;
import se.kth.iv1351.bankjdbc.model.Student;
import se.kth.iv1351.bankjdbc.model.Rental;

import java.time.format.DateTimeFormatter;  
import java.time.LocalDateTime;
import java.sql.Timestamp;

/**
 * This is the application's only controller, all calls to the model pass here.
 * The controller is also responsible for calling the DAO. Typically, the
 * controller first calls the DAO to retrieve data (if needed), then operates on
 * the data, and finally tells the DAO to store the updated data (if any).
 */
public class Controller {
    private final BankDAO bankDb;

    /**
     * Creates a new instance, and retrieves a connection to the database.
     * 
     * @throws BankDBException If unable to connect to the database.
     */
    public Controller() throws BankDBException {
        bankDb = new BankDAO();
    }

    /**
     * Lists all instruments on stock in the database.
     * 
     * @return A list containing all instruments. The list is empty if there 
     *         are no instruments in stock.
     * @throws StudentInstrumentException If unable to retrieve instruments.
     */
    public List<? extends InstrumentDTO> getAllInstruments() throws StudentInstrumentException {
        try { return bankDb.findAllInstruments(); } 
        catch (Exception e) {
            throw new StudentInstrumentException("Unable to list instruments.", e);
        }
    }

    /**
     * Lists all instruments rented by a student.
     * 
     * @param studentId The student's student ID.
     * @return A list containing all instruments rented by a student. The list is empty 
     *         if the student has no current rentals.
     * @throws StudentInstrumentException If unable to retrieve instruments.
     */
    public List<? extends Instrument> getInstrumentsForStudent(int studentId) throws StudentInstrumentException {
        try { return bankDb.findInstrumentsByStudentId(studentId); } 
        catch (Exception e) {
            throw new StudentInstrumentException("Could not search for student.", e);
        }
    }

    /**
     * Creates a new rental for the specified student for an instrument of the specified
     * type and brand.
     * 
     * @param studentId The ID of the student making the rental.
     * @param type The type of instrument.
     * @param brand The instrument's brand.
     * @throws RejectedException If not allowed to rent the specified instrument. 
     * @throws StudentInstrumentException  If failed to rent. 
     */
    public void rent(int studentId, String type, String brand) throws RejectedException, StudentInstrumentException {
        String failureMsg = "Could not rent instrument for student with ID: " + studentId;
        try {
            Student student = bankDb.findStudentByStudentId(studentId);
            Instrument instrument = bankDb.findInstrumentByTypeAndBrand(type, brand);
            student.rent(instrument);

            Rental rental = new Rental(student.getStudentId(), instrument.getInstrumentId());
            instrument.decrementStock();
            bankDb.updateRentalsRent(student, instrument, rental);
            
            System.out.println("SUCCESSFULLY RENTED OUT " + type + ", OF BRAND " + brand + ", TO STUDENT WITH ID "
            + studentId);
        } catch (BankDBException bdbe) {
            throw new StudentInstrumentException(failureMsg, bdbe);
        }
    }

    /**
     * Returns the specified <code>Instrument</code> from a student with the specified student ID.
     * 
     * @param studentId The ID of the student returning the instrument.
     * @param instrument The instrument being returned.
     * @throws RejectedException If not allowed to return the specified instrument. 
     * @throws StudentInstrumentException  If failed to return. 
     */
    public void returnInstrument(int studentId, Instrument instrument) throws RejectedException, StudentInstrumentException {
        String failureMsg = "Could not return instrument for student with ID: " + studentId;
        try {
            Student student = bankDb.findStudentByStudentId(studentId);
            student.returnInstrument(instrument);
            Rental rental = bankDb.findRental(studentId, instrument);
            instrument.incrementStock();

            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            rental.setRentalEnd(Timestamp.valueOf(dtf.format(LocalDateTime.now())));
            bankDb.updateRentalsReturn(student, instrument, rental);
            
            System.out.println("SUCCESSFULLY RETURNED INSTRUMENT");
        } catch (BankDBException bdbe) {
            throw new StudentInstrumentException(failureMsg, bdbe);
        }
    }
}
