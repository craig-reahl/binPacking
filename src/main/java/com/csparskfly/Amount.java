package com.csparskfly;

import java.math.BigDecimal;
import java.util.Comparator;

public class Amount implements Comparable<Amount>{
    BigDecimal amountValue;

    Amount(BigDecimal amountValue){
        this.amountValue = amountValue;
    }

    public Amount(int i) {
        this(new BigDecimal(i));
    }

    public Amount(long l) {
        this(new BigDecimal(l));
    }

    @Override
    public String toString() {
        return amountValue.toString();
    }

    public Amount plus(Amount amount) {
        return new Amount(this.amountValue.add(amount.amountValue));
    }

    public Amount minus(Amount amount) {
        return new Amount(this.amountValue.subtract(amount.amountValue));
    }

    public BigDecimal getAmountValue() {
        return amountValue;
    }

    @Override
    public int compareTo(Amount o) {
        return Comparator.comparing(Amount::getAmountValue)
                .compare(this,o);
    }


}
