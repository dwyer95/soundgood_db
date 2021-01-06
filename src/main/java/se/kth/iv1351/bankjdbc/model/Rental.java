package se.kth.iv1351.bankjdbc.model;

import java.time.format.DateTimeFormatter;  
import java.time.LocalDateTime;
import java.sql.Timestamp;

/**
 * A rental in the db.
 */
public class Rental{
    private int studentId;
    private int instrumentId;
    private Timestamp rentalDate;
    private Timestamp rentalEnd;

    public Rental(int studentId, int instrumentId){
        this.studentId = studentId;
        this.instrumentId = instrumentId;
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        this.rentalDate = Timestamp.valueOf(dtf.format(LocalDateTime.now()));
    }

    /**
     * Creates a rental object with the specified student ID, instrument ID and date the
     * rental took place.
     *
     * @param studentId         The student ID of the student having made the rental.
     * @param instrumentId      The instrument ID of the instrument having been rented.
     * @param rentalDate        The date and time on which the rental started.
     */
    public Rental(int studentId, int instrumentId, Timestamp rentalDate){
        this.studentId = studentId;
        this.instrumentId = instrumentId;
        this.rentalDate = rentalDate;
    }

    /**
     * @return The id of student renting.
     */
    public int getStudentId() {
        return studentId;
    }

    /**
     * @return The id of intrument being rented.
     */
    public int getInstrumentId() {
        return instrumentId;
    }

    /**
     * @return The date and time rental took place.
     */
    public Timestamp getRentalDate() {
        return rentalDate;
    }

    /**
     * @return Sets date and time rental ended.
     */
    public void setRentalEnd(Timestamp endOfRental) {
        this.rentalEnd = endOfRental;
    }

    /**
     * @return Returns date and time rental ended.
     */
    public Timestamp getRentalEnd() {
        return rentalEnd;
    }
}