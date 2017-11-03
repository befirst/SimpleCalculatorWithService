package com.example.simbirsoft.denis.calculatorservice;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainService extends Service {
    private static final String expressionRegex = "-?[0-9]+[\\.]?[0-9]*[+\\-*/]-?[0-9]+[\\.]?[0-9]*";
    private static final String acceptableMathSignsRegex = "[+\\-*/]";
    private static final String numberRegex = "-?[0-9]+[\\.]?[0-9]*";
    private Pattern expressionPattern = Pattern.compile(expressionRegex);
    private Pattern mathSignsPattern = Pattern.compile(acceptableMathSignsRegex);
    private Pattern numberPattern = Pattern.compile(numberRegex);

    ExecutorService executor;

    public MainService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        executor = Executors.newSingleThreadExecutor();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        CalculatorRun calculator = new CalculatorRun();
        if(intent.hasExtra(MainActivity.CODE_EXTRA_EXPRESSION)){
            String expression = intent.getStringExtra(MainActivity.CODE_EXTRA_EXPRESSION);
            calculator.addExpression(expression)
                    .addPattern(expressionPattern)
                    .addMathSignsPattern(mathSignsPattern)
                    .addNumberPattern(numberPattern);
        }

        executor.execute(calculator);

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    enum MathSigns{
        PLUS,
        MINUS,
        MULTIPLY,
        DIVIDE
    }

    class CalculatorRun implements Runnable{
        private String expression;
        private Pattern expressionPattern;
        private Pattern mathSignsPattern;
        private Pattern numberPattern;
        private double firstNumber;
        private double secondNumber;
        private MathSigns sign;

        CalculatorRun() {
        }

        CalculatorRun addExpression(String expression){
            this.expression = expression;
            return this;
        }

        CalculatorRun addPattern(Pattern expressionPattern){
            this.expressionPattern = expressionPattern;
            return this;
        }

        CalculatorRun addMathSignsPattern(Pattern mathSignsPattern){
            this.mathSignsPattern = mathSignsPattern;
            return this;
        }

        CalculatorRun addNumberPattern(Pattern numberPattern){
            this.numberPattern = numberPattern;
            return this;
        }

        @Override
        public void run() {
            if(isExpressionAcceptable()){
                if(parse()){
                    Double result = calculate();
                    if(result != null){
                        sendResult(result);
                        return;
                    } else {
                        sendErrorDivideByZero();
                        return;
                    }
                }
            }
            sendError();
            return;
        }

        private boolean isExpressionAcceptable(){
            return expressionPattern.matcher(expression).matches();
        }

        private boolean parse(){
            Matcher matcher = mathSignsPattern.matcher(expression);
            if(matcher.find()){
                try {
                    switch(expression.toCharArray()[matcher.start()]){
                        case '+': sign = MathSigns.PLUS; break;
                        case '-': sign = MathSigns.MINUS; break;
                        case '*': sign = MathSigns.MULTIPLY; break;
                        case '/': sign = MathSigns.DIVIDE; break;
                    }
                    matcher = numberPattern.matcher(expression);

                    matcher.find();
                    firstNumber = Double.valueOf(expression.substring(matcher.start(), matcher.end()));

                    matcher.find();
                    secondNumber = Double.valueOf(expression.substring(matcher.start(), matcher.end()));
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
                return true;
            }
            return false;
        }

        private Double calculate(){
            Double result = null;
            switch (sign){
                case PLUS: result = firstNumber + secondNumber; break;
                case MINUS: result = firstNumber - secondNumber; break;
                case MULTIPLY: result = firstNumber * secondNumber; break;
                case DIVIDE: result = secondNumber != 0 ? firstNumber / secondNumber : null; break;
            }
            return result;
        }

        private void sendError(){
            Intent intent  = new Intent(MainActivity.CODE_BROADCAST_ACTION);
            intent.putExtra(MainActivity.CODE_STATUS, MainActivity.CODE_ERROR);
            sendBroadcast(intent);
        }

        private void sendErrorDivideByZero(){
            Intent intent  = new Intent(MainActivity.CODE_BROADCAST_ACTION);
            intent.putExtra(MainActivity.CODE_STATUS, MainActivity.CODE_ERROR_DIVIDE_BY_ZERO);
            sendBroadcast(intent);
        }

        private void sendResult(double result){
            Intent intent  = new Intent(MainActivity.CODE_BROADCAST_ACTION);
            intent.putExtra(MainActivity.CODE_STATUS, MainActivity.CODE_SUCCESS);
            intent.putExtra(MainActivity.CODE_RESULT, result);
            sendBroadcast(intent);
        }
    }
}
