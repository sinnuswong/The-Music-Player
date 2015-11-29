package com.Project100Pi.themusicplayer; 



import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.view.*;
import android.view.View.OnClickListener;
import android.app.*;
import android.widget.*;
import android.widget.AdapterView.OnItemSelectedListener;
import android.content.*;
import android.media.audiofx.*;
import android.media.audiofx.BassBoost.Settings;

public class EqualizerSettings extends AppCompatActivity
    implements SeekBar.OnSeekBarChangeListener,
      OnItemSelectedListener,
      View.OnClickListener
{
    TextView bass_boost_label = null;
    TextView preset_label = null;
    SeekBar bass_boost = null;
    Button flat = null;
    List<String> presetNames =null;
    List<String> initialPreset=null;
    RelativeLayout outerWindows = null;

    Equalizer eq = null; 
    BassBoost bb = null;
    Spinner spinner=null;
    HashMap<String, ArrayList<Integer>> customPresets=null;
	 ArrayList<Integer> bandValues=null;

    int min_level = 0;
    int max_level = 100;
    Button saveButton=null;
    static final int MAX_SLIDERS = 5; // Must match the XML layout
    SeekBar sliders[] = new SeekBar[MAX_SLIDERS];
    TextView slider_labels[] = new TextView[MAX_SLIDERS];
    int num_sliders = 0;


/*=============================================================================
    onCreate 
=============================================================================*/


/*=============================================================================
    onCreate 
=============================================================================*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_equalizer, menu);
        final MenuItem item = menu.findItem(R.id.equalizerSwitch);
        SwitchCompat switchActionbar = (SwitchCompat) item.getActionView().findViewById(R.id.switchForActionBar);
        if(switchActionbar.isChecked())
            enableEqualizerAll();
        else
            disableEqualizerAll();

        switchActionbar.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                    enableEqualizerAll();
                else
                    disableEqualizerAll();
            }
        });
        return true;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
        {
    	Intent intent = getIntent();
    	//int audiosessionId=intent.getExtras().getInt("sessionId");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_equalizer);


        flat = (Button)findViewById(R.id.flat);
        flat.setOnClickListener(this);

        outerWindows = (RelativeLayout)findViewById(R.id.outerWindow);
        outerWindows.setBackgroundColor(ColorUtils.primaryBgColor);
        bass_boost = (SeekBar)findViewById(R.id.bass_boost);
        bass_boost.setOnSeekBarChangeListener(this);
        bass_boost_label = (TextView) findViewById (R.id.bass_boost_label);
        bass_boost_label.setTextColor(ColorUtils.primaryTextColor);

        preset_label = (TextView)findViewById(R.id.preset_label);
        preset_label.setTextColor(ColorUtils.primaryTextColor);
        sliders[0] = (SeekBar)findViewById(R.id.slider_1);
        slider_labels[0] = (TextView)findViewById(R.id.slider_label_1);
        sliders[1] = (SeekBar)findViewById(R.id.slider_2);
        slider_labels[1] = (TextView)findViewById(R.id.slider_label_2);
        sliders[2] = (SeekBar)findViewById(R.id.slider_3);
        slider_labels[2] = (TextView)findViewById(R.id.slider_label_3);
        sliders[3] = (SeekBar)findViewById(R.id.slider_4);
        slider_labels[3] = (TextView)findViewById(R.id.slider_label_4);
        sliders[4] = (SeekBar)findViewById(R.id.slider_5);
        slider_labels[4] = (TextView)findViewById(R.id.slider_label_5);
            /*
        sliders[5] = (SeekBar)findViewById(R.id.slider_6);
        slider_labels[5] = (TextView)findViewById(R.id.slider_label_6);
        sliders[6] = (SeekBar)findViewById(R.id.slider_7);
        slider_labels[6] = (TextView)findViewById(R.id.slider_label_7);
      //  sliders[7] = (SeekBar)findViewById(R.id.slider_8);
       // slider_labels[7] = (TextView)findViewById(R.id.slider_label_8);
       */
        eq = new Equalizer (0, 0);
        customPresets= new HashMap<String,ArrayList<Integer>>();
        if (eq != null)
          {
          eq.setEnabled (true);
          int num_bands = eq.getNumberOfBands();
          num_sliders = num_bands;
          short r[] = eq.getBandLevelRange();
          int noPresets = eq.getNumberOfPresets();
          presetNames = new ArrayList<String>();
          initialPreset=new ArrayList<String>();
          for(short presetValue=0; presetValue<noPresets; presetValue++)
          {
             presetNames.add(eq.getPresetName(presetValue));
             initialPreset.add(eq.getPresetName(presetValue));
          }
          
          presetNames.add("Manual");
          String[] items = new String[] {"One", "Two", "Three"};
          spinner = (Spinner) findViewById(R.id.myspinner);
          spinner.setOnItemSelectedListener(this);
          ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                      android.R.layout.simple_spinner_item, presetNames);
          adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
          spinner.setAdapter(adapter);

          min_level = r[0];
          max_level = r[1];
              /*
          saveButton=(Button)findViewById(R.id.savepreset);
          saveButton.setOnClickListener(new View.OnClickListener() {
        
  			
  			@Override
  			public void onClick(View v) {
  				// TODO Auto-generated method stub
  				//Equalizer currEq=new Equalizer(0,0);
  				showInputDialog();
  				
  			}
  		});
          */
          for (int i = 0; i < num_sliders && i < MAX_SLIDERS; i++)
            {
            int[] freq_range = eq.getBandFreqRange((short)i);
            sliders[i].setOnSeekBarChangeListener(this);
            slider_labels[i].setText (formatBandLabel (freq_range));
            slider_labels[i].setTextColor(ColorUtils.secondaryTextColor);
           // eq.setBandLevel((short)i,(short) min_level);
            }
          }
        for (int i = num_sliders ; i < MAX_SLIDERS; i++)
          {
          sliders[i].setVisibility(View.GONE);
          slider_labels[i].setVisibility(View.GONE);
          }

        bb = new BassBoost (0, 0);
        if (bb != null)
          {
          }
        else
          {
          bass_boost.setVisibility(View.GONE);
          bass_boost_label.setVisibility(View.GONE);
          }

    updateUI();
    disableEqualizerAll();
    }

