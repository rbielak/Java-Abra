package org.ephman.abra.tools;

import java.io.IOException;
/** just a generator interface
 *
 * @author Paul M Bethe
 * @version 9/19/02
 */

public interface Generator {

	public void generate (JClass currentClass) 
		throws IOException, SchemaException;
	/** incase files need to be closed or whatever */
	public  void close () throws IOException;

}
