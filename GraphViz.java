package GraphViz;

/*######################IMPORTANT######################
 *  set package name based on your project structure  #
 *#####################################################/

//GraphViz.java - a API to create graph images and dot graph representations
//from Java programs. API strives to be compatible with graph definitions and
//implementations on Data Structures and Algorithms 3 course on 
//Faculty of Sciences University of Novi Sad

/*
******************************************************************************
*                                                                            *
*                Copyright (c) 2020 Novica Petkovic                          *
*                                                                            *
* This program is free software; you can redistribute it and/or modify it    *
* under the terms of the GNU Lesser General Public License as published by   *
* the Free Software Foundation; either version 2.1 of the License, or        *
* (at your option) any later version.                                        *
*                                                                            *
* This program is distributed in the hope that it will be useful, but        *
* WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY *
* or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public    *
* License for more details.                                                  *
*                                                                            *
* You should have received a copy of the GNU Lesser General Public License   *
* along with this program. If not, see <https://www.gnu.org/licenses/>.      *
*                                                                            *
******************************************************************************
*/
import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.lang.reflect.*;
import java.util.Set;

public class GraphViz
{
    private final static String OS_NAME = System.getProperty("os.name").replaceAll("\\s","");
    private static final String GRAPH_VIZ_INSTALLATION_DIR = "Graphviz";
    private static final String GRAPH_VIZ_EXE_PATH = "/bin/dot.exe";
    private static final String GRAPH_COUNTER = "/info/counter.txt";
    private static final String DOT_DIR = "/dot";
    private static final String INFO_DIR = "/info";
    private static final String SPACING = "    ";
    private static final String GRAPH_DRAWING_ONLINE_SERVICE = "https://image-charts.com/chart?cht=gv:dot&chl=";
    private static final Set<String> ALLOWED_FILE_TYPES = Set.of("gif", "png", "jpg", "svg");
    //#############################################################
    //GRAPH METHODS
    private String numberOfVerticesMethodName = "V";
    private String adjacentVerticesMethodName = "adj";
    //#############################################################
    //#############################################################
    //EDGE WEIGHTED GRAPH METHODS
    private String edgeMethodName = "edges";
    private String edgeWeightMethodName = "weight";
    private String edgeWeightedGraphEither = "either";
    private String edgeWeightedGraphOther = "other";
    private String edgeWeightedDigraphFrom = "from";
    private String edgeWeightedDigraphTo = "to";
    //#############################################################
    //#############################################################
    //GRAPH CLASS NAMES
    private String graphClassName = "Graph";
    private String digraphClassName = "Digraph";
    private String edgeWeightedGraphClassName = "EdgeWeightedGraph";
    private String edgeWeightedDigraphClassName = "EdgeWeightedDigraph";
    //#############################################################
    private String fileType = "png";
    
    private String rootDir;
    private String executable;
    
    private boolean graphVizExists;

    public GraphViz() {
        this(null, null);
    }
    
    public GraphViz(String executable) {
    	this(executable, null);
    }
    
    public GraphViz(String executable, String rootDir) {
    	this.executable = executable;
    	this.rootDir = rootDir;
    	init();
    }
	
    public void init() {
    	if (OS_NAME.contains("Windows")) {
    		windowsSetup();
        } else if (OS_NAME.equals("MacOSX")) {
        	linuxSetup();
        } else if (OS_NAME.equals("Linux")) {
        	macOSSetup();
        }
    	
    	if(executable == null) {
    		graphVizExists = false;
    	} else {
    		File exe = new File(executable);
        	if(!exe.exists()) {
        		graphVizExists = false;
        	}
    	}
        initStructure();
    }
    
    private void windowsSetup() {
    	if(rootDir == null) {
    		rootDir = "C:/GraphViz";
    	}
    	
        if(executable == null) {
			executable = graphVizWindowsDefaultSearch();
			if(executable == null) {
				
			}
		}
    }
    
    private void linuxSetup() {
    	if(rootDir == null) {
    		rootDir = System.getProperty("user.home")+"/GraphViz";
    	}
    	
    	if(executable == null) {
    		executable = "dot";
    	}
    }
    
