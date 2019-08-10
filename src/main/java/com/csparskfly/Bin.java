package com.csparskfly;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class Bin<T extends AmountPod> {

    private final Amount minTotalAmount;
    private final Amount maxTotalAmount;
    List<T> items = new LinkedList<>();

    Amount totalAmount;
    Amount zeroAmount;

    public Bin(Amount minTotalAmount, Amount maxTotalAmount){
        if(minTotalAmount == null || maxTotalAmount == null || minTotalAmount.compareTo(maxTotalAmount) > 0 ){
            throw new RuntimeException("Something wrong with the amounts");
        }
        this.minTotalAmount = minTotalAmount;
        this.maxTotalAmount = maxTotalAmount;
        totalAmount = new Amount(BigDecimal.ZERO);
        zeroAmount =  new Amount(BigDecimal.ZERO);

    }

    public T addAmountPod(T amountPod){

        items.add(amountPod);
        totalAmount = totalAmount.plus(amountPod.getAmount());

        return amountPod;
    }

    public Amount getTotalAmount() {
        return totalAmount ;
    }

    @Override
    public String toString() {

        return String.format("Bin [%s] itemsValues[%s]", totalAmount, items.stream()
                .map(i -> ""+i.getAmount())
                .collect(Collectors.joining(",")));

    }

    public Optional<T> offerAmount(Optional<Amount> offerAmount) {
        if(offerAmount.isPresent()){
            T itemToRemove = items.stream().filter(t-> t.getAmount().equals(offerAmount.get())).findAny().get();
            totalAmount = totalAmount.minus(itemToRemove.getAmount());
            items.remove(itemToRemove);
            return Optional.of(itemToRemove);
        }
        return Optional.empty();
    }

    public Optional<Amount> canOffer(Amount shortAmount) {
        Amount requiredAmountMax = maxTotalAmount.minus(shortAmount);
        Optional<Amount> result = items.stream()
                .filter( t -> t.getAmount().compareTo(requiredAmountMax) <= 0
                        && getTotalAmount().minus(t.getAmount()).compareTo(minTotalAmount) >= 0 )//but not too high to starve the box
                .sorted(Comparator.comparing(T::getAmount).reversed())//get the highest offer first
                .map(T::getAmount).findFirst();
        return result;

    }

    public boolean canAdd(Amount amount) {
        if(totalAmount == null){
            return true;
        }
        return totalAmount.plus(amount).compareTo(maxTotalAmount) <= 0;
    }

    public Amount getShortAmount() {
        Amount shortAmount = minTotalAmount.minus(getTotalAmount());
        if (shortAmount.compareTo(zeroAmount) > 0)
            return shortAmount;
        return zeroAmount;
    }

    public boolean hasShortAmount() {
        return getShortAmount().compareTo(zeroAmount) > 0;
    }

    public List<T> getItems() {
        return items;
    }
}
