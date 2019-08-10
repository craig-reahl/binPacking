package com.csparskfly;


import java.util.*;
import java.util.stream.Collectors;


public class BinFactory {

    /**
     * see http://www.martinbroadhurst.com/bin-packing.html
     */
    public static <T extends AmountPod> List<Bin<T>> BestFit(List<T> amountPodList, Amount amountLowerLimit, Amount amountUpperLimit) {

        List<Bin<T>> bins = new LinkedList<>();

        for(T amountPod : amountPodList) {

            Optional<Bin<T>> bestFitBin = bins.stream()
                    .filter(b -> b.canAdd(amountPod.getAmount()))
                    .sorted(Comparator.comparing(Bin::getTotalAmount)).findFirst();

            Bin bin;
            if (bestFitBin.isPresent()){
                bin = bestFitBin.get();
            }else{
                bin = new Bin(amountLowerLimit, amountUpperLimit);
                bins.add(bin);
            }
            bin.addAmountPod(amountPod);

        }
        return bins;
    }


    /**
     * This will rearrange bin contents so that bins with totalAmounts less than the minimum are adjusted by taking items from other bins,
     * if possible.
     */
    public static <T extends AmountPod> List<Bin<T>> AdjustBinsToHaveAtLeastMinAmount(List<Bin<T>> bins, Amount minAmount) {

        LinkedList<Bin<T>> tooSmallBins = bins.stream().filter(b -> b.getTotalAmount().compareTo(minAmount) < 0).collect(Collectors.toCollection(LinkedList::new));

        tooSmallBins.stream().forEach(b-> bins.remove(b));
        Amount previousShortAmount;
        for(Bin<T> smallBin : tooSmallBins){
            previousShortAmount = null;
            boolean noOffersFound = false;

            while(smallBin.hasShortAmount() && !noOffersFound) {
                if (previousShortAmount == null || previousShortAmount.compareTo(smallBin.getShortAmount()) != 0 ){
                    previousShortAmount = smallBin.getShortAmount();
                }else{
                    noOffersFound = true;
                }
                LinkedList<Bin<T>> bigBins = bins.stream()
                        .sorted(Comparator.comparing(Bin<T>::getTotalAmount).reversed())
                        .collect(Collectors.toCollection(LinkedList::new));//biggest bin first

                for(int i=0; i < bigBins.size() && smallBin.hasShortAmount(); i++) {
                    Bin<T> bigBin = bigBins.get(i);
                    Optional<Amount> offer = bigBin.canOffer(smallBin.getShortAmount());

                    if (offer.isPresent() && smallBin.canAdd(offer.get())) {
                        Optional<T> offerAmount = bigBin.offerAmount(offer);
                        smallBin.addAmountPod(offerAmount.get());
                        noOffersFound = false;
                    }

                }

            }
        }

        bins.addAll(tooSmallBins);
        return bins;

    }

}
