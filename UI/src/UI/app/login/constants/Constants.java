package UI.app.login.constants;

import com.google.gson.Gson;

public class Constants {

    // global constants
    public final static String LINE_SEPARATOR = System.getProperty("line.separator");

    // fxml locations
    public final static String MAIN_PAGE_FXML_RESOURCE_LOCATION = "UI/app/app.fxml";
    public final static String LOGIN_PAGE_FXML_RESOURCE_LOCATION = "UI/app/login/login.fxml";

    // Server resources locations
    public final static String BASE_DOMAIN = "localhost";
    private final static String BASE_URL = "http://" + BASE_DOMAIN + ":8080";
    private final static String CONTEXT_PATH = "/abs";
    private final static String FULL_SERVER_PATH = BASE_URL + CONTEXT_PATH;

    public final static String LOGIN_PAGE = FULL_SERVER_PATH + "/login";
    public final static String CUSTOMER_PAGE = FULL_SERVER_PATH + "/customerView";
    public final static String PROMOTE_YAZ = FULL_SERVER_PATH + "/promoteYaz";
    public final static String LOAD_DATA = FULL_SERVER_PATH + "/loadData";
    public static final String CUSTOMER_CAPITAL = FULL_SERVER_PATH + "/customerCapital";
    public static final String TRANSACTION_PAGE = FULL_SERVER_PATH + "/transaction";
    public static final String CATEGORIES_PAGE = FULL_SERVER_PATH + "/categories";
    public static final String CREATE_NEW_LOAN_PAGE = FULL_SERVER_PATH + "/createNewLoan";
    public static final String SELL_LOAN = FULL_SERVER_PATH + "/sellLoan";
    public static final String BUY_OFFERED_LOAN = FULL_SERVER_PATH + "/OfferedLoans";
    public static final String LOANS_INFO = FULL_SERVER_PATH + "/LoansInfo";
    public static final String BUY_LOAN = FULL_SERVER_PATH + "/BuyLoan";
    public static final String FETCH_MODE = FULL_SERVER_PATH + "/ModeFetch";
    public final static int REFRESH_RATE = 2000;
    public static final String FETCH_YAZ = FULL_SERVER_PATH + "/yazFetch";
    public static final String PAYBACK_LOAN_PAGE = FULL_SERVER_PATH + "/payBackLoan";
    public static final String JOIN_AS_LENDER = FULL_SERVER_PATH + "/joinAsLender";
    public static final String LOAN_ASSIGNMENT = FULL_SERVER_PATH + "/loanAssignment";
    public static final String ADMIN_EXISTENCE = FULL_SERVER_PATH + "/AdminExistence";
    public static final String ADMIN_VIEW = FULL_SERVER_PATH + "/AdminView";
    public static final String CHANGE_MODE = FULL_SERVER_PATH + "/changeMode";
    public static final String DELTA_INFO_PAGE = FULL_SERVER_PATH + "/CustomerDeltaInfo";
    public static final String CUSTOMER_REWIND_FETCH = FULL_SERVER_PATH + "/CustomerRewindFetch";



    // GSON instance
    public final static Gson GSON_INSTANCE = new Gson();
}
