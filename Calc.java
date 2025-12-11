import java.util.ArrayList;

public class Calc {
    private float result = 0;
    public static void main(String[] args) {
        if(args.length != 1 ) {
            System.out.println("Use: java ./Calc.java <expression>");
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
        
        ArrayList<Token> tokens = tokenize(expression);
        
        if (!validate(tokens)) {
            error("Expressão inválida");
            return;
        }
        
        float result = evaluate(tokens);
        setResult(result);
        System.out.println("Resultado: " + result);
    }
    
    ArrayList<Token> tokenize(String expression) {
        ArrayList<Token> tokens = new ArrayList<>();
        StringBuilder number = new StringBuilder();
        
        for (int i = 0; i < expression.length(); i++) {
            char c = expression.charAt(i);
            
            if (c == ' ') continue;
            
            if (Character.isDigit(c) || c == '.') {
                number.append(c);
            } else if (c == '(' || c == ')') {
                if (number.length() > 0) {
                    tokens.add(new Token(Float.parseFloat(number.toString())));
                    number = new StringBuilder();
                }
                tokens.add(new Token(c, true));
            } else if (c == '+' || c == '-' || c == '*' || c == '/' || c == '^') {
                if (number.length() > 0) {
                    tokens.add(new Token(Float.parseFloat(number.toString())));
                    number = new StringBuilder();
                }
                if (c == '-' && (i == 0 || expression.charAt(i-1) == '(' || isOperator(expression.charAt(i-1)))) {
                    number.append(c);
                } else {
                    tokens.add(new Token(c, false));
                }
            } else {
                error("Caractere inválido: " + c);
            }
        }
        
        if (number.length() > 0) {
            tokens.add(new Token(Float.parseFloat(number.toString())));
        }
        
        return tokens;
    }
    
    boolean isOperator(char c) {
        return c == '+' || c == '-' || c == '*' || c == '/' || c == '^';
    }
    
    boolean validate(ArrayList<Token> tokens) {
        if (tokens.isEmpty()) return false;
        
        int parenCount = 0;
        boolean expectOperand = true;
        
        for (int i = 0; i < tokens.size(); i++) {
            Token token = tokens.get(i);
            
            if (token.type == Token.Type.LPAREN) {
                parenCount++;
                expectOperand = true;
            } else if (token.type == Token.Type.RPAREN) {
                parenCount--;
                if (parenCount < 0) return false;
                expectOperand = false;
            } else if (token.type == Token.Type.NUMBER) {
                if (!expectOperand && i > 0 && tokens.get(i-1).type != Token.Type.LPAREN) {
                    return false;
                }
                expectOperand = false;
            } else if (token.type == Token.Type.OPERATOR) {
                if (expectOperand) return false;
                expectOperand = true;
            }
        }
        
        return parenCount == 0 && !expectOperand;
    }
    
    float evaluate(ArrayList<Token> tokens) {
        tokens = resolveParentheses(tokens);
        tokens = resolveOperator(tokens, '^');
        tokens = resolveOperator(tokens, '*', '/');
        tokens = resolveOperator(tokens, '+', '-');
        
        if (tokens.size() == 1 && tokens.get(0).type == Token.Type.NUMBER) {
            return tokens.get(0).value;
        }
        
        error("Erro ao avaliar expressão");
        return 0;
    }
    
    ArrayList<Token> resolveParentheses(ArrayList<Token> tokens) {
        while (true) {
            int openIndex = -1;
            int closeIndex = -1;
            for (int i = 0; i < tokens.size(); i++) {
                if (tokens.get(i).type == Token.Type.LPAREN) {
                    openIndex = i;
                } else if (tokens.get(i).type == Token.Type.RPAREN) {
                    closeIndex = i;
                    break;
                }
            }
            
            if (openIndex == -1) break;
            ArrayList<Token> subTokens = new ArrayList<>();
            for (int i = openIndex + 1; i < closeIndex; i++) {
                subTokens.add(tokens.get(i));
            }
            
            float subResult = evaluate(subTokens);
            
            ArrayList<Token> newTokens = new ArrayList<>();
            for (int i = 0; i < openIndex; i++) {
                newTokens.add(tokens.get(i));
            }
            newTokens.add(new Token(subResult));
            for (int i = closeIndex + 1; i < tokens.size(); i++) {
                newTokens.add(tokens.get(i));
            }
            
            tokens = newTokens;
        }
        
        return tokens;
    }
    
    ArrayList<Token> resolveOperator(ArrayList<Token> tokens, char... operators) {
        for (int i = 0; i < tokens.size(); i++) {
            Token token = tokens.get(i);
            
            if (token.type == Token.Type.OPERATOR) {
                boolean match = false;
                for (char op : operators) {
                    if (token.operator == op) {
                        match = true;
                        break;
                    }
                }
                
                if (match && i > 0 && i < tokens.size() - 1) {
                    Token left = tokens.get(i - 1);
                    Token right = tokens.get(i + 1);
                    
                    if (left.type == Token.Type.NUMBER && right.type == Token.Type.NUMBER) {
                        float result = applyOperator(left.value, right.value, token.operator);
                        
                        ArrayList<Token> newTokens = new ArrayList<>();
                        for (int j = 0; j < i - 1; j++) {
                            newTokens.add(tokens.get(j));
                        }
                        newTokens.add(new Token(result));
                        for (int j = i + 2; j < tokens.size(); j++) {
                            newTokens.add(tokens.get(j));
                        }
                        
                        tokens = newTokens;
                        i = -1;
                    }
                }
            }
        }
        
        return tokens;
    }
    
    float applyOperator(float a, float b, char operator) {
        switch (operator) {
            case '+': return sum(a, b);
            case '-': return min(a, b);
            case '*': return mult(a, b);
            case '/': 
                if (b == 0) error("Divisão por zero");
                return a / b;
            case '^': return elevate(a, b);
            default: error("Operador desconhecido: " + operator);
        }
        return 0;
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

class Token {
    enum Type { NUMBER, OPERATOR, LPAREN, RPAREN }
    Type type;
    float value;
    char operator;
    
    Token(float value) {
        this.type = Type.NUMBER;
        this.value = value;
    }
    
    Token(char operator, boolean isParen) {
        if (isParen) {
            this.type = operator == '(' ? Type.LPAREN : Type.RPAREN;
        } else {
            this.type = Type.OPERATOR;
        }
        this.operator = operator;
    }
    
    @Override
    public String toString() {
        if (type == Type.NUMBER) return String.valueOf(value);
        return String.valueOf(operator);
    }
}