/*=============================================================================
    onProgressChanged
=============================================================================*/
    @Override
    public void onProgressChanged (SeekBar seekBar, int level, 
      boolean fromTouch) 
    {
    	//if(fromTouch)
    	// spinner.setSelection(10);//  hardcoded.should be changed
        if (seekBar == bass_boost)
          {
          bb.setEnabled (level > 0 ? true : false); 
          bb.setStrength ((short)level); // Already in the right range 0-1000
          }
        else if (eq != null)
          {
          int new_level = min_level + (max_level - min_level) * level / 100; 

          for (int i = 0; i < num_sliders; i++)
            {
            if (sliders[i] == seekBar)
              {
              try {
            	  eq.setBandLevel ((short)i, (short)new_level);
			  } catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
				Toast.makeText(getApplicationContext(), "Error is " +e, Toast.LENGTH_SHORT).show();
			}
             
              break;
              }
            }
          }
    }

/*=============================================================================
    onStartTrackingTouch
=============================================================================*/
    @Override
    public void onStartTrackingTouch(SeekBar seekBar) 
    {
    }

/*=============================================================================
    onStopTrackingTouch
=============================================================================*/
    @Override
    public void onStopTrackingTouch(SeekBar seekBar) 
    {
    }

/*=============================================================================
    formatBandLabel 
=============================================================================*/
    public String formatBandLabel (int[] band) 
    {
    return milliHzToString(band[0]) + "-" + milliHzToString(band[1]);
    }

/*=============================================================================
    milliHzToString 
=============================================================================*/
    public String milliHzToString (int milliHz) 
    {
    if (milliHz < 1000) return "";
    if (milliHz < 1000000)
      return "" + (milliHz / 1000) + "Hz";
    else
      return "" + (milliHz / 1000000) + "kHz";
    }

/*=============================================================================
    updateSliders 
=============================================================================*/
    public void updateSliders ()
    {
      for (int i = 0; i < num_sliders; i++)
        {
        int level;
        if (eq != null)
          level = eq.getBandLevel ((short)i);
        else
          level = 0;
        int pos = 100 * level / (max_level - min_level) + 50;
        sliders[i].setProgress (pos);
        }
    } 
    
    public void updateSliders (Equalizer eq)
    {
      for (int i = 0; i < num_sliders; i++)
        {
        int level;
        if (eq != null)
          level = eq.getBandLevel ((short)i);
        else
          level = 0;
        int pos = 100 * level / (max_level - min_level) + 50;
        sliders[i].setProgress (pos);
        }
    } 
    
    public void updateSliders (ArrayList<Integer> bandLevels)
    {
     if(bandLevels!=null)
     {
      for (int i = 0; i < eq.getNumberOfBands(); i++)
        {
        int level;      
        //  level = eq.getBandLevel (bandLevels.get(i));
        
       // int pos = 100 * level / (max_level - min_level) + 50;
        sliders[i].setProgress ((int)bandLevels.get(i));
        }
    } 
    }

/*=============================================================================
    updateBassBoost
=============================================================================*/
    public void updateBassBoost ()
    {
    if (bb != null)
     bass_boost.setProgress (bb.getRoundedStrength());
    else
     bass_boost.setProgress (0);
    } 

/*=============================================================================
    onCheckedChange
=============================================================================*/

/*=============================================================================
    onClick
=============================================================================*/
    @Override
    public void onClick (View view)
    {
      if (view == (View) flat)
        {
        setFlat();
        }
    }

/*=============================================================================
    updateUI
=============================================================================*/
    public void updateUI ()
      {
      updateSliders();
      updateBassBoost();
      }

