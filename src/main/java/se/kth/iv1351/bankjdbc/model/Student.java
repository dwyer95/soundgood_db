package se.kth.iv1351.bankjdbc.model;

/**
 * A student in the db. Inherits the <code>Person</code> class.
 */
public class Student extends Person {
    private int studentId;
    private int totalFee;
    private int instrumentQuota;

    /**
     * Creates a student object with the specified person ID, person number, first name, last name, 
     * student ID, current month's fee and instrument quota. The properties <code>personId</code>, 
     * <code>personNumber</code>, <code>firstName</code> and <code>lastName</code> are inherited 
     * from the <code>Person</code> class.
     *
     * @param personId          The student's person ID.
     * @param personNumber      The person number of the student.
     * @param firstName         The student's first name.
     * @param lastName          The student's last name.
     * @param studentId         The student's student ID.
     * @param totalFee          The amount for the student to pay in the current month.
     * @param instrumentQuota   The number of instruments currently rented by the student.
     */
    public Student(int personId, String personNumber, String firstName, String lastName,
     int studentId, int totalFee, int instrumentQuota) {
        super(personId, personNumber, firstName, lastName);
        this.studentId = studentId;
        this.totalFee = totalFee;
        this.instrumentQuota = instrumentQuota;
    }

    /**
     * @return The student's id.
     */
    public int getStudentId() {
        return studentId;
    }

    /**
     * @return Student's fee this month.
     */
    public int getTotalFee() {
        return totalFee;
    }

    /**
     * @return Number of instruments rented by students.
     */
    public int getInstrumentQuota() {
        return instrumentQuota;
    }

    /**
     * Updates the student object to reflect the rental of an instrument.
     *
     * @param instrument The instrument to be rented.
     * @throws RejectedException If unable to rent an instrument.
     */
    public void rent(Instrument instrument) throws RejectedException {
        if(this.instrumentQuota > 1){
            throw new RejectedException("TRIED TO RENT AN INSTRUMENT DESPITE FULL QUOTA."
                + " RETURN AN INSTRUMENT AND TRY AGAIN.");
        }

        this.totalFee += instrument.getMonthlyPrice();
        this.instrumentQuota++;
    }

    /**
     * Updates the student object to reflect the return of a rented instrument.
     *
     * @param instrument The instrument to be returned.
     * @throws RejectedException If unable to return an instrument.
     */
    public void returnInstrument(Instrument instrument) throws RejectedException {
        if(this.instrumentQuota < 1){
            throw new RejectedException("TRIED TO RETURN AN INSTRUMENT DESPITE NONE RENTED."
                + " RENT AN INSTRUMENT AND TRY AGAIN.");
        }

        this.totalFee -= instrument.getMonthlyPrice();
        this.instrumentQuota--;
    }


}