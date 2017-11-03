package com.example.simbirsoft.denis.calculatorservice;

import android.os.Bundle;
import android.os.Handler;

/**
 * Created by user on 03.11.2017.
 */

public class CalculatorResultReceiver extends android.os.ResultReceiver {
    public static final int RESULT_CODE_OK = 1;
    public static final int RESULT_CODE_ERROR = -1;
    public static final int RESULT_CODE_DIVIDE_BY_ZERO = -11;

    private ResultReceiverCallBack receiverCallBack;

    public CalculatorResultReceiver(Handler handler) {
        super(handler);
    }

    public void setReceiver(ResultReceiverCallBack receiver) {
        receiverCallBack = receiver;
    }

    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData) {
        if (receiverCallBack != null) {
            switch (resultCode){
                case RESULT_CODE_OK :
                    receiverCallBack.onSuccess(resultData.getDouble(MainActivity.CODE_RESULT));
                    break;
                case RESULT_CODE_ERROR :
                    receiverCallBack.onError();
                    break;
                case RESULT_CODE_DIVIDE_BY_ZERO :
                    receiverCallBack.onErrorDivideByZero();
                    break;

            }
        }
    }

    public interface ResultReceiverCallBack {
        void onSuccess(double data);
        void onError();
        void onErrorDivideByZero();
    }
}

