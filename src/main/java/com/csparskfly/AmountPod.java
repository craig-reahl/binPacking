package com.csparskfly;

public class AmountPod {

    Amount amount;

    public AmountPod(Amount amount){
        this.amount = amount;
    }

    public Amount getAmount() {
        return amount;
    }

    @Override
    public String toString() {
        return ""+amount.getAmountValue();
    }
}
