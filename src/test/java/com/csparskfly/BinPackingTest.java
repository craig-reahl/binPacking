package com.csparskfly;

import static org.junit.Assert.assertTrue;

import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class BinPackingTest {
    static long bank_transaction_ids = 0;


    class BankTransaction extends AmountPod{
        private long id;
        public BankTransaction(String amount){
            super(new Amount(new BigDecimal(amount)));
            this.id = bank_transaction_ids++;
        }
    }
    
    @Test
    public void exampleTest() {

        LinkedList<AmountPod> amountPods = new LinkedList<>();
        amountPods.add(new BankTransaction("2"));
        amountPods.add(new BankTransaction("3"));
        amountPods.add(new BankTransaction("5"));
        amountPods.add(new BankTransaction("10"));
        amountPods.add(new BankTransaction("8"));
        amountPods.add(new BankTransaction("7"));
        amountPods.add(new BankTransaction("2"));
        amountPods.add(new BankTransaction("1"));
        amountPods.add(new BankTransaction("1"));
        amountPods.add(new BankTransaction("1"));
        amountPods.add(new BankTransaction("1"));
        amountPods.add(new BankTransaction("11"));

        Amount AMOUNT_UPPER_LIMIT = new Amount(10);
        Amount AMOUNT_LOWER_LIMIT = new Amount(2);

        List<Bin<AmountPod>> bins = BinFactory.BestFit(amountPods, AMOUNT_LOWER_LIMIT, AMOUNT_UPPER_LIMIT);

        String expected = "Bin[1] items[1]\n" +
                            "Bin[10] items[10]\n" +
                            "Bin[10] items[2,3,5]\n" +
                            "Bin[10] items[7,2,1]\n" +
                            "Bin[10] items[8,1,1]\n" +
                            "Bin[11] items[11]\n";
        Assert.assertEquals(expected, getBinsAsString(bins));

        bins = BinFactory.AdjustBinsToHaveAtLeastMinAmount(bins, AMOUNT_LOWER_LIMIT);

        expected = "Bin[5] items[2,3]\n" +
                    "Bin[6] items[1,5]\n" +
                    "Bin[10] items[10]\n" +
                    "Bin[10] items[7,2,1]\n" +
                    "Bin[10] items[8,1,1]\n" +
                    "Bin[11] items[11]\n";
        Assert.assertEquals(expected, getBinsAsString(bins));
    }


    private <T extends AmountPod> String getBinsAsString(List<Bin<T>> bins) {
        LinkedList<Bin<T>> binsToCompare = bins.stream().sorted(Comparator.comparing(Bin<T>::getTotalAmount).thenComparing(Bin<T>::toString)).collect(Collectors.toCollection(LinkedList::new));
        StringBuffer actualOutput = new StringBuffer();
        binsToCompare.stream().forEach(b ->
                actualOutput.append(String.format("Bin[%s] items[%s]\n", b.getTotalAmount().getAmountValue(),
                        b.getItems().stream().map(i -> "" + i.toString()).collect(Collectors.joining(",")))));
        return actualOutput.toString();
    }


}
