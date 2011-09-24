package org.ephman.abra.ant;

import org.apache.tools.ant.*;
import org.apache.tools.ant.taskdefs.*;
import org.apache.tools.ant.types.*;

//import java.lang.reflect.*;
import org.ephman.abra.tools.MapToJava;

import java.io.*;
import java.util.*;
/** an ant task for Abra
 * @author Paul Bethe
 */

public class AbraTask extends MatchingTask {
	// setters for flags..

	String outdir = null;

	/**
	   * Set the value of outdir.
	   * @param v  Value to assign to outdir.
	   */
	public void setOutdir(String  v) {this.outdir = v;}


	boolean noClasses = false;

	/**
	   * Set the value of noClasses.
	   * @param v  Value to assign to noClasses.
	   */
	public void setNoClasses(boolean  v) {this.noClasses = v;}

	boolean factories = false;

	/**
	   * Set the value of factories.
	   * @param v  Value to assign to factories.
	   */
	public void setFactories(boolean  v) {this.factories = v;}


	boolean validation;

	/**
	   * Set the value of validation.
	   * @param v  Value to assign to validation.
	   */
	public void setValidation(boolean  v) {this.validation = v;}


	String schema;

	/**
	   * Set the value of schema.
	   * @param v  Value to assign to schema.
	   */
	public void setSchema(String  v) {this.schema = v;}

	boolean fork = false;
    public void setFork (boolean f) { this.fork = f; }

	boolean abraXmlParser;

	/**
	   * Set the value of abraXmlParser.
	   * @param v  Value to assign to abraXmlParser.
	   */
	public void setAbraXmlParser(boolean  v) {this.abraXmlParser = v;}


	String procs = null;

	/**
	   * Set the value of procs.
	   * @param v  Value to assign to procs.
	   */
	public void setProcs(String  v) {this.procs = v;}


	/** options are mega, verbose, short (default), supress. */
	String debugLevel = null;

	/**
	   * Set the value of debugLevel.
	   * @param v  Value to assign to debugLevel.
	   */
	public void setDebugLevel(String  v) {this.debugLevel = v;}

	//


	public void setClasspath(Path s) {
        createClasspath().append(s);
    }

    /**
     * Creates a nested classpath element
     */
    public Path createClasspath() {
        return cmdl.createClasspath(project).createPath();
    }

    /**
     * Adds a reference to a CLASSPATH defined elsewhere.
     */
    public void setClasspathRef(Reference r) {
        createClasspath().setRefid(r);
    }


	// ant stuff for running the task
    /**
     * Called by the project to let the task initialize properly.
     *
     * @throws BuildException if someting goes wrong with the build
     */
    public void init() throws BuildException {}

    /**
     * Called by the project to let the task do it's work. This method may be
     * called more than once, if the task is invoked more than once. For example,
     * if target1 and target2 both depend on target3, then running
     * "ant target1 target2" will run all tasks in target3 twice.
     *
     * @throws BuildException if someting goes wrong with the build
     */
    public void execute() throws BuildException {
		Vector v = new Vector ();
		if (outdir != null) {
			v.addElement ("-outdir");
			v.addElement (outdir);
		}
		if (schema != null) {
			v.addElement ("-schema");
			v.addElement (schema);
		}
		if (noClasses) v.addElement ("-noclasses");
		if (factories) v.addElement ("-factories");
		if (validation) v.addElement ("-validation");
		if (abraXmlParser) v.addElement ("-xp");

		if (procs != null) {
			v.addElement ("-procs");
			if (!"true".equalsIgnoreCase (procs))
				v.addElement (procs);
		}

		if (debugLevel != null) {
			if ("mega".equalsIgnoreCase (debugLevel))
				v.addElement ("-mega");
			else if ("verbose".equalsIgnoreCase (debugLevel))
				v.addElement ("-verbose");
			else if ("supress".equalsIgnoreCase (debugLevel))
				v.addElement ("-supress");
			else
				throw new BuildException ("possible values for debugLevel are "+
										  "(mega | verbose | supress) not '"+
										  debugLevel + "'");
		}

		cmdl.setClassname ("org.ephman.abra.tools.MapToJava");
		pwd = project.getBaseDir ();//new File (System.getProperty ("user.dir"));
		DirectoryScanner ds = getDirectoryScanner (pwd);
		ds.scan ();

		String [] xmlFiles = ds.getIncludedFiles ();

		String [] args = new String [v.size () + xmlFiles.length];
		int i=0;
		for ( ; i < v.size (); i++)
			cmdl.createArgument().setValue((String) v.elementAt(i));
		for ( i=0; i < xmlFiles.length; i++)
			cmdl.createArgument().setValue(xmlFiles[i]);

		//		MapToJava.main (args);
		if (fork)
			run(cmdl.getCommandline());
		else
			run (cmdl);
	}

	private File pwd;
	private CommandlineJava cmdl = new CommandlineJava();
    /**
     * Executes the given classname with the given arguments as it
     * was a command line application.
     */
    private void run(CommandlineJava command) throws BuildException {
        ExecuteJava exe = new ExecuteJava();
        exe.setJavaCommand(command.getJavaCommand());
        exe.setClasspath(command.getClasspath());
        exe.setSystemProperties(command.getSystemProperties());
		exe.execute(project);
	}

    /**
     * Executes the given classname with the given arguments in a separate VM.
     */
    private int run(String[] command) throws BuildException {
		Execute exe = null;
		exe = new Execute(new LogStreamHandler(this, Project.MSG_INFO,
											   Project.MSG_WARN),
						  null);

		exe.setAntRun(project);



		File pwd = project.getBaseDir();
        //System.out.println(pwd.getAbsolutePath());
		exe.setWorkingDirectory(pwd);

		exe.setCommandline(command);
		try {
			return exe.execute();
		} catch (IOException e) {
			throw new BuildException(e, location);
		}
    }

}
