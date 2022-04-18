package base.salesDepartment.Method;


public class publicMethod {

    public static boolean checkNumeric(String str){
        try {
            if(Long.parseLong(str) < 0){
                return false;
            }
            return true;
        }catch (NumberFormatException e){
            return false;
        }
    }

    public static boolean checkFloat(String str){
        try {
            if(Long.parseLong(str) < 0){
                return false;
            }
            return true;
        }catch (NumberFormatException e){
            try{
                Float.parseFloat(str);
                return true;
            }catch (NumberFormatException exception){
                return false;
            }
        }
    }

    public static boolean checkName(String name){
        //to avoid leading white space in name
        System.out.println("0");
        if(name.charAt(0) == ' '){
            System.out.println("0");
            return false;
        }
        for (int i = 0; i < name.length(); i++) {
            if(!Character.isLetter(name.charAt(i)) && !Character.isWhitespace(name.charAt(i))){

                System.out.println(name.charAt(i)+"\t i");
                return false;
            }
        }
        return true;
    }
    public static boolean checkEmail(String email){
        String[] emailDomains = {"@gmail.com", "@hotmail.com", "@mail.com", "@yahoo.com"};

        if(email.charAt(0) == '@'){
            return false;
        }

        for (int i = 0; i < emailDomains.length ; i++) {
            if(email.contains(emailDomains[i])){
                return true;
            }
        }
        return false;
    }

    public static boolean checkContact(String contact){
        int count = 0;
        for (int i = 0; i < contact.length(); i++) {
            if(!Character.isDigit(contact.charAt(i))){
                if(contact.charAt(i) == '-'){
                    count++;
                    //i is the index of '-' and only can placed at these three position
                    if(i == 2 || i == 3 || i ==7){

                    }else{
                        return false;
                    }
                }else if(count >2){
                    return false;
                }else{
                    return false;
                }
            }
        }
        if(count ==0){
            return false;
        }
        return true;
    }

    public static String[] checkLeadInfo(String email, String name, String contact, String location, String postcode){
        String[] value = new String[2];
        value[0] = "true";

        if (email.contains(" ")) {
            value[0] = "false";
            value[1] = "Please avoid spacing in email!";
        }
        else if(!checkEmail(email)){
            value[0] = "false";
            value[1] = "Invalid Email address!";
        }else if(!checkName(name)){
            value[0] = "false";
            value[1] = "Invalid Name input, please do not include special character in name";
        }else if(name.length()>50){
            value[0] = "false";
            value[1] = "Length of customer name cannot be greater than 30!";
        }else if(contact.length() != 12){
            value[0] = "false";
            value[1] = " Invalid contact number!\n Please follow the format:\n ###-###-#### or ##-####-####!";
        }else if(!checkContact(contact)) {
            value[0] = "false";
            value[1] = " Invalid contact number!\n Please follow the format:\n ###-###-#### or ##-####-####!";
        }else if(location.charAt(0) == ' '){
            value[0] = "false";
            value[1] = "Please remove trailing space in address!";
        }else if(location.length() > 300){
            value[0] = "false";
            value[1] = "Lenght of address cannot exceed 300 words!";
        }else if(postcode.length() != 5 || !checkNumeric(postcode)){
            value[0] = "false";
            value[1] = "Invalid postcode!";
        }
    return value;

    }
}
