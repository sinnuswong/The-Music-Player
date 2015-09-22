package com.Project100Pi.themusicplayer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import android.R.integer;
import android.content.Context;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SectionIndexer;
import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardArrayMultiChoiceAdapter;
import it.gmariotti.cardslib.library.view.base.CardViewWrapper;


 public class MyIndexerAdapter extends CardArrayMultiChoiceAdapter implements SectionIndexer {
	  ArrayList<Card> myElements;
      HashMap<String, Integer> alphaIndexer;
      HashMap<Integer, Integer> positionIndexer;
      String[] sections;
      ArrayList<String> listItems;

	public MyIndexerAdapter(Context context, List<Card> elements) {
		super(context, elements);
		// TODO Auto-generated constructor stub
		myElements = (ArrayList<Card>) elements;
		listItems = new ArrayList<String>();
        // here is the tricky stuff
        alphaIndexer = new HashMap<String, Integer>();
        // in this hashmap we will store here the positions for
        // the sections
        positionIndexer = new HashMap<Integer,Integer>();
        int size = elements.size();
        for (int i = size - 1; i >= 0; i--) {
                String element = elements.get(i).getCardHeader().getTitle();
                listItems.add(element);
                alphaIndexer.put(element.substring(0, 1), i);
        //We store the first letter of the word, and its index.
        //The Hashmap will replace the value for identical keys are putted in
        }

        // now we have an hashmap containing for each first-letter
        // sections(key), the index(value) in where this sections begins

        // we have now to build the sections(letters to be displayed)
        // array .it must contains the keys, and must (I do so...) be
        // ordered alphabetically

        Set<String> keys = alphaIndexer.keySet(); // set of letters ...sets
        // cannot be sorted...

        Iterator<String> it = keys.iterator();
        ArrayList<String> keyList = new ArrayList<String>(); // list can be
        // sorted

        while (it.hasNext()) {
                String key = it.next();
                keyList.add(key);
        }

        Collections.sort(keyList);

        sections = new String[keyList.size()]; // simple conversion to an
        // array of object
        keyList.toArray(sections);
        
        List<Integer> values = new ArrayList();
        for (int i : alphaIndexer.values()) {
            values.add(i);
        }
        values.add(listItems.size()-1);
        Collections.sort(values);

        int k = 0;
        int z = 0;
        for(int i = 0; i < values.size()-1; i++) {
            int temp = values.get(i+1);
            do {
                positionIndexer.put(k, z);
                k++;
            } while(k < temp);
            z++;
        }

	}

	@Override
	public void onItemCheckedStateChanged(ActionMode arg0, int arg1, long arg2,
			boolean arg3, CardViewWrapper arg4, Card arg5) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
		// TODO Auto-generated method stub
		return false;
	}

	
	
	   @Override
       public int getPositionForSection(int section) {
               // Log.v("getPositionForSection", ""+section);
               String letter = sections[section];

               return alphaIndexer.get(letter);
       }

       @Override
       public int getSectionForPosition(int position) {

               // you will notice it will be never called (right?)
              // Log.v("getSectionForPosition", "called");
               return positionIndexer.get(position);       }

       @Override
       public Object[] getSections() {

               return sections; // to string will be called each object, to display
               // the letter
       }
	
	

}