/*=============================================================================
    setFlat 
=============================================================================*/
    public void setFlat ()
      {
      if (eq != null)
        {
        for (int i = 0; i < num_sliders; i++)
          {
          eq.setBandLevel ((short)i, (short)0);
          }
        }

      if (bb != null)
        {
        bb.setEnabled (false); 
        bb.setStrength ((short)0); 
        }

      updateUI();
      }

/*=============================================================================
    showAbout 
=============================================================================*/
    public void showAbout ()
      {
      AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
 
      alertDialogBuilder.setTitle("About Simple EQ");
   //   alertDialogBuilder.setMessage(R.string.copyright_message);
      alertDialogBuilder.setCancelable(true);
      alertDialogBuilder.setPositiveButton("ok",
              new DialogInterface.OnClickListener() {
                  public void onClick(DialogInterface dialog, int id) {
                  }
              });
      AlertDialog ad = alertDialogBuilder.create();
      ad.show();
      
      }

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		// Toast.makeText(getBaseContext(),"Item is  "+parent.getItemAtPosition(position),Toast.LENGTH_SHORT).show();
		// Toast.makeText(getBaseContext(),"preset is  "+eq.getCurrentPreset(),Toast.LENGTH_SHORT).show();
		 Toast.makeText(getBaseContext(),"Position is  "+position,Toast.LENGTH_SHORT).show();
		 String selectedPreset=presetNames.get(position);
		 Toast.makeText(getBaseContext(),"Selected preset is  "+selectedPreset,Toast.LENGTH_SHORT).show();
		 if(initialPreset.contains(selectedPreset))
		 {
	       try {
	    	 eq.usePreset((short)position);	
		   } catch (Exception e) {
			e.printStackTrace();
			Toast.makeText(getBaseContext()," Error obtained inside item selected and Error is " +e,Toast.LENGTH_SHORT).show();
			// TODO: handle exception
		    }
		 	 
		     updateSliders();
		     
		 }else if(selectedPreset.equalsIgnoreCase("manual")){
			 
		 }else{	
			 Toast.makeText(getBaseContext(),"custom preset selected and it is   "+selectedPreset,Toast.LENGTH_SHORT).show();
		  
		   updateSliders(customPresets.get(selectedPreset));
		 
		 }
		 

	}
	 private void showInputDialog() {
     	// get prompts.xml view
     		LayoutInflater layoutInflater = LayoutInflater.from(this);
     		View promptView = layoutInflater.inflate(R.layout.dialog_box, null);
     		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
     		alertDialogBuilder.setView(promptView);

     		final EditText editText = (EditText) promptView.findViewById(R.id.edittext);
     		// setup a dialog window
     		alertDialogBuilder.setCancelable(false)
     				.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            String newName = editText.getText().toString();
                            presetNames.add(newName);

                            bandValues = new ArrayList<Integer>();

                            for (int i = 0; i < eq.getNumberOfBands(); i++) {

                                bandValues.add(sliders[i].getProgress());

                            }
                            customPresets.put(newName, bandValues);
                            //updateSliders(eq);

                            // spinner.setSe

                        }
                    })
     				.setNegativeButton("Cancel",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });

     		// create an alert dialog
     		AlertDialog alert = alertDialogBuilder.create();
     		alert.show();
     	// TODO Auto-generated method stub
     	
     }

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		// TODO Auto-generated method stub
		
	}

    public void disableEqualizerAll(){
        eq.setEnabled(false);
        flat.setFocusable(false);
        flat.setEnabled(false);
        flat.setClickable(false);
        flat.setBackgroundColor(Color.parseColor("#AAAAAA"));
        spinner.setEnabled(false);
        spinner.setClickable(false);
        spinner.setFocusable(false);
        disableSeekbar(bass_boost);
        for (int i = 0; i < num_sliders && i < MAX_SLIDERS; i++)
        {
            disableSeekbar(sliders[i]);
        }
    }

    public void enableEqualizerAll(){
        eq.setEnabled(true);
        flat.setFocusable(true);
        flat.setEnabled(true);
        flat.setClickable(true);
        flat.setBackgroundColor(Color.parseColor("#be4d56"));
        spinner.setEnabled(true);
        spinner.setClickable(true);
        spinner.setFocusable(true);
        enableSeekbar(bass_boost);
        for (int i = 0; i < num_sliders && i < MAX_SLIDERS; i++)
        {
            enableSeekbar(sliders[i]);
        }

    }

    public  void disableSeekbar(SeekBar seek ){
       seek.setEnabled(false);
        seek.setFocusable(false);
        seek.setClickable(false);
    }

    public void enableSeekbar(SeekBar seek){
        seek.setEnabled(true);
        seek.setFocusable(true);
        seek.setClickable(true);
    }

/*=============================================================================
    onOptionsItemSelected 
=============================================================================*/
    /*
     @Override
     public boolean onOptionsItemSelected(MenuItem item) 
       {
       switch (item.getItemId()) 
         {
         case R.id.equalizerSwitch:

           return true;
         }
       return super.onOptionsItemSelected(item);
       }
 */
}


