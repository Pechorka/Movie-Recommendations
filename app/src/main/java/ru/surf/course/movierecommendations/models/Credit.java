package ru.surf.course.movierecommendations.models;

/**
 * Created by andrew on 2/3/17.
 */

public class Credit {

    protected String mCreditId;
    protected Person mPerson;

    public Credit(String creditId, Person person) {
        mCreditId = creditId;
        mPerson = person;
    }

    public String getCreditId() {
        return mCreditId;
    }

    public void setCreditId(String creditId) {
        mCreditId = creditId;
    }

    public Person getPerson() {
        return mPerson;
    }

    public void setPerson(Person person) {
        mPerson = person;
    }
}
