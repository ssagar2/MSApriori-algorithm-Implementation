package Implementation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;


public class MsApriori {
	
	static List<Set<Item>> itemSets = new ArrayList<>();
	static Set<Item> items= null;
	static Set<Item> items_Sorted_MIS= null;
	static double sdc = 0.0;
	static List<String> notTogetherItems =  null;
	static Set<Set<String>> notTogetherItemPairs =  new HashSet<>();
	static List<String> mustBeItems = null;
	
	@SuppressWarnings("null")
	public static Set<Item> createItems(String itemInputFile_Path) {
		Set<Item> tempitems = new HashSet<Item>();
		try{
			File parameterFile = new File(itemInputFile_Path);

			if(!parameterFile.exists()) return null;

			FileReader fReader = new FileReader(parameterFile);
			BufferedReader bReader = new BufferedReader(fReader);
			String parameterLine = bReader.readLine();
			while(parameterLine != null){
				if(parameterLine.contains("MIS")){
					String[] tempArray = parameterLine.split("=");
					if(tempArray.length == 2){
						String itemName = tempArray[0].trim().replaceAll("\\D+","");
						Double minSupportValue = Double.parseDouble(tempArray[1].trim());
						Item tempItem = new Item(itemName,0,minSupportValue,null);
						tempitems.add(tempItem);
					}
				}else if(parameterLine.contains("SDC")){
					sdc = Double.parseDouble(parameterLine.replaceAll("[^0-9\\.]+","")); 
				}else if(parameterLine.contains("cannot_be_together")){
					while(parameterLine.contains("{") == true) {
						String sub = parameterLine.substring(parameterLine.indexOf("{") + 1, parameterLine.indexOf("}"));
					parameterLine = parameterLine.substring(parameterLine.indexOf("}") + 1);
					sub = sub.replaceAll("[^0-9,]","");	
				String[] itemNameArray = sub.split(",");
						List<String> temp = new LinkedList<String>();
						temp = Arrays.asList(itemNameArray);
						Set<String> tempSet = new HashSet<String>(temp);
						notTogetherItemPairs.add(tempSet);
					}
		//			String itemNameString = parameterLine.replaceAll("[^0-9,]","");
			//		String[] itemNameArray = itemNameString.split(",");
				//	notTogetherItems = Arrays.asList(itemNameArray);
				}
				
				
				else if(parameterLine.contains("must-have")){
					String tempItemLabels = parameterLine.trim().replaceAll(" or ",",").replaceAll("[^0-9,]","");
					mustBeItems = Arrays.asList(tempItemLabels.trim().split(","));
				}
				parameterLine = bReader.readLine();
			}
			bReader.close();
			fReader.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		
		//System.out.println(notTogetherItemPairs);
	//for(int i=0;i<notTogetherItems.size()-1;i++) {
		//Set<String> temp = new HashSet<>()  ;
		//temp.add(notTogetherItems.get(i));
		//temp.add(notTogetherItems.get(i+1));
		//notTogetherItemPairs.add(temp);
		//i=i+1;
		//temp = null;
	//	}
	//System.out.println(notTogetherItemPairs);
		return tempitems;
		
	}
	
	public static void createItemSets(String itemInputFile_Path) throws IOException {
		//List<Set<Item>> tempIS = new ArrayList<>();
		
		File inputFile = new File(itemInputFile_Path);

		if(!inputFile.exists()) return;

		FileReader fReader = new FileReader(inputFile);
		BufferedReader bReader = new BufferedReader(fReader);
		String line;
		while ( (line = bReader.readLine()) != null) {
			line = line.substring(1, line.length()-1);
			Set<Item> tempSet = new HashSet<>();
			String[] tokens = line.split(",");
			for(int i=0; i<tokens.length; i++){
				String tempIN =tokens[i].trim();
				for (Item item : items) {
					if (item.getItemName().equals(tempIN))
						tempSet.add(item);
				}
			}
			itemSets.add(tempSet);
		}

		bReader.close();
		fReader.close();
		
		
	}
	
	public static void sortItemsOnMIS_Asc() {
		List<Item> tempItems=  new ArrayList<Item>(items);
		Collections.sort(tempItems, new Comparator<Item>(){
			@Override
			public int compare(Item item1, Item item2) {
				if(item1.getMIS() > item2.getMIS())
					return 1;
				else if(item1.getMIS() == item2.getMIS())
					return 0;
				else 
					return -1;
			}
		});
		items_Sorted_MIS = new LinkedHashSet<Item>(tempItems);
	}
	
	public static Set<Item> initPass() {
		Set<Item> L = new LinkedHashSet<Item>();
		int totalNoOfTxn = itemSets.size();
		for(Item item:items_Sorted_MIS){
			for(Set<Item> txnItemSet:itemSets){
				if(txnItemSet.contains(item)){
					item.setItemCount(item.getItemCount() + 1);
				}
			}
			item.setactualSupport((item.getItemCount()*1.0)/totalNoOfTxn);
		}

		Item itemWithMinSupport = null;
		for(Item item:items_Sorted_MIS){
			if(L.isEmpty() && (item.getactualSupport() >= item.getMIS())){
				itemWithMinSupport = item;
				L.add(item);
			}else{
				if(!L.isEmpty() && item.getactualSupport() > itemWithMinSupport.getMIS()){
					L.add(item);
				}
			}
		}
		//System.out.println("Items After Init Pass: "+L);
		return L;
		
	}
	private static List<Set<Item>> getFrequentItemSet(List<FrequentItemsSet> inputList){
		List<Set<Item>> frequentItemSetList = new LinkedList<Set<Item>>();
		for(FrequentItemsSet itemSet: inputList){
			frequentItemSetList.add(itemSet.getFrequentItemSet());
		}
		return frequentItemSetList;
	}
	
	private static List<FrequentItemsSet> candidateGen(List<FrequentItemsSet> Fk) {
		List<FrequentItemsSet> frequentItemSetList = new LinkedList<FrequentItemsSet>();
		List<Set<Item>> fK1FrequentItemSet = getFrequentItemSet(Fk);

		for(int i = 0;i < Fk.size()-1;i++){
			FrequentItemsSet f1 = Fk.get(i);
			Item[] tempF1Array = f1.getFrequentItemSet().toArray(new Item[f1.getFrequentItemSet().size()]);
			List<Item> tempF1ItemList = Arrays.asList(tempF1Array).subList(0,tempF1Array.length-1);
			Item lastF1Item = tempF1Array[tempF1Array.length-1];

			for(int j = i+1;j<Fk.size();j++){
				FrequentItemsSet f2 = Fk.get(j);
				Item[] tempF2Array = f2.getFrequentItemSet().toArray(new Item[f2.getFrequentItemSet().size()]);
				List<Item> tempF2ItemList = Arrays.asList(tempF2Array).subList(0,tempF2Array.length-1);
				Item lastF2Item = tempF2Array[tempF2Array.length-1];

				double supportDiff = lastF2Item.getactualSupport() - lastF1Item.getactualSupport();
				supportDiff = (supportDiff < 0)?(-1*supportDiff):supportDiff;

				if(tempF1ItemList.equals(tempF2ItemList) && (lastF2Item.getMIS() > lastF1Item.getMIS()) 
						&& supportDiff <= sdc){

					FrequentItemsSet tempFreqItemSet = f1.clone();
					tempFreqItemSet.getFrequentItemSet().add(lastF2Item);
					tempFreqItemSet.setFrequentItemSetCount(0);
					frequentItemSetList.add(tempFreqItemSet);

					//Create a List of Items from Set<Item> present in tempFreqItemSet....
					List<Item> tempFrequentItemList = new LinkedList<Item>();
					tempFrequentItemList.addAll(tempFreqItemSet.getFrequentItemSet());

					List<Set<Item>> subsets = getSubsets(new ArrayList<Set<Item>>(), tempFrequentItemList,
							new LinkedHashSet<Item>(), tempFreqItemSet.getFrequentItemSet().size() - 1);

					Item c1 =  tempF1Array[0];
					Item c2 = tempF1Array[1];
					for(Set<Item> subset:subsets){
						if(subset.contains(c1) || (c1.getMIS() == c2.getMIS())){
							if(!(fK1FrequentItemSet.contains(subset))){
								frequentItemSetList.remove(tempFreqItemSet);
							}
						}
					}
				}
			}
		}
		return frequentItemSetList;

	}

	private static List<Set<Item>> getSubsets(List<Set<Item>> resultingSubsets, List<Item> inputItemSet, Set<Item> frequentItemSubset, int subsetSize) {
		if(frequentItemSubset!=null && frequentItemSubset.size() == subsetSize){
			resultingSubsets.add(frequentItemSubset);
		}else{
			for(int i = 0;i < inputItemSet.size(); i++){
				Set<Item> tempSet = new LinkedHashSet<Item>(frequentItemSubset);
				tempSet.add(inputItemSet.get(i));
				getSubsets(resultingSubsets,inputItemSet.subList(i+1, inputItemSet.size()),tempSet,subsetSize);
			}
		}
		return resultingSubsets;
	}
	
	private static List<FrequentItemsSet> l2CandidateGen() {
		List<FrequentItemsSet> frequent2ItemSetList = new LinkedList<FrequentItemsSet>();
		Item[] itemArray = items_Sorted_MIS.toArray(new Item[items_Sorted_MIS.size()]);

		for(int i = 0;i<items_Sorted_MIS.size()-1;i++){
			Item initItem = itemArray[i];
			if(initItem.getactualSupport() >= initItem.getMIS()){
				for(int j = i+1;j<items_Sorted_MIS.size();j++){
					Item postInitItem = itemArray[j];
					double supportDiff = postInitItem.getactualSupport() - initItem.getactualSupport();
					supportDiff = (supportDiff < 0)?(-1*supportDiff):supportDiff;
					if((postInitItem.getactualSupport() >= initItem.getMIS()) 
							&& supportDiff <= sdc){
						Set<Item> tempSet = new LinkedHashSet<Item>();
						tempSet.add(initItem);
						tempSet.add(postInitItem);

						FrequentItemsSet frequent2ItemSet = new FrequentItemsSet();
						frequent2ItemSet.setFrequentItemSet(tempSet);
						frequent2ItemSetList.add(frequent2ItemSet);
					}
				}
			}	
		}
		return frequent2ItemSetList;
	}
	
	private  static List<FrequentItemsSet> setFrequentItemSetTailCount(List<FrequentItemsSet> frequentItemSet){

		for(Set<Item> txnItemSet:itemSets){
			for(FrequentItemsSet frequentItems:frequentItemSet){
				Set<Item> tempSet = frequentItems.getFrequentItemSet();
				if(tempSet.size() > 1){
					List<Item> tempList = Arrays.asList(tempSet.toArray(new Item[tempSet.size()]));
					tempList = tempList.subList(1,tempList.size());
					if(txnItemSet.containsAll(tempList)){
						frequentItems.setTailCount(frequentItems.getTailCount() + 1);
					}
				}	
			}
		}
		return frequentItemSet;
	}
	
	private static List<FrequentItemsSet> frequentItemListAfetMustHave(List<FrequentItemsSet> f_afteNotTogether) {
		List<FrequentItemsSet> fianlF = new LinkedList<FrequentItemsSet>();
		for (FrequentItemsSet f : f_afteNotTogether) {
			List<Item> tempList = new LinkedList<Item>(f.getFrequentItemSet());
			for (Item item : tempList) {
				if(mustBeItems.contains(item.getItemName())) {
					fianlF.add(f);
					break;
				}
			}
		}
		return fianlF;
	}

	private static List<FrequentItemsSet> frequentItemListAfterNotTogether(List<FrequentItemsSet> F_all) {
		Set<FrequentItemsSet> frequentItemsListAfterMustNot = new LinkedHashSet<>();
		for(FrequentItemsSet frequentItemSet : F_all){
			Set<String> tempItemNameSet = frequentItemSet.getItemNameSet();
			int count = 0;
			for(Set<String> itemPairs : notTogetherItemPairs){
				if(tempItemNameSet.containsAll(itemPairs)){
					count++;
				}
			}

			if(count == 0){
				frequentItemsListAfterMustNot.add(frequentItemSet);
			}
		}
		return new LinkedList<FrequentItemsSet>(frequentItemsListAfterMustNot);
		
	}
	
	public static void print(List<FrequentItemsSet> frequentItemSetList){
		Map<Integer,List<FrequentItemsSet>> frequentItemSetSizeMap = new LinkedHashMap<Integer,List<FrequentItemsSet>>();

		for(FrequentItemsSet frequentItemSet:frequentItemSetList){
			Set<Item> tempSet = frequentItemSet.getFrequentItemSet();
			Integer setSize = tempSet.size();

			if(frequentItemSetSizeMap.containsKey(setSize)){
				List<FrequentItemsSet> tempList = frequentItemSetSizeMap.get(setSize);
				tempList.add(frequentItemSet);
			}else{
				List<FrequentItemsSet> tempList = new LinkedList<FrequentItemsSet>();
				tempList.add(frequentItemSet);
				frequentItemSetSizeMap.put(setSize, tempList);
			}
		}

		for(Integer freqItemSize: frequentItemSetSizeMap.keySet()){
			if (freqItemSize ==1) {
				System.out.println("\nFrequent "+freqItemSize+"-itemsets");
				List<FrequentItemsSet> tempList = frequentItemSetSizeMap.get(freqItemSize);
				for(FrequentItemsSet frequentItem : tempList){
					System.out.println("\t"+frequentItem.getFrequentItemSetCount() + " : " + frequentItem.getFrequentItemSet());
				}
				System.out.println("\n\tTotal number of frequent "+freqItemSize+"-itemsets = "+tempList.size());
				}
			
			else {
				System.out.println("\nFrequent "+freqItemSize+"-itemsets");
				List<FrequentItemsSet> tempList = frequentItemSetSizeMap.get(freqItemSize);
				for(FrequentItemsSet frequentItem : tempList){
					System.out.println("\t"+frequentItem.getFrequentItemSetCount() + " : " + frequentItem.getFrequentItemSet());
					System.out.println("Tailcount = "+frequentItem.getTailCount());
				}
				System.out.println("\n\tTotal number of frequent "+freqItemSize+"-itemsets = "+tempList.size());
				}
			}

	}

	public static void main(String[] args) throws IOException {
		String itemInputFile_Path = "C:/Users/swapnil sagar/workspace/MSApriori/ParamFile.txt";
		items = createItems(itemInputFile_Path);
		//System.out.println(notTogetherItemPairs);
		
		String inputFile_Path = "C:/Users/swapnil sagar/workspace/MSApriori/InputFile.txt";
		try {
			createItemSets(inputFile_Path);
			//System.out.println(itemSets);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//System.out.println(notTogetherItemPairs);
			
		sortItemsOnMIS_Asc();
		Set<Item> L = initPass();
		//System.out.println(items);
		
		List<FrequentItemsSet> F_all = new LinkedList<>();
		
		// F1
		List<FrequentItemsSet> Fk = new LinkedList<FrequentItemsSet>();
		for(Item item:items_Sorted_MIS){
			if(item.getactualSupport() >= item.getMIS()){
				FrequentItemsSet frequentItemSet = new FrequentItemsSet();
				Set<Item> tempSet = new HashSet<Item>();
				tempSet.add(item);
				frequentItemSet.setFrequentItemSet(tempSet);
				frequentItemSet.setFrequentItemSetCount(item.getItemCount());
				frequentItemSet.setFrequentItemSetSupportCount(item.getactualSupport());
				frequentItemSet.setTailCount(item.getItemCount());
				Fk.add(frequentItemSet);
			}
		}
		F_all.addAll(Fk);
	//	System.out.println("Frequent 1-itemsets");
	//	for(FrequentItemsSet f : Fk) {
		//	System.out.println("\t" + f.getFrequentItemSetCount() + " : {" + f.getItemNameSet() + "}");
		//}
		//System.out.println("\t" + "Total number of freuqent 1-itemsets = " + Fk.size());
		
		// F2
		for(int k = 2;!(Fk.isEmpty());k++){
			List<FrequentItemsSet> eligibleFrequentItemsList = null;
			if(k==2){
				eligibleFrequentItemsList = l2CandidateGen();
			}else{
				eligibleFrequentItemsList = candidateGen(Fk);
			}

			//Calculate the count of each item set generated...
			if(!eligibleFrequentItemsList.isEmpty()){
				for(Set<Item> txnItemSet:itemSets){
					for(FrequentItemsSet eligibleFrequentItems:eligibleFrequentItemsList){
						if(txnItemSet.containsAll(eligibleFrequentItems.getFrequentItemSet())){
							eligibleFrequentItems.setFrequentItemSetCount(eligibleFrequentItems.getFrequentItemSetCount() + 1);
						}
					}
				}
			}

			//Calculate the actual Frequent Item Set...
			Fk = new LinkedList<FrequentItemsSet>();
			for(FrequentItemsSet eligibleFrequentItemSet : eligibleFrequentItemsList){
				Set<Item> tempSet = eligibleFrequentItemSet.getFrequentItemSet();
				Item firstCandidate = tempSet.iterator().next();

				double actualSupportCount =  (eligibleFrequentItemSet.getFrequentItemSetCount()*1.0)/itemSets.size();

				if(actualSupportCount >= firstCandidate.getMIS()){
					eligibleFrequentItemSet.setFrequentItemSetSupportCount(actualSupportCount);
					Fk.add(eligibleFrequentItemSet);
				}
			}
			if(!Fk.isEmpty()){
				F_all.addAll(Fk);
			}
		}
		List<FrequentItemsSet> F_afterMustHave  =null;
		List<FrequentItemsSet> F_afteNotTogether = null;
		if(mustBeItems != null) 
			F_afterMustHave = frequentItemListAfetMustHave(F_all);
		else
			F_afterMustHave = F_all;
		if (notTogetherItemPairs != null)
			F_afteNotTogether= frequentItemListAfterNotTogether(F_afterMustHave);
		else
			F_afteNotTogether = F_afterMustHave;
		
		List<FrequentItemsSet> finalFrequentItemSet = setFrequentItemSetTailCount(F_afteNotTogether);
		print(finalFrequentItemSet);
		
		//String a = "cannot_be_together: {20, 50, 60} , {40,70}";
		//while(a.contains("{") == true){
			//String sub =a.substring(a.indexOf("{")+1, a.indexOf("}"));
			//String temp =a.substring(a.indexOf("}")+1);
		//	a = temp;
			//System.out.println(sub);
	//	}
		
	}





}
