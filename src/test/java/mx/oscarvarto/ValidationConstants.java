package mx.oscarvarto;

public class ValidationConstants {
    public static final int MAX_AGE = 130;
    public static final String NAME_EMPTY_OR_WHITESPACE_ERROR_MSG = "Name cannot be empty or contain only white space";
    public static final String NEGATIVE_AGE_ERROR_MSG = "Age cannot be negative";
    public static final String MAX_AGE_ERROR_MSG = "Age cannot be bigger than %s years".formatted(MAX_AGE);

    private ValidationConstants() {}
}