    private void macOSSetup() {
    	if(rootDir == null) {
    		rootDir = System.getProperty("user.home")+"/GraphViz";
    	}
    	
    	if(executable == null) {
    		executable = "dot";
    	}
    }
    
    /**
     * Initializes files on filesystem
     */
    public void initStructure() {
		File f = null;
		f = new File(rootDir);
		if(!f.exists()){
			f.mkdirs();
		}
        f = new File(rootDir+INFO_DIR);
        if(!f.exists()) {
        	f.mkdirs();
        }
        f = new File(rootDir+DOT_DIR);
        if(!f.exists()) {
        	f.mkdirs();
        }
        f = new File(rootDir+GRAPH_COUNTER);
        if(!f.exists()) {
        	try {
				f.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
        }
    }
    
	/**
	 * Search expected locations on windows where GraphViz could be located
	 * @return
	 * @throws FileNotFoundException
	 */
    private String graphVizWindowsDefaultSearch() {
    	String[] programFilesLocations = {"C:/Program Files (x86)", 
    			"C:/Program Files"};
    	String path = null;
    	for(String s : programFilesLocations) {
    		path = findGraphViz(s);
    		if(path != null) {
    			return path;
    		}
    	}
    	
    	return null;
    }
    
    /**
     * Check files and directories in provided path and searches for directory
     * that has GraphViz in its name.
     * @param String path
     * @return
     */
	private String findGraphViz(String path) {
		File[] files = new File(path).listFiles();
		if(files == null) {
			return null;
		}
		
		for(File f : files) {
			if(f.isDirectory() && f.getName().contains(GRAPH_VIZ_INSTALLATION_DIR)) {
				return path+"/"+f.getName()+GRAPH_VIZ_EXE_PATH;
			}
		}
		
		return null;
	}
	
	/**
	 * Creates graph image, graph should be compatible
	 * with definition and implementation of graph used
	 * on Data Structures and Algorithms 3 course.
	 * @param Object graph
	 */
    public void createGraphImage(String fileName, Object graph) {
    	String dotString = toDot(graph);
        int dotId;
        try {
        	dotId = writeDotToFile(fileName, dotString);
            
            if (dotId != -1) {
            	writeImg(fileName, dotId);
            }
            
        } catch (java.io.IOException e) {
        	e.printStackTrace();
        }
    }
    
    public void createGraphImage(Object graph) {
    	createGraphImage(null, graph);
    }
    
    /**
	 * Creates graph image from string. String should be
	 * compatible with dot format.
	 * @param String dotSource
	 */
    public void createGraphImage(String fileName, String dotString) {
    	int dotId;
        try {
        	dotId = writeDotToFile(fileName, dotString);
            
            if (dotId != -1) {
            	writeImg(fileName, dotId);
            }
            
        } catch (java.io.IOException e) {
        	e.printStackTrace();
        }
    }
    
    public void createGraphImage(String dotString) {
    	createGraphImage(null, dotString);
    }

    /**
     * Creates image based on dot file created
     * by using installed GraphViz.
     * @param dot
     */
    private void writeImg(String fileName, int dotId)
    {
    	if(!graphVizExists) {
    		System.out.println("GraphViz dot not found on expected location C:/Program Files/GraphViz... \n"
				+ "or C:/Program Files (x86)/GraphViz... on Windows. On MacOSX and Linux it should be on path.\n"
				+ "If you do not have GraphViz installed on your computer please\n"
				+ "install GraphViz from https://graphviz.org/.\n\n"
				+ "######### IMPORTANT #########\n"
				+ "If for some reason you don't want to install GraphViz, dont use createGraphImage(...),\n"
				+ "instead use method drawInBrowser(Object graph) or drawInBrowser(String dot).\n"
				+ "Graphs that have more than 400 edges can't be drawn using browser methods.\n");
    	}
    	
    	String dotFileLocation = createDotFileName(fileName, dotId);
    	String imgFileLocation = createDrawingFileName(fileName, dotId);
        
        try {
            Runtime rt = Runtime.getRuntime();
            String[] args = { executable, "-T", fileType, "-o", imgFileLocation, dotFileLocation};
            Process p = rt.exec(args);
            p.waitFor();
        } catch (IOException ioe) {
            System.err.println("Error: in I/O processing of tempfile in dir " + rootDir + "\n or in calling external command");
            ioe.printStackTrace();
        } catch (InterruptedException ie) {
            System.err.println("Error: the execution of the external program was interrupted");
            ie.printStackTrace();
        }
        
        System.out.println("From dot file "+dotFileLocation+" graph "
        		+ "image successfully created in "+imgFileLocation);
    }
    
    /**
     * Creates dot file name based on name passed by user.
     * @param fileName
     * @param dotId
     * @return filename
     */
    private String createDotFileName(String fileName, int dotId) {
    	StringBuilder sb = new StringBuilder(rootDir+DOT_DIR);
    	//length should be greater than 5 because `.dot` is 4 letters
    	if(fileName != null && fileName.length() > 5) {
    		sb.append("/"+fileName);
    		if(!fileName.substring(fileName.length() - 4).equals(".dot")) {
    			sb.append(".dot");
    		}
    	} else {
    		sb.append("/graph_"+dotId+".dot");
    	}
    	return sb.toString();
    }
    
    /**
     * Creates image file name based on name passed by user and parameters set
     * @param fileName
     * @param dotId
     * @return filename
     */
    private String createDrawingFileName(String fileName, int dotId) {
    	StringBuilder sb = new StringBuilder(rootDir);
    	//length should be greater than 5 because `.dot` is 4 letters
    	if(fileName != null && fileName.length() > 5) {
    		sb.append("/"+fileName);
    		if(!fileName.substring(fileName.length() - (fileType.length()+1)).equals(fileType)) {
    			sb.append("."+fileType);
    		}
    	} else {
    		sb.append("/graph_drawing_"+dotId+"."+fileType);
    	}
    	return sb.toString();
    }

    /**
     * Writes dot to file
     * @param str
     * @return
     * @throws java.io.IOException
     */
    private int writeDotToFile(String filename, String dotString) throws IOException
    {
    	int dotId = 1;
    	if(filename == null) {
    		dotId = updateDotId();
    	} 
    	
    	String file = createDotFileName(filename, dotId);
    	File dot = new File(file);

        try {
        	dot.createNewFile();
            FileWriter fout = new FileWriter(dot);
            fout.write(dotString);
            fout.close();
        }
        catch (Exception e) {
            System.err.println("Error: I/O error while writing the dot source to dot file!");
            e.printStackTrace();
            return -1;
        }
        return dotId;
    }
    
    /**
     * For no more than 400 edges
     * Opens default browser and sends query to image-charts to draw graph
     * Graph will be drawn as .png file
     * @param graph
     */
    public void drawInBrowser(Object graph) {
    	try {
    		String dot = URLEncoder.encode(toDot(graph).replace("\s", "")
    				.replace("\n", "").replace("\t", ""), "UTF-8");
        	String url = GRAPH_DRAWING_ONLINE_SERVICE+dot;
			Desktop.getDesktop().browse(new URL(url).toURI());
		} catch (IOException | URISyntaxException e) {
			e.printStackTrace();
		}
    }
    
    /**
     * For no more than 400 edges
     * Opens default browser and sends query to image-charts to draw graph
     * Graph will be drawn as .png file
     * @param string (dot)
     */
    public void drawInBrowser(String dotString) {
    	try {
    		String dot = URLEncoder.encode(dotString.replace("\s", "")
    				.replace("\n", "").replace("\t", ""), "UTF-8");
        	String url = GRAPH_DRAWING_ONLINE_SERVICE+dot;
			Desktop.getDesktop().browse(new URL(url).toURI());
		} catch (IOException | URISyntaxException e) {
			e.printStackTrace();
		}
    }
    
    
    
    /**
     * Creates dot representation of graph passed as parameter.
     * Name of class is checked and required methods are used.
     * @param g
     * @return
     */
    public String toDot(Object graph) {
    	String className = graph.getClass().getSimpleName();
    	try {
			validateClassName(className);
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		}
		
    	StringBuilder sb = new StringBuilder();
    	try {
    		sb.append(graphTypeDotIdentifier(className));
    		//if graph or digraph
    		if(className.equals(graphClassName) 
    		   || className.equals(digraphClassName)) {
    			sb.append(linkVertices(graph, className));
    		//if edge weighted graph or edge weighted digraph
			} else if (className.equals(edgeWeightedGraphClassName) 
					   || className.equals(edgeWeightedDigraphClassName)) {
				sb.append(linkEdgeWeightedVertices(graph, className));
			}
		} catch (IllegalAccessException 
				| IllegalArgumentException 
				| InvocationTargetException 
				| NoSuchMethodException
				| SecurityException
				| ClassNotFoundException e) {
			e.printStackTrace();
		}
    	sb.append("}");
    	
    	return sb.toString();
    }
    
    /**
     * For graph and digraph creates string of vertices
     * @param graph
     * @param className
     * @return
     * @throws NoSuchMethodException
     * @throws SecurityException
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws InvocationTargetException
     */
    @SuppressWarnings("unchecked")
	private String linkVertices(Object graph, String className) throws 
	NoSuchMethodException, SecurityException, IllegalAccessException, 
	IllegalArgumentException, InvocationTargetException {
    	StringBuilder sb = new StringBuilder();
    	Method V = graph.getClass().getMethod(numberOfVerticesMethodName);
		Method adj = graph.getClass().getMethod(adjacentVerticesMethodName, int.class);
		for(int v = 0; v < (int)V.invoke(graph); v++) {
			for(int w : (Iterable<Integer>) adj.invoke(graph, v)) {
				if(className.equals(graphClassName)) {
					if(v < w) {
						sb.append(SPACING+v+ glue(className) + w);
						sb.append(";\n");
					}
					
				} else if(className.equals(digraphClassName)) {
					sb.append(SPACING+v+ glue(className) + w);
					sb.append(";\n");
				}
			}
		}
		return sb.toString();
    }
    
    @SuppressWarnings("unchecked")
	private String linkEdgeWeightedVertices(Object graph, String className) throws 
    NoSuchMethodException, SecurityException, IllegalAccessException, 
    IllegalArgumentException, InvocationTargetException {
    	StringBuilder sb = new StringBuilder();
    	int v = -1;
		int w = -1;
    	Method edges = graph.getClass().getMethod(edgeMethodName);
    	for(Object edge : (Iterable<Object>) edges.invoke(graph)) {
    		Method weight = edge.getClass().getMethod(edgeWeightMethodName);
			double wt = (double) weight.invoke(edge);
    		if(className.equals(edgeWeightedGraphClassName)) {
    			Method from = edge.getClass().getMethod(edgeWeightedGraphEither);
    			Method to = edge.getClass().getMethod(edgeWeightedGraphOther, int.class);
    			
    			v = (int) from.invoke(edge);
    			w = (int) to.invoke(edge, v);
    		} else if(className.equals(edgeWeightedDigraphClassName)) {
    			Method from = edge.getClass().getMethod(edgeWeightedDigraphFrom);
    			Method to = edge.getClass().getMethod(edgeWeightedDigraphTo);
    			
    			v = (int) from.invoke(edge);
    			w = (int) to.invoke(edge);
    		}
    		
    		if(v == -1 || w == -1) {
    			throw new IllegalArgumentException();
    		}
    		
    		sb.append(SPACING+v+ glue(className) + w + "[label="+wt+"]");
			sb.append(";\n");
    	}
    	return sb.toString();
    }
    
    /**
     * Allowed classes are declared as properties
     * graphClassName, digraphClassName, edgeWeightedGraphClassName, 
     * edgeWeightedDigraphClassName. If class name does not match
     * any of allowed names exception is thrown. Names can be changed by
     * using set methods;
     * @param className
     * @throws ClassNotFoundException
     */
    public void validateClassName(String className) throws ClassNotFoundException {
    	if(!className.equals(graphClassName) &&
    	   !className.equals(digraphClassName) && 
    	   !className.equals(edgeWeightedGraphClassName) &&
    	   !className.equals(edgeWeightedDigraphClassName)) {

           throw new ClassNotFoundException();
    	}
    }
    
    /**
     * Creates glue between vertices in graph
     * @param className
     * @return String glue (-- for graph or -> for digraph)
     */
    public String glue(String className) {
    	if(className.equals(graphClassName) || className.equals(edgeWeightedGraphClassName)) {
    		return " -- ";
    	} 
    	
    	return " -> ";
    }
    
    /**
     * Based on class name checks if graph we are looking at is directed
     * @param className
     * @return boolean
     */
    public boolean isDigraph(String className) {
    	if(className.equals(digraphClassName) || className.equals(edgeWeightedDigraphClassName)) {
    		return true;
    	}
    	
    	return false;
    }
    
    /**
     * Based on class name checks if graph we are looking at is weighted
     * @param className
     * @return boolean
     */
    public boolean isWeighted(String className) {
    	if(className.equals(edgeWeightedGraphClassName) || className.equals(edgeWeightedDigraphClassName)) {
    		return true;
    	}
    	
    	return false;
    }
    
    public String graphTypeDotIdentifier(String className) throws ClassNotFoundException {
    	if(className.equals(graphClassName) || className.equals(edgeWeightedGraphClassName)) {
    		return "graph {\n";
    	} else if(className.equals(digraphClassName) || className.equals(edgeWeightedDigraphClassName)) {
    		return "digraph {\n";
    	}
    	
    	throw new ClassNotFoundException();
    }
    
    /**
     * Updates graph dot id in info/counter.txt (by default) file
     * Graph dot id tracks how many graph are created 
     * and increments when new graph is created
     * @return number of current graph
     */
    public int updateDotId() {
    	return writeDotId(readDotId());
    }
    
    /**
     * Reads graph dot id in info/counter.txt (by default) file.
     * @return
     */
    private int readDotId() {
    	BufferedReader br = null;
    	try {
			br = new BufferedReader(new FileReader(new File(rootDir+GRAPH_COUNTER)));
			String s = br.readLine();
			if(s == null) {
				return 0;
			}
			return Integer.parseInt(s);
		} catch (Exception e) {
			System.err.println("Cant read graph counter on location "+rootDir+GRAPH_COUNTER+".");
			e.printStackTrace();
		} finally {
			try {
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return -1;
    }
    
	/**
	 * Writes to graph counter in info/counter.txt (by default) file,
	 * currentGraph is read, incremented and written to counter as
	 * new count of graph created so far.
	 * @param currentGraph
	 * @return
	 */
    private int writeDotId(int currentGraph) {
    	PrintWriter pw = null;
    	currentGraph += 1;
    	try {
	      pw = new PrintWriter(rootDir+GRAPH_COUNTER);
	      pw.print("");
	      pw.print(currentGraph);
	    } catch (IOException e) {
	      System.err.println("Cant write in graph counter on location "+rootDir+GRAPH_COUNTER+".");
	      e.printStackTrace();
	    } finally {
	    	pw.close();
	    }

    	return currentGraph;
    }

    public void setNumberOfVerticesMethodName(String methodName) {
    	numberOfVerticesMethodName = methodName;
    }
    
    public void setAdjacentVerticesMethodName(String methodName) {
    	adjacentVerticesMethodName = methodName;
    }
    
    public void setGraphClassName(String className) {
    	graphClassName = className;
    }
    
    public void setDigraphClassName(String className) {
    	digraphClassName = className;
    }
    
    public void setEdgeWeightedGraphClassName(String className) {
    	edgeWeightedGraphClassName = className;
    }
    
    public void setEdgeWeightedDigraphClassName(String className) {
    	edgeWeightedDigraphClassName = className;
    }
    
    public void setEdgeMethodName(String methodName) {
    	edgeMethodName = methodName;
    }
    
    public void setEdgeWeightMethodName(String methodName) {
    	edgeWeightMethodName = methodName;
    }
    
    public void setEdgeWeightedGraphEither(String methodName) {
    	edgeWeightedGraphEither = methodName;
    }
    
    public void setEdgeWeightedGraphOther(String methodName) {
    	edgeWeightedGraphOther = methodName;
    }
    
    public void setEdgeWeightedDigraphFrom(String methodName) {
    	edgeWeightedDigraphFrom = methodName;
    }
    
    public void setEdgeWeightedDigraphTo(String methodName) {
    	edgeWeightedDigraphTo = methodName;
    }

    public void setFileType(String fileType) {
    	if(!ALLOWED_FILE_TYPES.contains(fileType)) {
    		throw new IllegalArgumentException();
    	}
    	
    	this.fileType = fileType;
    }
    
    public void resetMethodNames() {
    	numberOfVerticesMethodName = "V";
        adjacentVerticesMethodName = "adj";
    	edgeMethodName = "edges";
        edgeWeightMethodName = "weight";
        edgeWeightedGraphEither = "either";
        edgeWeightedGraphOther = "other";
        edgeWeightedDigraphFrom = "from";
        edgeWeightedDigraphTo = "to";
    }
    
    public void resetGraphClassNames() {
    	graphClassName = "Graph";
        digraphClassName = "Digraph";
        edgeWeightedGraphClassName = "EdgeWeightedGraph";
        edgeWeightedDigraphClassName = "EdgeWeightedDigraph";
    }

    /**
     * helper method //finish weighted graphs
     */
    public static void help() {
    	StringBuilder sb = new StringBuilder("-----GraphViz help-----\n");
    	sb.append("-GraphViz expects GraphViz (https://graphviz.org/) to be installed on your computer.\n"
    			+ "-GraphViz is made to be used within graph classes used and implemented on course Data Structures and\n"
    			+ "-Algorithms 3. GraphViz works as expected with Graph, Digraph, EdgeWeightedGraph and EdgeWeightedDigraph\n"
    			+ "-from edu.princeton.cs.algs4 package on Windows 10, and Ubuntu 20.04 LTS operating systems. Custom implemetations\n"
                + "-of mentioned classes also works fine as long as methods are defined or set as described further in text.\n"
                + "-MacOSX is not tested.\n\n");
    	sb.append("-GraphViz can be used for making dot string representation of Graphs used and implemented on course\n"
    			+ "-Data Structures and Algorithms 3. To make graph image GraphViz expects either object of one of graph\n"
    			+ "-types used on aforementioned course or valid dot representation of required graph.\n");
    	sb.append("-Default allowed class names are Graph, Digraph, EdgeWeightedGraph and EdgeWeightedDigraph.\n"
    			+ "-Class names can be changed by using methods: \n"
    			+ "\t-setGraphClassName(String className)\n"
    			+ "\t-setDigraphClassName(String className)\n"
    			+ "\t-setEdgeWeightedGraphClassName(String className)\n"
    			+ "\t-setEdgeWeightedDigraphClassName(String className)\n"
    			+ "-For example if name of Digraph class is Digraf, setDigraphClassName(\"Digraf\") should be used.\n\n");
    	sb.append("-Our implementation expects graph and digraph implementations to have following methods: \n"
    			+ "\t-(by default) V() that returns number of vertices in graph \n"
    			+ "\t-(by default) adj(int v) that return Iterable<Integer> of vertices adjacent to vertex v.\n"
    			+ "-These method names can be changed by using methods: \n"
    			+ "\t-setNumberOfVerticesMethodName(String methodName)\n"
    			+ "\t-setAdjacentVerticesMethodName(String methodName)\n\n"
    			+ "-For EdgeWeightedGraph and EdgeWeightedDigraph it is expected that edge is Object of some type.\n"
    			+ "-Methods expected in mentioned edge object are:\n"
    			+ "\t-(by default) edges() that returns Iterable<Object> of edges from EdgeWeightedGraph/EdgeWeightedDigraph\n"
    			+ "\t-(by default) either() and other(int v) that return vertices incident to Edge in EdgeWeightedGraph\n"
    			+ "\t-(by default) from() and to() that return vertices incident ot DirectedEdge in EdgeWeightedDigraph\n"
    			+ "\t-(by default) weight() that returns edge weight.\n"
    			+ "These method names can be changed by using: \n"
    			+ "\t-setEdgeMethodName(String methodName)\n"
    			+ "\t-setEdgeWeightedGraphEither(String methodName)\n"
    			+ "\t-setEdgeWeightedGraphOther(String methodName)\n"
    			+ "\t-setEdgeWeightedDigraphFrom(String methodName)\n"
    			+ "\t-setEdgeWeightedDigraphTo(String methodName)\n"
    			+ "\t-setEdgeWeightMethodName(String methodName)\n\n"
    			+ "-If in your implementation of Graph/Digraph your method for number of vertices in Graph/Digraph is:\n"
    			+ "-public int getNumberOfVertices(){...}\n"
    			+ "-then setNumberOfVerticesMethodName(String methodName) should be used as:\n"
    			+ "-setNumberOfVerticesMethodName(\"getNumberOfVertices\").\n\n"
    			+ "-Same can be applied for other methods for setting method name.\n"
    			+ "-All method and graph names can be reset to default by using methods resetMethodNames() and resetGraphClassNames()\n"
    			+ "-respectively.\n\n");
    	sb.append("-By using method toDot(Object graph) GraphViz will return String representaion of graph passed to method.\n");
    	sb.append("-Output image type can be changed by using method setFileType(String fileType). Allowed file types are:\n"
    			+ "\t-png (default)\n"
    			+ "\t-jpg\n"
    			+ "\t-gif.\n"
    			+ "-For example if gif is required, method setFileType(String fileType) should be used as setFileType(\"gif\").\n\n");
    	sb.append("-If in your implementation of graph you have toDot() method, or toString() method that returns dot format of graph,\n"
    			+ "-(or for that purpose any other method that returns dot formated string of graph)\n"
    			+ "-GraphViz can be used to draw your graph by using\n"
    			+ "-createGraphImage(String dotSource) as createGraphImage(graph.toDot()) or createGraphImage(graph.toString()).\n\n");
    	sb.append("-For graphs that have less than 400 edges method drawInBrowser(Object graph) or drawInBrowser(String dot) can be used.\n"
    			+ "-Default browser will be opened and appropriate query sent to service that does drawing.");
    	sb.append("----Usage example----\n"
    			+ "public class Main {\r\n"
    			+ "    public static void main(String[] args) {\r\n"
    			+ "        Digraph d = new Digraph(Svetovid.in(\"digraf-zavrsni.txt\"));\r\n"
    			+ "        Graph g = new Graph(Svetovid.in(\"mediumG.txt\"));\r\n"
    			+ "        EdgeWeightedDigraph ewd = new EdgeWeightedDigraph(new In(\"tinyEWD.txt\"));\r\n"
    			+ "        EdgeWeightedGraph ewdd = new EdgeWeightedGraph(new In(\"tinyEWD.txt\"));\r\n\r\n"
    			+ "        GraphViz graphViz = new GraphViz();\r\n"
    			+ "        //usage example by providing graph object, and notifying GraphViz about method names\r\n"
    			+ "        graphViz.createGraphImage(ewd);\r\n"
    			+ "        graphViz.setFileType(\"jpg\");\r\n"
    			+ "        graphViz.createGraphImage(ewdd);\r\n"
    			+ "        graphViz.setFileType(\"gif\");\r\n"
    			+ "        graphViz.setAdjacentVerticesMethodName(\"susjedi\");\r\n"
    			+ "        graphViz.setNumberOfVerticesMethodName(\"brojCvorova\");\r\n"
    			+ "        graphViz.createGraphImage(d);\r\n"
    			+ "        graphViz.resetMethodNames();\r\n"
    			+ "        graphViz.setFileType(\"png\");\r\n"
    			+ "        graphViz.createGraphImage(g);\r\n"
    			+ "        //if your toString() or toDot() method returns proper dot representation of graph respectively\r\n"
    			+ "        graphViz.createGraphImage(g.toString())\r\n"
    			+ "        graphViz.createGraphImage(g.toDot())\r\n"
    			+ "    }\r\n"
    			+ "}");
    	System.out.println(sb);
    }
    
    public static void main(String[] args) {
    	GraphViz.help();
    }
}

