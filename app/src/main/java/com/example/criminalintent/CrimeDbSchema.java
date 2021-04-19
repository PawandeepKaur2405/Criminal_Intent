package com.example.criminalintent;

public class CrimeDbSchema {

    public static final class CrimeTable
    {
        public static final String NAME = "crimes";

        public static final class cols
        {
            public static final String UUID = "uuid";
            public static final String TITLE = "title";
            public static final String DATE = "date";
            public static final String SOLVED = "solved";
            public static final String SUSPECT = "suspect";
            public static final String PHONE = "phone";
        }
    }


}
