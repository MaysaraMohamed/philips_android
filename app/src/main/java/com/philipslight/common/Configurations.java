package com.philipslight.common;

public class Configurations {
    // For online test
//    private static String baseURL = "http://ec2-18-217-38-100.us-east-2.compute.amazonaws.com:8080";
    // For offline test
//    private static String baseURL = "http://10.0.2.2:8080";
//    private static String baseURL = "http://192.168.1.4:8080";
//    private static String baseURL = "http://172.20.10.12:8080";
    private static String baseURL = "http://105.199.58.214:8080";
    private static String loginURL = "/login";
    private static String sendPasswordURL = "/sendpassword";
    private static String restPasswordURL = "/restpassword";
    private static String registerURL = "/user";
    private static String submitInvoice = "/submitInvoice";
    private static String categoriesWithSub = "/categoriesWithSub";
    private static String gifts = "/gifts";
    private static String allGifts = "/allGifts";
    private static String updatePoints = "/updatePoints";
    private static String userProfile = "/userProfile";
    private static String userPoints = "/pointsHistory";
    private static String userProfileImage = "/uploadProfileImage";
    private static String changePassword = "/changePassword";
    private static String submitedInvoices ="/submitedInvoices";



    private Configurations() {

    }

    public static String getLoginURL() {
        return baseURL + loginURL;
    }

    public static String getSendPasswordURL() {
        return baseURL + sendPasswordURL;
    }

    public static String getRestPasswordURL() {
        return baseURL + restPasswordURL;
    }

    public static String getRegisterURL() {
        return baseURL + registerURL;
    }

    public static String getSubmitInvoice() { return baseURL + submitInvoice;  }

    public static String getCategoriesWithSub() { return baseURL + categoriesWithSub;  }

    public static String getGiftsURL() { return baseURL + gifts;  }

    public static String getAllGiftsURL() { return baseURL + allGifts;  }

    public static String getUpdatePoints() { return baseURL + updatePoints;  }

    public static String getUserProfileImage() { return baseURL + userProfileImage;  }

    public static String getUserProfile() { return baseURL + userProfile;  }

    public static String getUserPoints() { return baseURL + userPoints;  }

    public static String getChangePassword() { return baseURL + changePassword;  }
    public static String getSubmitedInvoices() { return baseURL + submitedInvoices;  }
    public static String getPassword() { return "Philips+Maysara@2018";  }
    public static String getUserName() { return "user";  }


}
