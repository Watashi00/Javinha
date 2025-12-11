import java.util.ArrayList;

public class Calc {
    private float result = 0;
    public static void main(String[] args) {
        if(args.length != 1 ) {
            System.out.println("O merda, usa assim: java ./Calc.java <expression>");
            return;
        }
        String expression = args[0];
        new Calc().solver(expression);
    }

    float min(float a, float b) {
        return a - b;
    }

    float sum(float a, float b) {
        return a + b;
    }

    float elevate(float a, float x) {
        float carry = 1;



        for(int i = 0; i <= x; i++) {
            carry = carry * a;
        }

        return carry;
    }

    float sqrt(float a) {
        return (float) elevate(a, 1/2);
    }

    float mult(float a, float b) {
        return a * b;
    }

    void solver(String expression) {
        expression = expression.trim();
        if(expression.matches(".*[()]+.*")) {
            ArrayList<String> values = validateP(expression);
        }


    }



    ArrayList<String> validateP(String expression) {
        int p = 0;
        @SuppressWarnings("unchecked")
        ArrayList<String[]>[] levels = (ArrayList<String[]>[]) new ArrayList[5];
        ArrayList<String> values = new ArrayList<>(); 
        String e = "parentheses error";
        for(int i = 0; i < expression.length(); i++) {
            Character c = expression.charAt(i);
            if(expression.contains("()")) {
                error(e + " expression contains '()' ");
            }

            if(c == '(' ) {
                p++;
            }

            if(p >= levels.length){
                error(e + " Unsuported complexity");
            }

            if(levels[p] == null ) {
                System.out.println("o gloria" + p);
                levels[p] = new ArrayList<>();
            }

            if(p >= 1) {
                
            }

            if(c == ')' ) {
                p--;
            }

            if((p < 0 || p > 1) && i == expression.length() - 1) {
                error(e + " parentheses contains " + p + " more (positive)open|(negative)closed parentheses");
            }

        }

        System.out.println(p);
        return values;
    }


    public float getResult() {
        return result;
    }


    public void setResult(float result) {
        this.result = result;
    }

    void error (String error) {
        System.out.println(error);
        throw new Error(error);
    }
}
