package org.ephman.abra.tools.generators;

import java.util.*;
import org.ephman.abra.tools.plugins.*;

public class PhpPluginData extends ClassPluginData {

    protected int entryCols = 1;
    protected boolean genNew = true, genUpdate = true;

    public void setEntryCols (String inp) {
	if (inp != null&& inp.length () > 0) 
	    entryCols = Integer.parseInt(inp);
    }

    public void setGenNew (String inp) {
	if (inp != null&& inp.length () > 0) 
	    genNew = Boolean.getBoolean(inp);
    }

    public void setGenUpdate (String inp) {
	if (inp != null&& inp.length () > 0) 
	    genUpdate = Boolean.getBoolean(inp);
    }


    public PhpPluginData (String name, String formatName){
	this(name, formatName, "1", "true", "true");
    }

    public PhpPluginData (String name, String formatName, String entry_cols, String genNew, String genUpdate) {
	super (name, formatName);
	setGenUpdate(genUpdate);
	setGenNew(genNew);
	setEntryCols(entry_cols);
    }


}
